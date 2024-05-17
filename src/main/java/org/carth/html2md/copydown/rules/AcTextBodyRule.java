package org.carth.html2md.copydown.rules;

import org.jsoup.nodes.Node;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class AcTextBodyRule extends Rule {
    private static final Logger log = LoggerFactory.getLogger(AcTextBodyRule.class);

    public AcTextBodyRule() {
        super(new String[]{"ac:rich-text-body"});
    }

    @Override
    public String replacement(String content, Node element) {
        return content;
    }
}
