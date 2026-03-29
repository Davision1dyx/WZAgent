package org.davision1dyx.rag.controller;

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
 * @description 文件读取接口
 */
@Slf4j
@RestController
@RequestMapping("/process/rag/reader")
public class FileReadController {

    private final DocumentReaderHandler readerHandler;

    public FileReadController(DocumentReaderHandler readerHandler) {
        this.readerHandler = readerHandler;
    }

    @GetMapping("/read")
    public String read(@RequestParam String path) {
        File file = new File(path);
        List<Document> documents = readerHandler.handle(file);
//        documents = DocumentCleaner.cleanDocuments(documents);
        StringBuilder sb = new StringBuilder();
        for (Document document: documents) {
            sb.append(document.getText()).append("\n=====================\n");
            log.info("text: {}", document.getText());
            log.info("metadata: {}", document.getMetadata());
            log.info("======================");
        }
        return sb.toString();
    }
}
