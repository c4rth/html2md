package org.carth.html2md.copydown.rules;

public class SpanRule extends Rule {

    public SpanRule() {
        init(
                "span",
                (content, node, options) -> content
        );
    }

}
