package com.example.edunext;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import java.util.List;

public class CourseAdapter extends RecyclerView.Adapter<CourseAdapter.CourseViewHolder> {

    private List<Course> courseList;
    private Context context;

    public CourseAdapter(Context context, List<Course> courseList) {
        this.context = context;
        this.courseList = courseList;
    }

    @NonNull
    @Override
    public CourseViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_course_card, parent, false);
        return new CourseViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull CourseViewHolder holder, int position) {
        Course course = courseList.get(position);

        holder.courseName.setText(course.getName());
        holder.courseImage.setImageResource(course.getImageResource());

        holder.lanjutButton.setOnClickListener(v -> {
            Intent intent = new Intent(context, QuizActivity.class);

            intent.putExtra("QUIZ_ID", course.getQuizId());

            context.startActivity(intent);
        });
    }

    @Override
    public int getItemCount() {
        return courseList.size();
    }

    public static class CourseViewHolder extends RecyclerView.ViewHolder {
        TextView courseName;
        ImageView courseImage;
        Button lanjutButton;

        public CourseViewHolder(@NonNull View itemView) {
            super(itemView);
            // Mengambil referensi dari item_course_card.xml
            // Ganti ID jika berbeda
            courseName = itemView.findViewById(R.id.course_name_textview); // Ganti dengan ID TextView nama kursus Anda
            courseImage = itemView.findViewById(R.id.course_image_imageview); // Ganti dengan ID ImageView kursus Anda
            lanjutButton = itemView.findViewById(R.id.btnLanjut); // ID ini sudah benar sesuai file Anda
        }
    }
}
    