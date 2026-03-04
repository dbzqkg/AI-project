package com.lzh.pojo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ChatRequest {
    private String model;
    private boolean stream = false; // 流式开关
    private List<Map<String, Object>> messages = new ArrayList<>();

    public boolean isStream() { return stream; }

    public void addMessage(String role, Object content) {
        Map<String, Object> message = new HashMap<>();
        message.put("role", role);
        message.put("content", content);
        this.messages.add(message);
    }
}