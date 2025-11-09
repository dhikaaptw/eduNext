package com.example.edunext;

public class ChatMessage {
    private final String role;     // "user" atau "assistant"
    private final String content;  // isi pesan

    public ChatMessage(String role, String content) {
        this.role = role;
        this.content = content;
    }

    public String getRole() {
        return role;
    }

    public String getContent() {
        return content;
    }
}
