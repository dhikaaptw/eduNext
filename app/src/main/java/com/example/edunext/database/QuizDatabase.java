package com.example.edunext.database;

import android.content.Context;
import android.util.Log;
import androidx.annotation.NonNull;
import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;
import androidx.room.TypeConverters;
import androidx.sqlite.db.SupportSQLiteDatabase;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

@Database(entities = {Question.class}, version = 5, exportSchema = false) // NAIKKAN KE 5
@TypeConverters({Converters.class})
public abstract class QuizDatabase extends RoomDatabase {

    public abstract QuestionDao questionDao();
    private static volatile QuizDatabase INSTANCE;
    public static final ExecutorService databaseWriteExecutor = Executors.newFixedThreadPool(4);

    private static final String TAG = "QuizDatabase_DEBUG";

    public static QuizDatabase getDatabase(final Context context) {
        if (INSTANCE == null) {
            synchronized (QuizDatabase.class) {
                if (INSTANCE == null) {
                    INSTANCE = Room.databaseBuilder(context.getApplicationContext(),
                                    QuizDatabase.class, "quiz_database")
                            .fallbackToDestructiveMigration()
                            .addCallback(new RoomDatabase.Callback() {
                                @Override
                                public void onCreate(@NonNull SupportSQLiteDatabase db) {
                                    super.onCreate(db);
                                    Log.e(TAG, "=== DATABASE BARU DIBUAT ===");
                                    databaseWriteExecutor.execute(() -> {
                                        fillWithInitialData(context, INSTANCE.questionDao());
                                    });
                                }
                            })
                            .build();
                }
            }
        }
        return INSTANCE;
    }

    private static void fillWithInitialData(Context context, QuestionDao dao) {
        Log.d(TAG, "========================================");
        Log.d(TAG, "MEMULAI PENGISIAN DATA DARI questions.json");
        Log.d(TAG, "========================================");

        try {
            // 1. Buka dan baca file JSON
            InputStream inputStream = context.getAssets().open("questions.json");
            InputStreamReader reader = new InputStreamReader(inputStream);

            // 2. Parse JSON menjadi List<QuizData>
            List<QuizData> quizDataList = new Gson().fromJson(
                    reader,
                    new TypeToken<List<QuizData>>(){}.getType()
            );

            reader.close();
            inputStream.close();

            // 3. Validasi hasil parsing
            if (quizDataList == null || quizDataList.isEmpty()) {
                Log.e(TAG, "❌ GAGAL! JSON kosong atau format salah");
                return;
            }

            Log.d(TAG, "✅ Berhasil parse " + quizDataList.size() + " quiz dari JSON");

            // 4. Kumpulkan semua soal dari semua quiz
            List<Question> allQuestionsToInsert = new ArrayList<>();

            for (QuizData quizData : quizDataList) {
                int currentQuizId = quizData.getQuizId();
                List<Question> questionsInQuiz = quizData.getQuestions();

                if (questionsInQuiz == null || questionsInQuiz.isEmpty()) {
                    Log.w(TAG, "⚠️ Quiz ID " + currentQuizId + " tidak punya soal");
                    continue;
                }

                // 5. Set quizId untuk setiap soal & validasi questionId
                int validCount = 0;
                for (Question question : questionsInQuiz) {
                    // VALIDASI PENTING: Pastikan questionId tidak null
                    if (question.getQuestionId() == null || question.getQuestionId().isEmpty()) {
                        Log.e(TAG, "❌ SKIP: Soal dengan questionId NULL di quiz " + currentQuizId);
                        continue;
                    }

                    question.setQuizId(currentQuizId);
                    allQuestionsToInsert.add(question);
                    validCount++;
                }

                Log.d(TAG, "✅ Quiz ID " + currentQuizId + ": " + validCount + " soal valid");
            }

            // 6. Insert ke database
            if (allQuestionsToInsert.isEmpty()) {
                Log.e(TAG, "❌ TIDAK ADA SOAL VALID UNTUK DIMASUKKAN!");
                return;
            }

            Log.d(TAG, "📝 Memasukkan " + allQuestionsToInsert.size() + " soal ke database...");
            dao.insertAll(allQuestionsToInsert);
            Log.d(TAG, "========================================");
            Log.d(TAG, "✅ SUKSES! " + allQuestionsToInsert.size() + " SOAL TERSIMPAN");
            Log.d(TAG, "========================================");

            // 7. Verifikasi dengan query
            List<Question> verify = dao.getAnyQuestion();
            if (verify != null && !verify.isEmpty()) {
                Log.d(TAG, "✅ VERIFIKASI: Database berisi data");
            } else {
                Log.e(TAG, "❌ VERIFIKASI GAGAL: Database masih kosong!");
            }

        } catch (Exception e) {
            Log.e(TAG, "========================================");
            Log.e(TAG, "❌❌❌ ERROR KRITIS ❌❌❌");
            Log.e(TAG, "========================================");
            Log.e(TAG, "Error message: " + e.getMessage());
            Log.e(TAG, "Error type: " + e.getClass().getSimpleName());
            e.printStackTrace();
        }
    }
}