package org.carth.html2md.copydown.rules;

import org.carth.html2md.copydown.CopyNode;
import org.jsoup.nodes.Element;

import java.util.Optional;

public class AcStructuredMacroAdmonitionRule extends Rule {
    public AcStructuredMacroAdmonitionRule() {
        setRule(
                (node, options) -> CopyNode.isAcMacroWithName(node, "info", "warning"),
                (content, node, options) -> {
                    Optional<Element> title = CopyNode.getAcParametertWithName((Element) node, "title");
                    String titlePart = title.map(Element::wholeText).orElse("");
                    return "\n!!! " + node.attr("ac:name") + " \"" + titlePart + "\"" + content.replaceAll("\n", "\n    ");
                }
        );
    }
}