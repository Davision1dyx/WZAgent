package org.davision1dyx.rag.reader;

import org.springframework.ai.document.Document;
import org.springframework.ai.reader.tika.TikaDocumentReader;
import org.springframework.core.io.FileSystemResource;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Component;

import java.io.File;
import java.util.List;

/**
 * @author Davison
 * @date 2026-03-29
 * @description 通用文件读取策略
 */
@Component
public class TikaReaderStrategy implements DocumentReaderStrategy{

    @Override
    public boolean support(File file) {
        String name = file.getName().toLowerCase();
        return name.endsWith(".doc") || name.endsWith(".docx");
    }

    @Override
    public List<Document> read(File file) {
        Resource resource = new FileSystemResource(file);
        return new TikaDocumentReader(resource).get();
    }
}
