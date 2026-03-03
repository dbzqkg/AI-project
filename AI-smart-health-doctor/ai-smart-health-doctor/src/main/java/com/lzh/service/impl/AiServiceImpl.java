package com.lzh.service.impl;

import com.alibaba.fastjson2.JSON;
import com.alibaba.fastjson2.JSONObject;
import com.lzh.mapper.AiMapper;
import com.lzh.mapper.LoginMapper;
import com.lzh.pojo.ChatRequest;
import com.lzh.pojo.PatientProfile;
import com.lzh.service.AiService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

@Slf4j
@Service
public class AiServiceImpl implements AiService {

    @Autowired
    private AiMapper aiMapper;

    @Autowired
    private LoginMapper loginMapper;

    // 用来发 HTTP 请求的工具
    @Autowired
    private RestTemplate restTemplate;

    // 从 application.yml 读取的配置
    @Value("${ai.gpt.base-url}")
    private String baseUrl;
    @Value("${ai.gpt.api-key}")
    private String apiKey;
    @Value("${ai.gpt.model}")
    private String model;


    @Override
    public String chatWithHealthContext(Integer profileId, String question) {
        // 1. 去数据库查这个人的档案
        PatientProfile profile = loginMapper.getProfileById(profileId);
        if (profile == null) {
            return "未找到就诊人信息。";
        }

        // 2. 拼接强大的系统提示词 (包含以前的总结)
        String oldSummary = (profile.getAiSummary() != null && !profile.getAiSummary().trim().isEmpty())
                ? profile.getAiSummary() : "无";

        String systemMsg = String.format(
                "你是一位专业的AI健康顾问。当前患者信息：姓名:%s, 性别:%s, 年龄:%d。既往病史:%s。以前的就诊总结记录:%s。\n请基于上述信息，回答患者的问题。严禁开具具体的处方药。",
                profile.getRealName(), profile.getGender(), profile.getAge(),
                profile.getMedicalHistory() != null ? profile.getMedicalHistory() : "无",
                oldSummary
        );

        // 3. 准备发给大模型的数据
        ChatRequest request = new ChatRequest();
        request.setModel(model);
        request.addMessage("system", systemMsg); // 告诉 AI 它是谁，面对的是谁
        request.addMessage("user", question);    // 真正的提问

        // 4. 发送请求并返回结果
        return sendRequestToAi(request);
    }

    @Override
    public String generateSummary(String fullChatLog) {
        ChatRequest request = new ChatRequest();
        request.setModel(model);
        request.addMessage("system", "你是一个医疗文档总结助手。请根据以下对话记录，总结患者过往及现在所有的症状和医生（你）的建议。纯文字输出，一定要简明扼要。");
        request.addMessage("user", fullChatLog);

        return sendRequestToAi(request);
    }

    @Override
    public void updateProfileHistory(Integer profileId, String summary) {
        aiMapper.updateProfileHistory(profileId, summary);
    }

    /**
     * 抽取出来的公共方法：真正去调用中转站接口，并解析结果
     */
    private String sendRequestToAi(ChatRequest request) {
        // 1. 准备 HTTP 请求头（包含 API Key）
        try {
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);
            headers.set("Authorization", "Bearer " + apiKey);

            // 打包数据
            HttpEntity<ChatRequest> entity = new HttpEntity<>(request, headers);

            // 发起 POST 请求，请求地址比如是
            String url = baseUrl + "/chat/completions";
            String responseJson = restTemplate.postForObject(url, entity, String.class);

            // 使用 Fastjson2 解析返回的一大串 JSON
            JSONObject jsonObject = JSON.parseObject(responseJson);
            if (jsonObject != null && jsonObject.containsKey("choices")) {
                // 按照 OpenAI 格式，提取最终的回答内容
                String content = jsonObject.getJSONArray("choices")
                        .getJSONObject(0)
                        .getJSONObject("message")
                        .getString("content");
                return content;
            }
            return "大模型没有返回有效的回答。";

        } catch (Exception e) {
            log.error("调用大模型出错", e);
            return "对不起，网络连接异常或大模型服务出错：" + e.getMessage();
        }
    }
}