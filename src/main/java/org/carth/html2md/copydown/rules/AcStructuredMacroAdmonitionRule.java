package org.carth.html2md.copydown.rules;

import org.carth.html2md.utils.AcNodeUtils;
import org.jsoup.nodes.Element;

import java.util.List;
import java.util.Optional;

public class AcStructuredMacroAdmonitionRule extends Rule {
    public AcStructuredMacroAdmonitionRule() {
        setRule(
                (node, options) -> AcNodeUtils.isAcMacroWithName(node, "info", "warning"),
                (content, node, options) -> {
                    Optional<Element> title = AcNodeUtils.getAcParametertWithName((Element) node, "title");
                    String titlePart = title.map(Element::wholeText).orElse("");
                    return "\n!!! " + node.attr("ac:name") + " \"" + titlePart + "\"" + content.replaceAll("\n", "\n    ");
                }
        );
    }
}