package org.carth.html2md.copydown.rules;

import org.carth.html2md.copydown.CopyNode;

public class ImageRule extends Rule {

    public ImageRule() {
        init(
                "img",
                (content, node, options) -> {
                    String alt = CopyNode.cleanAttribute(node.attr("alt"));
                    String src = node.attr("src");
                    if (src.isEmpty()) {
                        return "";
                    }
                    String title = CopyNode.cleanAttribute(node.attr("title"));
                    String titlePart = "";
                    if (!title.isEmpty()) {
                        titlePart = " \"" + title + "\"";
                    }
                    return "![" + alt + "]" + "(" + src + titlePart + ")";
                }
        );
    }
}
