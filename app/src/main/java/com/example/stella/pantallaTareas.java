package com.example.stella;

import static android.content.ContentValues.TAG;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.Dialog;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Build;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageButton;
import android.widget.Spinner;
import android.widget.TextView;

import com.example.stella.reciclerViewsAdapters.listCompletedTasksAdapter;
import com.example.stella.reciclerViewsAdapters.listPendingTasksAdapter;
import com.example.stella.utils.loadSettings;
import com.google.firebase.auth.FirebaseAuth;

import java.util.Locale;

public class pantallaTareas extends AppCompatActivity {

    private FirebaseAuth mAuth;
    String userName;
    RecyclerView recyclerViewPending, recyclerViewCompleted;
    ImageButton btnNewTask;
    listPendingTasksAdapter adapterPendingTasks;
    listCompletedTasksAdapter adapterCompletedTasks;
    private Dialog bottomDialog, leftDialog = null;
    com.example.stella.utils.loadSettings loadSettings;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        loadSettings = new loadSettings(this);
        loadSettings.loadSettings();
        setContentView(R.layout.pantallatareas);

        ActionBar actionBar = getSupportActionBar();

        mAuth = FirebaseAuth.getInstance();
        userName = String.valueOf(mAuth.getCurrentUser().getDisplayName());

        Toolbar toolbar = findViewById(R.id.toolbarPantallaTareas);
        recyclerViewPending = findViewById(R.id.recyclerPendingTasks);
        recyclerViewCompleted = findViewById(R.id.recyclerCompletedTasks);
        btnNewTask = (ImageButton) findViewById(R.id.btnNewTask);
        btnNewTask.bringToFront();


        adapterPendingTasks = new listPendingTasksAdapter(this);
        adapterCompletedTasks = new listCompletedTasksAdapter(this);
        adapterPendingTasks.auxSetListCompletedTasksAdapter(adapterCompletedTasks);
        adapterCompletedTasks.auxSetListPendingTasksAdapter(adapterPendingTasks);

        createNotificationChannel();
        setSupportActionBar(toolbar);
    }

    @Override
    public void onStart() {
        super.onStart();
        setRecyclerViewPending();
        setRecyclerViewCompleted();
    }

    private void createNotificationChannel(){
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.O){
            CharSequence name = "Channel";
            String desc = "Channel description";
            int imp = NotificationManager.IMPORTANCE_HIGH;
            NotificationChannel channel = new NotificationChannel("channelNotification", name, imp);
            channel.setDescription(desc);

            NotificationManager notificationManager = getSystemService(NotificationManager.class);
            notificationManager.createNotificationChannel(channel);
            Log.i(TAG, "Canal de notificaciones creado");
        }
    }



    /**
     * Este método cierra la sesión del usuario. Llevándolo de vuelta a la pantalla para Iniciar sesión.
     * @param view
     */

    public void logOut(View view){
        FirebaseAuth.getInstance().signOut();
        returnMainScreen();
    }

    private void setRecyclerViewCompleted(){
        adapterCompletedTasks.fillCompletedTasks();
        recyclerViewCompleted.setHasFixedSize(true);
        recyclerViewCompleted.setLayoutManager(new LinearLayoutManager(this));
        recyclerViewCompleted.setNestedScrollingEnabled(false);
        recyclerViewCompleted.setAdapter(adapterCompletedTasks);

    }

    public void setRecyclerViewPending(){
        adapterPendingTasks.fillPendingTasks();
        recyclerViewPending.setHasFixedSize(true);
        recyclerViewPending.setLayoutManager(new LinearLayoutManager(this)); // antes lm
        recyclerViewPending.setAdapter(adapterPendingTasks);

    }




    // MÉTODOS PARA LLEGAR A OTRAS PANTALLAS

    private void returnMainScreen(){
        Intent intent = new Intent(this, MainActivity.class);
        startActivity(intent);
        finish();
    }

    public void showNewTaskScreen(View view){
        Intent intent = new Intent(this, pantallaNuevaTarea.class);
        startActivity(intent);

    }

    public void showBottomDialog(View view){

        if ((bottomDialog == null) || !bottomDialog.isShowing()){
            bottomDialog = new Dialog(this);
            bottomDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
            bottomDialog.setContentView(R.layout.bottomsheet_tareas_layout);

            // Se crea el adapter del spinner
            Spinner spinner = bottomDialog.findViewById(R.id.languageOptionsSpinner);
            ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this, R.array.languague_array, android.R.layout.simple_spinner_item);
            adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
            spinner.setAdapter(adapter);

            Spinner spinnerTheme = bottomDialog.findViewById(R.id.themeOptionsSpinner);
            ArrayAdapter<CharSequence> adapterTheme = ArrayAdapter.createFromResource(this, R.array.themes_array, android.R.layout.simple_spinner_item);
            adapterTheme.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
            spinnerTheme.setAdapter(adapterTheme);

            SharedPreferences settingsGeneral = getSharedPreferences("generalSettings", 0);
            int language = settingsGeneral.getInt("language", 0);
            int appTheme = settingsGeneral.getInt("appTheme", 0);

            spinner.setSelection(language,false);
            // TODO ARREGLAR ESTA MIERDA DEL SPINNER DEL IDIOMA.
            SharedPreferences.Editor editor = settingsGeneral.edit();

            spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                @Override
                public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {

                        String itemName = spinner.getSelectedItem().toString();
                        String nameSimplified = "";
                        String spanish = getResources().getString(R.string.spanish);
                        String english = getResources().getString(R.string.english);
                        if(itemName.equals(spanish)){
                            nameSimplified = "es";
                            editor.putInt("language", 0);
                            editor.commit();
                        } else if(itemName.equals(english)){
                            nameSimplified = "en";
                            editor.putInt("language", 1);
                            editor.commit();
                        }

                        setLocale(nameSimplified);

                }

                @Override
                public void onNothingSelected(AdapterView<?> adapterView) {

                }
            });


            bottomDialog.show();


            bottomDialog.getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
            bottomDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
            bottomDialog.getWindow().getAttributes().windowAnimations = R.style.DialogAnimationBottom;
            bottomDialog.getWindow().setGravity(Gravity.BOTTOM);
        }
    }


    public void setLocale(String lang) {
        Locale myLocale = new Locale(lang);
        Resources res = getResources();
        DisplayMetrics dm = res.getDisplayMetrics();
        Configuration conf = res.getConfiguration();
        conf.locale = myLocale;
        res.updateConfiguration(conf, dm);
        /**Intent refresh = new Intent(this, pantallaTareas.class);
        finish();
        startActivity(refresh);*/
        recreate();

    }

    public void showLeftDialog(View view){

        if((leftDialog == null) || !leftDialog.isShowing()){
            leftDialog = new Dialog(this);
            leftDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
            leftDialog.setContentView(R.layout.usr_info);
            // Se coloca el nombre del usuario en la parte de arriba
            TextView usernameText = leftDialog.findViewById(R.id.textUser);
            usernameText.setText(userName);

            leftDialog.show();
            leftDialog.getWindow().setLayout(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.MATCH_PARENT);
            leftDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
            leftDialog.getWindow().getAttributes().windowAnimations = R.style.DialogAnimationLeft;
            leftDialog.getWindow().setGravity(Gravity.LEFT);

            leftDialog.setOnCancelListener(new DialogInterface.OnCancelListener() {
                @Override
                public void onCancel(DialogInterface dialogInterface) {
                    leftDialog.dismiss();
                }
            });
        }
    }

    public void showSummaryScreen(View view){

        Intent intent = new Intent(this, pantallaRegistros.class);
        startActivity(intent);
    }

    public void showMyWeekScreen(View view){
        Intent intent = new Intent(this, pantallaMiSemana.class);
        startActivity(intent);
    }



}