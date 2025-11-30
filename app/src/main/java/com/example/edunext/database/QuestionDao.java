package com.example.edunext.database;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;

import java.util.List;

@Dao
public interface QuestionDao {

    @Query("SELECT * FROM questions")
    LiveData<List<Question>> getAllQuestions();

    @Query("SELECT * FROM questions LIMIT 1")
    List<Question> getAnyQuestion();

    @Query("SELECT * FROM questions WHERE quiz_id = :quizId")
    LiveData<List<Question>> getQuestionsByQuizId(int quizId);


    @Query("SELECT * FROM questions WHERE quiz_id = :quizId")
    List<Question> getQuestionsByQuizIdSync(int quizId);

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insertAll(List<Question> questions);
}
