package com.example.edunext;

import java.util.List;

public class Question {
    private String text;
    private List<String> options;
    private int correctIndex;

    // Konstruktor
    public Question(String text, List<String> options, int correctIndex) {
        this.text = text;
        this.options = options;
        this.correctIndex = correctIndex;
    }

    // Getter yang dibutuhkan QuizActivity
    public String getText() {
        return text;
    }

    public List<String> getOptions() {
        return options;
    }

    public int getCorrectIndex() {
        return correctIndex;
    }

    // Optional: setter (jika perlu)
    public void setText(String text) { this.text = text; }
    public void setOptions(List<String> options) { this.options = options; }
    public void setCorrectIndex(int correctIndex) { this.correctIndex = correctIndex; }
}
