package org.davison1dyx.agent.controller;

import com.alibaba.cloud.ai.graph.RunnableConfig;
import com.alibaba.cloud.ai.graph.agent.ReactAgent;
import com.alibaba.cloud.ai.graph.agent.hook.shelltool.ShellToolAgentHook;
import com.alibaba.cloud.ai.graph.agent.hook.skills.SkillsAgentHook;
import com.alibaba.cloud.ai.graph.agent.tools.ShellTool2;
import com.alibaba.cloud.ai.graph.checkpoint.savers.MemorySaver;
import com.alibaba.cloud.ai.graph.exception.GraphRunnerException;
import com.alibaba.cloud.ai.graph.skills.registry.SkillRegistry;
import com.alibaba.cloud.ai.graph.skills.registry.filesystem.FileSystemSkillRegistry;
import org.davison1dyx.agent.tool.FileReadTool;
import lombok.extern.slf4j.Slf4j;
import org.springframework.ai.anthropic.AnthropicChatModel;
import org.springframework.ai.chat.messages.AssistantMessage;
import org.springframework.ai.support.ToolCallbacks;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ClassPathResource;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * SkillController
 *
 * @author 229291
 * @since 2026/4/1 16:29
 */
@Slf4j
@RestController
@RequestMapping("/process/agent/skill")
public class SkillController {

    @Autowired
    private AnthropicChatModel chatModel;

    @GetMapping
    public String skill(String message, String conversationId) throws GraphRunnerException {
        // 1.注册技能
        SkillRegistry skillRegistry = FileSystemSkillRegistry.builder()
                .userSkillsDirectory("/work/skills")
                .projectSkillsDirectory(new ClassPathResource("skills"))
                .build();

        // 2.skillHook
        SkillsAgentHook skillsAgentHook = SkillsAgentHook.builder()
                .skillRegistry(skillRegistry)
                .build();

        // 3.操作系统shell的hook，实际就是提供一个shellTool2工具
        ShellToolAgentHook shellToolAgentHook = ShellToolAgentHook.builder()
                .shellTool2(ShellTool2.builder("/work/skills").withCommandTimeout(30000).build())
                .build();

        // 4. 构建agent, 使用skillHook、ShellToolHook和文件读取工具
        ReactAgent reactAgent = ReactAgent.builder()
                .name("useSkillAgent")
                .tools(ToolCallbacks.from(new FileReadTool()))
                .model(chatModel)
                .saver(new MemorySaver())
                .hooks(skillsAgentHook, shellToolAgentHook)
                .enableLogging(true)
                .build();

        RunnableConfig config = RunnableConfig.builder()
                .threadId(conversationId)
                .build();

        AssistantMessage call = reactAgent.call(message, config);
        return call.getText();
    }
}