package com.lzh.service;

public interface AiService {
    String chatWithHealthContext(Integer profileId, String question);

    String generateSummary(String fullChatLog);

    void updateProfileHistory(Integer profileId, String summary);
}
