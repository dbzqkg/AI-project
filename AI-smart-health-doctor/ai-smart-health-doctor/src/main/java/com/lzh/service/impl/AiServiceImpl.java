package com.lzh.service.impl;

import com.lzh.mapper.AiMapper;
import com.lzh.mapper.LoginMapper;
import com.lzh.pojo.ChatRequest;
import com.lzh.pojo.PatientProfile;
import com.lzh.service.AiService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

@Service
public class AiServiceImpl implements AiService {

    @Autowired
    private AiMapper aiMapper;
    @Value("${ai.gpt.base-url}")
    private String baseUrl;
    @Value("${ai.gpt.api-key}")
    private String apiKey;
    @Value("${ai.gpt.model}")
    private String model;

    @Autowired
    private RestTemplate restTemplate;
    @Autowired
    private LoginMapper loginMapper;

    @Override
    public String chatWithHealthContext(Integer profileId, String question) {
        // 1. 获取背景 (创新点：通过既往病史降低幻觉)
        PatientProfile profile = loginMapper.getProfileById(profileId);

        // 2. 构造 System Prompt
        String systemMsg = String.format("你是一位AI医生助手。患者:%s, 性别:%s, 年龄:%d, 既往病史:%s。请基于此背景回答，不要给出处方药建议。",
                profile.getRealName(), profile.getGender(), profile.getAge(), profile.getMedicalHistory());

        // 3. 构建请求体 (使用你已有的 ChatRequest 类)
        ChatRequest request = new ChatRequest();
        request.setModel(model);
        request.addMessage("system", systemMsg);
        request.addMessage("user", question);

        // 4. 发送 HTTP POST 请求
        HttpHeaders headers = new HttpHeaders();
        headers.setBearerAuth(apiKey); // 注入环境变量中的 API Key
        headers.setContentType(MediaType.APPLICATION_JSON);
        HttpEntity<ChatRequest> entity = new HttpEntity<>(request, headers);

        return restTemplate.postForObject(baseUrl + "/chat/completions", entity, String.class);
    }

    @Override
    public String generateSummary(String fullChatLog) {
        ChatRequest request = new ChatRequest();
        request.addMessage("system", "请根据之前所有对话，总结患者所有病情变化和医嘱，直接给出文字，详细但简明扼要，不要太长或者过于冗余");
        request.addMessage("user", fullChatLog);

        return "AI 总结的结果";
    }

    @Override
    public void updateProfileHistory(Integer profileId, String summary) {
            aiMapper.updateProfileHistory(profileId, summary);
    }
}

