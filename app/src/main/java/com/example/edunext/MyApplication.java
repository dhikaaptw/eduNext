package com.example.edunext;

import android.app.Application;
import android.util.Log;

import com.example.edunext.database.Question;
import com.example.edunext.database.QuestionDao;
import com.example.edunext.database.QuizDatabase;

import java.util.List;

public class MyApplication extends Application {
    private static final String TAG = "MyApplication";

    @Override
    public void onCreate() {
        super.onCreate();
        Log.d(TAG, "========================================");
        Log.d(TAG, "APPLICATION STARTING");
        Log.d(TAG, "========================================");

        QuizDatabase.databaseWriteExecutor.execute(() -> {
            try {
                QuizDatabase db = QuizDatabase.getDatabase(getApplicationContext());
                QuestionDao dao = db.questionDao();

                List<Question> testQuery = dao.getAnyQuestion();

                if (testQuery == null || testQuery.isEmpty()) {
                    Log.e(TAG, "⚠️ WARNING: Database kosong setelah init!");
                } else {
                    Log.d(TAG, "✅ Database siap dengan " + testQuery.size() + " soal");
                }

                Log.d(TAG, "========================================");
                Log.d(TAG, "DATABASE INITIALIZATION COMPLETE");
                Log.d(TAG, "========================================");

            } catch (Exception e) {
                Log.e(TAG, "❌ Error saat init database", e);
            }
        });
    }
}