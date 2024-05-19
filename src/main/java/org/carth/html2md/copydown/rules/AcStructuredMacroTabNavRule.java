package org.carth.html2md.copydown.rules;

import org.carth.html2md.utils.AcNodeUtils;
import org.jsoup.nodes.Element;

import java.util.Optional;

public class AcStructuredMacroTabNavRule extends Rule {
    public AcStructuredMacroTabNavRule() {
        setRule(
                (node, options) -> AcNodeUtils.isAcMacroWithName(node, "tab-pane", "horizontal-nav-item"),
                (content, node, options) -> {
                    Optional<Element> title = AcNodeUtils.getAcParametertWithName((Element) node, "name");
                    String titlePart = title.map(Element::text).orElse("Tab");
                    return "\n=== \"<span style=\"font-size: 1rem\">" + titlePart + "</span>\"" + content.replaceAll("\n", "\n    ");
                }
        );
    }
}