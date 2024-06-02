package com.mxgraph.gliffy.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

@Data
public class Constraint {

    private ConstraintType type;
    private ConstraintData StartPositionConstraint;
    private ConstraintData EndPositionConstraint;

    public enum ConstraintType {
        @JsonProperty("StartPositionConstraint") START_POSITION_CONSTRAINT,
        @JsonProperty("EndPositionConstraint") END_POSITION_CONSTRAINT,
        @JsonProperty("HeightConstraint") HEIGHT_CONSTRAINT;

        public String toString() {
            return this.name();
        }
    }

    @Data
    static public class ConstraintData {
        private String nodeId;

        private float px;

        private float py;
    }

}
