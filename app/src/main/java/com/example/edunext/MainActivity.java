package com.example.edunext;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;

public class MainActivity extends AppCompatActivity {

    private static final String TAG = "MainActivity_DEBUG";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Log.d(TAG, "========================================");
        Log.d(TAG, "MAIN ACTIVITY STARTED");
        Log.d(TAG, "========================================");

        // ========================================
        // SETUP KARTU PENGETAHUAN KUANTITATIF
        // ========================================
        setupPengetahuanKuantitatif();

        // ========================================
        // SETUP KARTU PENALARAN MATEMATIKA
        // ========================================
        setupPenalaranMatematika();
    }

    /**
     * Setup Kartu Pengetahuan Kuantitatif (Quiz ID 1)
     */
    private void setupPengetahuanKuantitatif() {
        Log.d(TAG, "--- Setting up Pengetahuan Kuantitatif ---");

        View cardPK = findViewById(R.id.card_pk);

        if (cardPK == null) {
            Log.e(TAG, "❌ FATAL: card_pk tidak ditemukan di layout!");
            return;
        }

        Log.d(TAG, "✅ card_pk ditemukan");

        TextView tvName = cardPK.findViewById(R.id.course_name_textview);
        ImageView ivImage = cardPK.findViewById(R.id.course_image_imageview);
        Button btnLanjut = cardPK.findViewById(R.id.btnLanjut);

        if (tvName != null) {
            tvName.setText("Pengetahuan\nKuantitatif");
            Log.d(TAG, "✅ Set nama PK");
        } else {
            Log.e(TAG, "❌ tvName PK tidak ditemukan!");
        }

        if (ivImage != null) {
            ivImage.setImageResource(R.drawable.pengetahuan_kuantitatif);
            Log.d(TAG, "✅ Set gambar PK");
        } else {
            Log.e(TAG, "❌ ivImage PK tidak ditemukan!");
        }

        if (btnLanjut != null) {
            btnLanjut.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Log.d(TAG, "========================================");
                    Log.d(TAG, "🔘 KLIK BUTTON: Pengetahuan Kuantitatif");
                    Log.d(TAG, "📤 Mengirim Quiz ID: 1");
                    Log.d(TAG, "========================================");

                    Intent intent = new Intent(MainActivity.this, CourseDetailActivity.class);
                    intent.putExtra("QUIZ_ID", 1);
                    intent.putExtra("COURSE_TITLE", "Pengetahuan Kuantitatif");
                    intent.putExtra("COURSE_IMAGE_RES_ID", R.drawable.pengetahuan_kuantitatif);
                    startActivity(intent);
                }
            });
            Log.d(TAG, "✅ Set onClick PK");
        } else {
            Log.e(TAG, "❌ btnLanjut PK tidak ditemukan!");
        }
    }

    /**
     * Setup Kartu Penalaran Matematika (Quiz ID 2)
     */
    private void setupPenalaranMatematika() {
        Log.d(TAG, "--- Setting up Penalaran Matematika ---");

        View cardPM = findViewById(R.id.card_pm);

        if (cardPM == null) {
            Log.e(TAG, "❌ FATAL: card_pm tidak ditemukan di layout!");
            return;
        }

        Log.d(TAG, "✅ card_pm ditemukan");

        TextView tvName = cardPM.findViewById(R.id.course_name_textview);
        ImageView ivImage = cardPM.findViewById(R.id.course_image_imageview);
        Button btnLanjut = cardPM.findViewById(R.id.btnLanjut);

        if (tvName != null) {
            tvName.setText("Penalaran\nMatematika");
            Log.d(TAG, "✅ Set nama PM");
        } else {
            Log.e(TAG, "❌ tvName PM tidak ditemukan!");
        }

        if (ivImage != null) {
            ivImage.setImageResource(R.drawable.penalaran_matematika);
            Log.d(TAG, "✅ Set gambar PM");
        } else {
            Log.e(TAG, "❌ ivImage PM tidak ditemukan!");
        }

        if (btnLanjut != null) {
            btnLanjut.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Log.d(TAG, "========================================");
                    Log.d(TAG, "🔘 KLIK BUTTON: Penalaran Matematika");
                    Log.d(TAG, "📤 Mengirim Quiz ID: 2");
                    Log.d(TAG, "========================================");

                    Intent intent = new Intent(MainActivity.this, CourseDetailActivity.class);
                    intent.putExtra("QUIZ_ID", 2);
                    intent.putExtra("COURSE_TITLE", "Penalaran Matematika");
                    intent.putExtra("COURSE_IMAGE_RES_ID", R.drawable.penalaran_matematika);
                    startActivity(intent);
                }
            });
            Log.d(TAG, "✅ Set onClick PM");
        } else {
            Log.e(TAG, "❌ btnLanjut PM tidak ditemukan!");
        }
    }
}