package org.carth.html2md.copydown.rules;

import org.carth.html2md.copydown.CopyNode;
import org.jsoup.nodes.Node;

public class BlankReplacementRule extends Rule {

    public BlankReplacementRule() {
        super(CopyNode::isBlank);
    }

    @Override
    public String replacement(String content, Node element) {
        return CopyNode.isBlock(element) ? "\n\n" : "";
    }
}
