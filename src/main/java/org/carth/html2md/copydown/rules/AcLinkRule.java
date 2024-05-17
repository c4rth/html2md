package org.carth.html2md.copydown.rules;

import org.jsoup.nodes.Element;
import org.jsoup.nodes.Node;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class AcLinkRule extends Rule {

    private static final Logger log = LoggerFactory.getLogger(AcLinkRule.class);

    public AcLinkRule() {
        super("ac:link");
    }

    @Override
    public String replacement(String content, Node node) {
        Element element = (Element) node;
        Element user = element.select("ac|plain-text-link-body").first();
        String userPart = user.wholeText();
        return "@" + userPart.trim() +"\n";
    }
}
