package org.davison1dyx.mcpserversse.entity;

import lombok.Data;
import org.springframework.ai.tool.annotation.ToolParam;

/**
 * PlaceRequest
 *
 * @author 229291
 * @since 2026/3/25 17:14
 */
@Data
public class PlaceRequest{
    @ToolParam(description = "城市名， 例如：杭州")
    private String name;
}