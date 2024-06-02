package com.mxgraph.gliffy.model;

import lombok.Data;

import java.util.List;

@Data
public class Stage {

    private String background;

    private float width;

    private float height;

    private boolean autofit;

    private boolean gridOn;

    private boolean drawingGuidesOn;

    private List<GliffyObject> objects;

    private List<GliffyLayer> layers;

    private TextStyles textStyles;

    public Stage() {
    }

    public String getBackgroundColor() {
        return background;
    }

}