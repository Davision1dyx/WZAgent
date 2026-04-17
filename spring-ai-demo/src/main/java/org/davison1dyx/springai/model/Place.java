package org.davison1dyx.springai.model;

import com.fasterxml.jackson.annotation.JsonPropertyDescription;

public record Place(@JsonPropertyDescription("景点名称") String name,
                    @JsonPropertyDescription("景点描述") String description,
                    @JsonPropertyDescription("景点位置") String location) {
}
