package org.carth.html2md.copydown.rules;

import org.jsoup.nodes.Element;

public class AcLinkRule extends Rule {

    public AcLinkRule() {
        setRule(
                "ac:link",
                (content, node, options) -> {
                    Element element = (Element) node;
                    Element user = element.select("ac|plain-text-link-body").first();
                    String userPart = user.wholeText();
                    return "@" + userPart.trim() + "\n";
                });
    }
}
