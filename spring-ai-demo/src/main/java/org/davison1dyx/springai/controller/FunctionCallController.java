package org.davison1dyx.springai.controller;

import org.davison1dyx.springai.tool.TimeTool;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.client.advisor.MessageChatMemoryAdvisor;
import org.springframework.ai.chat.client.advisor.SimpleLoggerAdvisor;
import org.springframework.ai.chat.memory.ChatMemory;
import org.springframework.ai.chat.model.ChatModel;
import org.springframework.ai.chat.prompt.ChatOptions;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

/**
 * FunctionCallController
 *
 * @author 229291
 * @since 2026/3/24 10:42
 */
@RestController
@RequestMapping("/process/function")
public class FunctionCallController implements InitializingBean {

    private final ChatModel chatModel;
    private ChatClient chatClient;

    public FunctionCallController(ChatModel chatModel) {
        this.chatModel = chatModel;
    }

    @GetMapping("/callFunction")
    public String callFunction(@RequestParam String message) {
        // 实现一个可以查看任意时区当前时间的助手
        return chatClient.prompt().user(message).toolNames("getTimeFunction").call().content();
    }



    @Autowired
    private ChatMemory chatMemory;
    @GetMapping("/callTool")
    public String callTool(@RequestParam String message) {
        // 实现一个可以查看任意时区当前时间的助手
        // 得加记忆，不然循环调用，陷入死循环。。。。

        return chatClient.prompt()
                .advisors(MessageChatMemoryAdvisor.builder(chatMemory).build())
                .tools(new TimeTool())
                .user(message)
                .call()
                // 很重要： .user要放在.tool后面。。。
                // 因为我初始测试时返回了13月13日，模型任务是错误的日期，所以重复调工具，没有停止并提示用户。当.tool()放在.user()前面时，模型停止并提示了用户
                .content();
    }

    @Override
    public void afterPropertiesSet() throws Exception {
        chatClient = ChatClient.builder(chatModel)
                .defaultAdvisors(new SimpleLoggerAdvisor())
                .defaultOptions(ChatOptions.builder().maxTokens(1000).build())
                .build();
    }
}