package org.davison1dyx.mcpclient.callback;

import io.modelcontextprotocol.client.McpSyncClient;
import io.modelcontextprotocol.spec.McpSchema;
import org.springframework.ai.mcp.SyncMcpToolCallback;
import org.springframework.ai.tool.metadata.ToolMetadata;

/**
 * DirectSyncToolCallBack
 *
 * @author 229291
 * @since 2026/3/26 9:31
 */
public class DirectSyncToolCallBack extends SyncMcpToolCallback {

    private boolean returnDirect = false;

    /**
     * Creates a callback with default settings.
     *
     * @param mcpClient the MCP client for tool execution
     * @param tool      the MCP tool to adapt
     * @deprecated use {@link #builder()} instead
     */
    public DirectSyncToolCallBack(McpSyncClient mcpClient, McpSchema.Tool tool, boolean returnDirect) {
        super(mcpClient, tool);
        this.returnDirect = returnDirect;
    }

    @Override
    public ToolMetadata getToolMetadata() {
        return ToolMetadata.builder()
                .returnDirect(returnDirect)
                .build();
    }
}