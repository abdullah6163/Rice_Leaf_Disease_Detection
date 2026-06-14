package com.example.dhanerrognirnoy;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.content.res.Resources;

import java.util.Locale;

public class LocaleHelper {

    public static Context setLocale(Context context) {
        try {
            SharedPreferences prefs = context.getSharedPreferences("AppSettings", Context.MODE_PRIVATE);
            String lang = prefs.getString("language", "en");

            Locale locale = new Locale(lang);
            Locale.setDefault(locale);

            Resources resources = context.getResources();
            Configuration config = new Configuration(resources.getConfiguration());
            config.setLocale(locale);

            return context.createConfigurationContext(config);

        } catch (Exception e) {
            return context; // fallback (IMPORTANT)
        }
    }
}