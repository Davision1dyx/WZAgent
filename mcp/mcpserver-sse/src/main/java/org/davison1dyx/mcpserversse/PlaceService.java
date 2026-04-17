package org.davison1dyx.mcpserversse;

import lombok.extern.slf4j.Slf4j;
import org.davison1dyx.mcpserversse.entity.PlaceRequest;
import org.davison1dyx.mcpserversse.entity.PlaceResponse;
import org.springframework.ai.tool.annotation.Tool;
import org.springframework.ai.tool.annotation.ToolParam;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

/**
 * PlaceService
 *
 * @author 229291
 * @since 2026/3/25 10:58
 */
@Slf4j
@Service
public class PlaceService {

    @Tool(name = "suggestPlace", description = "根据城市提供景点推荐", returnDirect = true)
    public PlaceResponse suggestPlace(PlaceRequest request) {
        log.info("query request: {}", request);
        if (request == null || !StringUtils.hasText(request.getName())) {
            throw new RuntimeException();
        }

        return switch (request.getName()) {
            case "杭州" -> new PlaceResponse("杭州", "杭州西湖, 中国十大名胜之一，以秀丽的湖光山色和众多的名胜古迹闻名于世，被誉为‘人间天堂’。");
            case "苏州" -> new PlaceResponse("苏州", "虎丘, 享有‘吴中第一名胜’美誉，以‘塔影倒映’和‘剑池’奇景闻名，是春秋时期吴王阖闾墓地所在。");
            case "南京" -> new PlaceResponse("南京", "栖霞山, 以秋日红枫和千年古刹栖霞寺闻名，被誉为“金陵第一明秀山”，是赏枫和礼佛的好去处。");
            default -> throw new RuntimeException();
        };
    }
}