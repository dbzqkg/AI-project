package com.lzh.controller;

import com.lzh.mapper.AiMapper;
import com.lzh.pojo.Result;
import com.lzh.service.AiService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
@Slf4j
@RequestMapping("/api/ai")
public class AiController {
    @Autowired
    private AiService aiService;


    @PostMapping("/save-summary")
    public Result saveSummary(@RequestBody Map<String, Object> params) {
        Integer profileId = (Integer) params.get("profileId");
        String fullChatLog = (String) params.get("fullChatLog"); // 前端传来的整段对话

        // 1. 调用 AI 生成一段精简的病情总结
        String summary = aiService.generateSummary(fullChatLog);

        // 2. 将总结覆盖写入数据库
        aiService.updateProfileHistory(profileId, summary);

        return Result.success(summary);
    }
}
