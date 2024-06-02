package com.mxgraph.gliffy.model;

import lombok.Data;

import java.util.List;

@Data
public class Constraints {
    private List<Constraint> constraints;
    private Constraint startConstraint;
    private Constraint endConstraint;
}
