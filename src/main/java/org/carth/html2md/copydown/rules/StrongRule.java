package org.carth.html2md.copydown.rules;

import org.carth.html2md.copydown.Options;
import org.jsoup.nodes.Node;

public class StrongRule extends Rule {

    private final String strongDelimiter;

    public StrongRule(Options options) {
        super(new String[]{"strong", "b"});
        this.strongDelimiter = options.strongDelimiter;
    }

    @Override
    public String replacement(String content, Node element) {
        if (content.trim().isEmpty()) {
            return "";
        }
        return strongDelimiter + content + strongDelimiter;
    }
}
