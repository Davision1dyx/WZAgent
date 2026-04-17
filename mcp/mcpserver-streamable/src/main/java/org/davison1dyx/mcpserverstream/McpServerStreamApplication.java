package org.davison1dyx.mcpserverstream;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * McpServerStreamApplication
 *
 * @author 229291
 * @since 2026/3/25 16:50
 */
@SpringBootApplication
public class McpServerStreamApplication {
    /**
        "mcpServers": {
            "place-streamable": {
                "url":"http://localhost:9997/mcp",
                "type":"streamableHttp",
                "timeout": 60,
                "disable": false
            }
        }
     */
    public static void main(String[] args) {
        SpringApplication.run(McpServerStreamApplication.class, args);
    }
}