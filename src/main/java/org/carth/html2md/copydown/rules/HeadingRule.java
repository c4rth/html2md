package org.carth.html2md.copydown.rules;

import org.carth.html2md.copydown.style.HeadingStyle;

import java.util.Collections;

public class HeadingRule extends Rule {


    public HeadingRule() {
        setRule(
                new String[]{"h1", "h2", "h3", "h4", "h5", "h6"},
                (content, node, options) ->
                {
                    int hLevel = Integer.parseInt(node.nodeName().substring(1, 2));
                    if (options.headingStyle == HeadingStyle.SETEXT && hLevel < 3) {
                        String underline = String.join("", Collections.nCopies(content.length(), hLevel == 1 ? "=" : "-"));
                        return "\n\n" + content + "\n" + underline + "\n\n";
                    } else {
                        return "\n\n" + String.join("", Collections.nCopies(hLevel, "#")) + " " + content + "\n\n";
                    }
                }
        );
    }
}
