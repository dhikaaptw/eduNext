package com.example.edunext;

import java.util.List;

public class SenopatiResponse {
    private String reply;
    private List<SenopatiRequest.Message> messages;

    public String getReply() {
        return reply;
    }

    public List<SenopatiRequest.Message> getMessages() {
        return messages;
    }
}
