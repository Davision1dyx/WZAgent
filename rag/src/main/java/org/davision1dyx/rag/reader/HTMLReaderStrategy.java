package org.davision1dyx.rag.reader;

import org.springframework.ai.document.Document;
import org.springframework.ai.reader.jsoup.JsoupDocumentReader;
import org.springframework.ai.reader.jsoup.config.JsoupDocumentReaderConfig;
import org.springframework.core.io.FileSystemResource;
import org.springframework.stereotype.Component;

import java.io.File;
import java.util.List;

/**
 * @author Davison
 * @date 2026-03-29
 * @description html读取策略
 */
@Component
public class HTMLReaderStrategy implements DocumentReaderStrategy{
    @Override
    public boolean support(File file) {
        return file.getName().toLowerCase().endsWith(".html");
    }

    @Override
    public List<Document> read(File file) {
        JsoupDocumentReaderConfig config = JsoupDocumentReaderConfig.builder()
                // 只提取p标签段落
                .selector("p")
                // 文件编码
                .charset("UTF-8")
                // 包含超链接
                .includeLinkUrls(true)
                // 提取meta标签的元数据
                .metadataTags(List.of("author", "date"))
                // 添加自定义元数据
                .additionalMetadata("filename", file.getName())
                .build();
        return new JsoupDocumentReader(new FileSystemResource(file), config).read();
    }
}
