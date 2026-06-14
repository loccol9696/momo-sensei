package com.example.be.controller;

import com.example.be.dto.request.AiChatRequest;
import com.example.be.dto.response.ApiResponse;
import com.example.be.service.AiService;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/ai")
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class AiController {

    AiService aiService;

    @PostMapping("/chat")
    public ResponseEntity<ApiResponse<String>> chat(@RequestBody AiChatRequest request) {
        String reply = aiService.chat(request);
        return ResponseEntity.ok(ApiResponse.<String>builder()
                .success(true)
                .message("Phản hồi từ AI")
                .data(reply)
                .build());
    }
}
