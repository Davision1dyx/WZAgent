package org.davison1dyx.mcpclient.callback;

import io.modelcontextprotocol.client.McpSyncClient;
import io.modelcontextprotocol.spec.McpSchema;
import lombok.extern.slf4j.Slf4j;
import org.springframework.ai.mcp.SyncMcpToolCallbackProvider;
import org.springframework.ai.tool.ToolCallback;
import org.springframework.ai.tool.support.ToolUtils;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * DirectSyncToolCallBackProvider
 *
 * @author 229291
 * @since 2026/3/26 9:30
 */
@Slf4j
public class DirectSyncToolCallBackProvider extends SyncMcpToolCallbackProvider {

    private List<McpSyncClient> clients;

    private boolean returnDirect = false;

    public DirectSyncToolCallBackProvider(List<McpSyncClient> clients, boolean returnDirect) {
        super(clients);
        this.clients = clients;
        this.returnDirect = returnDirect;
    }

    @Override
    public ToolCallback[] getToolCallbacks() {

        var callbacks = new ArrayList<>();

        for (McpSyncClient client : clients) {
            List<McpSchema.Tool> tools = Collections.emptyList();
            try {
                tools = client.listTools().tools();
            } catch (Exception e) {
                log.error("get mcp client tool error", e);
                continue;
            }

            for (McpSchema.Tool tool : tools) {
                callbacks.add(new DirectSyncToolCallBack(client, tool, returnDirect));
            }
        }
        ToolCallback[] toolCallbacks = callbacks.toArray(new ToolCallback[0]);
        validateToolCallbacks(toolCallbacks);
        return toolCallbacks;
    }


    private void validateToolCallbacks(ToolCallback[] toolCallbacks) {
        List<String> duplicateToolNames = ToolUtils.getDuplicateToolNames(toolCallbacks);
        duplicateToolNames.forEach(s -> log.info("tool name found: {}", s));
        if (!duplicateToolNames.isEmpty()) {
            throw new IllegalStateException(
                    "Multiple tools with the same name (%s)".formatted(String.join(", ", duplicateToolNames)));
        }
    }
}