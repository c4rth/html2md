package org.carth.html2md.copydown.rules;

import lombok.Getter;
import lombok.Setter;
import org.carth.html2md.copydown.Options;
import org.carth.html2md.utils.TriSupplier;
import org.jsoup.nodes.Node;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;
import java.util.function.BiPredicate;
import java.util.function.Function;

@Getter
abstract public class Rule {
    private BiPredicate<Node, Options> filter = null;
    private Function<Options, String> append = null;
    private TriSupplier<String, Node, Options, String> replace = null;

    @Setter
    private String name;

    public void setRule(
            String filter,
            TriSupplier<String, Node, Options, String> replace
    ) {
        this.filter = (el, options) -> el.nodeName().toLowerCase().equals(filter);
        this.replace = replace;
    }

    public void setRule(BiPredicate<Node, Options> filter, TriSupplier<String, Node, Options, String> replace) {
        this.filter = filter;
        this.replace = replace;
    }


    public void setRule(BiPredicate<Node, Options> filter, TriSupplier<String, Node, Options, String> replace, Function<Options, String> append) {
        this.filter = filter;
        this.replace = replace;
        this.append = append;
    }

    public void setRule(
            String[] filters,
            TriSupplier<String, Node, Options, String> replace
    ) {
        Set<String> availableFilters = new HashSet<>(Arrays.asList(filters));
        this.filter = (el, options) -> availableFilters.contains(el.nodeName());
        this.replace = replace;
    }

}