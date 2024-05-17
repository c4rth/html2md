package org.carth.html2md.copydown.rules;

import org.carth.html2md.copydown.Options;
import org.carth.html2md.copydown.style.CodeBlockStyle;
import org.jsoup.nodes.Element;
import org.jsoup.nodes.Node;

public class IndentedCodeBlockRule extends Rule {

    public IndentedCodeBlockRule(Options options) {
        super(
                (element) -> {
                    return options.codeBlockStyle == CodeBlockStyle.INDENTED
                            && element.nodeName().equals("pre")
                            && element.childNodeSize() > 0
                            && element.childNode(0).nodeName().equals("code");
                }
        );
    }

    @Override
    public String replacement(String content, Node element) {
        // TODO check textContent
        return "\n\n    " + ((Element) element.childNode(0)).wholeText().replaceAll("\n", "\n    ");
    }
}
