package org.davison1dyx.mcpserversse.entity;

import lombok.Data;

/**
 * PlaceResponse
 *
 * @author 229291
 * @since 2026/3/25 17:17
 */
@Data
public class PlaceResponse {
    private String name;
    private String description;

    public PlaceResponse(String name, String description) {
        this.name = name;
        this.description = description;
    }
}