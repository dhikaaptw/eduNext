package com.example.edunext;

import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.Handler;
import android.widget.Button;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class QuizActivity extends AppCompatActivity {

    private TextView tvSoal;
    private RadioGroup rgPilihan;
    private RadioButton rb1, rb2, rb3, rb4, rb5;
    private Button btnKembali, btnSelesai, btnSenopati;

    private List<Question> questions = new ArrayList<>();
    private List<Integer> userAnswers = new ArrayList<>(); // 🔹 Menyimpan jawaban user
    private int currentIndex = 0;
    private boolean isAnswerRevealed = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_quiz);

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

        loadQuestions();
        for (int i = 0; i < questions.size(); i++) userAnswers.add(-1); // belum dijawab

        showQuestion(currentIndex);
        updateBottomButtonText();

        btnSenopati.setOnClickListener(v -> {
            AiSenopatiDialog dialog = new AiSenopatiDialog(QuizActivity.this);
            dialog.show();
        });

        rgPilihan.setOnCheckedChangeListener((group, checkedId) -> {
            if (checkedId == -1) return;
            int selectedIndex = radioIdToIndex(checkedId);
            previewSelection(selectedIndex);
            userAnswers.set(currentIndex, selectedIndex); // ✅ simpan jawaban
        });

        btnKembali.setOnClickListener(v -> {
            if (currentIndex > 0) {
                currentIndex--;
                showQuestion(currentIndex);
                updateBottomButtonText();
            } else {
                finish();
            }
        });

        btnSelesai.setOnClickListener(v -> {
            int selectedIndex = userAnswers.get(currentIndex);
            if (selectedIndex == -1) {
                Toast.makeText(this, "Pilih jawaban dulu", Toast.LENGTH_SHORT).show();
                return;
            }

            if (!isAnswerRevealed) {
                revealAnswer(selectedIndex);
                isAnswerRevealed = true;
                updateBottomButtonText();

                new Handler().postDelayed(() -> {
                    if (currentIndex < questions.size() - 1) {
                        currentIndex++;
                        isAnswerRevealed = false;
                        showQuestion(currentIndex);
                        updateBottomButtonText();
                    } else {
                        submitQuiz();
                    }
                }, 700);
            } else {
                if (currentIndex < questions.size() - 1) {
                    currentIndex++;
                    isAnswerRevealed = false;
                    showQuestion(currentIndex);
                    updateBottomButtonText();
                } else {
                    submitQuiz();
                }
            }
        });
    }

    private void loadQuestions() {
        questions.add(new Question(
                "Harga 5 buku dan 3 pensil adalah Rp41.000. Sedangkan harga 3 buku dan 2 pensil adalah Rp25.000. Berapakah harga sebuah buku?",
                new ArrayList<>(Arrays.asList("Rp5.000", "Rp6.000", "Rp7.000", "Rp8.000", "Rp9.000")),
                2));
        questions.add(new Question(
                "Diketahui barisan aritmatika dengan suku ke-3 adalah 11 dan suku ke-7 adalah 23. Jumlah 10 suku pertama barisan tersebut adalah...",
                new ArrayList<>(Arrays.asList("155", "165", "175", "185", "195")),
                2));
        questions.add(new Question(
                "Perbandingan uang Ali dan Budi adalah 3 : 5. Jika jumlah uang mereka Rp800.000, maka selisih uang mereka adalah...",
                new ArrayList<>(Arrays.asList("Rp100.000", "Rp150.000", "Rp200.000", "Rp250.000", "Rp300.000")),
                2));
    }

    private void showQuestion(int index) {
        Question q = questions.get(index);
        tvSoal.setText(q.getText());

        List<String> opts = q.getOptions();
        rb1.setText(opts.get(0));
        rb2.setText(opts.get(1));
        rb3.setText(opts.get(2));
        rb4.setText(opts.get(3));
        rb5.setText(opts.get(4));

        // Reset tampilan setiap kali ganti soal
        previewResetUI();

        // ✅ Cek apakah user sudah pernah menjawab soal ini
        int savedAnswer = userAnswers.get(index);
        if (savedAnswer != -1) {
            // Jika sudah dijawab → tampilkan pilihan sebelumnya
            RadioButton rb = getRadioButtonByIndex(savedAnswer);
            if (rb != null) {
                rb.setChecked(true);
                previewSelection(savedAnswer);
            }
        } else {
            // Jika belum dijawab → kosong tanpa tanda
            rgPilihan.clearCheck();
        }
    }

    private void previewSelection(int selectedIndex) {
        Drawable checked = ContextCompat.getDrawable(this, R.drawable.ic_radio_checked);
        Drawable unchecked = ContextCompat.getDrawable(this, R.drawable.ic_radio_unchecked);

        for (RadioButton rb : new RadioButton[]{rb1, rb2, rb3, rb4, rb5})
            setRadioDrawableEnd(rb, unchecked);

        setRadioDrawableEnd(getRadioButtonByIndex(selectedIndex), checked);
    }

    private void revealAnswer(int selectedIndex) {
        int correct = questions.get(currentIndex).getCorrectIndex();

        RadioButton selectedRb = getRadioButtonByIndex(selectedIndex);
        RadioButton correctRb = getRadioButtonByIndex(correct);

        if (selectedIndex == correct) {
            selectedRb.setBackground(ContextCompat.getDrawable(this, R.drawable.bg_option_correct));
        } else {
            selectedRb.setBackground(ContextCompat.getDrawable(this, R.drawable.bg_option_incorrect));
            correctRb.setBackground(ContextCompat.getDrawable(this, R.drawable.bg_option_correct));
        }
    }

    private void previewResetUI() {
        Drawable unchecked = ContextCompat.getDrawable(this, R.drawable.ic_radio_unchecked);
        for (RadioButton rb : new RadioButton[]{rb1, rb2, rb3, rb4, rb5}) {
            rb.setBackground(ContextCompat.getDrawable(this, R.drawable.option_bg));
            setRadioDrawableEnd(rb, unchecked);
        }
        rgPilihan.clearCheck(); // pastikan kosong
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
        switch (idx) {
            case 0: return rb1;
            case 1: return rb2;
            case 2: return rb3;
            case 3: return rb4;
            case 4: return rb5;
            default: return null;
        }
    }

    private void setRadioDrawableEnd(RadioButton rb, Drawable d) {
        rb.setCompoundDrawablesWithIntrinsicBounds(null, null, d, null);
    }

    private void updateBottomButtonText() {
        btnSelesai.setText(currentIndex < questions.size() - 1 ? "Lanjut" : "Selesai");
    }

    private void submitQuiz() {
        int correctCount = 0;
        for (int i = 0; i < questions.size(); i++) {
            if (userAnswers.get(i) == questions.get(i).getCorrectIndex()) {
                correctCount++;
            }
        }

        Intent intent = new Intent(QuizActivity.this, ScoreActivity.class);
        intent.putExtra("total_questions", questions.size());
        intent.putExtra("correct_answers", correctCount);
        startActivity(intent);
        finish();
    }
}
