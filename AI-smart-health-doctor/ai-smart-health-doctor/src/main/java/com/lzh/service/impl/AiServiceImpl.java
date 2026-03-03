package com.lzh.service.impl;

import com.alibaba.fastjson2.JSON;
import com.alibaba.fastjson2.JSONArray;
import com.alibaba.fastjson2.JSONObject;
import com.lzh.mapper.AiMapper;
import com.lzh.mapper.LoginMapper;
import com.lzh.pojo.ChatRequest;
import com.lzh.pojo.PatientProfile;
import com.lzh.service.AiService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Flux;

import java.util.List;
import java.util.Map;

@Slf4j
@Service
public class AiServiceImpl implements AiService {

    @Autowired
    private AiMapper aiMapper;

    @Autowired
    private LoginMapper loginMapper;

    @Value("${ai.gpt.base-url}")
    private String baseUrl;
    @Value("${ai.gpt.api-key}")
    private String apiKey;
    @Value("${ai.gpt.model}")
    private String model;

    // WebFlux 的客户端，用于发送流式请求
    private final WebClient webClient = WebClient.create();

    @Override
    public Flux<String> chatStreamWithHealthContext(Integer profileId, List<Map<String, String>> historyMessages) {
        PatientProfile profile = loginMapper.getProfileById(profileId);
        String oldSummary = (profile != null && profile.getAiSummary() != null) ? profile.getAiSummary() : "无";

        String systemMsg = String.format(
                "你是一位专业的AI健康顾问。当前患者：姓名:%s, 性别:%s, 年龄:%d。既往病史:%s。上次的总结记录:%s。\n请基于以上信息进行回答，使用纯文本。",
                profile != null ? profile.getRealName() : "未知",
                profile != null ? profile.getGender() : "未知",
                profile != null ? profile.getAge() : 0,
                (profile != null && profile.getMedicalHistory() != null) ? profile.getMedicalHistory() : "无",
                oldSummary
        );

        ChatRequest request = new ChatRequest();
        request.setModel(model);
        request.setStream(true); // 开启流式打字机效果！

        request.addMessage("system", systemMsg);
        if (historyMessages != null) {
            request.getMessages().addAll(historyMessages);
        }

        return webClient.post()
                .uri(baseUrl + "/chat/completions")
                .header(HttpHeaders.AUTHORIZATION, "Bearer " + apiKey)
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(request)
                .retrieve()
                .bodyToFlux(String.class)
                .map(this::parseOpenAIStreamChunk) // 过滤掉杂质
                .filter(str -> !str.isEmpty());
    }

    /**
     * 过滤网：把大模型返回的 JSON 外壳剥掉，只留里面的文字
     */
    private String parseOpenAIStreamChunk(String chunk) {
        if (chunk == null || chunk.contains("[DONE]")) {
            return "";
        }
        try {
            // 切掉前面自带的 "data: "
            String jsonStr = chunk.replaceFirst("^data:\\s*", "").trim();
            if (jsonStr.isEmpty()) return "";

            JSONObject json = JSON.parseObject(jsonStr);
            JSONArray choices = json.getJSONArray("choices");
            if (choices != null && !choices.isEmpty()) {
                JSONObject delta = choices.getJSONObject(0).getJSONObject("delta");
                if (delta != null && delta.containsKey("content")) {
                    return delta.getString("content");
                }
            }
        } catch (Exception e) {
            log.error("解析流式响应失败: " + chunk, e);
        }
        return "";
    }

    @Override
    public String generateSummary(String fullChatLog) {
        ChatRequest request = new ChatRequest();
        request.setModel(model);
        request.setStream(false);
        request.addMessage("system", "你是一个医疗总结助手。请总结以下对话，提取患者症状和医生建议。纯文字输出，简明扼要。");
        request.addMessage("user", fullChatLog);

        try {
            String responseJson = webClient.post()
                    .uri(baseUrl + "/chat/completions")
                    .header(HttpHeaders.AUTHORIZATION, "Bearer " + apiKey)
                    .contentType(MediaType.APPLICATION_JSON)
                    .bodyValue(request)
                    .retrieve()
                    .bodyToMono(String.class)
                    .block(); // 阻塞等待全部结果

            JSONObject json = JSON.parseObject(responseJson);
            return json.getJSONArray("choices").getJSONObject(0).getJSONObject("message").getString("content");
        } catch (Exception e) {
            log.error("生成总结失败", e);
            return "生成总结失败";
        }
    }

    @Override
    public void updateProfileHistory(Integer profileId, String summary) {
        aiMapper.updateProfileHistory(profileId, summary);
    }
}