package org.carth.html2md.copydown.rules;

import org.jsoup.nodes.Node;

public class AcDefaultRule extends Rule {
    public AcDefaultRule() {
        super(new String[]{"ac:layout", "ac:layout-section", "ac:layout-cell"});
    }

    @Override
    public String replacement(String content, Node element) {
        return content;
    }
}
