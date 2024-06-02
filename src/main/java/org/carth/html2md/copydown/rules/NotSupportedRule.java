package org.carth.html2md.copydown.rules;

import org.carth.html2md.report.ConversionReport;

public class NotSupportedRule extends Rule {
    public NotSupportedRule() {
        init(
                (node, options) -> true,
                (content, node, options) -> {
                    ConversionReport.getInstance().addWarning("Tag '" + node.nodeName() + "' not supported -> skipped");
                    return content;
                }
        );
    }
}
