package org.carth.html2md.copydown.rules;

import org.jsoup.nodes.Element;

public class AcTaskRule extends Rule {

    public AcTaskRule() {
        setRule(
                new String[]{"ac:task-list", "ac:task", "ac:task-body"},
                (content, node, options) -> {
                    if (node.nodeName().equals("ac:task")) {
                        Element element = (Element) node;
                        boolean complete = element.select("ac|task-status").first().wholeText().equals("complete");
                        return options.bulletListMaker + " [" + (complete ? "x" : " ") + "] " + content + "\n";
                    }
                    return content;
                }
        );
    }
}
