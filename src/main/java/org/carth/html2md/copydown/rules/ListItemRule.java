package org.carth.html2md.copydown.rules;

import org.carth.html2md.log.Loggable;
import org.jsoup.nodes.Element;
import org.slf4j.LoggerFactory;

import java.util.regex.Pattern;

public class ListItemRule extends Rule implements Loggable {

    private static final org.slf4j.Logger logger = LoggerFactory.getLogger(ListItemRule.class);

    @Override
    public org.slf4j.Logger getLogger() {
        return logger;
    }

    public ListItemRule() {
        init(
                "li",
                (content, node, options) -> {
                    content = content.replaceAll("^\n+", "") // remove leading new lines
                            .replaceAll("\n+$", "\n") // remove trailing new lines with just a single one
                            .replaceAll("(?m)\n", "\n    "); // indent
                    String prefix = options.bulletListMaker + "   ";
                    Element parent = (Element) node.parentNode();
                    assert parent != null;
                    if (parent.nodeName().equals("ol")) {
                        String start = parent.attr("start");
                        int index = parent.children().indexOf(node);
                        int parsedStart = 1;
                        if (!start.isEmpty()) {
                            try {
                                parsedStart = Integer.parseInt(start);
                            } catch (NumberFormatException e) {
                                logError("Unable to parse number", e);
                            }
                        }
                        prefix = parsedStart + index + ".  ";
                    }
                    return prefix + content + (node.nextSibling() != null && !Pattern.compile("\n$").matcher(content).find() ? "\n" : "");
                });
    }

}
