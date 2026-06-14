package com.example.dhanerrognirnoy;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.widget.LinearLayout;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

public class MainActivity extends AppCompatActivity {

    LinearLayout btnTakePhoto, btnUploadPhoto, btnChat, cardDescription, cardResult, btnChangeLanguage;

    @Override
    protected void attachBaseContext(Context newBase) {
        super.attachBaseContext(LocaleHelper.setLocale(newBase));
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        btnTakePhoto = findViewById(R.id.btnTakePhoto);
        btnUploadPhoto = findViewById(R.id.btnUploadPhoto);
        btnChat = findViewById(R.id.btnChat);
        cardDescription = findViewById(R.id.cardDescription);
        cardResult = findViewById(R.id.cardResult);
        btnChangeLanguage = findViewById(R.id.btnChangeLanguage);

        btnTakePhoto.setOnClickListener(v -> openPredict("camera"));
        btnUploadPhoto.setOnClickListener(v -> openPredict("gallery"));

        btnChat.setOnClickListener(v ->
                startActivity(new Intent(MainActivity.this, ChatActivity.class))
        );

        btnChangeLanguage.setOnClickListener(v ->
                startActivity(new Intent(MainActivity.this, LanguageActivity.class))
        );

        cardDescription.setOnClickListener(v ->
                startActivity(new Intent(MainActivity.this, DiseaseInfoActivity.class))
        );

        cardResult.setOnClickListener(v -> {

            String disease = getSharedPreferences("LastResult", MODE_PRIVATE)
                    .getString("disease", "");

            float confidence = getSharedPreferences("LastResult", MODE_PRIVATE)
                    .getFloat("confidence", 0);

            String model = getSharedPreferences("LastResult", MODE_PRIVATE)
                    .getString("model", "Unknown");

            float inferenceTime = getSharedPreferences("LastResult", MODE_PRIVATE)
                    .getFloat("inference_time", 0);

            String imageUri = getSharedPreferences("LastResult", MODE_PRIVATE)
                    .getString("imageUri", "");

            if (disease.isEmpty()) {
                Toast.makeText(this, "No previous result found", Toast.LENGTH_SHORT).show();
            } else {
                Intent intent = new Intent(MainActivity.this, ResultActivity.class);
                intent.putExtra("disease", disease);
                intent.putExtra("confidence", (double) confidence);
                intent.putExtra("model", model);
                intent.putExtra("inference_time", (double) inferenceTime);
                intent.putExtra("imageUri", imageUri);
                startActivity(intent);
            }
        });
    }

    private void openPredict(String source) {
        Intent intent = new Intent(MainActivity.this, PredictActivity.class);
        intent.putExtra("source", source);
        startActivity(intent);
    }
}