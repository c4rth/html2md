package org.carth.html2md.copydown.rules;

import org.carth.html2md.copydown.style.LinkStyle;

import static org.carth.html2md.copydown.JsoupUtils.cleanAttribute;

public class InlineLinkRule extends Rule {

    public InlineLinkRule() {
        init(
                (node, options) ->
                        options.linkStyle == LinkStyle.INLINED
                                && node.nodeName().equals("a")
                                && !node.attr("href").isEmpty(),
                (content, node, options) -> {
                    String href = node.attr("href");
                    String title = cleanAttribute(node.attr("title"));
                    if (!title.isEmpty()) {
                        title = " \"" + title + "\"";
                    }
                    return "[" + content + "](" + href + title + ")";
                }
        );
    }
}
