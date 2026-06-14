package com.example.dhanerrognirnoy;

import java.util.concurrent.TimeUnit;

import okhttp3.OkHttpClient;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class ApiClient {

    // 🔥 MAIN MODEL (ResNet - Hugging Face)
    private static final String RESNET_URL =
            "https://Fahim6163-rice-resnet-backend.hf.space/";

    // 🔥 FALLBACK MODEL (MobileNet - Render)
    private static final String MOBILENET_URL =
            "https://rice-backend-cjko.onrender.com/";

    private static Retrofit resnetRetrofit = null;
    private static Retrofit mobilenetRetrofit = null;

    // 🔥 Important: increase timeout (HF model takes time)
    private static OkHttpClient getClient() {
        return new OkHttpClient.Builder()
                .connectTimeout(90, TimeUnit.SECONDS)
                .readTimeout(120, TimeUnit.SECONDS)
                .writeTimeout(120, TimeUnit.SECONDS)
                .retryOnConnectionFailure(true)
                .build();
    }

    // =========================
    // RESNET CLIENT (PRIMARY)
    // =========================
    public static Retrofit getResNetClient() {

        if (resnetRetrofit == null) {
            resnetRetrofit = new Retrofit.Builder()
                    .baseUrl(RESNET_URL)
                    .client(getClient())
                    .addConverterFactory(GsonConverterFactory.create())
                    .build();
        }

        return resnetRetrofit;
    }

    // =========================
    // MOBILENET CLIENT (FALLBACK)
    // =========================
    public static Retrofit getMobileNetClient() {

        if (mobilenetRetrofit == null) {
            mobilenetRetrofit = new Retrofit.Builder()
                    .baseUrl(MOBILENET_URL)
                    .client(getClient())
                    .addConverterFactory(GsonConverterFactory.create())
                    .build();
        }

        return mobilenetRetrofit;
    }
}