package org.carth.html2md.copydown.rules;

import java.util.List;

public class StrongRule extends Rule {

    public StrongRule() {
        init(
                List.of("strong", "b"),
                (content, node, options) -> {
                    if (content.trim().isEmpty()) {
                        return "";
                    }
                    return options.strongDelimiter + content + options.strongDelimiter;
                }
        );
    }

}
