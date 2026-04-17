package org.davison1dyx.springai.config;

import org.davison1dyx.springai.service.TimeService;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.function.Function;

/**
 * FunctionCallConfiguration
 *
 * @author 229291
 * @since 2026/3/24 11:24
 */
@Configuration
public class FunctionCallConfiguration {
    @Bean
    public Function<TimeService.TimeRequest, TimeService.TimeResponse> getTimeFunction(TimeService timeService) {
        return timeService::getTimeByZoneId;
    }
}