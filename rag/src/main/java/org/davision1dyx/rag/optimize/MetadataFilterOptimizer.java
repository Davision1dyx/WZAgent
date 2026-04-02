package org.davision1dyx.rag.optimize;

import lombok.extern.slf4j.Slf4j;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.stereotype.Service;

import java.util.Map;
import java.util.function.Consumer;

/**
 * @author Davison
 * @date 2026-04-01
 * @description 元数据过滤优化
 */
@Slf4j
@Service
public class MetadataFilterOptimizer {
    //

    /**
     * 针对元数据中某几个字段进行过滤，使向量检索时不生效
     * usage: SearchRequest.filterExpression()
     * @return
     */
    public String filterExpress() {
        return "fileName = \"kubernate in action\"";
    }

    /**
     * usage: ChatClient.advisor()
     * qa_filter_expression 见QuestionAnswerAdvisor
     */
    public Consumer<ChatClient.AdvisorSpec> advisorSpecConsumer() {
        return advisorSpec -> advisorSpec.params(Map.of("qa_filter_expression", "\"kubernate in action\""));
    }

}
