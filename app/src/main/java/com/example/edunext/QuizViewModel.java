package com.example.edunext;

import android.app.Application;
import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;

import com.example.edunext.database.Question;
import com.example.edunext.database.QuestionDao;
import com.example.edunext.database.QuizDatabase;
import java.util.List;

public class QuizViewModel extends AndroidViewModel {

    private final QuestionDao questionDao;

    public QuizViewModel(@NonNull Application application) {
        super(application);
        QuizDatabase db = QuizDatabase.getDatabase(application);
        questionDao = db.questionDao();
    }

    public LiveData<List<Question>> getQuestionsByQuizId(int quizId) {
        return questionDao.getQuestionsByQuizId(quizId);
    }
}
