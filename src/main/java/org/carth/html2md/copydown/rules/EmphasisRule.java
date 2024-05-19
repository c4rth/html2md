package org.carth.html2md.copydown.rules;

import java.util.List;

public class EmphasisRule extends Rule {

    public EmphasisRule() {
        init(
                List.of("em", "i"),
                (content, node, options) -> content.trim().isEmpty() ? "" : options.emDelimiter + content + options.emDelimiter
        );
    }
}
