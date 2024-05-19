package org.carth.html2md.copydown.rules;

import org.jsoup.nodes.Element;

import java.util.List;

public class AcTaskListRule extends Rule {

    public AcTaskListRule() {
        setRule(
                List.of("ac:task-list", "ac:task", "ac:task-body"),
                (content, node, options) -> {
                    if (node.nodeName().equals("ac:task")) {
                        Element element = (Element) node;
                        boolean complete = element.select("ac|task-status").first().wholeText().equals("complete");
                        return options.bulletListMaker + " [" + (complete ? "x" : " ") + "] " + content + "\n";
                    }
                    if (node.nodeName().equals("ac:task-body")) {
                        return content.replaceAll("\n", "\n  ");
                    }
                    return content;
                }
        );
    }
}
