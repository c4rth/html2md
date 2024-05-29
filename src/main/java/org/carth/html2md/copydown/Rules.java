package org.carth.html2md.copydown;

import org.carth.html2md.copydown.rules.*;
import org.carth.html2md.log.Loggable;
import org.jsoup.nodes.Node;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;

public class Rules implements Loggable {

    private static final Logger logger = LoggerFactory.getLogger(Rules.class);

    @Override
    public Logger getLogger() {
        return logger;
    }

    public final List<Rule> allRules;
    public final List<String> references;

    public Rules() {
        this.allRules = new ArrayList<>();
        this.references = new ArrayList<>();

        // html
        addRule("paragraph", new ParagraphRule());
        addRule("br", new BrRule());
        addRule("heading", new HeadingRule());
        addRule("blockquote", new BlockquoteRule());
        addRule("list", new ListRule());
        addRule("listItem", new ListItemRule());
        addRule("indentedCodeBlock", new IndentedCodeBlockRule());
        addRule("fencedCodeBock", new FencedCodeBock());
        addRule("horizontalRule", new HorizontalRule());
        addRule("inlineLink", new InlineLinkRule());
        addRule("referenceLink", new ReferenceLinkRule(references));
        addRule("textFormatting", new TextFormattingRule());
        addRule("code", new CodeRule());
        addRule("img", new ImageRule());
        addRule("table", new TableRule());
        addRule("span", new SpanRule());
        addRule("time", new TimeRule());
        // ac:
        addRule("acRichTextBody", new AcTextBodyRule());
        addRule("acEmoticon", new AcEmoticonRule());
        addRule("acLink", new AcLinkRule());
        addRule("acImage", new AcImageRule());
        addRule("acStructuredMacroTabNav", new AcStructuredMacroTabNavRule());
        addRule("acStructuredMacroAdmonition", new AcStructuredMacroAdmonitionRule());
        addRule("acStructuredMacroStatus", new AcStructuredMacroStatusRule());
        addRule("acStructuredMacroCode", new AcStructuredMacroCodeRule());
        addRule("acStructuredMacroGliffy", new AcStructuredMacroGliffyRule());
        addRule("acTaskList", new AcTaskListRule());
        addRule("acParameter", new AcParameterRule());
        addRule("acStructuredMacro", new AcStructuredMacroRule());
        addRule("acDefault", new AcDefaultRule());
        addRule("acBlank", new AcBlankRule());
        // others
        //addRule("blankReplacement", new BlankReplacementRule());
        addRule("notSupported", new NotSupportedRule());
    }

    public Rule findRule(Node node, Options options) {
        for (Rule rule : allRules) {
            if (rule.getFilter().test(node, options)) {
                logTrace("tag: '{}' -> rule: '{}'", node.nodeName(), rule.getName());
                return rule;
            }
        }
        logTrace("tag: '{}' -> rule: NO rule found", node.nodeName());
        return null;
    }

    private void addRule(String name, Rule rule) {
        rule.setName(name);
        allRules.add(rule);
    }
}
