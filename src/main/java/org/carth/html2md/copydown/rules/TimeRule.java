package org.carth.html2md.copydown.rules;

import java.util.List;

public class TimeRule extends Rule {
    public TimeRule() {
        init(
                List.of("time"),
                (content, node, options) -> content
        );
    }
}
