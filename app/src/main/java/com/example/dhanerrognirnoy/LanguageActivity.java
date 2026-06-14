package com.example.dhanerrognirnoy;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.Button;

import androidx.appcompat.app.AppCompatActivity;

public class LanguageActivity extends AppCompatActivity {

    Button btnBangla, btnEnglish, btnHindi, btnUrdu;

    @Override
    protected void attachBaseContext(Context newBase) {
        super.attachBaseContext(LocaleHelper.setLocale(newBase));
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_language);

        btnBangla = findViewById(R.id.btnBangla);
        btnEnglish = findViewById(R.id.btnEnglish);
        btnHindi = findViewById(R.id.btnHindi);
        btnUrdu = findViewById(R.id.btnUrdu);

        btnBangla.setOnClickListener(v -> saveLanguage("bn"));
        btnEnglish.setOnClickListener(v -> saveLanguage("en"));
        btnHindi.setOnClickListener(v -> saveLanguage("hi"));
        btnUrdu.setOnClickListener(v -> saveLanguage("ur"));
    }

    private void saveLanguage(String langCode) {
        SharedPreferences prefs = getSharedPreferences("AppSettings", MODE_PRIVATE);
        prefs.edit().putString("language", langCode).apply();

        Intent intent = new Intent(LanguageActivity.this, MainActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);
        finish();
    }
}