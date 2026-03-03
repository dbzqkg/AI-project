package com.lzh.service;

import reactor.core.publisher.Flux;
import java.util.List;
import java.util.Map;

public interface AiService {

    // 生成总结
    String generateSummary(String fullChatLog);

    // 保存总结到数据库
    void updateProfileHistory(Integer profileId, String summary);

    // 【新增】流式聊天 (接收历史记录数组)
    Flux<String> chatStreamWithHealthContext(Integer profileId, List<Map<String, String>> historyMessages);
}