package org.davison1dyx.mcpclient.service;

import io.modelcontextprotocol.client.McpClient;
import io.modelcontextprotocol.client.McpSyncClient;
import io.modelcontextprotocol.client.transport.HttpClientSseClientTransport;
import io.modelcontextprotocol.client.transport.HttpClientStreamableHttpTransport;
import io.modelcontextprotocol.client.transport.ServerParameters;
import io.modelcontextprotocol.client.transport.StdioClientTransport;
import io.modelcontextprotocol.json.McpJsonMapper;
import io.modelcontextprotocol.spec.McpSchema;
import jakarta.annotation.PostConstruct;
import org.springframework.ai.anthropic.AnthropicChatModel;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.client.advisor.SimpleLoggerAdvisor;
import org.springframework.ai.chat.prompt.ChatOptions;
import org.springframework.ai.mcp.SyncMcpToolCallbackProvider;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.util.List;

/**
 * ManualMcpService
 *
 * @author 229291
 * @since 2026/3/25 20:17
 */
@Service
public class ManualMcpService {

    @Autowired
    private AnthropicChatModel chatModel;

    private ChatClient chatClient;


    /**
     * 手动获取mcpClient，并且可以按照需求过滤获取到的Tools
     */
    @PostConstruct
    public void init() {
        // stdio
//        ServerParameters parameters = ServerParameters.builder("C:\\\\Program Files\\\\Java\\\\jdk-21.0.7\\\\bin\\\\java")
//                .args("-jar", "D:\\\\ProgramSelf\\\\WZAgent\\\\mcp\\\\mcpserver-stdio\\\\target\\\\mcpserver-stdio-1.0.0-SNAPSHOT.jar")
//                .build();
//        StdioClientTransport stdioClientTransport = new StdioClientTransport(parameters, McpJsonMapper.createDefault());
//
//        McpSyncClient stdioClient = McpClient.sync(stdioClientTransport).clientInfo(new McpSchema.Implementation("my‑client", "1.0"))
//                .requestTimeout(Duration.ofSeconds(10)).build();
//        stdioClient.initialize();

        // SSE
        HttpClientSseClientTransport sseClientTransport = HttpClientSseClientTransport.builder("http://localhost:9998")
                .sseEndpoint("/sse")
                .build();
        McpSyncClient sseClient = McpClient.sync(sseClientTransport).clientInfo(new McpSchema.Implementation("sse‑client", "1.0"))
                .requestTimeout(Duration.ofSeconds(10)).build();
        sseClient.initialize();

        // Streamable Http
        HttpClientStreamableHttpTransport streamableHttpTransport = HttpClientStreamableHttpTransport.builder("http://localhost:9997")
                .endpoint("/mcp")
                .build();
        McpSyncClient streamableHttpClient = McpClient.sync(streamableHttpTransport).clientInfo(new McpSchema.Implementation("streamable‑http‑client", "1.0"))
               .requestTimeout(Duration.ofSeconds(10)).build();
        streamableHttpClient.initialize();

        List<McpSyncClient> mcpSyncClientList = List.of(sseClient, streamableHttpClient);

        SyncMcpToolCallbackProvider callbackProvider = SyncMcpToolCallbackProvider.builder()
                .mcpClients(mcpSyncClientList)
                // 按需求过滤工具
                .toolFilter((conn, tool) -> {
                    return tool.name().contains("place");
                })
                .build();

        this.chatClient = ChatClient.builder(chatModel)
                .defaultToolCallbacks(callbackProvider.getToolCallbacks())
                .defaultOptions(ChatOptions.builder().maxTokens(1000).build())
                .defaultAdvisors(new SimpleLoggerAdvisor())
                .build();
    }

    public String chatByManualMcp(String message) {
        return chatClient.prompt().user(message).call().content();
    }

    public static void main(String[] args) {
        // stdio
        ServerParameters parameters = ServerParameters.builder("C:\\\\Program Files\\\\Java\\\\jdk-21.0.7\\\\bin\\\\java")
                .args("-jar", "D:\\\\ProgramSelf\\\\WZAgent\\\\mcp\\\\mcpserver-stdio\\\\target\\\\mcpserver-stdio-1.0.0-SNAPSHOT.jar")
                .build();
        StdioClientTransport stdioClientTransport = new StdioClientTransport(parameters, McpJsonMapper.createDefault());

        McpSyncClient stdioClient = McpClient.sync(stdioClientTransport).clientInfo(new McpSchema.Implementation("my‑client", "1.0"))
                .requestTimeout(Duration.ofSeconds(10)).build();
        stdioClient.initialize();
    }
}