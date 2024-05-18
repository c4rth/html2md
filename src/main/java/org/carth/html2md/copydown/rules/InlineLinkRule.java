package org.carth.html2md.copydown.rules;

import org.carth.html2md.copydown.style.LinkStyle;
import org.carth.html2md.utils.MarkdownUtils;

public class InlineLinkRule extends Rule {

    public InlineLinkRule() {
        setRule(
                (element, options) ->
                        options.linkStyle == LinkStyle.INLINED
                                && element.nodeName().equals("a")
                                && !element.attr("href").isEmpty(),
                (content, node, options) -> {
                    String href = node.attr("href");
                    String title = MarkdownUtils.cleanAttribute(node.attr("title"));
                    if (!title.isEmpty()) {
                        title = " \"" + title + "\"";
                    }
                    return "[" + content + "](" + href + title + ")";
                }
        );
    }
}
