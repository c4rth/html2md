package org.carth.html2md.copydown.rules;

import org.carth.html2md.utils.MarkdownUtils;

public class ImageRule extends Rule {

    public ImageRule() {
        setRule(
                "img",
                (content, node, options) -> {
                    String alt = MarkdownUtils.cleanAttribute(node.attr("alt"));
                    String src = node.attr("src");
                    if (src.isEmpty()) {
                        return "";
                    }
                    String title = MarkdownUtils.cleanAttribute(node.attr("title"));
                    String titlePart = "";
                    if (!title.isEmpty()) {
                        titlePart = " \"" + title + "\"";
                    }
                    return "![" + alt + "]" + "(" + src + titlePart + ")";
                }
        );
    }
}
