package org.davison1dyx.mcpclient.service;

import org.davison1dyx.mcpclient.callback.DirectSyncToolCallBackProvider;
import io.modelcontextprotocol.client.McpClient;
import io.modelcontextprotocol.client.McpSyncClient;
import io.modelcontextprotocol.client.transport.HttpClientStreamableHttpTransport;
import io.modelcontextprotocol.spec.McpSchema;
import jakarta.annotation.PostConstruct;
import lombok.extern.slf4j.Slf4j;
import org.springframework.ai.anthropic.AnthropicChatModel;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.client.advisor.SimpleLoggerAdvisor;
import org.springframework.ai.chat.prompt.ChatOptions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.util.List;

/**
 * McpReturnDirectService
 * 绕过大模型总结，直接返回工具调用结果
 *
 * @author 229291
 * @since 2026/3/25 20:44
 */
@Slf4j
@Service
public class McpReturnDirectService {

    private ChatClient chatClient;
    @Autowired
    private AnthropicChatModel chatModel;
    @Autowired
    private List<McpSyncClient> mcpSyncClients;

    @PostConstruct
    void init() {
        // Streamable Http
//        HttpClientStreamableHttpTransport streamableHttpTransport = HttpClientStreamableHttpTransport.builder("http://localhost:9997")
//                .endpoint("/mcp")
//                .build();
//        McpSyncClient streamableHttpClient = McpClient.sync(streamableHttpTransport).clientInfo(new McpSchema.Implementation("streamable‑http‑client", "1.0"))
//                .requestTimeout(Duration.ofSeconds(10)).build();
//        streamableHttpClient.initialize();


        chatClient = ChatClient.builder(chatModel)
                .defaultOptions(ChatOptions.builder().maxTokens(1000).build())
                .defaultAdvisors(new SimpleLoggerAdvisor())
                .defaultToolCallbacks(new DirectSyncToolCallBackProvider(mcpSyncClients, true).getToolCallbacks())
                .build();
    }

    public String callToolDirect(String message) {
        return chatClient.prompt().user(message).call().content();
    }
}