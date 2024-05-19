package org.carth.html2md.copydown.rules;

import org.carth.html2md.copydown.CopyNode;
import org.jsoup.nodes.Element;

import java.util.Optional;

public class AcStructuredMacroAdmonitionRule extends Rule {
    public AcStructuredMacroAdmonitionRule() {
        init(
                (node, options) -> CopyNode.isAcMacroWithName(node, "info", "warning", "note", "tip"),
                (content, node, options) -> {
                    Optional<Element> title = CopyNode.getAcParametertWithName((Element) node, "title");
                    Optional<Element> collapse = CopyNode.getAcParametertWithName((Element) node, "collapse");
                    String titlePart = title.map(Element::wholeText).orElse("");
                    boolean collapsed = collapse.map(Element::wholeText).orElse("false").equals("true");
                    return "\n" + (collapsed ? options.collapsedAdmonition : options.admonition) + " " + node.attr("ac:name") + " \""
                            + titlePart + "\"" + content.replaceAll("\n", "\n    ");
                }
        );
    }
}