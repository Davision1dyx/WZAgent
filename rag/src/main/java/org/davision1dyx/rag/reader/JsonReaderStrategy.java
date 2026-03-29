package org.davision1dyx.rag.reader;

import com.alibaba.fastjson2.JSON;
import com.alibaba.fastjson2.JSONArray;
import com.alibaba.fastjson2.JSONObject;
import org.springframework.ai.document.Document;
import org.springframework.stereotype.Component;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author Davison
 * @date 2026-03-29
 * @description json读取策略
 */
@Component
public class JsonReaderStrategy implements DocumentReaderStrategy {
    @Override
    public boolean support(File file) {
        return file.getName().toLowerCase().endsWith(".json");
    }

    @Override
    public List<Document> read(File file) {
        List<Document> documents = new ArrayList<>();
        try {
            String content = Files.readString(file.toPath());
            Object jsonObject = JSON.parse(content);

            if (jsonObject instanceof JSONArray jsonArray) {
                // JSON 数组，每个元素作为一个文档
                for (int i = 0; i < jsonArray.size(); i++) {
                    JSONObject item = jsonArray.getJSONObject(i);
                    documents.add(createDocument(item, file.getName(), i));
                }
            } else if (jsonObject instanceof JSONObject jsonObj) {
                // 单个 JSON 对象
                documents.add(createDocument(jsonObj, file.getName(), null));
            }
        } catch (IOException e) {
            throw new RuntimeException("Failed to read JSON file: " + file.getAbsolutePath(), e);
        }
        return documents;
    }

    private Document createDocument(JSONObject jsonObject, String filename, Integer index) {
        Map<String, Object> metadata = new HashMap<>();
        metadata.put("filename", filename);
        if (index != null) {
            metadata.put("index", index);
        }

        // 将 JSON 内容转换为字符串作为文档内容
        String text = jsonObject.toJSONString();

        return new Document(text, metadata);
    }
}
