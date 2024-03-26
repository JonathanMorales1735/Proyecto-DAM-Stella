package com.example.stella.utils;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.util.DisplayMetrics;

import java.util.Locale;

public class loadSettings {
    Context c;

    public loadSettings(Context context){
        c = context;
    }

    public void loadSettings(){
        chargeLanguage();
        //TODO AÑADIR EL THEME
    }

    private void chargeLanguage(){
        SharedPreferences loadSettings = c.getSharedPreferences("generalSettings", 0);
        int language = loadSettings.getInt("language", 0);
        String lang = "";

        if(language == 0){
            lang = "es";
        } else if (language == 1){
            lang = "en";
        }

        Locale myLocale = new Locale(lang);
        Resources res = c.getResources();
        DisplayMetrics dm = res.getDisplayMetrics();
        Configuration conf = res.getConfiguration();
        conf.locale = myLocale;
        res.updateConfiguration(conf, dm);
        Locale.setDefault(myLocale);
        //onConfigurationChanged(conf);
    }
}
