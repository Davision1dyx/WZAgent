package org.davision1dyx.rag.reader;

import org.springframework.ai.document.Document;
import org.springframework.stereotype.Component;

import java.io.File;
import java.util.List;

/**
 * @author Davison
 * @date 2026-03-29
 * @description 文件读取策略处理器
 */
@Component
public class DocumentReaderHandler {

    private final List<DocumentReaderStrategy> documentReaderStrategies;

    public DocumentReaderHandler(List<DocumentReaderStrategy> documentReaderStrategies) {
        this.documentReaderStrategies = documentReaderStrategies;
    }

    public List<Document> handle(File file) {
        for (DocumentReaderStrategy strategy: documentReaderStrategies) {
            if (strategy.support(file)) {
                return strategy.read(file);
            }
        }
        throw new RuntimeException("can't handle this file type: " + file.getName());
    }
}
