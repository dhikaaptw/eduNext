package com.example.edunext;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.view.inputmethod.EditorInfo;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.edunext.database.Question;

import org.json.JSONObject;
import org.json.JSONArray;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class AiSenopatiDialog extends Dialog {

    private static final String TAG = "AiSenopatiDialog";
    private static final String API_URL = "https://senopati-elysia.vercel.app/api/chat";

    private EditText etMessage;
    private ImageButton btnSend, btnClose;
    private RecyclerView rvChatMessages;
    private LinearLayout layoutTyping;
    private ChatAdapter chatAdapter;
    private List<ChatMessage> chatMessages = new ArrayList<>();

    private Question currentQuestion;

    // Single OkHttpClient instance reused for all requests
    private final OkHttpClient httpClient;

    // Prevent overlapping sends
    private final AtomicBoolean isSending = new AtomicBoolean(false);

    // Constructor tanpa question
    public AiSenopatiDialog(@NonNull Context context) {
        super(context);
        this.currentQuestion = null;
        this.httpClient = buildHttpClient();
    }

    // Constructor dengan question
    public AiSenopatiDialog(@NonNull Context context, Question question) {
        super(context);
        this.currentQuestion = question;
        this.httpClient = buildHttpClient();
    }

    private OkHttpClient buildHttpClient() {
        return new OkHttpClient.Builder()
                .connectTimeout(30, TimeUnit.SECONDS)
                .writeTimeout(60, TimeUnit.SECONDS)
                .readTimeout(60, TimeUnit.SECONDS)
                .callTimeout(120, TimeUnit.SECONDS)
                .retryOnConnectionFailure(true)
                .build();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);

        View view = LayoutInflater.from(getContext()).inflate(R.layout.activity_ai_senopati, null);
        setContentView(view);

        // ========================================
        // FIX KEYBOARD: SET DIALOG FULLSCREEN + ADJUST RESIZE
        // ========================================
        Window window = getWindow();
        if (window != null) {
            WindowManager.LayoutParams params = window.getAttributes();
            params.width = ViewGroup.LayoutParams.MATCH_PARENT;
            params.height = ViewGroup.LayoutParams.MATCH_PARENT;
            window.setAttributes(params);

            // Biar keyboard tidak menutupi input
            window.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE);
        }

        Log.d(TAG, "AI Senopati Dialog Created");
        if (currentQuestion != null) {
            Log.d(TAG, "Question ID: " + currentQuestion.getQuestionId());
        }

        // Init Views
        etMessage = view.findViewById(R.id.etMessage);
        btnSend = view.findViewById(R.id.btnSend);
        btnClose = view.findViewById(R.id.btnClose);
        rvChatMessages = view.findViewById(R.id.rvChatMessages);
        layoutTyping = view.findViewById(R.id.layoutTyping);

        // Setup RecyclerView
        chatAdapter = new ChatAdapter(getContext(), chatMessages);
        rvChatMessages.setLayoutManager(new LinearLayoutManager(getContext()));
        rvChatMessages.setAdapter(chatAdapter);

        // Button enable/disable
        etMessage.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                boolean hasText = !s.toString().trim().isEmpty();
                btnSend.setEnabled(hasText);
                btnSend.setAlpha(hasText ? 1f : 0.5f);
            }

            @Override
            public void afterTextChanged(Editable s) {}
        });

        // Send on Enter
        etMessage.setOnEditorActionListener((v, actionId, event) -> {
            if (actionId == EditorInfo.IME_ACTION_SEND ||
                    (event != null && event.getKeyCode() == KeyEvent.KEYCODE_ENTER &&
                            event.getAction() == KeyEvent.ACTION_DOWN && !event.isShiftPressed())) {
                sendMessage();
                return true;
            }
            return false;
        });

        // Button clicks
        btnSend.setOnClickListener(v -> sendMessage());
        btnClose.setOnClickListener(v -> dismiss());

        // Auto-send question saat dialog dibuka
        if (currentQuestion != null) {
            autoSendQuestionContext();
        }
    }

    /**
     * Auto-send context soal ke AI dengan format baku
     */
    private void autoSendQuestionContext() {
        String questionText = currentQuestion.getQuestionText();
        List<String> options = currentQuestion.getOptions();

        // FORMAT BAKU: Langsung isi soal tanpa embel-embel
        StringBuilder contextMessage = new StringBuilder();
        contextMessage.append(questionText).append("\n\n");

        char optionLetter = 'A';
        for (String option : options) {
            contextMessage.append(optionLetter).append(". ").append(option).append("\n");
            optionLetter++;
        }

        String finalMessage = contextMessage.toString().trim();
        Log.d(TAG, "Auto-sending question: " + finalMessage);

        // Tambahkan pesan user ke chat UI
        ChatMessage userMsg = new ChatMessage("user", finalMessage);
        chatMessages.add(userMsg);
        chatAdapter.notifyItemInserted(chatMessages.size() - 1);
        rvChatMessages.scrollToPosition(chatMessages.size() - 1);

        // Tampilkan typing indicator
        layoutTyping.setVisibility(View.VISIBLE);

        // Kirim ke API
        sendMessageToAPI(finalMessage);
    }

    private void sendMessage() {
        String userMessage = etMessage.getText().toString().trim();

        if (userMessage.isEmpty()) {
            return;
        }

        Log.d(TAG, "Sending message: " + userMessage);

        ChatMessage userMsg = new ChatMessage("user", userMessage);
        chatMessages.add(userMsg);
        chatAdapter.notifyItemInserted(chatMessages.size() - 1);
        rvChatMessages.scrollToPosition(chatMessages.size() - 1);

        etMessage.setText("");
        layoutTyping.setVisibility(View.VISIBLE);

        sendMessageToAPI(userMessage);
    }

    /**
     * Method untuk mengirim pesan ke API dengan conversation history
     */
    private void sendMessageToAPI(String message) {
        // Prevent overlapping network requests
        if (!isSending.compareAndSet(false, true)) {
            Log.w(TAG, "A request is already in progress. Ignoring duplicate send.");
            return;
        }

        try {
            JSONObject payload = new JSONObject();
            payload.put("message", message);

            // Bangun conversation history dari chatMessages
            JSONArray messagesArray = new JSONArray();
            for (ChatMessage chatMsg : chatMessages) {
                JSONObject msgObj = new JSONObject();
                msgObj.put("role", chatMsg.getRole());
                msgObj.put("content", chatMsg.getContent());
                messagesArray.put(msgObj);
            }

            // Tambahkan pesan baru yang akan dikirim hanya jika belum ada di chatMessages
            boolean alreadyIncluded = false;
            if (!chatMessages.isEmpty()) {
                ChatMessage last = chatMessages.get(chatMessages.size() - 1);
                if ("user".equals(last.getRole()) && message.equals(last.getContent())) {
                    alreadyIncluded = true;
                }
            }
            if (!alreadyIncluded) {
                JSONObject newMsg = new JSONObject();
                newMsg.put("role", "user");
                newMsg.put("content", message);
                messagesArray.put(newMsg);
            } else {
                Log.d(TAG, "New message already present in chatMessages; skipping duplicate add.");
            }

            payload.put("messages", messagesArray);

            Log.d(TAG, "Payload length = " + payload.toString().length());
            Log.d(TAG, "Payload: " + payload.toString());

            MediaType JSON = MediaType.parse("application/json; charset=utf-8");
            RequestBody body = RequestBody.create(JSON, payload.toString());

            Request request = new Request.Builder()
                    .url(API_URL)
                    .post(body)
                    .addHeader("Content-Type", "application/json")
                    .build();

            // Async call
            httpClient.newCall(request).enqueue(new Callback() {
                @Override
                public void onFailure(@NonNull Call call, @NonNull IOException e) {
                    Log.e(TAG, "Request failed: " + e.getMessage(), e);
                    isSending.set(false);

                    layoutTyping.post(() -> {
                        layoutTyping.setVisibility(View.GONE);

                        ChatMessage errorMsg = new ChatMessage("assistant",
                                "Maaf, gagal terhubung ke Senopati. Cek koneksi internet Anda.");
                        chatMessages.add(errorMsg);
                        chatAdapter.notifyItemInserted(chatMessages.size() - 1);
                        rvChatMessages.scrollToPosition(chatMessages.size() - 1);
                    });
                }

                @Override
                public void onResponse(@NonNull Call call, @NonNull Response response) throws IOException {
                    isSending.set(false);
                    layoutTyping.post(() -> layoutTyping.setVisibility(View.GONE));

                    if (response.isSuccessful() && response.body() != null) {
                        try {
                            String responseBody = response.body().string();
                            Log.d(TAG, "Response: " + responseBody);

                            JSONObject json = new JSONObject(responseBody);

                            String aiReply = "";

                            if (json.has("data") && !json.isNull("data")) {
                                JSONObject dataObj = json.getJSONObject("data");
                                if (dataObj.has("reply") && !dataObj.isNull("reply")) {
                                    aiReply = dataObj.getString("reply");
                                }
                            }

                            if (aiReply == null || aiReply.isEmpty()) {
                                if (json.has("response")) {
                                    aiReply = json.getString("response");
                                } else if (json.has("reply")) {
                                    aiReply = json.getString("reply");
                                }
                            }

                            if (aiReply == null || aiReply.isEmpty()) {
                                aiReply = "Maaf, tidak ada respon dari Senopati.";
                            }

                            Log.d(TAG, "AI Reply: " + aiReply);

                            final String finalReply = aiReply;

                            rvChatMessages.post(() -> {
                                ChatMessage aiMsg = new ChatMessage("assistant", finalReply);
                                chatMessages.add(aiMsg);
                                chatAdapter.notifyItemInserted(chatMessages.size() - 1);
                                rvChatMessages.scrollToPosition(chatMessages.size() - 1);
                            });

                        } catch (Exception e) {
                            Log.e(TAG, "Parse error: " + e.getMessage(), e);

                            rvChatMessages.post(() -> {
                                ChatMessage errorMsg = new ChatMessage("assistant",
                                        "Maaf, terjadi kesalahan saat memproses respon.");
                                chatMessages.add(errorMsg);
                                chatAdapter.notifyItemInserted(chatMessages.size() - 1);
                                rvChatMessages.scrollToPosition(chatMessages.size() - 1);
                            });
                        } finally {
                            if (response.body() != null) response.close();
                        }
                    } else {
                        int code = response != null ? response.code() : -1;
                        Log.e(TAG, "Response code: " + code);

                        rvChatMessages.post(() -> {
                            ChatMessage errorMsg = new ChatMessage("assistant",
                                    "Senopati tidak dapat merespon saat ini. (Error " + code + ")");
                            chatMessages.add(errorMsg);
                            chatAdapter.notifyItemInserted(chatMessages.size() - 1);
                            rvChatMessages.scrollToPosition(chatMessages.size() - 1);
                        });

                        if (response.body() != null) response.close();
                    }
                }
            });

        } catch (Exception e) {
            isSending.set(false);
            Log.e(TAG, "Exception: " + e.getMessage(), e);
            layoutTyping.setVisibility(View.GONE);

            ChatMessage errorMsg = new ChatMessage("assistant",
                    "Maaf, terjadi kesalahan saat mengirim pesan.");
            chatMessages.add(errorMsg);
            chatAdapter.notifyItemInserted(chatMessages.size() - 1);
            rvChatMessages.scrollToPosition(chatMessages.size() - 1);
        }
    }
}
