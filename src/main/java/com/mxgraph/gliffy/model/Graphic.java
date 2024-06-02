package com.mxgraph.gliffy.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;

import java.util.ArrayList;
import java.util.List;

@Getter
public class Graphic {

    public Type type;
    public GliffyText Text;
    public GliffyLine Line;
    public GliffyLink Link;
    public GliffyShape Shape;
    public GliffyImage Image;
    public GliffySvg Svg;
    public GliffyMindmap Mindmap;
    public GliffyPopupNote PopupNote;

    public Graphic() {
        super();
    }

    public Type getType() {
        return type != null ? type : Type.UNKNOWN;
    }

    public enum Type {
        @JsonProperty("Svg") SVG,
        @JsonProperty("Line") LINE,
        @JsonProperty("Shape") SHAPE,
        @JsonProperty("Text") TEXT,
        @JsonProperty("Image") IMAGE,
        @JsonProperty("Link") LINK,
        @JsonProperty("Mindmap") MINDMAP,
        @JsonProperty("PopupNote") POPUPNOTE,
        @JsonProperty("Unwknown") UNKNOWN;

        public String toString() {
            return this.name();
        }
    }

    public static abstract class GliffyAbstractShape {
        public String strokeColor;
        public String fillColor;
        public String dashStyle;
        private float strokeWidth;

        public int getStrokeWidth() {
            return Math.round(strokeWidth);
        }
    }

    public static class GliffyLine extends GliffyAbstractShape {
        public Integer startArrow;
        public Integer endArrow;
        public String interpolationType;
        public Integer cornerRadius;

        public List<float[]> controlPath = new ArrayList<>();
    }

    public static class GliffyShape extends GliffyAbstractShape {
        public String tid;
        public boolean gradient;
        public boolean dropShadow;
        public int state;
        public float shadowX;
        public float shadowY;
        public float opacity;

        /**
         * @return true if there is no_fill string found in this element
         */
        public boolean isNoFill() {
            if (tid != null) {
                return tid.contains("no_fill");
            }
            return false;
        }

    }

    public static class GliffyImage extends GliffyShape {
        private String url;

        public String getUrl() {
            return url.replace(";base64", "");
        }
    }

    public static class GliffySvg extends GliffyShape {
        public Integer embeddedResourceId;
    }

    public static class GliffyMindmap extends GliffyShape {
    }

    public static class GliffyPopupNote extends GliffyShape {
        public String text;
    }

    public static class GliffyLink {
        String href;
        boolean renderIcon;
    }

}
