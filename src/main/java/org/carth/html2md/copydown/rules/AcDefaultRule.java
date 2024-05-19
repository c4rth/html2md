package org.carth.html2md.copydown.rules;

import java.util.List;

public class AcDefaultRule extends Rule {
    public AcDefaultRule() {
        setRule(
                List.of("ac:layout", "ac:layout-section", "ac:layout-cell"),
                (content, node, options) -> content
        );
    }

}
