package org.carth.html2md.copydown.rules;

import static org.carth.html2md.copydown.JsoupUtils.isBlock;

public class DefaultRule extends Rule {
    public DefaultRule() {
        init(
                (node, options) -> true,
                (content, node, options) -> isBlock(node) ? "\n\n" + content + "\n\n" : content
        );
    }
}
