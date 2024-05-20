package org.carth.html2md.copydown.rules;

import org.carth.html2md.copydown.style.CodeBlockStyle;
import org.jsoup.nodes.Element;

public class IndentedCodeBlockRule extends Rule {

    public IndentedCodeBlockRule() {
        init(
                (node, options) ->
                        options.codeBlockStyle == CodeBlockStyle.INDENTED
                                && node.nodeName().equals("pre")
                                && node.childNodeSize() > 0
                                && node.childNode(0).nodeName().equals("code"),
                // TODO check textContent
                (content, node, options) -> "\n\n    " + ((Element) node.childNode(0)).wholeText().replace("\n", "\n    ")

        );
    }
}
