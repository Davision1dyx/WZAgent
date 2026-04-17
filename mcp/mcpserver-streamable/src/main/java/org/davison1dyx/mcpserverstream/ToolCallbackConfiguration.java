package org.davison1dyx.mcpserverstream;

import org.springframework.ai.tool.ToolCallbackProvider;
import org.springframework.ai.tool.method.MethodToolCallbackProvider;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * ToolCallbackConfiguration
 *
 * @author 229291
 * @since 2026/3/25 11:21
 */
@Configuration
public class ToolCallbackConfiguration {
    @Bean
    public ToolCallbackProvider placeSuggestTool(PlaceService placeService) {
        return MethodToolCallbackProvider.builder().toolObjects(placeService).build();
    }
}