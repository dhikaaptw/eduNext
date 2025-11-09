package com.example.edunext;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

public class ScoreActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_score); // XML yang kamu kirim di atas

        // Ambil data hasil quiz
        int total = getIntent().getIntExtra("total_questions", 0);
        int correct = getIntent().getIntExtra("correct_answers", 0);
        int wrong = total - correct;

        // Hitung persentase skor
        int percentage = (int) ((correct * 100.0f) / total);

        // Hubungkan ke view
        TextView tvTotal = findViewById(R.id.tvTotalQuestions);
        TextView tvCorrect = findViewById(R.id.tvCorrectAnswers);
        TextView tvWrong = findViewById(R.id.tvWrongAnswers);
        TextView tvPercent = findViewById(R.id.tvPercentage);
        TextView tvMotivation = findViewById(R.id.tvMotivation);

        tvTotal.setText(String.valueOf(total));
        tvCorrect.setText(String.valueOf(correct));
        tvWrong.setText(String.valueOf(wrong));
        tvPercent.setText(percentage + "%");

        // Pesan motivasi sederhana
        if (percentage >= 80) {
            tvMotivation.setText("Luar biasa! Pertahankan 💪");
        } else if (percentage >= 60) {
            tvMotivation.setText("Bagus! Terus tingkatkan lagi ya 💪");
        } else {
            tvMotivation.setText("Jangan menyerah, coba lagi yuk! 🔥");
        }

        // Tombol
        Button btnHome = findViewById(R.id.btnBackToHome);
        Button btnReview = findViewById(R.id.btnReviewAnswers);

        btnHome.setOnClickListener(v -> {
            Intent intent = new Intent(ScoreActivity.this, MainActivity.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(intent);
            finish();
        });

        btnReview.setOnClickListener(v -> {
            // opsional: bisa tampilkan pembahasan
            // misalnya buka ReviewActivity (belum kamu buat)
        });
    }
}
