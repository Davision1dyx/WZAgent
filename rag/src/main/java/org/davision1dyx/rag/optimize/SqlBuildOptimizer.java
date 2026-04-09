package org.davision1dyx.rag.optimize;

import lombok.extern.slf4j.Slf4j;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.model.ChatModel;
import org.springframework.ai.chat.prompt.PromptTemplate;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.Map;

/**
 * @author Davison
 * @date 2026-04-09
 * @description 查询构造优化器
 */
@Slf4j
@Service
public class SqlBuildOptimizer {

    private final ChatClient chatClient;

    private static final String SQL_BUILD_PROMPT = """
            # 角色
            你是一个SQL专家。请根据以下表结构信息将用户问题转换为SQL查询语句。特别注意，你只能查询，不能做修改、删除等操作。
            
            # 表结构信息
            
            {tables}
            
            # 用户问题
            
            {user_query}
            
            # 要求
            1. 只返回SQL语句，不需要包含任何解释和说明
            2. 确保SQL语法正确
            3. 使用上下文中提供的表名和字段名
            4. 如果根据所提供的表无法做查询，请直接返回空字符串""
            
            # 其他说明
            今天是:{today}
            """;

    public SqlBuildOptimizer(ChatModel chatModel) {
        chatClient = ChatClient.builder(chatModel)
                .build();
    }

    public String sqlBuild(String tables, String query) {
        PromptTemplate promptTemplate = new PromptTemplate(SQL_BUILD_PROMPT);
        promptTemplate.add("tables", tables);
        promptTemplate.add("user_query", query);
        promptTemplate.add("today", new Date().toString());
        return chatClient.prompt(promptTemplate.create())
                .call().content();
    }
}
