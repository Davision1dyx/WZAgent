package org.davison1dyx.springai.tool;

import org.davison1dyx.springai.service.TimeService;
import org.springframework.ai.tool.annotation.Tool;
import org.springframework.ai.tool.annotation.ToolParam;

/**
 * TimeTool
 *
 * @author 229291
 * @since 2026/3/24 11:42
 */
public class TimeTool {

    @Tool(name = "getTimeByZoneId", description = "通过时区id获取对应时间")
    public TimeService.TimeResponse getTimeByZoneId(@ToolParam(description = "时区id，比如Asia/Shanghai") String zoneId) {
        System.out.println("get time zoneId: " + zoneId);
//        ZoneId zid = ZoneId.of(request.zoneId);
//        ZonedDateTime zonedDateTime = ZonedDateTime.now(zid);
//        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss z");
//        return new TimeResponse(zonedDateTime.format(formatter));
        return new TimeService.TimeResponse("2025-10-10 13:13:13");
    }
}