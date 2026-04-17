package org.davison1dyx.mcpclient.controller;

import org.davison1dyx.mcpclient.service.ManualMcpService;
import org.davison1dyx.mcpclient.service.McpClientService;
import org.davison1dyx.mcpclient.service.McpReturnDirectService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

/**
 * ChatController
 *
 * @author 229291
 * @since 2026/3/25 19:27
 */
@Slf4j
@RestController
@RequestMapping("/process/mcp")
public class ChatController {

    @Autowired
    private McpClientService mcpClientService;
    @Autowired
    private ManualMcpService manualMcpService;
    @Autowired
    private McpReturnDirectService mcpReturnDirectService;

    @GetMapping("/chat")
    public String chat(@RequestParam String message) {
        return mcpClientService.callMcpTool(message);
    }

    @GetMapping("/chatManual")
    public String chatManual(@RequestParam String message) {
        return manualMcpService.chatByManualMcp(message);
    }

    @GetMapping("/chatToolDirect")
    public String chatToolDirect(@RequestParam String message) {
        return mcpReturnDirectService.callToolDirect(message);
    }
}