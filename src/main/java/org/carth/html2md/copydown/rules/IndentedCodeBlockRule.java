package org.carth.html2md.copydown.rules;

import org.carth.html2md.copydown.style.CodeBlockStyle;
import org.jsoup.nodes.Element;

public class IndentedCodeBlockRule extends Rule {

    public IndentedCodeBlockRule() {
        setRule(
                (element, options) ->
                        options.codeBlockStyle == CodeBlockStyle.INDENTED
                                && element.nodeName().equals("pre")
                                && element.childNodeSize() > 0
                                && element.childNode(0).nodeName().equals("code"),
                // TODO check textContent
                (content, node, options) -> "\n\n    " + ((Element) node.childNode(0)).wholeText().replaceAll("\n", "\n    ")

        );
    }
}
