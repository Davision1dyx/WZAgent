package org.davison1dyx.agent.controller;

import com.alibaba.cloud.ai.graph.RunnableConfig;
import com.alibaba.cloud.ai.graph.agent.ReactAgent;
import com.alibaba.cloud.ai.graph.checkpoint.savers.MemorySaver;
import com.alibaba.cloud.ai.graph.exception.GraphRunnerException;
import org.davison1dyx.agent.advisor.HITLAdvisor;
import org.davison1dyx.agent.agent.HITLAgent;
import org.davison1dyx.agent.agent.PlanExecuteAgent;
import org.davison1dyx.agent.agent.ReflectionAgent;
import org.davison1dyx.agent.agent.SimpleReActAgent;
import org.davison1dyx.agent.agent.hitl.AgentFinished;
import org.davison1dyx.agent.agent.hitl.AgentInterrupted;
import org.davison1dyx.agent.agent.hitl.AgentResult;
import org.davison1dyx.agent.agent.hitl.PendingToolCall;
import org.davison1dyx.agent.tool.PlaceTool;
import lombok.extern.slf4j.Slf4j;
import org.springframework.ai.anthropic.AnthropicChatModel;
import org.springframework.ai.chat.client.advisor.SimpleLoggerAdvisor;
import org.springframework.ai.chat.memory.ChatMemory;
import org.springframework.ai.chat.memory.MessageWindowChatMemory;
import org.springframework.ai.chat.messages.AssistantMessage;
import org.springframework.ai.chat.prompt.Prompt;
import org.springframework.ai.support.ToolCallbacks;
import org.springframework.ai.tool.ToolCallback;
import org.springframework.ai.tool.method.MethodToolCallbackProvider;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;
import java.util.Set;

/**
 * SimpleAgentController
 *
 * @author 229291
 * @since 2026/3/27 12:49
 */
@Slf4j
@RestController
@RequestMapping("/process/simpleAgent/")
public class SimpleAgentController {

    @Autowired
    private AnthropicChatModel chatModel;
    @Autowired
    private PlaceTool placeTool;


    @GetMapping("/callSpringAI")
    public String callSpringAI(@RequestParam String message) {
        // 使用springAI来实现reactAgent。  重点是看chatResponse中有无调用tooCall，有的话，代表还没有到最终答案总结步骤，要重复react
        return null;
    }

    @GetMapping("/callSpringAIAlibaba")
    public String callAlibaba(@RequestParam String message, @RequestParam String conversationId) throws GraphRunnerException {

        Prompt prompt = new Prompt("""
                你是一个智能助手，你擅长使用工具来帮助用户解决问题。
                你的工作流程是：
                1.思考：先根据用户的提问进行思考，推理出下一步需要进行的具体操作；
                2.行动：做具体的行动，这一步可以使用工具；
                3.观察：记录前一步行动的结果；
                4. 最终答案（用自然语言给出答复）
                你可以进行多轮思考和行动。如果要使用工具，请务必调用工具，不要自己随便捏造结果。必须在获得工具结果后进入“最终答案”步骤，并用自然语言回答用户。
                """);

        MethodToolCallbackProvider methodToolCallbackProvider = MethodToolCallbackProvider.builder()
                .toolObjects(placeTool)
                .build();

        // 创建alibaba 内置ReActAgent
        ReactAgent reactAgent = ReactAgent.builder()
                .name("reActAgent")
                .model(chatModel)
                .tools(ToolCallbacks.from(new PlaceTool()))
                .systemPrompt(prompt.getContents())
                .saver(new MemorySaver())
                .build();

        RunnableConfig config = RunnableConfig.builder()
                .threadId(conversationId)
                .build();

        AssistantMessage call = reactAgent.call(message, config);
        return call.getText();
    }

    @GetMapping("/callReactAgent")
    public String callReactAgent(@RequestParam String message, @RequestParam String conversationId) {
        ToolCallback[] toolCallbacks = ToolCallbacks.from(new PlaceTool());

        SimpleReActAgent simpleReActAgent = SimpleReActAgent.builder()
                .name("simpleAgent")
                .chatModel(chatModel)
                .chatMemory(MessageWindowChatMemory.builder().maxMessages(100).build())
                .systemPrompt("你是一个专业的旅游助手")
                .advisors(new SimpleLoggerAdvisor())
                .tools(toolCallbacks)
                .build();

        return simpleReActAgent.call(conversationId, message);
    }

    @GetMapping("/callReflectionAgent")
    public String callReflectionAgent(@RequestParam String message, @RequestParam String conversationId) {
        ToolCallback[] toolCallbacks = ToolCallbacks.from(new PlaceTool());

        ReflectionAgent reflectionAgent = ReflectionAgent.builder()
                .name("reflectionAgent")
                .chatModel(chatModel)
                .systemPrompt("你是一个专业的旅游助手")
                .maxRounds(-1)
                .tools(toolCallbacks)
                .advisors(new SimpleLoggerAdvisor())
                .build();

        return reflectionAgent.call(conversationId, message);
    }

    @GetMapping("/callPlanExecuteAgent")
    public String callPlanExecuteAgent(@RequestParam String message, @RequestParam String conversationId) {
        ToolCallback[] toolCallbacks = ToolCallbacks.from(new PlaceTool());

        ChatMemory chatMemory = MessageWindowChatMemory.builder().maxMessages(20).build();

        PlanExecuteAgent agent = PlanExecuteAgent.builder()
                .chatModel(chatModel)
                .tools(toolCallbacks)
                .maxRounds(3)
                .maxToolRetries(2)
                .chatMemory(chatMemory)
                .contextCharLimit(1000).build();

        return agent.call(conversationId, message);
    }

    @GetMapping("/callHITLAgent")
    public String callHITLAgent(@RequestParam String message) {

        ToolCallback[] toolCallbacks = ToolCallbacks.from(new PlaceTool());

        HITLAdvisor hitlAdvisor = new HITLAdvisor(Set.of("hotPlace", "suggestPlace"));

        HITLAgent hitlAgent = HITLAgent.builder()
                .chatModel(chatModel)
                .name("HITLAgent")
                .maxRounds(2)
                .advisors(hitlAdvisor)
                .tools(toolCallbacks)
                .build();

        // 第一次call
        AgentResult result = hitlAgent.call(message);

        // 多次 HITL 处理
        while (result instanceof AgentInterrupted interrupted) {

            System.out.println("===== HITL 中断 =====");

            for (PendingToolCall tc : interrupted.pendingToolCalls()) {
                System.out.println("=================== 需要用户审批的工具： =================");
                System.out.println("工具: " + tc.name());
                System.out.println("参数: " + tc.arguments());
            }
            System.out.println("=================== 以上工具需要用户审批 ===================");

            // 模拟人工审批
            List<PendingToolCall> feedbacks = new ArrayList<>();
            List<PendingToolCall> pendingToolCalls = interrupted.pendingToolCalls();
            for (PendingToolCall tc : pendingToolCalls) {
                System.out.println("请输入审批结果（同意/拒绝）：");
                Scanner scanner = new Scanner(System.in);
                String approval = scanner.nextLine();
                if (approval.equalsIgnoreCase("同意")) {
                    feedbacks.add(tc.approve());
                } else {
                    feedbacks.add(tc.reject("用户拒绝使用"));
                }
            }
//            List<PendingToolCall> feedbacks = interrupted.pendingToolCalls().stream()
//                    .map(tc -> new PendingToolCall(tc.id(), tc.name(), tc.arguments(), PendingToolCall.FeedbackResult.REJECTED, "拒绝使用"))
//                    .toList();

            // 再次发起调用
            result = hitlAgent.resume(interrupted, feedbacks);
        }

        if (result instanceof AgentFinished finished) {
            System.out.println("===== 最终结果 =====");
            System.out.println(finished.content());
            return finished.content();
        } else {
            throw new RuntimeException("execute error");
        }
    }
}