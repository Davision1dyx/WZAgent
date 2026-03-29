package org.davision1dyx.rag.controller;

import lombok.extern.slf4j.Slf4j;
import org.davision1dyx.rag.reader.DocumentReaderHandler;
import org.springframework.ai.document.Document;
import org.springframework.ai.embedding.BatchingStrategy;
import org.springframework.ai.embedding.EmbeddingModel;
import org.springframework.ai.embedding.EmbeddingOptions;
import org.springframework.ai.embedding.TokenCountBatchingStrategy;
import org.springframework.ai.vectorstore.VectorStore;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.io.File;
import java.util.List;

/**
 * @author Davison
 * @date 2026-03-29
 * @description embed接口
 */
@Slf4j
@RestController
@RequestMapping("/process/rag/embed")
public class EmbeddingController {

    private final DocumentReaderHandler readerHandler;
    private final EmbeddingModel embeddingModel;
    private final VectorStore vectorStore;

    public EmbeddingController(DocumentReaderHandler readerHandler, EmbeddingModel embeddingModel, VectorStore vectorStore) {
        this.readerHandler = readerHandler;
        this.embeddingModel = embeddingModel;
        this.vectorStore = vectorStore;
    }

    @GetMapping("/embedModel")
    public List<float[]> embedModel() {
        List<Document> documents = readerHandler.handle(new File("/Users/dyxfight/Document/agentLearning/RAG材料/Java八股文介绍.md"));
        List<float[]> embed = embeddingModel.embed(documents, EmbeddingOptions.builder().build(), new TokenCountBatchingStrategy());
        return embed;
    }

    @GetMapping("embedStore")
    public void embedStore() {
        List<Document> documents = readerHandler.handle(new File("/Users/dyxfight/Document/agentLearning/RAG材料/Java八股文介绍.md"));
        vectorStore.add(documents);
    }
}
