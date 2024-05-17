package org.carth.html2md.copydown.rules;

import org.carth.html2md.copydown.Options;
import org.jsoup.nodes.Node;

public class EmphasisRule extends Rule {
    private final String emDelimiter;

    public EmphasisRule(Options options) {
        super(new String[]{"em", "i"});
        this.emDelimiter = options.emDelimiter;
    }

    @Override
    public String replacement(String content, Node element) {
        if (content.trim().isEmpty()) {
            return "";
        }
        return emDelimiter + content + emDelimiter;
    }
}
