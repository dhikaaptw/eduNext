package com.example.edunext;

import java.util.List;

public class SenopatiRequest {
    private String prompt;
    private List<Message> messages;
    private String systemPrompt;

    public SenopatiRequest(String prompt, List<Message> messages, String systemPrompt) {
        this.prompt = prompt;
        this.messages = messages;
        this.systemPrompt = systemPrompt;
    }

    // Inner class Message
    public static class Message {
        private String role;
        private String content;

        public Message(String role, String content) {
            this.role = role;
            this.content = content;
        }
    }
}
