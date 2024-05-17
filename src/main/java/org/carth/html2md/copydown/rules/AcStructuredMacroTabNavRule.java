package org.carth.html2md.copydown.rules;

import org.jsoup.nodes.Element;
import org.jsoup.nodes.Node;

import java.util.Optional;

public class AcStructuredMacroTabNavRule extends Rule {
    public AcStructuredMacroTabNavRule() {
        super(
                (element) -> {
                    boolean isOk = element.nodeName().equals("ac:structured-macro")
                            && (element.attr("ac:name").equals("tab-pane") || element.attr("ac:name").equals("horizontal-nav-item"));
                    if (isOk) {
                    }
                    return isOk;
                }
        );
    }

    @Override
    public String replacement(String content, Node node) {
        Element element = (Element) node;

        Optional<Element> title = element.select("ac|parameter").stream().filter(el -> el.hasAttr("ac:name") && el.attr("ac:name").equals("name")).findFirst();
        String titlePart = title.map(Element::text).orElse("Tab");
        return "\n=== \"<span style=\"font-size: 1rem\">" + titlePart + "</span>\"" + content.replaceAll("\n", "\n    ");
    }
}