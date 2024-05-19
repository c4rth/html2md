package org.carth.html2md.copydown.rules;

public class HorizontalRule extends Rule {

    public HorizontalRule() {
        init(
                "hr",
                (content, node, options) -> "\n\n" + options.hr + "\n\n"
        );
    }

}
