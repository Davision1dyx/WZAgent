package org.davision1dyx.rag.reader;

import org.springframework.ai.document.Document;
import org.springframework.ai.reader.TextReader;
import org.springframework.core.io.FileSystemResource;
import org.springframework.stereotype.Component;

import java.io.File;
import java.util.List;

/**
 * @author Davison
 * @date 2026-03-29
 * @description 文本读取策略
 */
@Component
public class TEXTReaderStrategy implements DocumentReaderStrategy{
    @Override
    public boolean support(File file) {
        return file.getName().toLowerCase().endsWith(".txt");
    }

    @Override
    public List<Document> read(File file) {
        TextReader reader = new TextReader(new FileSystemResource(file));
        return reader.read();
    }
}
