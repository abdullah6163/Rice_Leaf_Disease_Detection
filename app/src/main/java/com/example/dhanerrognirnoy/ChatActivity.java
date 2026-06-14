package com.example.dhanerrognirnoy;

import android.content.Context;
import android.os.Bundle;
import android.view.Gravity;
import android.widget.*;

import androidx.appcompat.app.AppCompatActivity;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ChatActivity extends AppCompatActivity {

    LinearLayout chatContainer;
    EditText edtMessage;
    Button btnSend;
    ScrollView chatScroll;

    @Override
    protected void attachBaseContext(Context newBase) {
        super.attachBaseContext(LocaleHelper.setLocale(newBase));
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);

        chatContainer = findViewById(R.id.chatContainer);
        edtMessage = findViewById(R.id.edtMessage);
        btnSend = findViewById(R.id.btnSend);
        chatScroll = findViewById(R.id.chatScroll);

        addBotMessage("Hello! Ask me about rice diseases 🌾");

        String autoMessage = getIntent().getStringExtra("autoMessage");

        if (autoMessage != null && !autoMessage.isEmpty()) {
            addUserMessage(autoMessage);
            sendMessageToApi(autoMessage);
        }

        btnSend.setOnClickListener(v -> {
            String message = edtMessage.getText().toString().trim();

            if (!message.isEmpty()) {
                addUserMessage(message);
                edtMessage.setText("");
                sendMessageToApi(message);
            }
        });
    }

    private void sendMessageToApi(String message) {

        addBotMessage("Typing...");

        // Chat now uses Hugging Face backend
        ApiService apiService =
                ApiClient.getResNetClient().create(ApiService.class);

        Call<ChatResponse> call =
                apiService.sendMessage(new ChatRequest(message));

        call.enqueue(new Callback<ChatResponse>() {

            @Override
            public void onResponse(Call<ChatResponse> call, Response<ChatResponse> response) {

                removeLastMessage();

                if (response.isSuccessful() && response.body() != null) {

                    ChatResponse result = response.body();

                    if (result.success) {
                        addBotMessage(result.reply);
                    } else {
                        addBotMessage("Error: " + result.error);
                    }

                } else {
                    addBotMessage("Failed to get response");
                }
            }

            @Override
            public void onFailure(Call<ChatResponse> call, Throwable t) {
                removeLastMessage();
                addBotMessage("Error: " + t.getMessage());
            }
        });
    }

    private void addUserMessage(String message) {
        addMessage(message, true);
    }

    private void addBotMessage(String message) {
        addMessage(message, false);
    }

    private void addMessage(String message, boolean isUser) {

        TextView textView = new TextView(this);
        textView.setText(message);
        textView.setTextSize(15);
        textView.setPadding(20, 12, 20, 12);

        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.WRAP_CONTENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
        );

        params.setMargins(8, 8, 8, 8);

        if (isUser) {
            textView.setBackgroundColor(0xFFDCF8C6);
            params.gravity = Gravity.RIGHT;
        } else {
            textView.setBackgroundColor(0xFFFFFFFF);
            params.gravity = Gravity.LEFT;
        }

        textView.setLayoutParams(params);
        chatContainer.addView(textView);

        chatScroll.post(() -> chatScroll.fullScroll(ScrollView.FOCUS_DOWN));
    }

    private void removeLastMessage() {
        int count = chatContainer.getChildCount();
        if (count > 0) {
            chatContainer.removeViewAt(count - 1);
        }
    }
}