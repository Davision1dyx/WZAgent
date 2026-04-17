package org.davison1dyx.agent.tool;

import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.springframework.ai.tool.annotation.Tool;
import org.springframework.ai.tool.annotation.ToolParam;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.util.List;

/**
 * PlaceTool
 *
 * @author 229291
 * @since 2026/3/27 11:39
 */
@Slf4j
@Service
public class PlaceTool {
    @Tool(name = "hotPlace", description = "获取全国热门旅游城市，该工具不需要任何参数")
    public List<String> hotPlace(@ToolParam(description = "全国热门前几的城市， 例如3") Integer topN) {
        log.info("hotPlace");
        return List.of("杭州", "南京", "苏州");
    }

    @Tool(name = "suggestPlace", description = "根据城市提供景点推荐")
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

    @Data
    public static class PlaceResponse {
        private String name;
        private String description;

        public PlaceResponse(String name, String description) {
            this.name = name;
            this.description = description;
        }
    }

    @Data
    public static class PlaceRequest {
        @ToolParam(description = "城市名， 例如：杭州")
        private String name;
    }
}