package com.example.edunext;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;

public class MainActivity extends AppCompatActivity {

    // jika nanti butuh referensi, bisa jadikan field; sekarang local sudah cukup
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        initCourseButtonsInsideCards();
    }

    private void initCourseButtonsInsideCards() {
        // ambil root include (CardView) — id ini sudah ada di activity_main.xml
        CardView courseCard1 = findViewById(R.id.courseCard1);
        CardView courseCard2 = findViewById(R.id.courseCard2);
        CardView courseCard3 = findViewById(R.id.courseCard3);
        CardView courseCard4 = findViewById(R.id.courseCard4);
        CardView courseCard5 = findViewById(R.id.courseCard5);

        // safety: cek null karena jika include gagal atau id beda, jangan crash
        if (courseCard1 != null) attachButtonListener(courseCard1, "Pengetahuan Kuantitatif");
        if (courseCard2 != null) attachButtonListener(courseCard2, "Course 2");
        if (courseCard3 != null) attachButtonListener(courseCard3, "Course 3");
        if (courseCard4 != null) attachButtonListener(courseCard4, "Course 4");
        if (courseCard5 != null) attachButtonListener(courseCard5, "Course 5");
    }

    /**
     * Mencari Button R.id.btnLanjut DI DALAM include layout (cardRoot)
     * dan memasang listener yang membuka CourseDetailActivity.
     */
    private void attachButtonListener(CardView cardRoot, String courseName) {
        // cari button di dalam card (butuh android:id="@+id/btnLanjut" di item_course_card.xml)
        Button btn = cardRoot.findViewById(R.id.btnLanjut);

        if (btn == null) {
            // debug: jika null, jangan crash — (opsional) log atau toast
            return;
        }

        btn.setOnClickListener(v -> {
            Intent intent = new Intent(MainActivity.this, CourseDetailActivity.class);
            intent.putExtra("course_name", courseName); // opsional, kirim nama kursus
            startActivity(intent);
        });

        // penting: pastikan cardRoot tidak "consume" clickable sehingga swipe tetap bisa geser;
        // kita tidak mengubah clickable cardRoot di sini (biarkan default), karena btn menangani klik.
    }
}
