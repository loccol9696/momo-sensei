package com.example.be.controller;

import com.example.be.service.TtsService;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/tts")
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class TtsController {

    TtsService ttsService;

    @GetMapping
    public ResponseEntity<byte[]> getTts(
            @RequestParam("text") String text,
            @RequestParam(value = "lang", required = false) String lang) {
        
        byte[] audioBytes = ttsService.getTtsAudio(text, lang);
        
        if (audioBytes == null || audioBytes.length == 0) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.parseMediaType("audio/mpeg"));
        headers.setContentLength(audioBytes.length);
        headers.set("Cache-Control", "no-cache, no-store, must-revalidate"); // Disable cache to apply fixes immediately
        
        return new ResponseEntity<>(audioBytes, headers, HttpStatus.OK);
    }
}
