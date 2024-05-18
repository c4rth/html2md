package org.carth.html2md.copydown.rules;

public class BlockquoteRule extends Rule {

    public BlockquoteRule() {
        setRule(
                "blockquote",
                (content, node, options) -> {
                    content = content.replaceAll("^\n+|\n+$", "");
                    content = content.replaceAll("(?m)^", "> ");
                    return "\n\n" + content + "\n\n";
                }
        );
    }
}
