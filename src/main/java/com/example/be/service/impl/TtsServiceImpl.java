package com.example.be.service.impl;

import com.example.be.service.TtsService;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.http.ResponseEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;

@Service
public class TtsServiceImpl implements TtsService {
    
    private final RestTemplate restTemplate = new RestTemplate();

    @Override
    public byte[] getTtsAudio(String text, String lang) {
        if (text == null || text.trim().isEmpty()) {
            return new byte[0];
        }
        
        String targetLang = (lang != null && !lang.trim().isEmpty()) ? lang : "ja";
        try {
            System.out.println("[TTS] Original input: \"" + text + "\"");
            String cleanText = fixEncoding(text.trim());
            System.out.println("[TTS] Decoded & fixed text: \"" + cleanText + "\"");

            String encodedText = URLEncoder.encode(cleanText, StandardCharsets.UTF_8);
            System.out.println("[TTS] Final Encoded text to Google: \"" + encodedText + "\"");
            String urlStr = String.format("https://translate.google.com/translate_tts?ie=UTF-8&client=tw-ob&tl=%s&q=%s", targetLang, encodedText);
            java.net.URI uri = java.net.URI.create(urlStr);
            
            HttpHeaders headers = new HttpHeaders();
            headers.set("User-Agent", "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/120.0.0.0 Safari/537.36");
            HttpEntity<Void> entity = new HttpEntity<>(headers);
            
            ResponseEntity<byte[]> response = restTemplate.exchange(uri, HttpMethod.GET, entity, byte[].class);
            if (response.getStatusCode().is2xxSuccessful() && response.getBody() != null) {
                return response.getBody();
            }
        } catch (Exception e) {
            System.err.println("TTS Service Error: " + e.getMessage());
        }
        return new byte[0];
    }

    private String fixEncoding(String text) {
        if (text == null) return null;
        
        // Check if the string contains any character > 255 (which indicates it's already decoded to Unicode)
        boolean hasMultibyte = false;
        for (int i = 0; i < text.length(); i++) {
            if (text.charAt(i) > 255) {
                hasMultibyte = true;
                break;
            }
        }
        
        if (!hasMultibyte) {
            try {
                // If all characters are <= 255, it might have been incorrectly decoded as ISO-8859-1.
                // Convert it back to raw bytes and decode as UTF-8.
                byte[] bytes = text.getBytes(StandardCharsets.ISO_8859_1);
                String decoded = new String(bytes, StandardCharsets.UTF_8);
                
                // If the decoded string contains percent-encoded sequences, decode it
                if (decoded.contains("%")) {
                    try {
                        decoded = java.net.URLDecoder.decode(decoded, StandardCharsets.UTF_8);
                    } catch (Exception e) {
                        // ignore
                    }
                }
                return decoded;
            } catch (Exception e) {
                return text;
            }
        }
        
        // If it already has characters > 255, it was decoded as UTF-8.
        // We still check if it contains '%' in case it was only partially decoded.
        if (text.contains("%")) {
            try {
                return java.net.URLDecoder.decode(text, StandardCharsets.UTF_8);
            } catch (Exception e) {
                // ignore
            }
        }
        return text;
    }
}
