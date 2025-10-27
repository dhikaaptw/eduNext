package com.example.edunext;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;

import androidx.appcompat.app.AppCompatActivity;

public class CourseDetailActivity extends AppCompatActivity {

    private Button btnStartLearning;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_course_detail);

        // Inisialisasi button Mulai Belajar
        btnStartLearning = findViewById(R.id.btnStartLearning);

        // Ambil course name dari intent (opsional)
        String courseName = getIntent().getStringExtra("course_name");

        // Set click listener untuk button Mulai Belajar
        btnStartLearning.setOnClickListener(v -> {
            // Pindah ke QuizActivity
            Intent intent = new Intent(CourseDetailActivity.this, QuizActivity.class);
            intent.putExtra("course_name", courseName);
            startActivity(intent);
        });
    }
}