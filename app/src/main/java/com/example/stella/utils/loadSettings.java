package com.example.stella.utils;

import android.app.Activity;
import android.content.Context;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.util.DisplayMetrics;

import com.example.stella.R;

import java.util.Locale;

/**
 * loadSettings es la clase que se utiliza para cargar la configuracion elegida por el usuario. Se utiliza al cargan una pantalla
 */

public class loadSettings {
    Context c;
    settings settings;

    public loadSettings(Context context){
        c = context;
        settings = new settings(context);
    }

    public void loadSettings(Activity activity){
        chargeLanguage();
        chargeTheme(activity);

    }

    /**
     * chargeTheme se encarga de cargar el tema que eligió el usuario
     * @param activity
     */

    private void chargeTheme(Activity activity){
        int theme = settings.getAppTheme();
        // Se establece un tema en la app dependiendo de la configuración guardada en settings
        if (theme == 0) {
            activity.setTheme(R.style.Theme_Stella); // Tema claro
        } else if (theme == 1) {
            activity.setTheme(R.style.Theme_dark); // Tu tema oscuro
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

    /**
     * chargeLanguage se encarga de cargar el lenguaje que eligió el usuario
     */

    private void chargeLanguage(){
        int language = settings.getAppLanguage();
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
    }
}
