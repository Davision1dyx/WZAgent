package org.davison1dyx.agent.advisor;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.extern.slf4j.Slf4j;
import org.springframework.ai.chat.client.ChatClientRequest;
import org.springframework.ai.chat.client.ChatClientResponse;
import org.springframework.ai.chat.client.advisor.api.CallAdvisor;
import org.springframework.ai.chat.client.advisor.api.CallAdvisorChain;
import org.springframework.ai.chat.messages.SystemMessage;
import org.springframework.ai.chat.messages.UserMessage;
import org.springframework.ai.chat.model.ChatModel;
import org.springframework.ai.chat.prompt.Prompt;
import org.springframework.ai.converter.BeanOutputConverter;

import java.util.List;

/**
 * ReflectionAdvisor
 *
 * @author 229291
 * @since 2026/3/30 10:33
 */
@Slf4j
public class ReflectionAdvisor implements CallAdvisor {

    private final String prompt = """
            你是一个严格的智能体反思评估专家。

            请判断【当前回答】是否已经充分、准确地满足【用户问题】。

            评估标准：
            1. 信息是否完整
            2. 逻辑是否清晰
            3. 结论是否可靠、与上下文一致
            4. 表达是否符合最终交付质量

            【你必须且只能输出一个 JSON 对象，格式如下】

            {
              "passed": true | false,
              "feedback": "如果 passed=false，给出明确、可执行的改进建议，但是必须不要过长，控制在100字以内；如果 passed=true，值为 null"
            }

            禁止输出任何额外文本。
            """;

    private final ChatModel reflectChatModel;
    private final BeanOutputConverter<ReflectionJudgement> outputConverter = new BeanOutputConverter<>(ReflectionJudgement.class);

    public ReflectionAdvisor(ChatModel reflectChatModel) {
        this.reflectChatModel = reflectChatModel;
    }

    @Override
    public ChatClientResponse adviseCall(ChatClientRequest chatClientRequest, CallAdvisorChain callAdvisorChain) {
        ChatClientResponse chatClientResponse = callAdvisorChain.nextCall(chatClientRequest);

        if (chatClientResponse.chatResponse() != null && chatClientResponse.chatResponse().hasToolCalls()) {
            return chatClientResponse;
        }

        if (chatClientResponse.chatResponse() == null || chatClientResponse.chatResponse().getResult() == null) {
            return chatClientResponse;
        }

        String answer = chatClientResponse.chatResponse().getResult().getOutput().getText();

        String question = extractQuestion(chatClientRequest.prompt());

        ReflectionJudgement judgement = reflect(question, answer);

        if (judgement.passed()) {
            log.debug("=======Reflection 反思通过=======");
            return chatClientResponse;
        }

        log.info("=======Reflection 反思未通过, 需要agent重新执行=======reason: {}", judgement.feedback());

        return chatClientResponse.mutate()
                .context("reflection.required", true)
                .context("reflection.feedback", judgement.feedback())
                .build();
    }

    @Override
    public String getName() {
        return "ReflectionAdvisor";
    }

    @Override
    public int getOrder() {
        return 50;
    }

    private String extractQuestion(Prompt prompt) {
        return prompt.getInstructions().stream()
                .filter(m -> m instanceof UserMessage)
                .map(m -> ((UserMessage) m).getText())
                .findFirst()
                .orElse("");
    }

    private ReflectionJudgement reflect(String question, String answer) {
        Prompt prompt = new Prompt(
                List.of(
                        new SystemMessage(this.prompt),
                        new UserMessage("""
                        ## 用户问题：
                        %s

                        ## 当前回答：
                        %s
                                                
                        ## 输出format
                        %s
                        """.formatted(question, answer, outputConverter.getFormat()))
                )
        );

        String text = reflectChatModel.call(prompt).getResult().getOutput().getText();
        return outputConverter.convert(text);
    }

    public record ReflectionJudgement(@JsonProperty("passed") boolean passed,
                                      @JsonProperty("feedback") String feedback) {}
}