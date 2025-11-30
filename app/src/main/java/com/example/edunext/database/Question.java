package com.example.edunext.database;

import androidx.annotation.NonNull;
import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;
import com.google.gson.annotations.SerializedName;

import java.util.List;

@Entity(tableName = "questions")
public class Question {

    @PrimaryKey
    @NonNull
    @SerializedName("questionId")
    @ColumnInfo(name = "question_id")
    private String questionId;

    @ColumnInfo(name = "quiz_id")
    private int quizId;

    @SerializedName("text")
    @ColumnInfo(name = "question_text")
    private String questionText;

    @SerializedName("options")
    @ColumnInfo(name = "options")
    private List<String> options;

    @SerializedName("correctIndex")
    @ColumnInfo(name = "correct_option")
    private int correctOption;

    public Question(@NonNull String questionId, int quizId, String questionText, List<String> options, int correctOption) {
        this.questionId = questionId;
        this.quizId = quizId;
        this.questionText = questionText;
        this.options = options;
        this.correctOption = correctOption;
    }

    @NonNull
    public String getQuestionId() { return questionId; }
    public void setQuestionId(@NonNull String questionId) { this.questionId = questionId; }

    public int getQuizId() { return quizId; }
    public void setQuizId(int quizId) { this.quizId = quizId; } // Setter ini PENTING

    public String getQuestionText() { return questionText; }
    public void setQuestionText(String questionText) { this.questionText = questionText; }

    public List<String> getOptions() { return options; }
    public void setOptions(List<String> options) { this.options = options; }

    public int getCorrectOption() { return correctOption; }
    public void setCorrectOption(int correctOption) { this.correctOption = correctOption; }
}
