package org.carth.html2md.copydown.rules;

import org.carth.html2md.copydown.Options;
import org.jsoup.nodes.Element;
import org.jsoup.nodes.Node;

import static org.carth.html2md.copydown.CopyNode.cleanAttribute;

public class AcImageRule extends Rule {

    public AcImageRule() {
        setRule(
                "ac:image",
                this::replace
        );
    }

    private String replace(String content, Node node, Options options) {
        String alt = cleanAttribute(node.attr("ac:alt"));
        String src = "attachments/";
        if (node instanceof Element img) {
            Element riAttachment = img.select("ri|attachment").first();
            if (riAttachment != null) {
                src = riAttachment.attr("ri:filename");
            }
        }
        String title = cleanAttribute(node.attr("ac:title"));
        String titlePart = "";
        if (!title.isEmpty()) {
            titlePart = " \"" + title + "\"";
        }
        String width = cleanAttribute(node.attr("ac:width"));
        String height = cleanAttribute(node.attr("ac:height"));
        String stylePart = "";
        if (!width.isEmpty() || !height.isEmpty()) {
            stylePart = "{: style=\"";
            if (!width.isEmpty()) {
                stylePart += "width: " + width + "px;";
            }
            if (!height.isEmpty()) {
                stylePart += "height: " + height + "px;";
            }
            stylePart += "\" }";
        }
        return "![" + alt + "]" + "(attachments/" + src + titlePart + ")" + stylePart;
    }
}
