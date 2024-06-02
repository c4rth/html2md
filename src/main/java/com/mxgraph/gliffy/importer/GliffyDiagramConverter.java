/**
 * Copyright (c) 2006-2018, JGraph Ltd
 * Copyright (c) 2006-2018, Gaudenz Alder
 */
package com.mxgraph.gliffy.importer;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.module.SimpleModule;
import com.mxgraph.gliffy.model.*;
import com.mxgraph.io.mxCodec;
import com.mxgraph.model.mxCell;
import com.mxgraph.model.mxGeometry;
import com.mxgraph.model.mxIGraphModel;
import com.mxgraph.online.Utils;
import com.mxgraph.util.*;
import com.mxgraph.view.mxGraphHeadless;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Performs a conversion of a Gliffy diagram into a Draw.io diagram
 */
@Slf4j
public class GliffyDiagramConverter {
    private static final Pattern lightboxPageIdGliffyMigration = Pattern.compile("pageId=(.*?)(?=&)");
    private static final Pattern lightboxNameGliffyMigration = Pattern.compile("name=(.*?)(?=&)");
    private final String diagramString;
    private final mxGraphHeadless drawioDiagram;
    private final Map<String, GliffyObject> vertices;
    private final Map<String, GliffyLayer> layers;
    private final Pattern rotationPattern = Pattern.compile("rotation=(\\-?\\w+)");
    @Getter
    private final StringBuilder report;
    private Diagram gliffyDiagram;

    /**
     * Constructs a new converter and starts a conversion.
     *
     * @param gliffyDiagramString JSON string of a gliffy diagram
     */
    public GliffyDiagramConverter(String gliffyDiagramString) {
        vertices = new LinkedHashMap<String, GliffyObject>();
        layers = new LinkedHashMap<String, GliffyLayer>();
        this.diagramString = gliffyDiagramString;
        drawioDiagram = new mxGraphHeadless();
        //Disable parent (groups) auto extend feature as it miss with the coordinates of vsdx format
        drawioDiagram.setExtendParents(false);
        drawioDiagram.setExtendParentsOnAdd(false);
        drawioDiagram.setConstrainChildren(false);
        this.report = new StringBuilder();
        start();
    }

    private void start() {
        try {
            ObjectMapper objectMapper = new ObjectMapper();
            objectMapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);

            SimpleModule module = new SimpleModule();
            module.setDeserializerModifier(new PostDeserializer());
            objectMapper.registerModule(module);

            this.gliffyDiagram = objectMapper.readValue(diagramString, Diagram.class);
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }

        collectLayersAndConvert(layers, gliffyDiagram.stage.getLayers());
        collectVerticesAndConvert(vertices, gliffyDiagram.stage.getObjects(), null);
        //sort objects by the order specified in the Gliffy diagram
        sortObjectsByOrder(gliffyDiagram.stage.getObjects());
        drawioDiagram.getModel().beginUpdate();

        try {
            importLayers();
            for (GliffyObject obj : gliffyDiagram.stage.getObjects()) {
                try {
                    importObject(obj, obj.parent);
                } catch (Throwable thr) {
                    report.append("-- Warning, Object ").append(obj.id).append(" cannot be transformed. Please contact support for more details.").append(System.lineSeparator());
                }
            }
        } finally {
            drawioDiagram.getModel().endUpdate();
        }
    }

    /**
     * Imports the layers into the draw.io diagram after sorting them by their order.
     */
    private void importLayers() {
        Object root = drawioDiagram.getModel().getRoot();

        List<GliffyLayer> layers = gliffyDiagram.stage.getLayers();

        if (layers != null) {
            sortLayersByOrder(layers);

            for (GliffyLayer layer : layers) {
                drawioDiagram.addCell(layer.mxObject, root);
            }
        }
    }

    /**
     * Imports the objects into the draw.io diagram. Recursively adds the children
     */
    private void importObject(GliffyObject obj, GliffyObject gliffyParent) {
        mxCell parent = gliffyParent != null ? gliffyParent.mxObject : null;

        //add a layer as a parent only if the object is not a child object
        if (parent == null && obj.layerId != null) {
            GliffyLayer layer = layers.get(obj.layerId);

            if (layer != null) {
                parent = layer.mxObject;
            }
        }

        drawioDiagram.addCell(obj.mxObject, parent);

        if (obj.hasChildren()) {
            // sort the children except for swimlanes
            // their order value is "auto"
            if (obj.isSwimlane()) {
                // rotated swimlane child order is inverse
                if (obj.rotation != 0) {
                    Collections.reverse(obj.children);
                }
            } else {
                sortObjectsByOrder(obj.children);
            }

            for (GliffyObject child : obj.children) {
                importObject(child, obj);
            }
        }

        if (obj.isLine()) {
            // gets the terminal cells for the edge
            mxCell startTerminal = getTerminalCell(obj, true);
            mxCell endTerminal = getTerminalCell(obj, false);

            obj.getMxObject().setTerminal(startTerminal, true);
            obj.getMxObject().setTerminal(endTerminal, false);

            setWaypoints(obj, startTerminal, endTerminal);
        }
    }

    private void sortObjectsByOrder(Collection<GliffyObject> values) {
        Comparator<GliffyObject> c = (o1, o2) -> {
            Float o1o;
            float o2o;
            try {
                //we treat the "null" order as higher than "non-null"
                if (o1.order == null && o2.order == null) {
                    return 0;
                } else if (o1.order == null && o2.order != null) {
                    return 1;
                } else if (o1.order != null && o2.order == null) {
                    return -1;
                }

                o1o = Float.parseFloat(o1.order);
                o2o = Float.parseFloat(o2.order);

                return o1o.compareTo(o2o);
            } catch (NumberFormatException e) {
                return o1.order.compareTo(o2.order);
            }

        };

        ((List<GliffyObject>) values).sort(c);
    }

    private void sortLayersByOrder(List<GliffyLayer> values) {
        Comparator<GliffyLayer> c = (o1, o2) -> o1.order - o2.order;

        values.sort(c);
    }

    private mxCell getTerminalCell(GliffyObject gliffyEdge, boolean start) {
        Constraints cons = gliffyEdge.getConstraints();

        if (cons == null) {
            return null;
        }

        Constraint con = start ? cons.getStartConstraint() : cons.getEndConstraint();

        if (con == null) {
            return null;
        }

        Constraint.ConstraintData cst = start ? con.getStartPositionConstraint() : con.getEndPositionConstraint();
        if (cst == null) {
            return null;
        }
        String nodeId = cst.getNodeId();
        GliffyObject gliffyEdgeTerminal = vertices.get(nodeId);

        //edge could be terminated with another edge, so import it as a dangling edge
        if (gliffyEdgeTerminal == null) {
            return null;
        }

        return gliffyEdgeTerminal.getMxObject();
    }

    private String getStyle(mxCell cell, String key, String defaultValue) {
        String style = cell.getStyle();

        if (style != null && !style.isEmpty()) {
            String[] pairs = style.split(";");

            for (String tmp : pairs) {
                int c = tmp.indexOf('=');
                if (c >= 0 && tmp.substring(0, c).equalsIgnoreCase(key)) {
                    return tmp.substring(c + 1);
                }
            }
        }

        return defaultValue;
    }

    private boolean addConstraint(GliffyObject object, mxCell terminal, boolean source, boolean orthogonal) {
        Constraints cons = object.getConstraints();
        Constraint con = (cons != null) ? ((source) ? cons.getStartConstraint() : cons.getEndConstraint()) : null;
        Constraint.ConstraintData data = (con != null) ? ((source) ? con.getStartPositionConstraint() : con.getEndPositionConstraint()) : null;

        if (data != null) {
            String direction = getStyle(terminal, mxConstants.STYLE_DIRECTION, "east");
            mxPoint temp = new mxPoint(data.getPx(), data.getPy());
            int rotation = 0;

            if (direction.equalsIgnoreCase("south")) {
                rotation = 270;
            } else if (direction.equalsIgnoreCase("west")) {
                rotation = 180;
            } else if (direction.equalsIgnoreCase("north")) {
                rotation = 90;
            }

            if (rotation != 0) {
                double rad = Math.toRadians(rotation);

                temp = mxUtils.getRotatedPoint(temp, Math.cos(rad), Math.sin(rad), new mxPoint(0.5, 0.5));
            }

            if (!orthogonal || (temp.getX() == 0.5 && temp.getY() == 0.5) || GliffyObject.FORCE_CONSTRAINTS_SHAPES.contains(object.uid)) {
                mxCell cell = object.getMxObject();
                cell.setStyle(cell.getStyle() + ((source) ? "exitX=" : "entryX=") + temp.getX() + ";" + ((source) ? "exitY=" : "entryY=")
                        + temp.getY() + ";" + ((source) ? "exitPerimeter=0" : "entryPerimeter=0") + ";");
            }

            return true;
        }

        return orthogonal;
    }

    ;

    /**
     * Sets the waypoints
     *
     * @param object        Gliffy line
     * @param startTerminal starting point
     * @param endTerminal   ending point
     */
    private void setWaypoints(GliffyObject object, mxCell startTerminal, mxCell endTerminal) {
        mxCell cell = object.getMxObject();
        mxGeometry geo = drawioDiagram.getModel().getGeometry(cell);
        geo.setRelative(true);

        List<float[]> points = object.getGraphic().getLine().controlPath;

        if (points.size() < 2) {
            return;
        }

        List<mxPoint> mxPoints = new ArrayList<mxPoint>();

        mxPoint pivot = new mxPoint(object.x + object.width / 2, object.y + object.height / 2);

        for (float[] point : points) {
            mxPoint waypoint = new mxPoint(point[0] + object.x, point[1] + object.y);

            if (object.rotation != 0) {
                double rads = Math.toRadians(object.rotation);
                double cos = Math.cos(rads);
                double sin = Math.sin(rads);
                waypoint = mxUtils.getRotatedPoint(waypoint, cos, sin, pivot);
            }

            mxPoints.add(waypoint);
        }

        // Analyze waypoints
        boolean orthogonal = true;

        mxPoint p0 = mxPoints.getFirst();
        mxPoint pe = mxPoints.getLast();

        Iterator<mxPoint> it = mxPoints.iterator();
        mxPoint last = it.next();

        while (it.hasNext()) {
            mxPoint current = it.next();
            orthogonal = orthogonal && (last.getX() == current.getX() || last.getY() == current.getY());
            last = current;
        }

        if (startTerminal == null) {
            geo.setTerminalPoint(p0, true);
            mxPoints.remove(p0);// remove first so it doesn't become a waypoint
        } else {
            // Do not add constraint for orthogonal edges
            if (addConstraint(object, startTerminal, true, orthogonal)) {
                //Removing the point resulted in incorrect edges
                //TODO confirm nothing is affected from this change
                //mxPoints.remove(p0);
            }
        }

        if (endTerminal == null) {
            geo.setTerminalPoint(pe, false);
            mxPoints.remove(pe);// remove last so it doesn't become a waypoint
        } else {
            // Do not add constraint for orthogonal edges
            if (addConstraint(object, endTerminal, false, orthogonal)) {
                //Removing the point resulted in incorrect edges
                //TODO confirm nothing is affected from this change
                //mxPoints.remove(pe);
            }
        }

        if (orthogonal) {
            cell.setStyle(cell.getStyle() + "edgeStyle=orthogonalEdgeStyle;");
            List<mxPoint> result = new ArrayList<mxPoint>();

            // Removes duplicate waypoints
            if (!mxPoints.isEmpty()) {
                it = mxPoints.iterator();
                last = it.next();

                result.add(last);

                while (it.hasNext()) {
                    mxPoint current = it.next();

                    if (last.getX() != current.getX() || last.getY() != current.getY()) {
                        result.add(current);
                    }

                    last = current;
                }
            } else if ((startTerminal == null && endTerminal != null) || (endTerminal == null && startTerminal != null)) {
                // Adds control points to fix floating connection point
                mxPoint center = new mxPoint(p0.getX() + (pe.getX() - p0.getX()) / 2, p0.getY() + (pe.getY() - p0.getY()) / 2);
                result.add(center);
                result.add(center);
            }

            mxPoints = result;
        }

        if (!mxPoints.isEmpty()) {
            geo.setPoints(mxPoints);
        }

        drawioDiagram.getModel().setGeometry(cell, geo);
    }

    /**
     * Creates a map of all layers so they can be easily accessed when looking
     * up cells layers
     */
    private void collectLayersAndConvert(Map<String, GliffyLayer> layersMap, Collection<GliffyLayer> layers) {
        if (layers == null) {
            return;
        }

        // Removes empty default layer
        if (!layers.isEmpty()) {
            mxIGraphModel model = drawioDiagram.getModel();

            if (model.getChildCount(model.getRoot()) > 0) {
                model.remove(model.getChildAt(model.getRoot(), 0));
            }
        }

        for (GliffyLayer layer : layers) {
            mxCell layerCell = new mxCell();
            layerCell.setVisible(layer.visible);
            if (layer.locked) {
                layerCell.setStyle("locked=1;");
            }

            //			layer.active //How can we set the active layer in draw.io?
            //			layer.nodeIndex //??

            layerCell.setValue(layer.name);
            layer.mxObject = layerCell;
            layersMap.put(layer.guid, layer);
        }
    }

    /**
     * Creates a map of all vertices so they can be easily accessed when looking
     * up terminal cells for edges
     */
    private void collectVerticesAndConvert(Map<String, GliffyObject> vertices, Collection<GliffyObject> objects, GliffyObject parent) {
        for (GliffyObject object : objects) {
            object.parent = parent;

            convertGliffyObject(object, parent);

            if (!object.isLine()) {
                vertices.put(object.id, object);
            }

            // don't collect for swimlanes and mindmaps, their children are treated differently
            if (object.isGroup() || object.isSelection() || (object.isLine() && object.hasChildren())) {
                collectVerticesAndConvert(vertices, object.children, object);
            }
        }
    }

    /**
     * Converts the mxGraph to xml string
     */
    public String getGraphXml() {
        mxCodec codec = new mxCodec();
        Element node = (Element) codec.encode(drawioDiagram.getModel());
        node.setAttribute("style", "default-style2");
        node.setAttribute("background", gliffyDiagram.stage.getBackgroundColor());
        node.setAttribute("grid", gliffyDiagram.stage.isGridOn() ? "1" : "0");
        node.setAttribute("guides", gliffyDiagram.stage.isDrawingGuidesOn() ? "1" : "0");
        return mxXmlUtils.getXml(node);
    }

    /**
     * Performs the object conversion
     */
    private mxCell convertGliffyObject(GliffyObject gliffyObject, GliffyObject parent) {
        mxCell cell = new mxCell();

        if (gliffyObject.isUnrecognizedGraphicType()) {
            log.warn("Unrecognized graphic type for object with ID : {}", gliffyObject.id);
            return cell;
        }

        StringBuilder style = new StringBuilder();

        mxGeometry geometry = new mxGeometry(gliffyObject.x, gliffyObject.y, gliffyObject.width, gliffyObject.height);
        gliffyObject.adjustGeo(geometry);
        cell.setGeometry(geometry);

        GliffyObject textObject = null;
        String link = null;

        Graphic graphic = gliffyObject.getGraphic();
        String translatedStyle = StencilTranslator.translate(gliffyObject.uid,
                graphic != null && graphic.getShape() != null ? graphic.getShape().tid : null);

        if (gliffyObject.isGroup()) {
            if (graphic == null || translatedStyle == null)
                style.append("group;");

            cell.setVertex(true);
        } else {
            textObject = gliffyObject.getTextObject();
        }

        if (graphic != null) {
            link = gliffyObject.getLink();

            if (gliffyObject.isShape()) {
                Graphic.GliffyShape shape = graphic.Shape;

                cell.setVertex(true);

                boolean isChevron = gliffyObject.uid != null && gliffyObject.uid.contains("chevron");

                if (translatedStyle != null) {
                    style.append("shape=").append(translatedStyle).append(";");
                }

                if (style.lastIndexOf("shadow=") == -1) {
                    style.append("shadow=").append(shape.dropShadow ? 1 : 0).append(";");
                }

                if (style.lastIndexOf("strokeWidth") == -1) {
                    style.append("strokeWidth=").append(shape.getStrokeWidth()).append(";");

                    if (shape.getStrokeWidth() == 0 && !isChevron) {
                        style.append("strokeColor=none;");
                    }
                }

                if (style.lastIndexOf("fillColor") == -1) {
                    if (shape.isNoFill() && !isChevron) {
                        style.append("fillColor=none;");
                    } else {
                        style.append("fillColor=").append(shape.fillColor).append(";");
                    }
                    if (shape.fillColor.equals("none")) {
                        style.append("pointerEvents=0;");
                    }
                }

                if (style.lastIndexOf("strokeColor") == -1 && !shape.isNoFill()) {
                    String strokeClr = gliffyObject.isUseFillColor4StrokeColor() ? shape.fillColor : shape.strokeColor;
                    style.append("strokeColor=").append(strokeClr).append(";");
                }

                if (style.lastIndexOf("gradient") == -1 && shape.gradient && !gliffyObject.isGradientIgnored()) {
                    style.append("gradientColor=").append(gliffyObject.getGradientColor()).append(";gradientDirection=north;");
                }

                // opacity value is wrong for venn circles, so ignore it and use the one in the mapping
                if (!gliffyObject.isVennCircle() && style.lastIndexOf("opacity") == -1) {
                    style.append("opacity=").append(shape.opacity * 100).append(";");
                }

                style.append(DashStyleMapping.get(shape.dashStyle, 1));

                if (gliffyObject.isSubRoutine()) {
                    //Gliffy's subroutine maps to drawio process, whose inner boundary, unlike subroutine's, is relative to it's width so here we set it to 10px
                    style.append("size=").append(10 / gliffyObject.width).append(";");
                }

                String fragmentText;
                if ((fragmentText = gliffyObject.getUmlSequenceCombinedFragmentText()) != null) {
                    cell.setValue(fragmentText);
                    gliffyObject.children.removeFirst();
                }
            } else if (gliffyObject.isLine()) {
                Graphic.GliffyLine line = graphic.Line;

                cell.setEdge(true);
                style.append("shape=filledEdge;");
                //style.append("edgeStyle=none;");
                style.append("strokeWidth=").append(line.getStrokeWidth()).append(";");
                style.append("strokeColor=").append(line.strokeColor).append(";");
                style.append("fillColor=").append(line.fillColor).append(";");
                style.append(ArrowMapping.get(line.startArrow).toString(true)).append(";");
                style.append(ArrowMapping.get(line.endArrow).toString(false)).append(";");
                style.append("rounded=").append(line.cornerRadius != null ? "1" : "0").append(";");
                style.append(DashStyleMapping.get(line.dashStyle, line.getStrokeWidth()));
                style.append(LineMapping.get(line.interpolationType));

                geometry.setX(0);
                geometry.setY(0);
            } else if (gliffyObject.isText()) {
                textObject = gliffyObject;
                cell.setVertex(true);
                style.append("text;html=1;nl2Br=0;");
                cell.setValue(gliffyObject.getText());

                //if text is a child of a cell, use relative geometry and set X and Y to 0
                if (gliffyObject.parent != null && !gliffyObject.parent.isGroup()) {
                    mxGeometry parentGeometry = gliffyObject.parent.mxObject.getGeometry();

                    //if text is a child of a line, special positioning is in place
                    if (gliffyObject.parent.isLine()) {
                        /* Gliffy's text offset is a float in the range of [0,1]
                         * draw.io's text offset is a float in the range of [-1,-1] (while still keeping the text within the line)
                         * The equation that translates Gliffy offset to draw.io offset is : G*2 - 1 = D
                         */
                        mxGeometry mxGeo = new mxGeometry(graphic.Text.lineTValue != null ? graphic.Text.lineTValue * 2 - 1 : 0, 0, 0, 0);

                        float lblY = 0, lblX = 0;

                        if (graphic.Text.linePerpValue != null) {
                            List<float[]> controlPath = gliffyObject.parent.graphic.Line.controlPath;

                            if (controlPath != null && controlPath.size() >= 2) {
                                int i1 = 0, i2 = controlPath.size() - 1;
                                boolean noCardinal = false;

                                if ("begin".equals(graphic.Text.cardinalityType)) {
                                    i2 = 1;
                                } else if ("end".equals(graphic.Text.cardinalityType)) {
                                    i1 = controlPath.size() - 2;
                                } else {
                                    noCardinal = true;
                                }

                                if (noCardinal || (controlPath.get(i1)[1] == controlPath.get(i2)[1])) {
                                    lblY = graphic.Text.linePerpValue;

                                    if (controlPath.get(i1)[0] - controlPath.get(i2)[0] > 0) {
                                        lblY = -lblY;
                                    }
                                } else {
                                    lblX = graphic.Text.linePerpValue;

                                    if (controlPath.get(i1)[1] - controlPath.get(i2)[1] < 0) {
                                        lblX = -lblX;
                                    }
                                }
                            }
                        }

                        mxGeo.setOffset(new mxPoint(lblX, lblY));
                        cell.setGeometry(mxGeo);

                        style.append("labelBackgroundColor=").append(gliffyDiagram.stage.getBackgroundColor()).append(";");
                        //should we force horizontal align for text on lines?
                        //Most probably yes, as extracting alignment from html messes with some cases [set halign to null later]
                        //style.append("align=center;");

                        //This is a workaround for edges as its label offset is incorrect when alignment is set. It should be handled in a better way
                        //HTML text-align is not mapped to halign for edges
                        //TODO Enhance edge's label positioning
                        gliffyObject.graphic.getText().setHalign(null);
                    } else {
                        cell.setGeometry(new mxGeometry(0, 0, parentGeometry.getWidth(), parentGeometry.getHeight()));
                    }

                    cell.getGeometry().setRelative(true);
                }
            } else if (gliffyObject.isImage()) {
                Graphic.GliffyImage image = graphic.getImage();
                cell.setVertex(true);
                style.append("shape=").append(StencilTranslator.translate(gliffyObject.uid, null)).append(";");
                style.append("image=").append(image.getUrl()).append(";");
            } else if (gliffyObject.isSvg()) {
                Graphic.GliffySvg svg = graphic.Svg;
                cell.setVertex(true);
                style.append("shape=image;imageAspect=0;");
                EmbeddedResources.Resource res = gliffyDiagram.embeddedResources.get(svg.embeddedResourceId);
                SVGImporterUtils svgUtils = new SVGImporterUtils();
                res.data = svgUtils.setViewBox(res.data);
                style.append("image=data:image/svg+xml,").append(res.getBase64EncodedData()).append(";");
            }
        }
        // swimlanes have children without uid so their children are converted here ad hoc
        else if (gliffyObject.isSwimlane() && gliffyObject.children != null && !gliffyObject.children.isEmpty()) {
            cell.setVertex(true);
            style.append(StencilTranslator.translate(gliffyObject.uid, null)).append(";");

            if (gliffyObject.rotation == 0) //270 case is handled in rotation below
            {
                style.append("childLayout=stackLayout;resizeParent=1;resizeParentMax=0;");
            }

            GliffyObject header = gliffyObject.children.getFirst();// first child is the header of the swimlane

            Graphic.GliffyShape shape = header.graphic.getShape();
            style.append("strokeWidth=").append(shape.getStrokeWidth()).append(";");
            style.append("shadow=").append(shape.dropShadow ? 1 : 0).append(";");
            style.append("fillColor=").append(shape.fillColor).append(";");
            style.append("strokeColor=").append(shape.strokeColor).append(";");
            style.append("startSize=").append(header.height).append(";");
            style.append("whiteSpace=wrap;");

            for (int i = 1; i < gliffyObject.children.size(); i++) // rest of the children are lanes
            {
                GliffyObject gLane = gliffyObject.children.get(i);
                gLane.parent = gliffyObject;

                Graphic.GliffyShape gs = gLane.graphic.getShape();
                StringBuilder laneStyle = new StringBuilder();
                laneStyle.append("swimlane;collapsible=0;swimlaneLine=0;");
                laneStyle.append("strokeWidth=").append(gs.getStrokeWidth()).append(";");
                laneStyle.append("shadow=").append(gs.dropShadow ? 1 : 0).append(";");
                laneStyle.append("fillColor=").append(gs.fillColor).append(";");
                laneStyle.append("strokeColor=").append(gs.strokeColor).append(";");
                laneStyle.append("whiteSpace=wrap;html=1;fontStyle=0;");

                mxGeometry childGeometry = new mxGeometry(gLane.x, gLane.y, gLane.width, gLane.height);

                if (gliffyObject.rotation != 0) {

                    if (gliffyObject.rotation == 270) //Special handling for this common case
                    {
                        laneStyle.append("horizontal=0;");
                        double width = childGeometry.getWidth();
                        childGeometry.setWidth(childGeometry.getHeight());
                        childGeometry.setHeight(width);
                        double x = childGeometry.getX();
                        childGeometry.setX(childGeometry.getY());
                        childGeometry.setY(gliffyObject.width - width - x);
                    } else {
                        laneStyle.append("rotation=").append(gliffyObject.rotation).append(";");
                        Utils.rotatedGeometry(childGeometry, gliffyObject.rotation, gliffyObject.width / 2, gliffyObject.height / 2);
                    }
                }

                mxCell mxLane = new mxCell();
                mxLane.setVertex(true);
                cell.insert(mxLane);

                GliffyObject laneTxt = gLane.children.getFirst();
                mxLane.setValue(laneTxt.getText());
                laneStyle.append(laneTxt.graphic.getText().getStyle(0, 0));
                //for debugging, add gliffy id to the output in the style
                laneStyle.append("gliffyId=").append(gLane.id).append(";");
                mxLane.setStyle(laneStyle.toString());

                mxLane.setGeometry(childGeometry);
                gLane.mxObject = mxLane;
            }
        } else if (gliffyObject.isMindmap() && gliffyObject.children != null && !gliffyObject.children.isEmpty()) {
            GliffyObject rectangle = gliffyObject.children.getFirst();

            Graphic.GliffyMindmap mindmap = rectangle.graphic.Mindmap;

            style.append("shape=").append(StencilTranslator.translate(gliffyObject.uid, null)).append(";");
            style.append("shadow=").append(mindmap.dropShadow ? 1 : 0).append(";");
            style.append("strokeWidth=").append(mindmap.getStrokeWidth()).append(";");
            style.append("fillColor=").append(mindmap.fillColor).append(";");
            style.append("strokeColor=").append(mindmap.strokeColor).append(";");
            style.append(DashStyleMapping.get(mindmap.dashStyle, 1));

            if (mindmap.gradient) {
                style.append("gradientColor=#FFFFFF;gradientDirection=north;");
            }

            cell.setVertex(true);
        }

        if (!gliffyObject.isLine()) {
            //if there's a rotation by default, add to it
            if (style.lastIndexOf("rotation") != -1) {
                Matcher m = rotationPattern.matcher(style);

                if (m.find()) {
                    String rot = m.group(1);
                    float initialRotation = Float.parseFloat(rot);
                    float rotation = initialRotation + gliffyObject.rotation;
                    String tmp = m.replaceFirst("rotation=" + Float.toString(rotation));
                    style.setLength(0);
                    style.append(tmp);
                }
            } else if (gliffyObject.rotation != 0) {
                //handling the special common case
                if (style.indexOf("swimlane;collapsible=0;") > -1 && gliffyObject.rotation == 270) {
                    double w = geometry.getWidth();
                    double h = geometry.getHeight();
                    geometry.setX(geometry.getX() + (w - h) / 2);
                    geometry.setY(geometry.getY() + +(h - w) / 2);
                    geometry.setWidth(h);
                    geometry.setHeight(w);

                    style.append("childLayout=stackLayout;resizeParent=1;resizeParentMax=0;horizontal=0;horizontalStack=0;");
                } else {
                    if (gliffyObject.isGroup()) {
                        for (GliffyObject childObject : gliffyObject.children) {
                            rotateGroupedObject(gliffyObject, childObject);
                        }

                    }
                    style.append("rotation=").append(gliffyObject.rotation).append(";");
                }
            }
        }

        if (textObject != null) {
            style.append("html=1;nl2Br=0;");

            if (!gliffyObject.isLine()) {
                GliffyText txt = textObject.graphic.getText();

                if (gliffyObject.isSwimlane()) {
                    txt.setForceTopPaddingShift(true);
                    txt.setValign("middle");
                }

                cell.setValue(textObject.getText());
                gliffyObject.adjustTextPos(textObject);
                // If gliffyObject is Frame then always stick text on top left corner.
                if (gliffyObject.containsTextBracket()) {
                    fixFrameTextBorder(gliffyObject, style);
                    style.append(txt.getStyle(0, 0).replaceAll("verticalAlign=middle", "verticalAlign=top"));
                } else {
                    boolean isChevron = gliffyObject.uid != null && gliffyObject.uid.contains("chevron");
                    style.append(textObject == gliffyObject || isChevron ? txt.getStyle(0, 0) : txt.getStyle(textObject.x, textObject.y));
                }
            }
        }

        GliffyObject popup = getGliffyPopup(gliffyObject);
        if (link != null || popup != null) {
            Document doc = mxDomUtils.createDocument();
            Element uo = doc.createElement("UserObject");

            if (link != null) {
                AbstractMap.SimpleEntry<Long, String> lightBox = extractLightboxDataFromGliffyUrl(link);
                if (lightBox != null) {
                    link = "/plugins/drawio/lightbox.action?ceoId=" + lightBox.getKey() + "&diagramName=" + lightBox.getValue() + ".drawio";
                }
                uo.setAttribute("link", link);
                drawioDiagram.getModel().setValue(cell, uo);

                if (textObject != null) {
                    uo.setAttribute("label", textObject.getText());
                }
            }

            if (popup != null) {
                uo.setAttribute("tooltip", popup.graphic.getPopupNote().text);
                drawioDiagram.getModel().setValue(cell, uo);
            }

        }

        //for debugging, add gliffy id to the output in the style
        style.append("gliffyId=").append(gliffyObject.id).append(";");

        cell.setStyle(style.toString());
        gliffyObject.mxObject = cell;

        return cell;
    }

    private GliffyObject getGliffyPopup(GliffyObject gliffyObject) {
        if (gliffyObject.hasChildren()) {
            for (GliffyObject child : gliffyObject.children) {
                if (child.graphic != null && child.graphic.type != null && child.graphic.type.equals(Graphic.Type.POPUPNOTE)) {
                    return child;
                }
            }
        }

        return null;
    }

    /**
     * Rotate objects inside Group
     */
    private void rotateGroupedObject(GliffyObject group, GliffyObject childObject) {
        mxPoint pivot = new mxPoint(group.width / 2 - childObject.width / 2, group.height / 2 - childObject.height / 2);
        mxPoint temp = new mxPoint(childObject.x, childObject.y);
        if (group.rotation != 0) {
            double rads = Math.toRadians(group.rotation);
            double cos = Math.cos(rads);
            double sin = Math.sin(rads);
            temp = mxUtils.getRotatedPoint(temp, cos, sin, pivot);
            childObject.x = (float) temp.getX();
            childObject.y = (float) temp.getY();
            childObject.rotation += group.rotation;
        }
    }

    /**
     * Update borders of Text bracket in Frame objects.
     *
     * @param gliffyObject the GliffyObject
     * @param style        the StringBuilder with our style
     */
    private void fixFrameTextBorder(GliffyObject gliffyObject, StringBuilder style) {
        String wrongValue = "labelX=32"; // hard-coded 32 from gliffyTranslation.properties needs to be replaced
        String correctValue = "labelX=" + gliffyObject.getTextObject().width * 1.1f; // 10% more to bracket width, looks nicer on UI
        int start = style.indexOf(wrongValue);
        int end = start + wrongValue.length();
        style.replace(start, end, correctValue);
    }

    public AbstractMap.SimpleEntry<Long, String> extractLightboxDataFromGliffyUrl(String link) {
        Matcher pagem = lightboxPageIdGliffyMigration.matcher(link);
        Matcher namem = lightboxNameGliffyMigration.matcher(link);
        if (pagem.find()) {
            Long oldPageId = Long.parseLong(pagem.group(1));
            if (namem.find()) {
                String oldDiagramName = namem.group(1);
                return new AbstractMap.SimpleEntry<Long, String>(oldPageId, oldDiagramName);
            }
        }
        return null;
    }
}