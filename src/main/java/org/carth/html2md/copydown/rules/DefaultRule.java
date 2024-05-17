package org.carth.html2md.copydown.rules;

import org.carth.html2md.copydown.CopyNode;
import org.jsoup.nodes.Node;

public class DefaultRule extends Rule {
    public DefaultRule() {
        super((element -> true));
    }

    @Override
    public String replacement(String content, Node element) {
        return CopyNode.isBlock(element) ? "\n\n" + content + "\n\n" : content;
    }
}
