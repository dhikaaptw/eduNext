package com.example.edunext;

import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.lifecycle.ViewModelProvider;

import com.example.edunext.database.Question;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

public class QuizActivity extends AppCompatActivity {

    private TextView tvSoal;
    private RadioGroup rgPilihan;
    private RadioButton rb1, rb2, rb3, rb4, rb5;
    private Button btnKembali, btnSelesai, btnSenopati;
    private View quizContent;
    private View progressBar;

    private List<Question> questions = new ArrayList<>();
    private List<Integer> userAnswers;
    private int currentIndex = 0;
    private boolean isAnswerRevealed = false;

    private final Handler handler = new Handler(Looper.getMainLooper());
    private static final String TAG = "QuizActivity";

    private QuizViewModel quizViewModel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_quiz);

        Log.d(TAG, "========================================");
        Log.d(TAG, "QUIZ ACTIVITY STARTED");
        Log.d(TAG, "========================================");

        initViews();

        int quizId = getIntent().getIntExtra("QUIZ_ID", -1);

        Log.d(TAG, "📥 Received QUIZ_ID from Intent: " + quizId);

        if (quizId == -1) {
            Toast.makeText(this, "Error: Kategori Kuis Tidak Valid!", Toast.LENGTH_LONG).show();
            Log.e(TAG, "❌ quizId tidak valid atau tidak ada di Intent!");
            finish();
            return;
        }

        quizViewModel = new ViewModelProvider(this).get(QuizViewModel.class);

        loadQuestionsFromDb(quizId);
    }

    private void initViews() {
        tvSoal = findViewById(R.id.tvSoal);
        rgPilihan = findViewById(R.id.rgPilihan);
        rb1 = findViewById(R.id.rbPilihan1);
        rb2 = findViewById(R.id.rbPilihan2);
        rb3 = findViewById(R.id.rbPilihan3);
        rb4 = findViewById(R.id.rbPilihan4);
        rb5 = findViewById(R.id.rbPilihan5);
        btnKembali = findViewById(R.id.btnKembali);
        btnSelesai = findViewById(R.id.btnSelesai);
        btnSenopati = findViewById(R.id.btnTanyaSnopati);
        quizContent = findViewById(R.id.quiz_content);
        progressBar = findViewById(R.id.progressBar);

        Log.d(TAG, "✅ All views initialized");
    }

    private void loadQuestionsFromDb(int quizId) {
        Log.d(TAG, "========================================");
        Log.d(TAG, "🔍 MEMUAT SOAL UNTUK QUIZ ID: " + quizId);
        Log.d(TAG, "========================================");

        quizContent.setVisibility(View.GONE);
        if(progressBar != null) progressBar.setVisibility(View.VISIBLE);

        quizViewModel.getQuestionsByQuizId(quizId).observe(this, questionsFromDb -> {
            Log.d(TAG, "📡 LiveData triggered untuk quizId: " + quizId);

            if (questionsFromDb == null) {
                Log.e(TAG, "❌ questionsFromDb is NULL");
                Log.e(TAG, "❌ Kemungkinan penyebab:");
                Log.e(TAG, "   1. Database belum terisi");
                Log.e(TAG, "   2. Query gagal dijalankan");
                Log.e(TAG, "   3. QuizId tidak ditemukan");
            } else {
                Log.d(TAG, "📊 Jumlah soal diterima dari database: " + questionsFromDb.size());

                if (questionsFromDb.size() > 0) {
                    // Log detail soal pertama untuk debugging
                    Question firstQ = questionsFromDb.get(0);
                    Log.d(TAG, "✅ Sample soal pertama:");
                    Log.d(TAG, "   - ID: " + firstQ.getQuestionId());
                    Log.d(TAG, "   - Quiz ID: " + firstQ.getQuizId());
                    Log.d(TAG, "   - Text: " + firstQ.getQuestionText().substring(0, Math.min(30, firstQ.getQuestionText().length())) + "...");
                }
            }

            if (questionsFromDb == null || questionsFromDb.isEmpty()) {
                Log.e(TAG, "========================================");
                Log.e(TAG, "❌ GAGAL MEMUAT SOAL!");
                Log.e(TAG, "========================================");
                Log.e(TAG, "Tidak ada soal untuk quizId: " + quizId);
                Log.e(TAG, "Pastikan:");
                Log.e(TAG, "1. File questions.json sudah benar");
                Log.e(TAG, "2. Database sudah terisi (cek log QuizDatabase_DEBUG)");
                Log.e(TAG, "3. QuizId yang dikirim sesuai dengan yang ada di JSON");

                Toast.makeText(this, "Gagal memuat soal untuk Quiz ID " + quizId + ". Database mungkin kosong.", Toast.LENGTH_LONG).show();
                if(progressBar != null) progressBar.setVisibility(View.GONE);
                finish();
                return;
            }

            Log.d(TAG, "✅ Berhasil memuat " + questionsFromDb.size() + " soal dari database");
            questions.clear();
            questions.addAll(questionsFromDb);
            Collections.shuffle(questions);
            Log.d(TAG, "🔀 Soal sudah diacak");

            if(progressBar != null) progressBar.setVisibility(View.GONE);
            setupQuiz();
        });
    }

    private void setupQuiz() {
        if (questions.isEmpty()) {
            Log.e(TAG, "❌ setupQuiz dipanggil, tetapi daftar soal kosong!");
            return;
        }

        Log.d(TAG, "⚙️ Setting up quiz dengan " + questions.size() + " soal");
        userAnswers = new ArrayList<>(Collections.nCopies(questions.size(), -1));
        showQuestion(currentIndex);
        updateBottomButtonText();
        setupListeners();
        quizContent.setVisibility(View.VISIBLE);
        Log.d(TAG, "✅ Quiz setup selesai, menampilkan soal pertama");
    }

    private void setupListeners() {
        btnSenopati.setOnClickListener(v -> {
            Log.d(TAG, "🤖 Membuka Senopati AI Dialog");

            // Ambil soal yang sedang ditampilkan
            Question currentQuestion = questions.get(currentIndex);

            // Buat dialog dan kirim data soal
            AiSenopatiDialog dialog = new AiSenopatiDialog(
                    QuizActivity.this,
                    currentQuestion
            );
            dialog.show();
        });

        rgPilihan.setOnCheckedChangeListener((group, checkedId) -> {
            if (isAnswerRevealed || checkedId == -1) return;
            int selectedIndex = radioIdToIndex(checkedId);
            Log.d(TAG, "✔️ User memilih opsi index: " + selectedIndex);
            userAnswers.set(currentIndex, selectedIndex);
            previewSelection(selectedIndex);
        });

        btnKembali.setOnClickListener(v -> {
            handler.removeCallbacksAndMessages(null);
            if (currentIndex > 0) {
                currentIndex--;
                isAnswerRevealed = false;
                Log.d(TAG, "⬅️ Kembali ke soal " + (currentIndex + 1));
                showQuestion(currentIndex);
                updateBottomButtonText();
            } else {
                Log.d(TAG, "🚪 Keluar dari quiz");
                finish();
            }
        });

        btnSelesai.setOnClickListener(v -> {
            setNavigationButtonsEnabled(false);
            handler.removeCallbacksAndMessages(null);

            if (isAnswerRevealed) {
                moveToNextOrSubmit();
            } else {
                int selectedIndex = userAnswers.get(currentIndex);
                if (selectedIndex == -1) {
                    Toast.makeText(this, "Pilih jawaban terlebih dahulu", Toast.LENGTH_SHORT).show();
                    setNavigationButtonsEnabled(true);
                    return;
                }
                Log.d(TAG, "📝 Mengecek jawaban...");
                revealAnswer(selectedIndex);
            }
        });
    }

    private void moveToNextOrSubmit() {
        if (currentIndex < questions.size() - 1) {
            currentIndex++;
            isAnswerRevealed = false;
            Log.d(TAG, "➡️ Pindah ke soal " + (currentIndex + 1) + " dari " + questions.size());
            showQuestion(currentIndex);
            updateBottomButtonText();
        } else {
            Log.d(TAG, "🏁 Semua soal selesai, submit quiz");
            submitQuiz();
        }
    }

    private void showQuestion(int index) {
        if (questions == null || index < 0 || index >= questions.size()) {
            Log.e(TAG, "❌ showQuestion: index tidak valid atau list kosong");
            return;
        }

        Question q = questions.get(index);
        Log.d(TAG, "📄 Menampilkan soal " + (index + 1) + ": " + q.getQuestionId());

        tvSoal.setText(q.getQuestionText());

        List<String> opts = q.getOptions();
        List<RadioButton> radioButtons = Arrays.asList(rb1, rb2, rb3, rb4, rb5);

        for (int i = 0; i < radioButtons.size(); i++) {
            if (i < opts.size()) {
                radioButtons.get(i).setVisibility(View.VISIBLE);
                radioButtons.get(i).setText(opts.get(i));
            } else {
                radioButtons.get(i).setVisibility(View.GONE);
            }
        }

        resetOptionsUI();

        int savedAnswer = userAnswers.get(index);
        if (savedAnswer != -1) {
            RadioButton rb = getRadioButtonByIndex(savedAnswer);
            if (rb != null) {
                rb.setChecked(true);
                previewSelection(savedAnswer);
            }
        }
        setNavigationButtonsEnabled(true);
    }

    private void previewSelection(int selectedIndex) {
        List<RadioButton> radioButtons = Arrays.asList(rb1, rb2, rb3, rb4, rb5);
        Drawable checked = ContextCompat.getDrawable(this, R.drawable.ic_radio_checked);
        Drawable unchecked = ContextCompat.getDrawable(this, R.drawable.ic_radio_unchecked);

        for (int i = 0; i < radioButtons.size(); i++) {
            if (radioButtons.get(i).getVisibility() == View.VISIBLE) {
                setRadioDrawableEnd(radioButtons.get(i), (i == selectedIndex) ? checked : unchecked);
            }
        }
    }

    private void revealAnswer(int selectedIndex) {
        isAnswerRevealed = true;
        updateBottomButtonText();

        int correctIndex = questions.get(currentIndex).getCorrectOption();

        Log.d(TAG, "🎯 Jawaban user: " + selectedIndex + ", Jawaban benar: " + correctIndex);

        RadioButton selectedRb = getRadioButtonByIndex(selectedIndex);
        RadioButton correctRb = getRadioButtonByIndex(correctIndex);

        for (int i = 0; i < rgPilihan.getChildCount(); i++) {
            View child = rgPilihan.getChildAt(i);
            if(child instanceof RadioButton) {
                child.setEnabled(false);
            }
        }

        if (selectedIndex == correctIndex) {
            Log.d(TAG, "✅ Jawaban BENAR!");
            if (selectedRb != null) selectedRb.setBackgroundResource(R.drawable.bg_option_correct);
        } else {
            Log.d(TAG, "❌ Jawaban SALAH!");
            if (selectedRb != null) selectedRb.setBackgroundResource(R.drawable.bg_option_incorrect);
            if (correctRb != null) correctRb.setBackgroundResource(R.drawable.bg_option_correct);
        }

        handler.postDelayed(this::moveToNextOrSubmit, 1500);
    }

    private void resetOptionsUI() {
        rgPilihan.clearCheck();
        for (int i = 0; i < rgPilihan.getChildCount(); i++) {
            View child = rgPilihan.getChildAt(i);
            if (child instanceof RadioButton) {
                child.setEnabled(true);
                child.setBackgroundResource(R.drawable.option_bg);
                setRadioDrawableEnd((RadioButton) child, ContextCompat.getDrawable(this, R.drawable.ic_radio_unchecked));
            }
        }
    }

    private void updateBottomButtonText() {
        if (isAnswerRevealed) {
            btnSelesai.setText("Lanjut");
        } else {
            btnSelesai.setText(currentIndex == questions.size() - 1 ? "Selesai" : "Jawab");
        }
    }

    private void submitQuiz() {
        Log.d(TAG, "========================================");
        Log.d(TAG, "📊 MENGHITUNG SKOR");
        Log.d(TAG, "========================================");

        int correctCount = 0;
        for (int i = 0; i < questions.size(); i++) {
            int userAnswer = userAnswers.get(i);
            int correctAnswer = questions.get(i).getCorrectOption();

            if (userAnswer == correctAnswer) {
                correctCount++;
                Log.d(TAG, "✅ Soal " + (i+1) + ": BENAR");
            } else {
                Log.d(TAG, "❌ Soal " + (i+1) + ": SALAH (jawaban: " + userAnswer + ", benar: " + correctAnswer + ")");
            }
        }

        Log.d(TAG, "========================================");
        Log.d(TAG, "🏆 HASIL AKHIR: " + correctCount + "/" + questions.size());
        Log.d(TAG, "========================================");

        Intent intent = new Intent(QuizActivity.this, ScoreActivity.class);
        intent.putExtra("total_questions", questions.size());
        intent.putExtra("correct_answers", correctCount);
        startActivity(intent);
        finish();
    }

    private int radioIdToIndex(int id) {
        if (id == R.id.rbPilihan1) return 0;
        if (id == R.id.rbPilihan2) return 1;
        if (id == R.id.rbPilihan3) return 2;
        if (id == R.id.rbPilihan4) return 3;
        if (id == R.id.rbPilihan5) return 4;
        return -1;
    }

    private RadioButton getRadioButtonByIndex(int idx) {
        if (idx < 0 || idx >= rgPilihan.getChildCount()) return null;
        View view = rgPilihan.getChildAt(idx);
        if (view instanceof RadioButton) {
            return (RadioButton) view;
        }
        return null;
    }

    private void setRadioDrawableEnd(RadioButton rb, Drawable d) {
        if (rb != null) {
            rb.setCompoundDrawablesWithIntrinsicBounds(null, null, d, null);
        }
    }

    private void setNavigationButtonsEnabled(boolean isEnabled) {
        btnSelesai.setEnabled(isEnabled);
        btnKembali.setEnabled(isEnabled);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        handler.removeCallbacksAndMessages(null);
        Log.d(TAG, "🛑 Quiz Activity destroyed");
    }
}