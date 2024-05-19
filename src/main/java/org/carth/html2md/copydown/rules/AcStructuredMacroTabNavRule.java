package org.carth.html2md.copydown.rules;

import org.carth.html2md.copydown.CopyNode;
import org.jsoup.nodes.Element;

import java.util.Optional;

public class AcStructuredMacroTabNavRule extends Rule {
    public AcStructuredMacroTabNavRule() {
        init(
                (node, options) -> CopyNode.isAcMacroWithName(node, "tab-pane", "horizontal-nav-item"),
                (content, node, options) -> {
                    Optional<Element> title = CopyNode.getAcParametertWithName((Element) node, "name");
                    String titlePart = title.map(Element::text).orElse("Tab");
                    return "\n=== \"<span style=\"font-size: 1rem\">" + titlePart + "</span>\"" + content.replaceAll("\n", "\n    ");
                }
        );
    }
}