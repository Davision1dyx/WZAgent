package org.davision1dyx.rag.optimize;

import com.alibaba.fastjson2.JSON;
import lombok.extern.slf4j.Slf4j;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.model.ChatModel;
import org.springframework.ai.chat.prompt.PromptTemplate;
import org.springframework.stereotype.Service;

import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * @author Davison
 * @date 2026-04-01
 * @description rag问题改写优化
 */
@Slf4j
@Service
public class QuestionRewriteOptimizer {

    private final ChatClient chatClient;

    public QuestionRewriteOptimizer(ChatModel chatModel) {
        this.chatClient = ChatClient.builder(chatModel)
                .build();
    }

    /**
     * 问题富化：根据对话历史上下文，丰富用户原始问题。
     * query compression
     */
    public String enrich(String history, String question) {
        String prompt = """
            # 角色
            你是一个专业的问题重写优化器。
            
            # 任务
            根据提供的“对话历史”和“用户原始问题”，重写为一个独立、完整、且包含所有必要背景信息的新查询，用于RAG检索。
            
            ## 对话历史：
            {CHAT_HISTORY}
            
            ## 原始问题：
            {QUESTION}
            
            # 输出
            输出富化过后的新问题，不要包含多余的解释性内容
            """;
        log.info("===========进入问题富化流程===========");
        log.info("对话历史: {}", history);
        log.info("原始问题: {}", question);
        String content = chatClient.prompt(new PromptTemplate(prompt).create(Map.of("CHAT_HISTORY", history, "QUESTION", question)))
                .call().content();
        log.info("===========问题富化完成，结果: {} ===========", content);
        return content;
    }

    /**
     * 问题分解：把一个复杂的问题，拆分成明确单一的子问题。
     */
    public List<String> decompose(String question) {
        String prompt = """
                # 角色
            你是一名专业的查询逻辑分析专家。
            
            # 任务
            将给定的“用户原始问题”分解为一系列**相互独立、逻辑清晰**，且可单独用于检索的子查询列表。
            你的输出必须是一个标准的JSON数组格式。
            
            # 用户原始问题
            {QUESTION}
            
            # 输出格式要求 (JSON Array)
            [
              "子查询1",
              "子查询2",
              "子查询3",
              "..."
            ]
            
            （不强制要求数组元素个数，可根据真实情况输出，至少保留1个）
            
            # 输出
            请直接输出JSON数组，不要包含解释或多余的文字。
                """;

        log.info("===========进入问题分解流程===========");
        log.info("原始问题: {}", question);

        PromptTemplate promptTemplate = new PromptTemplate(prompt);
        promptTemplate.add("question", question);

        String content = chatClient.prompt(promptTemplate.create()).call().content();
        log.info("===========问题分解完成，结果: {} ===========", content);
        return JSON.parseArray(content, String.class);
    }

    /**
     * 问题回退：将用户问题抽象化
     */
    public String stepBack(String question) {
        String prompt = """
            # 角色
            你是一个擅长抽象思维和原理推理的专家。
            
            # 任务
            请根据用户提出的具体问题，先“后退一步”，将其转化为一个更通用、更本质的问题，聚焦于背后的原理、规律、概念或一般性知识，而不是具体细节。
            
            # 原始问题
            
            {QUESTION}
            
            # 输出
            请只输出改写后的“后退问题”，不要解释，不要包含原始问题，也不要回答它。
            """;

        log.info("===========进入问题回退流程===========");
        log.info("原始问题: {}", question);

        PromptTemplate promptTemplate = new PromptTemplate(prompt);
        promptTemplate.add("question", question);

        String content = chatClient.prompt(promptTemplate.create()).call().content();
        log.info("===========问题回退完成，结果: {} ===========", content);
        return content;
    }

    /**
     * 问题多样化：将用户问题中名词进行同义多样化，增加向量检索命中率
     * query expander
     */
    public List<String> diversify(String question) {
        String prompt = """
            # 角色
            你是一名专业的语义扩展专家。
            
            # 任务
            为给定的“原始问题”生成**3个**语义相同但**措辞完全不同、且利于检索**的查询变体，以提高检索的召回率。
            你的输出必须是一个标准的JSON数组格式。
            
            # 原始问题
            {QUESTION}
            
            # 输出格式要求 (JSON Array)
            [
              "变体1",
              "变体2",
              "变体3"
            ]
            
            # 输出
            输出富化过后的新问题，不要包含多余的解释性内容
            """;

        log.info("===========进入问题多样化流程===========");
        log.info("原始问题: {}", question);

        PromptTemplate promptTemplate = new PromptTemplate(prompt);
        promptTemplate.add("question", question);

        String content = chatClient.prompt(promptTemplate.create()).call().content();
        log.info("===========问题多样化完成，结果: {} ===========", content);
        return JSON.parseArray(content, String.class);
    }

    /**
     * 组合方法：上述四种方法组合使用
     */
    public List<String> combineRewrite(String question) {
        log.info("===========进入问题改写优化流程===========");
        log.info("原始问题: {}", question);
        // 问题回退
        String stepBack = stepBack(question);
        // 问题拆解
        List<String> decompose = decompose(stepBack);
        // 问题多样化
        List<String> diversify = decompose.stream()
                .map(this::diversify)
                .flatMap(Collection::stream)
                .toList();

        log.info("===========组合重写完成，最终查询列表: {} ===========", diversify);
        return diversify;
    }
}
