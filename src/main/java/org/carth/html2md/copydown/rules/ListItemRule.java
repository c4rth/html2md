package org.carth.html2md.copydown.rules;

import org.carth.html2md.copydown.Options;
import org.jsoup.nodes.Element;
import org.jsoup.nodes.Node;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.regex.Pattern;

public class ListItemRule extends Rule {

    private static final Logger log = LoggerFactory.getLogger(ListItemRule.class);
    private final String bulletListMaker;

    public ListItemRule(Options options) {
        super("li");
        this.bulletListMaker = options.bulletListMaker;
    }

    @Override
    public String replacement(String content, Node element) {
        content = content.replaceAll("^\n+", "") // remove leading new lines
                .replaceAll("\n+$", "\n") // remove trailing new lines with just a single one
                .replaceAll("(?m)\n", "\n    "); // indent
        String prefix = bulletListMaker + "   ";
        Element parent = (Element) element.parentNode();
        if (parent.nodeName().equals("ol")) {
            String start = parent.attr("start");
            int index = parent.children().indexOf(element);
            int parsedStart = 1;
            if (!start.isEmpty()) {
                try {
                    parsedStart = Integer.parseInt(start);
                } catch (NumberFormatException e) {
                    log.error("Unable to parse number", e);
                }
            }
            prefix = parsedStart + index + ".  ";
        }
        return prefix + content + (element.nextSibling() != null && !Pattern.compile("\n$").matcher(content).find() ? "\n" : "");
    }
}
