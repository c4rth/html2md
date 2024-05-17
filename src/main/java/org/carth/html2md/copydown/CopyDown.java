package org.carth.html2md.copydown;

import org.carth.html2md.copydown.rules.Rule;
import org.jsoup.nodes.Node;
import org.jsoup.nodes.TextNode;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class CopyDown {

    private static final Pattern leadingNewLinePattern = Pattern.compile("^(\n*)");
    private static final Pattern trailingNewLinePattern = Pattern.compile("(\n*)$");
    private static final Logger log = LoggerFactory.getLogger(CopyDown.class);
    private final Options options;
    private final List<Escape> escapes = Arrays.asList(
            new Escape("\\\\", "\\\\\\\\"),
            new Escape("\\*", "\\\\*"),
            new Escape("^-", "\\\\-"),
            new Escape("^\\+ ", "\\\\+ "),
            new Escape("^(=+)", "\\\\$1"),
            new Escape("^(#{1,6}) ", "\\\\$1 "),
            new Escape("`", "\\\\`"),
            new Escape("^~~~", "\\\\~~~"),
            new Escape("\\[", "\\\\["),
            new Escape("\\]", "\\\\]"),
            new Escape("^>", "\\\\>"),
            new Escape("_", "\\\\_"),
            new Escape("^(\\d+)\\. ", "$1\\\\. ")
    );
    private Rules rules;
    private List<String> references;

    public CopyDown() {
        this.options = OptionsBuilder.anOptions().build();
        setUp();
    }

    public CopyDown(Options options) {
        this.options = options;
        setUp();
    }

    public String convert(String input) {
        references = new ArrayList<>();
        CopyNode copyRootNode = new CopyNode(input);
        String result = process(copyRootNode);
        return postProcess(result);
    }

    private void setUp() {
        references = new ArrayList<>();
        rules = new Rules(options, references);
    }

    private String postProcess(String output) {
        for (Rule rule : rules.rules) {
            if (rule.getAppend() != null) {
                output = join(output, rule.getAppend().get());
            }
        }
        return output.replaceAll("^[\\t\\n\\r]+", "").replaceAll("[\\t\\r\\n\\s]+$", "");
    }

    private String process(CopyNode node) {
        String result = "";
        for (Node child : node.element.childNodes()) {
            CopyNode copyNodeChild = new CopyNode(child, node);
            String replacement = "";
            if (NodeUtils.isNodeType3(child)) {
                // TODO it should be child.nodeValue
                replacement = copyNodeChild.isCode() ? ((TextNode) child).text() : escape(((TextNode) child).text());
            } else if (NodeUtils.isNodeType1(child)) {
                replacement = replacementForNode(copyNodeChild);
            }
            result = join(result, replacement);
        }
        return result;
    }

    private String replacementForNode(CopyNode node) {
        Rule rule = rules.findRule(node.element);
        String content = process(node);
        CopyNode.FlankingWhiteSpaces flankingWhiteSpaces = node.flankingWhitespace();
        if (!flankingWhiteSpaces.getLeading().isEmpty() || !flankingWhiteSpaces.getTrailing().isEmpty()) {
            content = content.trim();
        }
        content = flankingWhiteSpaces.getLeading() + rule.replacement(content, node.element) + flankingWhiteSpaces.getTrailing();
        return content;
    }

    private String join(String string1, String string2) {
        Matcher trailingMatcher = trailingNewLinePattern.matcher(string1);
        trailingMatcher.find();
        Matcher leadingMatcher = leadingNewLinePattern.matcher(string2);
        leadingMatcher.find();
        int nNewLines = Integer.min(2, Integer.max(leadingMatcher.group().length(), trailingMatcher.group().length()));
        String newLineJoin = String.join("", Collections.nCopies(nNewLines, "\n"));
        return trailingMatcher.replaceAll("")
                + newLineJoin
                + leadingMatcher.replaceAll("");
    }

    private String escape(String string) {
        for (Escape escape : escapes) {
            string = string.replaceAll(escape.pattern, escape.replace);
        }
        return string;
    }

    private static class Escape {
        String pattern;
        String replace;

        public Escape(String pattern, String replace) {
            this.pattern = pattern;
            this.replace = replace;
        }
    }

}