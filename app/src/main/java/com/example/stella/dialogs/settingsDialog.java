package com.example.stella.dialogs;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;

import androidx.annotation.NonNull;

import com.example.stella.R;
import com.example.stella.utils.settings;

import java.util.Locale;

public class settingsDialog extends Dialog {

    settings settings;

    public settingsDialog(@NonNull Context context) {
        super(context);
        if (context instanceof Activity) {
            setOwnerActivity((Activity) context);
        }
        settings = new settings(context);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);

        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.bottomsheet_settings_layout);

        Spinner spinnerLanguage = findViewById(R.id.languageOptionsSpinner);
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(getContext(), R.array.language_array, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerLanguage.setAdapter(adapter);

        Spinner spinnerTheme = findViewById(R.id.themeOptionsSpinner);
        ArrayAdapter<CharSequence> adapterTheme = ArrayAdapter.createFromResource(getContext(), R.array.themes_array, android.R.layout.simple_spinner_item);
        adapterTheme.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerTheme.setAdapter(adapterTheme);

        Button btn_apply = findViewById(R.id.btn_apply);


        int language = settings.getAppLanguage();
        int appTheme = settings.getAppTheme();

        spinnerLanguage.setSelection(language, false);
        spinnerTheme.setSelection(appTheme, false);



        spinnerLanguage.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {

            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });

        btn_apply.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String language = spinnerLanguage.getSelectedItem().toString();
                String theme = spinnerTheme.getSelectedItem().toString();
                setSettings(language, theme);
                getOwnerActivity().recreate();
            }
        });

        getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        getWindow().getAttributes().windowAnimations = R.style.DialogAnimationBottom;
        getWindow().setGravity(Gravity.BOTTOM);
    }

    @Override
    public void dismiss() {
        if (getOwnerActivity() != null && !getOwnerActivity().isFinishing()) {
            super.dismiss();
        }
    }


    private void setSettings(String language, String theme){


        // Setting language
        String nameSimplified = "";
        String spanish = getContext().getResources().getString(R.string.spanish);
        String english = getContext().getResources().getString(R.string.english);
        if (language.equals(spanish)) {
            nameSimplified = "es";
            settings.setLanguage(0);
        } else if (language.equals(english)) {
            nameSimplified = "en";
            settings.setLanguage(1);
        }



        // Setting theme
        String light = getContext().getResources().getString(R.string.theme_Light);
        String dark = getContext().getResources().getString(R.string.theme_Dark);
        String ocher = getContext().getResources().getString(R.string.theme_Ocher);
        String blue = getContext().getResources().getString(R.string.theme_blue);
        String pink = getContext().getResources().getString(R.string.theme_pink);
        String violet = getContext().getResources().getString(R.string.theme_violet);
        String green = getContext().getResources().getString(R.string.theme_green);

        if(theme.equals(light)){
            settings.setAppTheme(0);
            getOwnerActivity().setTheme(R.style.Theme_Stella);
        } else if(theme.equals(dark)){
            settings.setAppTheme(1);
            //TODO PONER EL DARK THEME
        } else if(theme.equals(ocher)){
            settings.setAppTheme(2);
            getOwnerActivity().getApplicationContext().setTheme(R.style.Theme_ocher);
        } else if (theme.equals(blue)) {
            settings.setAppTheme(3);
            getOwnerActivity().getApplicationContext().setTheme(R.style.Theme_blue);
        }else if (theme.equals(pink)) {
            settings.setAppTheme(4);
            getOwnerActivity().getApplicationContext().setTheme(R.style.Theme_pink);
        }else if (theme.equals(violet)) {
            settings.setAppTheme(5);
            getOwnerActivity().getApplicationContext().setTheme(R.style.Theme_violet);
        }else if (theme.equals(green)) {
            settings.setAppTheme(6);
            getOwnerActivity().getApplicationContext().setTheme(R.style.Theme_green);
        }

        setLocale(nameSimplified);

    }

    private void setLocale(String lang) {
        Locale myLocale = new Locale(lang);
        Resources res = getContext().getResources();
        DisplayMetrics dm = res.getDisplayMetrics();
        Configuration conf = res.getConfiguration();
        conf.locale = myLocale;
        res.updateConfiguration(conf, dm);
    }
}
