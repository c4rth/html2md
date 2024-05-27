package org.carth.html2md.copydown.rules;

import static org.carth.html2md.copydown.JsoupUtils.cleanAttribute;

public class ImageRule extends Rule {

    public ImageRule() {
        init(
                "img",
                (content, node, options) -> {
                    String alt = cleanAttribute(node.attr("alt"));
                    String src = node.attr("src");
                    if (src.isEmpty()) {
                        return "";
                    }
                    String title = cleanAttribute(node.attr("title"));
                    String titlePart = "";
                    if (!title.isEmpty()) {
                        titlePart = " \"" + title + "\"";
                    }
                    return "![" + alt + "]" + "(" + src + titlePart + ")";
                }
        );
    }
}
