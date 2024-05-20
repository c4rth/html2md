package org.carth.html2md.copydown.rules;

import org.carth.html2md.report.ConversionReport;

public class AcStructuredMacroRule extends Rule {
    public AcStructuredMacroRule() {
        init(
                "ac:structured-macro",
                (content, node, options) -> {
                    String acName = node.attr("ac:name");
                    if (acName.equals("tabs-group")) {
                        ConversionReport.getInstance().addWarning("Tab group");
                    } else if (acName.contains("nav-grou")) {
                        ConversionReport.getInstance().addWarning("Nav group");
                    }
                    return content;
                }
        );
    }

}
