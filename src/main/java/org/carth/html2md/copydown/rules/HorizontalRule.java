package org.carth.html2md.copydown.rules;

import org.carth.html2md.copydown.Options;
import org.jsoup.nodes.Node;

public class HorizontalRule extends Rule {

    private final String hr;

    public HorizontalRule(Options options) {
        super("hr");
        this.hr = options.hr;
    }

    @Override
    public String replacement(String content, Node element) {
        return "\n\n" + hr + "\n\n";
    }
}
