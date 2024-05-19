package org.carth.html2md.copydown.rules;

import lombok.Getter;
import lombok.Setter;
import org.carth.html2md.copydown.Options;
import org.carth.html2md.utils.TriSupplier;
import org.jsoup.nodes.Node;

import java.util.List;
import java.util.function.BiPredicate;
import java.util.function.Function;

@Getter
abstract public class Rule {
    private BiPredicate<Node, Options> filter = null;
    private Function<Options, String> append = null;
    private TriSupplier<String, Node, Options, String> replace = null;

    @Setter
    private String name;

    public void init(BiPredicate<Node, Options> filter, TriSupplier<String, Node, Options, String> replace, Function<Options, String> append) {
        this.filter = filter;
        this.replace = replace;
        this.append = append;
    }

    public void init(BiPredicate<Node, Options> filter, TriSupplier<String, Node, Options, String> replace) {
        init(filter, replace, null);
    }

    public void init(String filter, TriSupplier<String, Node, Options, String> replace) {
        init((el, options) -> el.nodeName().toLowerCase().equals(filter),
                replace,
                null
        );
    }

    public void init(List<String> filters, TriSupplier<String, Node, Options, String> replace) {
        init((el, options) -> filters.contains(el.nodeName()),
                replace,
                null
        );
    }

}