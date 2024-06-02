package com.mxgraph.gliffy.importer;

import lombok.extern.slf4j.Slf4j;

import java.util.HashMap;
import java.util.Map;
import java.util.PropertyResourceBundle;
import java.util.ResourceBundle;

@Slf4j
public class StencilTranslator {

    private static final Map<String, String> translationTable = new HashMap<>();

    static {
        init();
    }

    private static void init() {
        ResourceBundle rb = PropertyResourceBundle.getBundle("gliffyTranslation");
        for (String key : rb.keySet()) {
            translationTable.put(key, rb.getString(key));
        }
    }

    public static String translate(String gliffyShapeKey, String tid) {
        String shape = translationTable.get(gliffyShapeKey);

        if (shape == null && tid != null)
            shape = translationTable.get(tid);

        log.info("{} -> {}", gliffyShapeKey, shape);

        return shape;
    }
}
