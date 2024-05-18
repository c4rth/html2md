package org.carth.html2md.copydown.rules;

public class EmphasisRule extends Rule {

    public EmphasisRule() {
        setRule(
                new String[]{"em", "i"},
                (content, node, options) -> {
                    if (content.trim().isEmpty()) {
                        return "";
                    }
                    return options.emDelimiter + content + options.emDelimiter;
                }
        );
    }
}
