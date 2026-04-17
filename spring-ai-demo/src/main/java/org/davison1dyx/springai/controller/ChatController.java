package org.davison1dyx.springai.controller;

import org.springframework.ai.anthropic.AnthropicChatOptions;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.client.DefaultChatClient;
import org.springframework.ai.chat.client.advisor.MessageChatMemoryAdvisor;
import org.springframework.ai.chat.client.advisor.SimpleLoggerAdvisor;
import org.springframework.ai.chat.memory.ChatMemory;
import org.springframework.ai.chat.messages.AssistantMessage;
import org.springframework.ai.chat.messages.UserMessage;
import org.springframework.ai.chat.prompt.ChatOptions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Flux;

/**
 * ChatController
 *
 * @author 229291
 * @since 2026/3/23 11:35
 */
@RestController
@RequestMapping("/process/chat")
public class ChatController {

    private final ChatClient chatClient;

    public ChatController(ChatClient.Builder chatClientBuilder) {
        this.chatClient = chatClientBuilder.build();
    }

    @GetMapping(value = "/stream", produces = MediaType.TEXT_EVENT_STREAM_VALUE)
    public Flux<String> stream(@RequestParam String message) {
        return chatClient.prompt(message).stream().content();
    }

    @GetMapping("/call")
    public String call(@RequestParam String message) {
        return chatClient.prompt().system("你是一个翻译，将用户输入的话翻译成英文").user(message).call().content();
    }


    @Autowired
    private ChatMemory chatMemory;

    @GetMapping("/memory")
    public Flux<String> memory(@RequestParam String message, @RequestParam String conversationId) {
        return chatClient.prompt()
                .messages(new UserMessage(message))
                // 实现 Logger 的 Advisor
                .advisors(MessageChatMemoryAdvisor.builder(chatMemory).conversationId(conversationId)
                        .build(), new SimpleLoggerAdvisor())
                .stream()
                .content();
    }

    @Autowired
    private ChatMemory jdbcChatMemory;

    @GetMapping("/memoryDB")
    public Flux<String> memoryDB(@RequestParam String message, @RequestParam String conversationId) {
        return chatClient.prompt()
                .messages(new UserMessage(message))
                .advisors(MessageChatMemoryAdvisor
                        .builder(jdbcChatMemory)
                        .conversationId(conversationId)
                        .build(), new SimpleLoggerAdvisor()).stream().content();
    }
}