package com.mxgraph.gliffy.importer;

import java.util.HashMap;
import java.util.Map;

public class LineMapping {

    private static final Map<String, String> mapping = new HashMap<>();

    static {
        mapping.put("linear", "");
        mapping.put("orthogonal", "edgeStyle=orthogonal;");
        mapping.put("quadratic", "curved=1;edgeStyle=orthogonalEdgeStyle;");
    }

    public static String get(String style) {
        return mapping.get(style);
    }
}
