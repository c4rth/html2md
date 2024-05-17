package org.carth.html2md.copydown.rules;

import org.jsoup.nodes.Element;
import org.jsoup.nodes.Node;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class AcTaskRule extends Rule {
    private static final Logger log = LoggerFactory.getLogger(AcTaskRule.class);

    public AcTaskRule() {
        super(new String[]{"ac:task-list", "ac:task", "ac:task-body"});
    }

    @Override
    public String replacement(String content, Node node) {
        if (node.nodeName().equals("ac:task")) {
            Element element = (Element) node;
            boolean complete = element.select("ac|task-status").first().wholeText().equals("complete");
            return "- [" + (complete ? "x" : " ") + "] " + content + "\n";
        }
        return content;
    }
}
/*

- [ ] Task 1
 - [ ] Task 2
 - [ ] Task 3
 - [x] Task 4 is checked
 - [ ] **Task 6 bold**
 - [ ] Task 7
 - [ ] Task 8 underline
 - [x] Task9 line 1
Task9 line 2
 - [ ]   Task 10 image:![](netcool.png) {: style="height: 113px;" }

 */
