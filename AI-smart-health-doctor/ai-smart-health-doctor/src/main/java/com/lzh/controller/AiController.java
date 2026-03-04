package com.lzh.controller;

import com.lzh.pojo.Result;
import com.lzh.service.AiService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Flux;

import java.util.List;
import java.util.Map;

@CrossOrigin
@RestController
@Slf4j
@RequestMapping("/api/ai")
public class AiController {

    @Autowired
    private AiService aiService;

    /**
     * 流式聊天接口 (完美升级版)
     * 注意：这里必须加上 produces = MediaType.TEXT_EVENT_STREAM_VALUE
     */
    @PostMapping(value = "/chat/stream", produces = MediaType.TEXT_EVENT_STREAM_VALUE)
    public Flux<String> chatStream(@RequestBody Map<String, Object> params) {
        Integer profileId = (Integer) params.get("profileId");

        List<Map<String, Object>> messages = (List<Map<String, Object>>) params.get("messages");

        //流式输出，不返回Result
        return aiService.chatStreamWithHealthContext(profileId, messages);
    }


    /**
     * 保存并生成总结接口
     */

    @PostMapping("/save-summary")
    public Result saveSummary(@RequestBody Map<String, Object> params) {
        Integer profileId = (Integer) params.get("profileId");
        String fullChatLog = (String) params.get("fullChatLog"); // 前端传来的整段对话

        // 调用 AI 生成一段精简的病情总结
        String summary = aiService.generateSummary(fullChatLog);

        // 将总结覆盖写入数据库
        aiService.updateProfileHistory(profileId, summary);

        return Result.success(summary);
    }
}