package org.carth.html2md.copydown.rules;

import org.jsoup.nodes.Node;

public class ParagraphRule extends Rule {

    public ParagraphRule() {
        super("p");
    }

    @Override
    public String replacement(String content, Node element) {
        return "\n\n" + content + "\n\n";
    }
}
