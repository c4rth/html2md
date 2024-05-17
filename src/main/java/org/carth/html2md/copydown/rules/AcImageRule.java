package org.carth.html2md.copydown.rules;

import org.jsoup.nodes.Element;
import org.jsoup.nodes.Node;

public class AcImageRule extends Rule {
    /*
    <ac:image ac:thumbnail="true" ac:height="113">
        <ri:attachment ri:filename="netcool.png" />
    </ac:image>
     */

    public AcImageRule() {
        super("ac:image");
    }

    @Override
    public String replacement(String content, Node element) {
        String alt = cleanAttribute(element.attr("ac:alt"));
        String src = "attachments/";
        if (element instanceof Element img) {
            Element riAttachment = img.select("ri|attachment").first();
            if (riAttachment != null) {
                src = riAttachment.attr("ri:filename");
            }
        }
        String title = cleanAttribute(element.attr("ac:title"));
        String titlePart = "";
        if (!title.isEmpty()) {
            titlePart = " \"" + title + "\"";
        }
        String width = cleanAttribute(element.attr("ac:width"));
        String height = cleanAttribute(element.attr("ac:height"));
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
