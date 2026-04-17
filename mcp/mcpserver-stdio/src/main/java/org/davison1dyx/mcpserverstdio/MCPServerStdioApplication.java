package org.davison1dyx.mcpserverstdio;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * MCPServerStdioApplication
 *
 * @author 229291
 * @since 2026/3/25 10:55
 */
@SpringBootApplication
public class MCPServerStdioApplication {

    /**
     *
      stdio MCP配置格式：
      {
        "mcpServers": {
          "place-stdio": {
            "disabled": true,
            "timeout": 60,
            "type": "stdio",
            "command": "java",
            "args": [
              "-jar",
              "D:\\ProgramSelf\\WZAgent\\mcp\\mcpserver-stdio\\target\\mcpserver-stdio-1.0.0-SNAPSHOT.jar"
            ]
          }
        }
      }
     */
    public static void main(String[] args) {
        SpringApplication.run(MCPServerStdioApplication.class, args);
    }
}
