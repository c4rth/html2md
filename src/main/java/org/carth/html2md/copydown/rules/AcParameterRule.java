package org.carth.html2md.copydown.rules;

import org.jsoup.nodes.Node;

public class AcParameterRule extends Rule {

    public AcParameterRule() {
        super("ac:parameter");
    }

    @Override
    public String replacement(String content, Node element) {
        return "";
    }
}
