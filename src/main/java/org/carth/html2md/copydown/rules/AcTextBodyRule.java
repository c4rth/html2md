package org.carth.html2md.copydown.rules;

import java.util.List;

public class AcTextBodyRule extends Rule {

    public AcTextBodyRule() {
        setRule(
                List.of("ac:rich-text-body"),
                (content, node, options) -> content
        );
    }
}
