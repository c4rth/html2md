package com.mxgraph.gliffy.model;

import lombok.Data;

@Data
public class Diagram {
    public Stage stage;
    public Metadata metadata;
    public EmbeddedResources embeddedResources;
    private String contentType;
    private String version;
}
