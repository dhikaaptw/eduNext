package com.example.edunext;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.LinearLayout;

import androidx.appcompat.app.AppCompatActivity;

public class MainActivity extends AppCompatActivity {

    private LinearLayout courseCard1;
    private LinearLayout courseCard2;
    private LinearLayout courseCard3;
    private LinearLayout courseCard4;
    private LinearLayout courseCard5;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Initialize course cards
        initCourseCards();

        // Set click listeners
        setCourseClickListeners();
    }

    private void initCourseCards() {
        // Get references to your course cards
        // Assuming your course cards have IDs in item_course_card.xml
        LinearLayout courseListContainer = findViewById(R.id.courseListContainer);

        if (courseListContainer != null && courseListContainer.getChildCount() > 0) {
            // Get each card (assuming they are direct children)
            if (courseListContainer.getChildCount() > 0)
                courseCard1 = (LinearLayout) courseListContainer.getChildAt(0);
            if (courseListContainer.getChildCount() > 1)
                courseCard2 = (LinearLayout) courseListContainer.getChildAt(1);
            if (courseListContainer.getChildCount() > 2)
                courseCard3 = (LinearLayout) courseListContainer.getChildAt(2);
            if (courseListContainer.getChildCount() > 3)
                courseCard4 = (LinearLayout) courseListContainer.getChildAt(3);
            if (courseListContainer.getChildCount() > 4)
                courseCard5 = (LinearLayout) courseListContainer.getChildAt(4);
        }
    }

    private void setCourseClickListeners() {
        // Set click listener for first card (Pengetahuan Kuantitatif)
        if (courseCard1 != null) {
            courseCard1.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    openCourseDetail("Pengetahuan Kuantitatif");
                }
            });
        }

        // Set click listeners for other cards
        if (courseCard2 != null) {
            courseCard2.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    openCourseDetail("Course 2");
                }
            });
        }

        if (courseCard3 != null) {
            courseCard3.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    openCourseDetail("Course 3");
                }
            });
        }

        if (courseCard4 != null) {
            courseCard4.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    openCourseDetail("Course 4");
                }
            });
        }

        if (courseCard5 != null) {
            courseCard5.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    openCourseDetail("Course 5");
                }
            });
        }
    }

    private void openCourseDetail(String courseName) {
        Intent intent = new Intent(MainActivity.this, CourseDetailActivity.class);
        intent.putExtra("course_name", courseName);
        startActivity(intent);
    }
}