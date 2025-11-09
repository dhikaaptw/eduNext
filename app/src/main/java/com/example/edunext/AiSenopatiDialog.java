package com.example.edunext;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.view.inputmethod.EditorInfo;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import org.json.JSONArray;
import org.json.JSONObject;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class AiSenopatiDialog extends Dialog {

    private EditText etMessage;
    private ImageButton btnSend, btnClose;
    private RecyclerView rvChatMessages;
    private LinearLayout layoutTyping;
    private ChatAdapter chatAdapter;
    private List<ChatMessage> chatMessages = new ArrayList<>();

    private static final String API_URL = "https://senopati-api.vercel.app/api/v1/chat";

    public AiSenopatiDialog(@NonNull Context context) {
        super(context);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        View view = LayoutInflater.from(getContext()).inflate(R.layout.activity_ai_senopati, null);
        setContentView(view);

        // 🔹 Init View
        etMessage = findViewById(R.id.etMessage);
        btnSend = findViewById(R.id.btnSend);
        btnClose = findViewById(R.id.btnClose);
        rvChatMessages = findViewById(R.id.rvChatMessages);
        layoutTyping = findViewById(R.id.layoutTyping);

        // 🔹 Setup RecyclerView
        chatAdapter = new ChatAdapter(getContext(), chatMessages);
        rvChatMessages.setLayoutManager(new LinearLayoutManager(getContext()));
        rvChatMessages.setAdapter(chatAdapter);

        // 🔹 Button enable/disable
        etMessage.addTextChangedListener(new TextWatcher() {
            @Override public void beforeTextChanged(CharSequence s, int start, int count, int after) {}
            @Override public void onTextChanged(CharSequence s, int start, int before, int count) {
                btnSend.setEnabled(!s.toString().trim().isEmpty());
                btnSend.setAlpha(s.toString().trim().isEmpty() ? 0.5f : 1f);
            }
            @Override public void afterTextChanged(Editable s) {}
        });

        // 🔹 Kirim pesan saat tekan Enter
        etMessage.setOnEditorActionListener((v, actionId, event) -> {
            if (actionId == EditorInfo.IME_ACTION_SEND ||
                    (event != null && event.getKeyCode() == KeyEvent.KEYCODE_ENTER && event.getAction() == KeyEvent.ACTION_DOWN && !event.isShiftPressed())) {
                sendMessage();
                return true;
            }
            return false;
        });

        // 🔹 Tombol kirim
        btnSend.setOnClickListener(v -> sendMessage());

        // 🔹 Tombol tutup
        btnClose.setOnClickListener(v -> dismiss());
    }

    // 🔹 Kirim pesan user ke Senopati API
    private void sendMessage() {
        String text = etMessage.getText().toString().trim();
        if (text.isEmpty()) return;

        // Tambahkan pesan user ke list
        ChatMessage userMsg = new ChatMessage("user", text);
        chatMessages.add(userMsg);
        chatAdapter.notifyItemInserted(chatMessages.size() - 1);
        rvChatMessages.scrollToPosition(chatMessages.size() - 1);

        etMessage.setText("");
        layoutTyping.setVisibility(View.VISIBLE);

        // 🔹 Buat payload JSON
        try {
            JSONObject payload = new JSONObject();
            payload.put("prompt", text);

            JSONArray msgArray = new JSONArray();
            msgArray.put(new JSONObject().put("role", "user").put("content", text));
            payload.put("messages", msgArray);

            MediaType JSON = MediaType.parse("application/json; charset=utf-8");
            RequestBody body = RequestBody.create(JSON, payload.toString());

            Request request = new Request.Builder()
                    .url(API_URL)
                    .post(body)
                    .build();

            OkHttpClient client = new OkHttpClient();
            client.newCall(request).enqueue(new Callback() {
                @Override
                public void onFailure(@NonNull Call call, @NonNull IOException e) {
                    e.printStackTrace();
                    layoutTyping.post(() -> layoutTyping.setVisibility(View.GONE));
                }

                @Override
                public void onResponse(@NonNull Call call, @NonNull Response response) throws IOException {
                    layoutTyping.post(() -> layoutTyping.setVisibility(View.GONE));

                    if (response.isSuccessful()) {
                        try {
                            String resStr = response.body().string();
                            JSONObject json = new JSONObject(resStr);
                            String reply = json.optString("reply", "(Tidak ada respon)");

                            // Tambahkan balasan AI ke list
                            ChatMessage aiMsg = new ChatMessage("assistant", reply);

                            rvChatMessages.post(() -> {
                                chatMessages.add(aiMsg);
                                chatAdapter.notifyItemInserted(chatMessages.size() - 1);
                                rvChatMessages.scrollToPosition(chatMessages.size() - 1);
                            });
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                }
            });

        } catch (Exception e) {
            e.printStackTrace();
            layoutTyping.setVisibility(View.GONE);
        }
    }
}
