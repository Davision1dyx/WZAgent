package org.davision1dyx.rag.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.client.advisor.vectorstore.QuestionAnswerAdvisor;
import org.springframework.ai.chat.model.ChatModel;
import org.springframework.ai.chat.prompt.PromptTemplate;
import org.springframework.ai.document.Document;
import org.springframework.ai.vectorstore.SearchRequest;
import org.springframework.ai.vectorstore.VectorStore;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * @author Davison
 * @date 2026-03-30
 * @description 检索接口
 */
@Slf4j
@RestController()
@RequestMapping("/process/rag/retrieval")
public class RetrievalController {

    private final VectorStore vectorStore;
    private final ChatModel chatModel;

    public RetrievalController(VectorStore vectorStore, ChatModel chatModel) {
        this.vectorStore = vectorStore;
        this.chatModel = chatModel;
    }

    @GetMapping("/query")
    public String query(String query) {
        SearchRequest searchRequest = SearchRequest.builder()
                .topK(5)
                .query(query)
                .similarityThreshold(0.5)
                .build();
        List<Document> documents = vectorStore.similaritySearch(searchRequest);
        StringBuilder stringBuilder = new StringBuilder();
        for (Document document: documents) {
            stringBuilder.append(document.getText());
            stringBuilder.append("\n================\n");
        }
        return stringBuilder.toString();
    }

    @GetMapping("/retrieval")
    public String retrieval(String query) {
        SearchRequest searchRequest = SearchRequest.builder()
                .topK(5)
                .query(query)
                .similarityThreshold(0.5)
                .build();
        List<Document> documents = vectorStore.similaritySearch(searchRequest);

        String documentContent = documents.stream()
                .map(Document::getText)
                .collect(Collectors.joining("\n\n=========文档分隔线===========\n\n"));

        PromptTemplate promptTemplate = new PromptTemplate("""
                请基于以下提供的参考文档内容，回答用户的问题。
                如果参考文档中没有相关信息，请直接说明"没有找到相关信息"，不要编造内容。
                
                参考文档:
                {documents}
                
                用户问题: {question}
                """);

        return chatModel.call(promptTemplate.create(Map.of("documents", documentContent, "question", query)))
                .getResult().getOutput().getText();
    }

    /**
     * 通过advisor自动完成从向量数据库中检索的操作
     * @param query
     * @return
     */
    @GetMapping("retrievalAdvisor")
    public String retrievalAdvisor(@RequestParam String query) {
        PromptTemplate promptTemplate = new PromptTemplate("""
                请基于以下提供的参考文档内容，回答用户的问题。
                如果参考文档中没有相关信息，请直接说明"没有找到相关信息"，不要编造内容。
                
                参考文档:
                {question_answer_context}
                
                用户问题: {query}
                """);

        SearchRequest searchRequest = SearchRequest.builder()
                .topK(4)
                .query(query)
                .similarityThreshold(0.7)
//                .filterExpression("fileName == \"kubernates in action\"")
                .build();

        ChatClient chatClient = ChatClient.builder(chatModel)
                .defaultAdvisors(
                    QuestionAnswerAdvisor.builder(vectorStore)
                        .searchRequest(searchRequest)
                        .promptTemplate(promptTemplate)
                        .build()
                ).build();

        return chatClient.prompt()
                .advisors(advisorSpec -> advisorSpec.param("qa_filter_expression", "fileName == \"kubernates in action\""))
                .user(query)
                .call()
                .content();
    }
}
