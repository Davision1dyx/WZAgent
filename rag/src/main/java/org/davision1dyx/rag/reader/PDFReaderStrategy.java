package org.davision1dyx.rag.reader;

import org.springframework.ai.document.Document;
import org.springframework.ai.reader.ExtractedTextFormatter;
import org.springframework.ai.reader.pdf.PagePdfDocumentReader;
import org.springframework.ai.reader.pdf.ParagraphPdfDocumentReader;
import org.springframework.ai.reader.pdf.config.PdfDocumentReaderConfig;
import org.springframework.core.io.FileSystemResource;
import org.springframework.stereotype.Component;

import java.io.File;
import java.util.List;

/**
 * @author Davison
 * @date 2026-03-29
 * @description pdf读取策略
 */
@Component
public class PDFReaderStrategy implements DocumentReaderStrategy{
    @Override
    public boolean support(File file) {
        return file.getName().toLowerCase().endsWith(".pdf");
    }

    @Override
    public List<Document> read(File file) {
        PdfDocumentReaderConfig config = PdfDocumentReaderConfig.builder()
                .withPageTopMargin(50)         // 忽略顶部50个单位的页眉
                .withPageBottomMargin(50)      // 忽略底部50个单位的页脚
                .withPagesPerDocument(1)       // 每一页作为一个 Document
                .withPageExtractedTextFormatter(new ExtractedTextFormatter.Builder()
                        .withNumberOfTopTextLinesToDelete(0) // 每页再额外删掉前0行
                        .build())
                .build();

//        return new ParagraphPdfDocumentReader(new FileSystemResource(file), config).read();
        return new PagePdfDocumentReader(new FileSystemResource(file), config).read();
    }
}
