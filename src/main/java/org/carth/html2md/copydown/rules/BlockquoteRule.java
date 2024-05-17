package org.carth.html2md.copydown.rules;

import org.jsoup.nodes.Node;

public class BlockquoteRule extends Rule {

    public BlockquoteRule() {
        super("blockquote");
    }

    @Override
    public String replacement(String content, Node element) {
        content = content.replaceAll("^\n+|\n+$", "");
        content = content.replaceAll("(?m)^", "> ");
        return "\n\n" + content + "\n\n";
    }
}
