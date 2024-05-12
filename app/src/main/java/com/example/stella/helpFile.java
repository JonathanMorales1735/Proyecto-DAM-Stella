package com.example.stella;

import android.os.Bundle;
import android.webkit.WebSettings;
import android.webkit.WebView;

import androidx.appcompat.app.AppCompatActivity;

public class helpFile extends AppCompatActivity {
    private WebView webView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.help_file);

        webView = findViewById(R.id.webView);

        // Habilitar JavaScript (opcional)
        WebSettings webSettings = webView.getSettings();
        webSettings.setJavaScriptEnabled(true);

        // Cargar el PDF desde Google Drive
        webView.loadUrl("https://drive.google.com/file/d/1utinqYN-ZCFLLY5ppBLjuBaZkKjtryUP/view?usp=drive_link");
    }
}
