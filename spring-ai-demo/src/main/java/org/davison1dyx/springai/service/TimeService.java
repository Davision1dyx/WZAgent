package org.davison1dyx.springai.service;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyDescription;
import org.springframework.stereotype.Service;

import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;

/**
 * TimeService
 *
 * @author 229291
 * @since 2026/3/24 10:54
 */
@Service
public class TimeService {

    public TimeResponse getTimeByZoneId(TimeRequest request) {
        System.out.println("get time request: " + request);
//        ZoneId zid = ZoneId.of(request.zoneId);
//        ZonedDateTime zonedDateTime = ZonedDateTime.now(zid);
//        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss z");
//        return new TimeResponse(zonedDateTime.format(formatter));
        return new TimeResponse("2025-12-12 12:12:12");
    }

    public record TimeRequest(@JsonProperty(required = true, value = "zoneId") @JsonPropertyDescription("时区， 比如 Asia/Shanghai") String zoneId) {

    }

    public record TimeResponse(@JsonProperty(required = true, value = "time") @JsonPropertyDescription("时间，格式为yyyy-MM-dd HH:mm:ss z") String time) {

    }
}