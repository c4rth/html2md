package org.carth.html2md.copydown.rules;

import lombok.extern.slf4j.Slf4j;
import org.jsoup.nodes.Element;

import java.util.regex.Pattern;

@Slf4j
public class ListItemRule extends Rule {

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
                                log.error("Unable to parse number", e);
                            }
                        }
                        prefix = parsedStart + index + ".  ";
                    }
                    return prefix + content + (node.nextSibling() != null && !Pattern.compile("\n$").matcher(content).find() ? "\n" : "");
                });
    }

}
