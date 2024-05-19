package org.carth.html2md.copydown.rules;

import org.carth.html2md.copydown.CopyNode;

public class DefaultRule extends Rule {
    public DefaultRule() {
        init(
                (node, options) -> true,
                (content, node, options) -> CopyNode.isBlock(node) ? "\n\n" + content + "\n\n" : content
        );
    }
}
