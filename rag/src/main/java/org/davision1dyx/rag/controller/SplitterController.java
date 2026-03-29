package org.davision1dyx.rag.controller;

import com.alibaba.cloud.ai.transformer.splitter.RecursiveCharacterTextSplitter;
import lombok.extern.slf4j.Slf4j;
import org.davision1dyx.rag.reader.DocumentReaderHandler;
import org.davision1dyx.rag.splitter.OverlapParagraphTextSplitter;
import org.davision1dyx.rag.utils.DocumentCleaner;
import org.springframework.ai.document.Document;
import org.springframework.ai.transformer.splitter.TokenTextSplitter;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.io.File;
import java.util.List;

/**
 * @author Davison
 * @date 2026-03-29
 * @description 文本分片接口
 */
@Slf4j
@RestController
@RequestMapping("/process/rag/split")
public class SplitterController {

    private final DocumentReaderHandler readerHandler;

    public SplitterController(DocumentReaderHandler readerHandler) {
        this.readerHandler = readerHandler;
    }

    @GetMapping("/tokenSplit")
    public String tokenSplit(@RequestParam String path) {
        File file = new File(path);
        List<Document> documents = readerHandler.handle(file);
        documents = DocumentCleaner.cleanDocuments(documents);
        StringBuilder sb = new StringBuilder();
        TokenTextSplitter tokenTextSplitter = new TokenTextSplitter(80, 35, 5, 10000, true);
        List<Document> splited = tokenTextSplitter.split(documents);

        for (Document document: splited) {
            sb.append(document.getText()).append("\n=====================\n");
            log.info("text: {}", document.getText());
            log.info("metadata: {}", document.getMetadata());
            log.info("======================");
        }
        return sb.toString();
    }

    @GetMapping("/overlapSplit")
    public String overlapSplit(@RequestParam String path) {
        File file = new File(path);
        List<Document> documents = readerHandler.handle(file);
        documents = DocumentCleaner.cleanDocuments(documents);
        StringBuilder sb = new StringBuilder();
        OverlapParagraphTextSplitter overlapParagraphTextSplitter = new OverlapParagraphTextSplitter(80, 10);
        List<Document> splited = overlapParagraphTextSplitter.split(documents);
        for (Document document: splited) {
            sb.append(document.getText()).append("\n=====================\n");
            log.info("text: {}", document.getText());
            log.info("metadata: {}", document.getMetadata());
            log.info("======================");
        }
        return sb.toString();
    }

    @GetMapping("/recursiveSplit")
    public String recursiveSplit(@RequestParam String path) {
        File file = new File(path);
        List<Document> documents = readerHandler.handle(file);
//        documents = DocumentCleaner.cleanDocuments(documents);
        StringBuilder sb = new StringBuilder();

        RecursiveCharacterTextSplitter recursiveCharacterTextSplitter = new RecursiveCharacterTextSplitter(100);
        List<Document> splited = recursiveCharacterTextSplitter.split(documents);

        for (Document document: splited) {
            sb.append(document.getText()).append("\n=====================\n");
            log.info("text: {}", document.getText());
            log.info("metadata: {}", document.getMetadata());
            log.info("======================");
        }
        return sb.toString();
    }
}
