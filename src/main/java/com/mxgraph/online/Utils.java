/**
 * Copyright (c) 2006-2016, JGraph Ltd
 * Copyright (c) 2006-2016, Gaudenz Alder
 */
package com.mxgraph.online;

import com.mxgraph.model.mxGeometry;

/**
 * String/byte array encoding/manipulation utilities
 */
public class Utils {
    /**
     * Rotates the given geometry (in place) by the given rotation (in degrees).
     */
    public static void rotatedGeometry(mxGeometry geo, double rotation,
                                       double cx, double cy) {
        rotation = Math.toRadians(rotation);
        double cos = Math.cos(rotation), sin = Math.sin(rotation);

        double x = geo.getCenterX() - cx;
        double y = geo.getCenterY() - cy;

        double x1 = x * cos - y * sin;
        double y1 = y * cos + x * sin;

        geo.setX(Math.round(x1 + cx - geo.getWidth() / 2));
        geo.setY(Math.round(y1 + cy - geo.getHeight() / 2));
    }

}