package com.example.edunext;

import android.graphics.Color;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.view.View;
import android.widget.Button;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import java.util.ArrayList;
import java.util.List;

public class QuizActivity extends AppCompatActivity {

    private TextView tvSoal, tvTimer;
    private RadioGroup rgPilihan;
    private RadioButton rbPilihan1, rbPilihan2, rbPilihan3, rbPilihan4, rbPilihan5;
    private Button btnTanyaSnopati, btnKembali, btnSelesai;

    private List<Question> questionList;
    private int currentQuestionIndex = 0;
    private CountDownTimer countDownTimer;
    private long timeLeftInMillis = 600000; // 10 menit
    private String selectedAnswer = "";
    private int correctAnswers = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_quiz);

        // Inisialisasi views
        initViews();

        // Load questions
        loadQuestions();

        // Tampilkan soal pertama
        displayQuestion();

        // Start timer
        startTimer();

        // Set listeners
        setListeners();
    }

    private void initViews() {
        tvSoal = findViewById(R.id.tvSoal);
        tvTimer = findViewById(R.id.tvTimer);
        rgPilihan = findViewById(R.id.rgPilihan);
        rbPilihan1 = findViewById(R.id.rbPilihan1);
        rbPilihan2 = findViewById(R.id.rbPilihan2);
        rbPilihan3 = findViewById(R.id.rbPilihan3);
        rbPilihan4 = findViewById(R.id.rbPilihan4);
        rbPilihan5 = findViewById(R.id.rbPilihan5);
        btnTanyaSnopati = findViewById(R.id.btnTanyaSnopati);
        btnKembali = findViewById(R.id.btnKembali);
        btnSelesai = findViewById(R.id.btnSelesai);
    }

    private void loadQuestions() {
        questionList = new ArrayList<>();

        // Soal 1 - dari screenshot
        Question q1 = new Question();
        q1.setSoal("Harga 5 buku dan 3 pensil adalah Rp41.000. Sedangkan harga 3 buku dan 2 pensil adalah Rp25.000. Berapa harga sebuah buku?");
        q1.setPilihan1("Rp5.000");
        q1.setPilihan2("Rp6.000");
        q1.setPilihan3("Rp7.000");
        q1.setPilihan4("Rp8.000");
        q1.setPilihan5("Rp9.000");
        q1.setJawabanBenar("Rp7.000");
        questionList.add(q1);

        // Soal 2
        Question q2 = new Question();
        q2.setSoal("Jika 2x + 3 = 11, berapakah nilai x?");
        q2.setPilihan1("2");
        q2.setPilihan2("3");
        q2.setPilihan3("4");
        q2.setPilihan4("5");
        q2.setPilihan5("6");
        q2.setJawabanBenar("4");
        questionList.add(q2);

        // Soal 3
        Question q3 = new Question();
        q3.setSoal("Sebuah persegi panjang memiliki panjang 12 cm dan lebar 8 cm. Berapakah luas persegi panjang tersebut?");
        q3.setPilihan1("80 cm²");
        q3.setPilihan2("84 cm²");
        q3.setPilihan3("88 cm²");
        q3.setPilihan4("92 cm²");
        q3.setPilihan5("96 cm²");
        q3.setJawabanBenar("96 cm²");
        questionList.add(q3);

        // Tambahkan soal lainnya sesuai kebutuhan
    }

    private void displayQuestion() {
        if (currentQuestionIndex < questionList.size()) {
            Question currentQuestion = questionList.get(currentQuestionIndex);

            tvSoal.setText(currentQuestion.getSoal());
            rbPilihan1.setText(currentQuestion.getPilihan1());
            rbPilihan2.setText(currentQuestion.getPilihan2());
            rbPilihan3.setText(currentQuestion.getPilihan3());
            rbPilihan4.setText(currentQuestion.getPilihan4());
            rbPilihan5.setText(currentQuestion.getPilihan5());

            // Reset pilihan
            rgPilihan.clearCheck();
            resetRadioButtonColors();
            selectedAnswer = "";
        }
    }

    private void setListeners() {
        // Listener untuk RadioGroup
        rgPilihan.setOnCheckedChangeListener((group, checkedId) -> {
            RadioButton selectedRb = findViewById(checkedId);
            if (selectedRb != null) {
                selectedAnswer = selectedRb.getText().toString();
            }
        });

        // Listener untuk button Selesai
        btnSelesai.setOnClickListener(v -> {
            if (selectedAnswer.isEmpty()) {
                Toast.makeText(this, "Silakan pilih jawaban terlebih dahulu", Toast.LENGTH_SHORT).show();
                return;
            }

            checkAnswer();
        });

        // Listener untuk button Kembali
        btnKembali.setOnClickListener(v -> {
            // Kembali ke CourseDetailActivity
            finish();
        });

        // Listener untuk button Tanya Snopati
        btnTanyaSnopati.setOnClickListener(v -> {
            Toast.makeText(this, "Fitur Tanya Snopati segera hadir", Toast.LENGTH_SHORT).show();
        });
    }

    private void checkAnswer() {
        Question currentQuestion = questionList.get(currentQuestionIndex);
        boolean isCorrect = selectedAnswer.equals(currentQuestion.getJawabanBenar());

        if (isCorrect) {
            correctAnswers++;
        }

        // Highlight jawaban
        highlightAnswer(isCorrect);

        // Disable selection setelah menjawab
        disableRadioButtons();
        btnSelesai.setEnabled(false);

        // Pindah ke soal berikutnya setelah delay
        new android.os.Handler().postDelayed(() -> {
            if (currentQuestionIndex < questionList.size() - 1) {
                currentQuestionIndex++;
                enableRadioButtons();
                btnSelesai.setEnabled(true);
                displayQuestion();
            } else {
                // Quiz selesai
                showResult();
            }
        }, 2000);
    }

    private void highlightAnswer(boolean isCorrect) {
        Question currentQuestion = questionList.get(currentQuestionIndex);

        // Highlight jawaban yang dipilih
        RadioButton selectedRb = null;
        if (rbPilihan1.isChecked()) {
            selectedRb = rbPilihan1;
        } else if (rbPilihan2.isChecked()) {
            selectedRb = rbPilihan2;
        } else if (rbPilihan3.isChecked()) {
            selectedRb = rbPilihan3;
        } else if (rbPilihan4.isChecked()) {
            selectedRb = rbPilihan4;
        } else if (rbPilihan5.isChecked()) {
            selectedRb = rbPilihan5;
        }

        if (selectedRb != null) {
            if (isCorrect) {
                selectedRb.setBackgroundColor(Color.parseColor("#4CAF50")); // Hijau
                selectedRb.setTextColor(Color.WHITE);
            } else {
                selectedRb.setBackgroundColor(Color.parseColor("#F44336")); // Merah
                selectedRb.setTextColor(Color.WHITE);
            }
        }

        // Jika salah, highlight jawaban yang benar
        if (!isCorrect) {
            highlightCorrectAnswer(currentQuestion.getJawabanBenar());
        }
    }

    private void highlightCorrectAnswer(String correctAnswer) {
        if (rbPilihan1.getText().toString().equals(correctAnswer)) {
            rbPilihan1.setBackgroundColor(Color.parseColor("#4CAF50"));
            rbPilihan1.setTextColor(Color.WHITE);
        } else if (rbPilihan2.getText().toString().equals(correctAnswer)) {
            rbPilihan2.setBackgroundColor(Color.parseColor("#4CAF50"));
            rbPilihan2.setTextColor(Color.WHITE);
        } else if (rbPilihan3.getText().toString().equals(correctAnswer)) {
            rbPilihan3.setBackgroundColor(Color.parseColor("#4CAF50"));
            rbPilihan3.setTextColor(Color.WHITE);
        } else if (rbPilihan4.getText().toString().equals(correctAnswer)) {
            rbPilihan4.setBackgroundColor(Color.parseColor("#4CAF50"));
            rbPilihan4.setTextColor(Color.WHITE);
        } else if (rbPilihan5.getText().toString().equals(correctAnswer)) {
            rbPilihan5.setBackgroundColor(Color.parseColor("#4CAF50"));
            rbPilihan5.setTextColor(Color.WHITE);
        }
    }

    private void resetRadioButtonColors() {
        int defaultColor = Color.parseColor("#1A1A1A");
        int defaultTextColor = Color.WHITE;

        rbPilihan1.setBackgroundColor(defaultColor);
        rbPilihan2.setBackgroundColor(defaultColor);
        rbPilihan3.setBackgroundColor(defaultColor);
        rbPilihan4.setBackgroundColor(defaultColor);
        rbPilihan5.setBackgroundColor(defaultColor);

        rbPilihan1.setTextColor(defaultTextColor);
        rbPilihan2.setTextColor(defaultTextColor);
        rbPilihan3.setTextColor(defaultTextColor);
        rbPilihan4.setTextColor(defaultTextColor);
        rbPilihan5.setTextColor(defaultTextColor);
    }

    private void disableRadioButtons() {
        rbPilihan1.setEnabled(false);
        rbPilihan2.setEnabled(false);
        rbPilihan3.setEnabled(false);
        rbPilihan4.setEnabled(false);
        rbPilihan5.setEnabled(false);
    }

    private void enableRadioButtons() {
        rbPilihan1.setEnabled(true);
        rbPilihan2.setEnabled(true);
        rbPilihan3.setEnabled(true);
        rbPilihan4.setEnabled(true);
        rbPilihan5.setEnabled(true);
    }

    private void startTimer() {
        countDownTimer = new CountDownTimer(timeLeftInMillis, 1000) {
            @Override
            public void onTick(long millisUntilFinished) {
                timeLeftInMillis = millisUntilFinished;
                updateTimerText();
            }

            @Override
            public void onFinish() {
                Toast.makeText(QuizActivity.this, "Waktu habis!", Toast.LENGTH_SHORT).show();
                showResult();
            }
        }.start();
    }

    private void updateTimerText() {
        int minutes = (int) (timeLeftInMillis / 1000) / 60;
        int seconds = (int) (timeLeftInMillis / 1000) % 60;

        String timeFormatted = String.format("%d Menit", minutes);
        tvTimer.setText(timeFormatted);
    }

    private void showResult() {
        if (countDownTimer != null) {
            countDownTimer.cancel();
        }

        String message = "Quiz Selesai!\nJawaban Benar: " + correctAnswers + "/" + questionList.size();
        Toast.makeText(this, message, Toast.LENGTH_LONG).show();

        // Kembali ke CourseDetailActivity
        finish();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (countDownTimer != null) {
            countDownTimer.cancel();
        }
    }
}