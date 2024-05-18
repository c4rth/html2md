package org.carth.html2md.copydown.rules;

public class ParagraphRule extends Rule {

    public ParagraphRule() {
        setRule(
                "p",
                (content, node, options) -> "\n\n" + content + "\n\n"
        );
    }

}
