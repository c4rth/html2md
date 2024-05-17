package org.carth.html2md.copydown.rules;

import org.carth.html2md.copydown.Options;
import org.jsoup.nodes.Node;

public class BrRule extends Rule {

    private final String optionsBr;

    public BrRule(Options options) {
        super("br");
        this.optionsBr = options.br;
    }

    @Override
    public String replacement(String content, Node element) {
        return optionsBr + "\n";
    }
}
