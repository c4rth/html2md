package org.carth.html2md.copydown.rules;

public class StrongRule extends Rule {


    public StrongRule() {
        setRule(
                new String[]{"strong", "b"},
                (content, node, options) -> {
                    if (content.trim().isEmpty()) {
                        return "";
                    }
                    return options.strongDelimiter + content + options.strongDelimiter;
                }
        );
    }

}
