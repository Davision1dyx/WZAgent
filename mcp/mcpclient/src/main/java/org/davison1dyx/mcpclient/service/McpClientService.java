package org.davison1dyx.mcpclient.service;

import org.springframework.ai.anthropic.AnthropicChatModel;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.client.advisor.SimpleLoggerAdvisor;
import org.springframework.ai.chat.prompt.ChatOptions;
import org.springframework.ai.mcp.SyncMcpToolCallbackProvider;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * McpClientService
 *
 * @author 229291
 * @since 2026/3/25 19:47
 */
@Service
public class McpClientService {

    @Autowired
    private SyncMcpToolCallbackProvider syncMcpToolCallbackProvider;
    @Autowired
    private AnthropicChatModel anthropicChatModel;

    public String callMcpTool(String message) {
        ChatClient chatClient = ChatClient.builder(anthropicChatModel)
                .defaultAdvisors(new SimpleLoggerAdvisor())
                .defaultToolCallbacks(syncMcpToolCallbackProvider.getToolCallbacks())
                .defaultOptions(ChatOptions.builder().maxTokens(1000)
                .build()).build();
        return chatClient.prompt().user(message).call().content();
    }
}