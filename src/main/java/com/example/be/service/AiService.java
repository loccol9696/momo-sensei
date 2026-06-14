package com.example.be.service;

import com.example.be.dto.request.AiChatRequest;

public interface AiService {
    String chat(AiChatRequest request);
}
