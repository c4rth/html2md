package com.mxgraph.gliffy.model;

import lombok.Data;

@Data
public class GlobalTextStyles {
    private String size;

    private String color;

    public String getSize() {
        return size != null ? size.substring(0, size.indexOf("px")) : size;
    }

}
