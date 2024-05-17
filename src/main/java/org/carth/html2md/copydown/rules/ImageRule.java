package org.carth.html2md.copydown.rules;

import org.jsoup.nodes.Node;

public class ImageRule extends Rule {

    public ImageRule() {
        super("img");
    }

    @Override
    public String replacement(String content, Node element) {
        String alt = cleanAttribute(element.attr("alt"));
        String src = element.attr("src");
        if (src.isEmpty()) {
            return "";
        }
        String title = cleanAttribute(element.attr("title"));
        String titlePart = "";
        if (!title.isEmpty()) {
            titlePart = " \"" + title + "\"";
        }
        return "![" + alt + "]" + "(" + src + titlePart + ")";
    }
}
