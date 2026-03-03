package com.lzh.pojo;

import lombok.Data;
import java.util.*;

@Data
public class ChatRequest {
    private String model = "gpt-4"; // 或者你中转站支持的 5.1 模型名
    private List<Map<String, String>> messages = new ArrayList<>();
    private boolean stream = false;

    public void addMessage(String role, String content) {
        Map<String, String> msg = new HashMap<>();
        msg.put("role", role);
        msg.put("content", content);
        this.messages.add(msg);
    }
}
