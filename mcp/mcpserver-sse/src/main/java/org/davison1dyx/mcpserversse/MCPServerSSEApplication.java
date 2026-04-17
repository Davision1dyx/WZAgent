package org.davison1dyx.mcpserversse;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * MCPServerSSEApplication
 *
 * @author 229291
 * @since 2026/3/25 12:47
 */
@SpringBootApplication
public class MCPServerSSEApplication {
    /**
      MCP 配置
          "place-sse": {
            "disabled": false,
            "type": "sse",
            "url": "http://localhost:9998/sse",
            "timeout": 60,
            "autoApprove": []
          }
     */
    public static void main(String[] args) {
        SpringApplication.run(MCPServerSSEApplication.class, args);
    }
}