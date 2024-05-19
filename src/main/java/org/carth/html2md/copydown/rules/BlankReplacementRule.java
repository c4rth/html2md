package org.carth.html2md.copydown.rules;

import org.carth.html2md.copydown.CopyNode;

public class BlankReplacementRule extends Rule {

    public BlankReplacementRule() {
        setRule(
                (node, options) -> CopyNode.isBlank(node),
                (content, node, options) -> CopyNode.isBlock(node) ? "\n\n" : ""
        );
    }

}
