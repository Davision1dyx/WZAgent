package org.davison1dyx.springai.controller;

import org.davison1dyx.springai.model.Place;
import org.springframework.ai.anthropic.AnthropicChatModel;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.client.advisor.SimpleLoggerAdvisor;
import org.springframework.ai.chat.prompt.ChatOptions;
import org.springframework.ai.chat.prompt.PromptTemplate;
import org.springframework.ai.converter.BeanOutputConverter;
import org.springframework.ai.converter.MapOutputConverter;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Map;

/**
 * ChatStructController
 *
 * @author 229291
 * @since 2026/3/23 20:06
 */
@RestController
@RequestMapping("/process/structChat")
public class ChatStructController implements InitializingBean {

    private ChatClient chatClient;

    private final AnthropicChatModel chatModel;

    public ChatStructController(AnthropicChatModel chatModel) {
        this.chatModel = chatModel;
    }

    @GetMapping("/call")
    public String call(@RequestParam String city) {
        PromptTemplate promptTemplate = PromptTemplate.builder().template("请向我推荐一下{city}的景点，输出格式:{format}").build();

        BeanOutputConverter<Place> converter = new BeanOutputConverter<>(Place.class);

        String resp = chatClient.prompt(promptTemplate.create(Map.of("format", converter.getFormat(), "city", city))).call().content();
        Place place = converter.convert(resp);
        return place.toString();
    }

    @GetMapping("/convert")
    public String convert(@RequestParam String city) {
        PromptTemplate promptTemplate = PromptTemplate.builder().template("请向我推荐一下{city}的景点").build();
        Place place = chatClient.prompt(promptTemplate.create(Map.of("city", city))).call().entity(Place.class);
        return place.toString();
    }

    @GetMapping("/convertList")
    public List<Place> convertList(@RequestParam String city) {
        PromptTemplate promptTemplate = PromptTemplate.builder().template("请向我推荐一下{city}的景点").build();
        List<Place> place = chatClient.prompt(promptTemplate.create(Map.of("city", city))).call().entity(new ParameterizedTypeReference<List<Place>>() {
        });
        return place;
    }

    @GetMapping("/convertMap")
    public Map<String, Object> convertMap(@RequestParam String city) {
        PromptTemplate promptTemplate = PromptTemplate.builder().template("请向我推荐一下{city}的景点").build();
        Map<String, Object> map = chatClient.prompt(promptTemplate.create(Map.of("city", city))).call().entity(new MapOutputConverter());
        return map;
    }


    @Override
    public void afterPropertiesSet() throws Exception {
        chatClient = ChatClient.builder(chatModel)
                .defaultOptions(ChatOptions.builder().maxTokens(1000).build())
                .defaultAdvisors(new SimpleLoggerAdvisor()).build();
    }
}