package org.carth.html2md.copydown.rules;

import lombok.extern.slf4j.Slf4j;
import org.jsoup.nodes.Node;
import org.jsoup.nodes.TextNode;

import java.util.Map;
import java.util.regex.Pattern;

@Slf4j
public class TableRule extends Rule {

    public TableRule() {
        super(new String[]{"th", "td", "tr", "thead", "tbody", "tfoot"});
    }

    @Override
    public String replacement(String content, Node element) {
        return switch (element.nodeName()) {
            case "th", "td" -> cell(content, element, false);
            case "tr" -> row(content, element);
            default -> content;
        };
    }

    private String cell(String content, Node element, boolean borderCell) {
        String prefix = " ";
        StringBuilder result;
        if (element.siblingIndex() == 0) prefix = "| ";
        result = new StringBuilder(prefix + content + " |");
        try {
            int colspan = Integer.parseInt(element.attr("colspan")) - 1;
            String colspanContent = borderCell ? " " + content + " |" : " |";
            result.append(colspanContent.repeat(Math.max(0, colspan)));
        } catch (NumberFormatException e) {
        }
        return result.toString();
    }

    private boolean isHeadingRow(Node tableRow) {
        var parentNode = tableRow.parentNode();
        assert parentNode != null;
        return (
                parentNode.nodeName().equalsIgnoreCase("THEAD") ||
                        (
                                parentNode.firstChild() == tableRow && (parentNode.nodeName().equalsIgnoreCase("TABLE") || isFirstTbody(parentNode))
                        )
        );
    }

    private boolean isFirstTbody(Node element) {
        Node previousSibling = element.previousSibling();
        while (previousSibling instanceof TextNode) {
            previousSibling = previousSibling.previousSibling();
        }
        return (element.nodeName().equalsIgnoreCase("TBODY") &&
                (previousSibling == null ||
                        (previousSibling.nodeName().equalsIgnoreCase("THEAD") && Pattern.compile("(?i)^\\s*$").matcher(element.outerHtml()).find()) ||
                        (previousSibling.nodeName().equalsIgnoreCase("COLGROUP"))
                )
        );
    }

    private String row(String content, Node element) {
        StringBuilder borderCells = new StringBuilder();
        var alignMap = Map.of("left", ":--", "right", "--:", "center", ":-:");
        if (isHeadingRow(element)) {
            for (var i = 0; i < element.childNodes().size(); i++) {
                String border = "---";
                String align = element.childNode(i).attr("align").toLowerCase();
                border = alignMap.getOrDefault(align, border);
                borderCells.append(cell(border, element.childNode(i), true));
            }
        }
        return "\n" + content + ((!borderCells.isEmpty()) ? '\n' + borderCells.toString() : "");
    }
}
