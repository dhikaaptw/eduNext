package com.example.edunext.database;

import com.google.gson.annotations.SerializedName;
import java.util.List;

public class QuizData {

    @SerializedName("quizId")
    private int quizId;

    @SerializedName("questions")
    private List<Question> questions;

    // --- Getters ---
    public int getQuizId() {
        return quizId;
    }

    public List<Question> getQuestions() {
        return questions;
    }
}
    