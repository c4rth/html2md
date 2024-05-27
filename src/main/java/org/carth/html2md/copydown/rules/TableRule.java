package org.carth.html2md.copydown.rules;

import lombok.extern.slf4j.Slf4j;
import org.carth.html2md.copydown.Options;
import org.carth.html2md.report.ConversionReport;
import org.jsoup.nodes.Node;
import org.jsoup.nodes.TextNode;

import java.util.List;
import java.util.Map;
import java.util.regex.Pattern;

@Slf4j
public class TableRule extends Rule {

    private static final String TABLE = "table";
    private static final String THEAD = "thead";
    private static final String TBODY = "tbody";
    private static final String TH = "th";
    private static final String TD = "td";
    private static final String TR = "tr";
    private static final String COLGROUP = "colgroup";
    private static final String COL = "col";

    public TableRule() {
        init(
                List.of(TABLE, TH, TD, TR, THEAD, TBODY, "tfoot", COLGROUP, COL),
                this::replace
        );
    }

    public String replace(String content, Node node, Options options) {
        return switch (node.nodeName()) {
            case TABLE -> table(content, node);
            case TH, TD -> cell(content, node, false);
            case TR -> row(content, node);
            case COLGROUP, COL -> "";
            default -> content;
        };
    }

    private String table(String content, Node node) {
        if (isNestedTable(node)) {
            // TODO: convert only non-standard HTML tags
            return node.outerHtml().trim().replace("\n", "");
        }
        return content;
    }

    private boolean isNestedTable(Node node) {
        var parent = node.parent();
        while (parent != null) {
            if (parent.nodeName().equals(TABLE)) {
                ConversionReport.getInstance().addError("Nested table is not supported");
                return true;
            }
            parent = parent.parent();
        }
        return false;
    }

    private String cell(String content, Node node, boolean borderCell) {
        String prefix = " ";
        StringBuilder result;
        if (node.siblingIndex() == 0) prefix = "| ";
        result = new StringBuilder(prefix + content + " |");
        try {
            int colspan = Integer.parseInt(node.attr("colspan")) - 1;
            String colspanContent = borderCell ? " " + content + " " : " |";
            result.append(colspanContent.repeat(Math.max(0, colspan)));
        } catch (NumberFormatException e) {
            // empty
        }
        return result.toString();
    }

    private boolean isHeadingRow(Node tableRow) {
        var parentNode = tableRow.parentNode();
        assert parentNode != null;
        return (parentNode.nodeName().equalsIgnoreCase(THEAD) ||
                (parentNode.firstChild() == tableRow &&
                        (parentNode.nodeName().equalsIgnoreCase(TABLE) || isFirstTbody(parentNode))
                )
        );
    }

    private boolean isFirstTbody(Node node) {
        Node previousSibling = node.previousSibling();
        while (previousSibling instanceof TextNode) {
            previousSibling = previousSibling.previousSibling();
        }
        return (node.nodeName().equalsIgnoreCase(TBODY) &&
                (previousSibling == null ||
                        (previousSibling.nodeName().equalsIgnoreCase(THEAD) && Pattern.compile("(?i)^\\s*$").matcher(node.outerHtml()).find()) ||
                        (previousSibling.nodeName().equalsIgnoreCase(COLGROUP))
                )
        );
    }

    private String row(String content, Node node) {
        StringBuilder borderCells = new StringBuilder();
        var alignMap = Map.of("left", ":--", "right", "--:", "center", ":-:");
        if (isHeadingRow(node)) {
            for (var i = 0; i < node.childNodes().size(); i++) {
                String border = "---";
                String align = node.childNode(i).attr("align").toLowerCase();
                border = alignMap.getOrDefault(align, border);
                borderCells.append(cell(border, node.childNode(i), true));
            }
        }
        return "\n" + content + ((!borderCells.isEmpty()) ? '\n' + borderCells.toString() : "");
    }
}
