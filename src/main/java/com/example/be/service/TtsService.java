package com.example.be.service;

public interface TtsService {
    byte[] getTtsAudio(String text, String lang);
}
