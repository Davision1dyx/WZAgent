package org.davision1dyx.rag.reader;

import org.springframework.ai.document.Document;

import java.io.File;
import java.util.List;

/**
* @author Davison
* @date 2026-03-29
* @description 文档读取策略模式
*/
public interface DocumentReaderStrategy {

    boolean support(File file);

    List<Document> read(File file);
}
