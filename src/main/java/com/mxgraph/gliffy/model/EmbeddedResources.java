package com.mxgraph.gliffy.model;

import lombok.Data;

import java.nio.charset.StandardCharsets;
import java.util.Base64;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Data
public class EmbeddedResources {

    public List<Resource> resources;
    public Map<Integer, Resource> resourceMap;

    public Resource get(Integer id) {
        if (resourceMap == null) {
            resourceMap = new HashMap<>();
            for (Resource r : resources) {
                resourceMap.put(r.id, r);
            }
        }
        return resourceMap.get(id);
    }

    public static class Resource {
        public Integer id;
        public String mimeType;
        public String data;

        public String getBase64EncodedData() {
            return Base64.getEncoder().encodeToString(data.getBytes(StandardCharsets.UTF_8));
        }
    }

}
