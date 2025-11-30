package com.example.edunext;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

public class CourseDetailActivity extends AppCompatActivity {

    private static final String TAG = "CourseDetail_DEBUG";

    // Header Views
    private ImageView ivCourseHeader;
    private TextView tvCourseTitle;

    // Stats Views
    private TextView tvTryoutCount;
    private TextView tvRating;
    private TextView tvDuration;

    // Description Views
    private TextView tvDescription;

    // Learning Items
    private TextView tvLearning1;
    private TextView tvLearning2;
    private TextView tvLearning3;

    // Button
    private Button btnStartLearning;

    private int quizId = -1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_course_detail);

        Log.d(TAG, "========================================");
        Log.d(TAG, "COURSE DETAIL ACTIVITY STARTED");
        Log.d(TAG, "========================================");

        initViews();

        Intent intent = getIntent();
        if (intent != null) {
            quizId = intent.getIntExtra("QUIZ_ID", -1);
            String courseTitle = intent.getStringExtra("COURSE_TITLE");
            int imageResId = intent.getIntExtra("COURSE_IMAGE_RES_ID", 0);

            Log.d(TAG, "📥 Data diterima dari Intent:");
            Log.d(TAG, "   Quiz ID: " + quizId);
            Log.d(TAG, "   Course Title: " + courseTitle);
            Log.d(TAG, "   Image Res ID: " + imageResId);
            Log.d(TAG, "========================================");

            // Set Title
            if (courseTitle != null) {
                tvCourseTitle.setText(courseTitle.replace("\n", " "));
            }

            // Set Header Image
            if (imageResId != 0) {
                ivCourseHeader.setImageResource(imageResId);
            }

            // Setup Detail Content
            setupCourseDetails(quizId);

            if (quizId == -1) {
                Toast.makeText(this, "Error: Gagal memuat detail kursus.", Toast.LENGTH_SHORT).show();
                btnStartLearning.setEnabled(false);
            }
        } else {
            Log.e(TAG, "❌ Intent is NULL!");
        }

        btnStartLearning.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startQuiz();
            }
        });
    }

    private void initViews() {
        ivCourseHeader = findViewById(R.id.ivCourseHeader);
        tvCourseTitle = findViewById(R.id.tvCourseTitle);
        tvTryoutCount = findViewById(R.id.tvTryoutCount);
        tvRating = findViewById(R.id.tvRating);
        tvDuration = findViewById(R.id.tvDuration);
        tvDescription = findViewById(R.id.tvDescription);

        LinearLayout learningItem1 = findViewById(R.id.learningItem1);
        LinearLayout learningItem2 = findViewById(R.id.learningItem2);
        LinearLayout learningItem3 = findViewById(R.id.learningItem3);

        tvLearning1 = (TextView) learningItem1.getChildAt(1);
        tvLearning2 = (TextView) learningItem2.getChildAt(1);
        tvLearning3 = (TextView) learningItem3.getChildAt(1);

        btnStartLearning = findViewById(R.id.btnStartLearning);

        Log.d(TAG, "✅ All views initialized");
    }

    private void setupCourseDetails(int quizId) {
        Log.d(TAG, "⚙️ setupCourseDetails called with Quiz ID: " + quizId);

        if (quizId == 1) {
            Log.d(TAG, "📚 LOADING: PENGETAHUAN KUANTITATIF");

            tvTryoutCount.setText("490 Tryout");
            tvRating.setText("4.7 (3.6k+)");
            tvDuration.setText("3 Minggu");

            tvDescription.setText(
                    "Pengetahuan Kuantitatif adalah salah satu subtes penting dalam UTBK yang menguji " +
                            "kemampuan berpikir logis, analitis, dan matematis. Materi ini membantu kamu memahami " +
                            "konsep matematika dasar hingga tingkat lanjut untuk menghadapi soal-soal UTBK dengan " +
                            "lebih percaya diri."
            );

            tvLearning1.setText("Menguasai konsep dasar matematika dan logika numerik");
            tvLearning2.setText("Melatih kemampuan berpikir kritis dalam menyelesaikan soal");
            tvLearning3.setText("Mengembangkan strategi dan manajemen waktu yang efektif");

            Log.d(TAG, "✅ PK content loaded");

        } else if (quizId == 2) {
            Log.d(TAG, "📚 LOADING: PENALARAN MATEMATIKA");

            tvTryoutCount.setText("380 Tryout");
            tvRating.setText("4.8 (2.9k+)");
            tvDuration.setText("4 Minggu");

            tvDescription.setText(
                    "Penalaran Matematika menguji kemampuan memecahkan masalah kontekstual menggunakan " +
                            "logika matematis. Subtes ini fokus pada aplikasi matematika dalam kehidupan sehari-hari " +
                            "dan kemampuan menganalisis informasi numerik dengan tepat dan sistematis."
            );

            tvLearning1.setText("Memahami penerapan matematika dalam konteks kehidupan nyata");
            tvLearning2.setText("Mengasah kemampuan analisis data dan interpretasi informasi");
            tvLearning3.setText("Meningkatkan kecepatan dan ketepatan problem solving");

            Log.d(TAG, "✅ PM content loaded");

        } else {
            Log.e(TAG, "❌ QUIZ ID TIDAK DIKENALI: " + quizId);

            tvTryoutCount.setText("-- Tryout");
            tvRating.setText("-- (--k+)");
            tvDuration.setText("-- Minggu");
            tvDescription.setText("Deskripsi kursus tidak tersedia.");
            tvLearning1.setText("Materi pembelajaran 1");
            tvLearning2.setText("Materi pembelajaran 2");
            tvLearning3.setText("Materi pembelajaran 3");
        }
    }

    private void startQuiz() {
        if (quizId == -1) {
            Toast.makeText(this, "Tidak bisa memulai kuis, ID tidak valid.", Toast.LENGTH_SHORT).show();
            return;
        }

        Log.d(TAG, "🚀 Starting quiz with ID: " + quizId);

        Intent quizIntent = new Intent(CourseDetailActivity.this, QuizActivity.class);
        quizIntent.putExtra("QUIZ_ID", quizId);
        startActivity(quizIntent);
    }
}