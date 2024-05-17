package org.carth.html2md.copydown.rules;

import lombok.Getter;
import lombok.Setter;
import org.jsoup.nodes.Node;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;
import java.util.function.Predicate;
import java.util.function.Supplier;

@Getter
abstract public class Rule {
    private static final Logger log = LoggerFactory.getLogger(Rule.class);
    private final Predicate<Node> filter;
    private final Supplier<String> append = null;

    @Setter
    private String name;

    public Rule(String filter) {
        this.filter = (el) -> el.nodeName().toLowerCase().equals(filter);
    }

    public Rule(String[] filters) {
        Set<String> availableFilters = new HashSet<>(Arrays.asList(filters));
        filter = (element -> availableFilters.contains(element.nodeName()));
    }

    public Rule(Predicate<Node> filter) {
        this.filter = filter;
    }

    public abstract String replacement(String content, Node element);

    public Supplier<String> getAppend() {
        return null;
    }

    protected String cleanAttribute(String attribute) {
        return attribute.replaceAll("(\n+\\s*)+", "\n");
    }
}