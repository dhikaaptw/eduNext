package com.example.edunext;

public class Course {
    private String name;
    private int imageResource;
    private int quizId;

    public Course(String name, int imageResource, int quizId) {
        this.name = name;
        this.imageResource = imageResource;
        this.quizId = quizId;
    }

    public String getName() {
        return name;
    }

    public int getImageResource() {
        return imageResource;
    }

    public int getQuizId() {
        return quizId;
    }
}
    