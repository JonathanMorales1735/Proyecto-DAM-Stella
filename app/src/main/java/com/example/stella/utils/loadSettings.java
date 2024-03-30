package com.example.stella.utils;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.util.DisplayMetrics;

import com.example.stella.R;

import java.util.Locale;

public class loadSettings {
    Context c;

    public loadSettings(Context context){
        c = context;

    }

    public void loadSettings(Activity activity){
        chargeLanguage();
        chargeTheme(activity);

    }

    private void chargeTheme(Activity activity){
        SharedPreferences loadSettings = c.getSharedPreferences("generalSettings", 0);
        int theme = loadSettings.getInt("appTheme", 0);

        if (theme == 0) {
            activity.setTheme(R.style.Theme_Stella); // Tema claro
        } else if (theme == 1) {
            //activity.setTheme(R.style.TuTemaDark); // Tu tema oscuro
        } else if (theme == 2) {
            activity.setTheme(R.style.Theme_ocher); // Tema ocher
        } else if (theme == 3){
            activity.setTheme(R.style.Theme_blue); // Tema blue
        }else if (theme == 4){
            activity.setTheme(R.style.Theme_pink); // Tema pink
        }else if (theme == 5){
            activity.setTheme(R.style.Theme_violet); // Tema violet
        } else if(theme == 6){
            activity.setTheme(R.style.Theme_green); // Tema green
        }

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
