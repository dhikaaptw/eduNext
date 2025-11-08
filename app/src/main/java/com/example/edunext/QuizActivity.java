package com.example.edunext; // sesuaikan package

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
    private Button btnKembali, btnSelesai;

    private List<Question> questions = new ArrayList<>();
    private int currentIndex = 0;
    private boolean isAnswerRevealed = false; // penting: track apakah jawaban sudah di-*reveal*

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_quiz);

        // find views (sesuaikan id)
        tvSoal = findViewById(R.id.tvSoal);
        rgPilihan = findViewById(R.id.rgPilihan);
        rb1 = findViewById(R.id.rbPilihan1);
        rb2 = findViewById(R.id.rbPilihan2);
        rb3 = findViewById(R.id.rbPilihan3);
        rb4 = findViewById(R.id.rbPilihan4);
        rb5 = findViewById(R.id.rbPilihan5);
        btnKembali = findViewById(R.id.btnKembali);
        btnSelesai = findViewById(R.id.btnSelesai);

        // load soal
        loadQuestions();

        // tampilkan soal awal
        showQuestion(currentIndex);

        // ketika user klik pilihan -> hanya preview (checked icon + preview bg)
        rgPilihan.setOnCheckedChangeListener((group, checkedId) -> {
            if (checkedId == -1) return;
            int selectedIndex = radioIdToIndex(checkedId);
            // preview only: tidak reveal benar/salah
            previewSelection(selectedIndex);
        });

        // tombol kembali
        btnKembali.setOnClickListener(v -> {
            if (isAnswerRevealed) {
                // kalau sedang dalam state revealed, kembali ke state preview (atau clear) pada soal yang sama
                isAnswerRevealed = false;
                previewResetUI(); // kembalikan ke kondisi awal (tapi tetap di soal yg sama)
                updateBottomButtonText();
                return;
            }

            if (currentIndex > 0) {
                currentIndex--;
                showQuestion(currentIndex);
                isAnswerRevealed = false;
                previewResetUI();
                updateBottomButtonText();
            } else {
                // jika sudah di soal pertama, behavior: finish activity atau disable
                finish();
            }
        });

        // tombol Lanjut / Selesai
        btnSelesai.setOnClickListener(v -> {
            int checkedId = rgPilihan.getCheckedRadioButtonId();
            if (checkedId == -1) {
                Toast.makeText(QuizActivity.this, "Pilih jawaban dulu", Toast.LENGTH_SHORT).show();
                return;
            }
            int selectedIndex = radioIdToIndex(checkedId);

            if (!isAnswerRevealed) {
                // pertama kali klik -> reveal final (warna hijau/merah)
                revealAnswer(selectedIndex);
                isAnswerRevealed = true;
                updateBottomButtonText(); // ubah teks ke "Selesai" jika soal terakhir (tetap)
                // lanjut otomatis setelah delay supaya user lihat feedback
                new Handler().postDelayed(() -> {
                    if (currentIndex < questions.size() - 1) {
                        currentIndex++;
                        isAnswerRevealed = false;
                        showQuestion(currentIndex);
                        previewResetUI();
                        updateBottomButtonText();
                    } else {
                        // sudah soal terakhir -> submit/finish
                        submitQuiz();
                    }
                }, 700); // delay 700ms (sesuaikan)
            } else {
                // jika sudah revealed (user tekan lagi), langsung pindah (fallback)
                if (currentIndex < questions.size() - 1) {
                    currentIndex++;
                    isAnswerRevealed = false;
                    showQuestion(currentIndex);
                    previewResetUI();
                    updateBottomButtonText();
                } else {
                    submitQuiz();
                }
            }
        });

        updateBottomButtonText();
    }

    // ---------- Helper: loadQuestions ----------
    private void loadQuestions() {
        questions.clear();
        questions.add(new Question(
                "Harga 5 buku dan 3 pensil adalah Rp41.000. Sedangkan harga 3 buku dan 2 pensil adalah Rp25.000. Berapakah harga sebuah buku?",
                new ArrayList<>(Arrays.asList("Rp5.000","Rp6.000","Rp7.000","Rp8.000","Rp9.000")),
                2)); // index 2 => Rp7.000

        questions.add(new Question(
                "Diketahui barisan aritmatika dengan suku ke-3 adalah 11 dan suku ke-7 adalah 23. Jumlah 10 suku pertama barisan tersebut adalah...",
                new ArrayList<>(Arrays.asList("155","165","175","185","195")),
                2));

        questions.add(new Question(
                "Perbandingan uang Ali dan Budi adalah 3 : 5. Jika jumlah uang mereka Rp800.000, maka selisih uang mereka adalah...",
                new ArrayList<>(Arrays.asList("Rp100.000","Rp150.000","Rp200.000","Rp250.000","Rp300.000")),
                2));
    }

    // ---------- tampilkan soal ----------
    private void showQuestion(int index) {
        Question q = questions.get(index);
        tvSoal.setText(q.getText());

        List<String> opts = q.getOptions();
        rb1.setText(opts.get(0));
        rb2.setText(opts.get(1));
        rb3.setText(opts.get(2));
        rb4.setText(opts.get(3));
        rb5.setText(opts.get(4));

        // set default icons & backgrounds
        Drawable unchecked = ContextCompat.getDrawable(this, R.drawable.ic_radio_unchecked);
        setRadioDrawableEnd(rb1, unchecked);
        setRadioDrawableEnd(rb2, unchecked);
        setRadioDrawableEnd(rb3, unchecked);
        setRadioDrawableEnd(rb4, unchecked);
        setRadioDrawableEnd(rb5, unchecked);

        rb1.setBackground(ContextCompat.getDrawable(this, R.drawable.option_bg));
        rb2.setBackground(ContextCompat.getDrawable(this, R.drawable.option_bg));
        rb3.setBackground(ContextCompat.getDrawable(this, R.drawable.option_bg));
        rb4.setBackground(ContextCompat.getDrawable(this, R.drawable.option_bg));
        rb5.setBackground(ContextCompat.getDrawable(this, R.drawable.option_bg));

        // clear selection (we clear now when switching question)
        rgPilihan.setOnCheckedChangeListener(null);
        rgPilihan.clearCheck();
        rgPilihan.setOnCheckedChangeListener((group, checkedId) -> {
            if (checkedId == -1) return;
            previewSelection(radioIdToIndex(checkedId));
        });
    }

    // ---------- previewSelection: HANYA preview ----------
    private void previewSelection(int selectedIndex) {
        Drawable checked = ContextCompat.getDrawable(this, R.drawable.ic_radio_checked);
        Drawable unchecked = ContextCompat.getDrawable(this, R.drawable.ic_radio_unchecked);

        // set all to unchecked + default bg
        setRadioDrawableEnd(rb1, unchecked);
        setRadioDrawableEnd(rb2, unchecked);
        setRadioDrawableEnd(rb3, unchecked);
        setRadioDrawableEnd(rb4, unchecked);
        setRadioDrawableEnd(rb5, unchecked);

        rb1.setBackground(ContextCompat.getDrawable(this, R.drawable.option_bg));
        rb2.setBackground(ContextCompat.getDrawable(this, R.drawable.option_bg));
        rb3.setBackground(ContextCompat.getDrawable(this, R.drawable.option_bg));
        rb4.setBackground(ContextCompat.getDrawable(this, R.drawable.option_bg));
        rb5.setBackground(ContextCompat.getDrawable(this, R.drawable.option_bg));

        RadioButton selectedRb = getRadioButtonByIndex(selectedIndex);
        if (selectedRb != null) {
            setRadioDrawableEnd(selectedRb, checked);
            selectedRb.setBackground(ContextCompat.getDrawable(this, R.drawable.option_bg));
        }
    }

    private void revealAnswer(int selectedIndex) {
        int correct = questions.get(currentIndex).getCorrectIndex();

        // set all to unchecked icon removed, we'll set checked icon to correct and selected
        Drawable checked = ContextCompat.getDrawable(this, R.drawable.ic_radio_checked);
        Drawable unchecked = ContextCompat.getDrawable(this, R.drawable.ic_radio_unchecked);

        setRadioDrawableEnd(rb1, unchecked);
        setRadioDrawableEnd(rb2, unchecked);
        setRadioDrawableEnd(rb3, unchecked);
        setRadioDrawableEnd(rb4, unchecked);
        setRadioDrawableEnd(rb5, unchecked);

        RadioButton selectedRb = getRadioButtonByIndex(selectedIndex);
        RadioButton correctRb = getRadioButtonByIndex(correct);

        // show final states
        if (selectedIndex == correct) {
            // correct pick
            if (selectedRb != null) {
                setRadioDrawableEnd(selectedRb, checked);
                selectedRb.setBackground(ContextCompat.getDrawable(this, R.drawable.bg_option_correct));
            }
        } else {
            // wrong pick -> red, correct -> green
            if (selectedRb != null) {
                setRadioDrawableEnd(selectedRb, checked);
                selectedRb.setBackground(ContextCompat.getDrawable(this, R.drawable.bg_option_incorrect));
            }
            if (correctRb != null) {
                setRadioDrawableEnd(correctRb, checked);
                correctRb.setBackground(ContextCompat.getDrawable(this, R.drawable.bg_option_correct));
            }
        }
    }

    private void previewResetUI() {
        Drawable unchecked = ContextCompat.getDrawable(this, R.drawable.ic_radio_unchecked);
        setRadioDrawableEnd(rb1, unchecked);
        setRadioDrawableEnd(rb2, unchecked);
        setRadioDrawableEnd(rb3, unchecked);
        setRadioDrawableEnd(rb4, unchecked);
        setRadioDrawableEnd(rb5, unchecked);

        rb1.setBackground(ContextCompat.getDrawable(this, R.drawable.option_bg));
        rb2.setBackground(ContextCompat.getDrawable(this, R.drawable.option_bg));
        rb3.setBackground(ContextCompat.getDrawable(this, R.drawable.option_bg));
        rb4.setBackground(ContextCompat.getDrawable(this, R.drawable.option_bg));
        rb5.setBackground(ContextCompat.getDrawable(this, R.drawable.option_bg));
        // do not call rgPilihan.clearCheck() here unless you intentionally want to clear selection
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
        if (rb == null) return;
        if (d == null) {
            rb.setCompoundDrawablesWithIntrinsicBounds(null, null, null, null);
            return;
        }
        d.setBounds(0, 0, d.getIntrinsicWidth(), d.getIntrinsicHeight());
        rb.setCompoundDrawablesWithIntrinsicBounds(null, null, d, null);
    }

    private void updateBottomButtonText() {
        if (currentIndex < questions.size() - 1) {
            btnSelesai.setText("Lanjut");
        } else {
            btnSelesai.setText("Selesai");
        }
    }

    private void submitQuiz() {
        finish();
    }
}
