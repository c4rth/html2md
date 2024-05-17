package org.carth.html2md.copydown;

import org.carth.html2md.copydown.rules.*;
import org.jsoup.nodes.Node;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;

public class Rules {
    private static final Logger log = LoggerFactory.getLogger(Rules.class);
    public List<Rule> rules;

    public Rules(Options options, List<String> references) {
        this.rules = new ArrayList<>();

        // html
        addRule("paragraph", new ParagraphRule());
        addRule("br", new BrRule(options));
        addRule("heading", new HeadingRule(options));
        addRule("blockquote", new BlockquoteRule());
        addRule("list", new ListRule());
        addRule("listItem", new ListItemRule(options));
        addRule("indentedCodeBlock", new IndentedCodeBlockRule(options));
        addRule("fencedCodeBock", new FencedCodeBock(options));
        addRule("horizontalRule", new HorizontalRule(options));
        addRule("inlineLink", new InlineLinkRule(options));
        addRule("referenceLink", new ReferenceLinkRule(options, references));
        addRule("emphasis", new EmphasisRule(options));
        addRule("strong", new StrongRule(options));
        addRule("code", new CodeRule());
        addRule("img", new ImageRule());
        addRule("table", new TableRule());
        // ac:
        addRule("acRichTextBody", new AcTextBodyRule());
        addRule("acEmoticon", new AcEmoticonRule());
        addRule("acLink", new AcLinkRule());
        addRule("acImage", new AcImageRule());
        addRule("acStructuredMacroTabNav", new AcStructuredMacroTabNavRule());
        addRule("acStructuredMacroAdmonition", new AcStructuredMacroAdmonitionRule());
        addRule("acStructuredMacroStatus", new AcStructuredMacroStatusRule());
        addRule("acStructuredMacroCode", new AcStructuredMacroCodeRule(options));
        addRule("acStructuredMacroGliffy", new AcStructuredMacroGliffyRule());
        addRule("acTask", new AcTaskRule());
        addRule("acParameter", new AcParameterRule());
        addRule("acStructuredMacro", new AcStructuredMacroRule());
        addRule("acDefault", new AcDefaultRule());
        addRule("acBlank", new AcBlankRule());
        addRule("blankReplacement", new BlankReplacementRule());
        addRule("default", new DefaultRule());
    }

    public Rule findRule(Node node) {
        for (Rule rule : rules) {
            if (rule.getFilter().test(node)) {
                log.trace("tag: '" + node.nodeName() + "' -> rule: '" + rule.getName() + "'");
                return rule;
            }
        }
        log.trace("tag: '" + node.nodeName() + "' -> NO rule'");
        return null;
    }

    private void addRule(String name, Rule rule) {
        rule.setName(name);
        rules.add(rule);
    }
}
