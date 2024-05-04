package com.example.stella;

import static android.content.ContentValues.TAG;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.Dialog;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.stella.dialogs.settingsDialog;
import com.example.stella.reciclerViewsAdapters.adaptersLogic;
import com.example.stella.reciclerViewsAdapters.listCompletedTasksAdapter;
import com.example.stella.reciclerViewsAdapters.listPendingTasksAdapter;
import com.example.stella.utils.loadSettings;
import com.example.stella.utils.settings;

/**
 * Clase que muestra la pantalla en donde se recogen las tareas pendientes y completadas
 */

public class pantallaTareas extends AppCompatActivity {


    String userName;
    RecyclerView recyclerViewPending, recyclerViewCompleted;
    ImageButton btnNewTask;
    ImageView image_arrow1, image_arrow2;
    listPendingTasksAdapter adapterPendingTasks;
    listCompletedTasksAdapter adapterCompletedTasks;
    private Dialog bottomDialog, leftDialog = null;
    loadSettings loadSettings;
    settingsDialog settingsDialog = null;
    boolean settingsDialogChecker = false;
    adaptersLogic adapterLogic;
    settings settings;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        loadSettings = new loadSettings(this);
        loadSettings.loadSettings(this);
        setContentView(R.layout.pantallatareas);

        recyclerViewPending = findViewById(R.id.recyclerPendingTasks);
        recyclerViewCompleted = findViewById(R.id.recyclerCompletedTasks);
        btnNewTask = (ImageButton) findViewById(R.id.btnNewTask);
        btnNewTask.bringToFront();
        image_arrow1 = findViewById(R.id.image_arrow1);
        image_arrow2 = findViewById(R.id.image_arrow2);


        adapterPendingTasks = new listPendingTasksAdapter(this);
        adapterCompletedTasks = new listCompletedTasksAdapter(this);
        adapterPendingTasks.auxSetListCompletedTasksAdapter(adapterCompletedTasks);
        adapterCompletedTasks.auxSetListPendingTasksAdapter(adapterPendingTasks);
        adapterLogic = new adaptersLogic(this);
        settings = new settings(this);

        createNotificationChannel();
        setCurrentProfileName();
        changeAddCircleBtnColor();

    }

    @Override
    public void onStart() {
        super.onStart();
        setRecyclerViewPending();
        setRecyclerViewCompleted();
        changeAddCircleBtnColor();
    }

    @Override
    protected void onPause() {
        super.onPause();
        if (settingsDialog != null && settingsDialog.isShowing()) {
            settingsDialog.dismiss();
            settingsDialogChecker = true;

        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (settingsDialogChecker) {
            settingsDialog.show();
            settingsDialogChecker = false;
        }
    }

    /**
     * createNotificationChannel se encarga de crear y poner en marcha el canal por donde se trasmiten las notificaciones
     */

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
     * logOut Este método cierra la sesión del perfil. Llevándolo de vuelta a la pantalla para escoger perfil.
     * @param view
     */

    public void logOut(View view){
        //FirebaseAuth.getInstance().signOut();
        setUserInactive();
        returnMainScreen();
    }

    /**
     * setRecyclerViewCompleted prepara el adapter y el recyclerview que muestra las tareas que ya estan completas
     */

    private void setRecyclerViewCompleted(){
        //adapterCompletedTasks.fillCompletedTasks();
        adapterCompletedTasks.notifyDataSetChanged();
        recyclerViewCompleted.setHasFixedSize(true);
        recyclerViewCompleted.setLayoutManager(new LinearLayoutManager(this));
        recyclerViewCompleted.setNestedScrollingEnabled(false);
        recyclerViewCompleted.setAdapter(adapterCompletedTasks);
        adapterCompletedTasks.notifyDataSetChanged();
        adapterCompletedTasks.reSetItemList();

    }

    /**
     * setRecyclerViewPending prepara el adapter y el recyclerview que muestra las tareas que estan pendientes
     */

    public void setRecyclerViewPending(){
        adapterPendingTasks.notifyDataSetChanged();
        recyclerViewPending.setHasFixedSize(true);
        recyclerViewPending.setLayoutManager(new LinearLayoutManager(this)); // antes lm
        recyclerViewPending.setAdapter(adapterPendingTasks);
        adapterPendingTasks.notifyDataSetChanged();
        adapterPendingTasks.reSetItemList();
    }

    /**
     * setUserInactive se utiliza para indicar que el perfil se encuentra inactivo. Hace su uso al cerrar sesion
     */

    private void setUserInactive(){
        settings.setUserActivity(false);
    }

    /**
     * setCurrentProfileName recoge el nombre del perfil en uso y lo muestra en un textView
     */

    private void setCurrentProfileName(){
        userName = settings.getCurrentProfileName();
    }

    /**
     * changeAddCircleBtnColor se encarga de cambiar el grafico del boton de añadir tarea dependiendo del tema de la app
     */

    private void changeAddCircleBtnColor(){
        //btnNewTask
        int appTheme = settings.getAppTheme();
        if(appTheme == 1){
            btnNewTask.setImageResource(R.drawable.ic_baseline_add_circle_white_24);
        } else{
            btnNewTask.setImageResource(R.drawable.ic_baseline_add_circle_24);
        }
    }

    /**
     * retractPendingRecyclerView se encarga de contraer o expandir el recyclerview que contiene las tareas pendientes
     * @param view
     */

    public void retractPendingRecyclerView(View view){
        if(recyclerViewPending.getVisibility() == View.VISIBLE){
            recyclerViewPending.setVisibility(View.GONE);
            image_arrow1.setImageResource(android.R.drawable.arrow_up_float);
        } else{
            recyclerViewPending.setVisibility(View.VISIBLE);
            image_arrow1.setImageResource(android.R.drawable.arrow_down_float);
        }
    }

    /**
     * retractCompletedRecyclerView se encarga de contraer o expandir el recyclerview que conteiene las tareas completadas
     * @param view
     */

    public void retractCompletedRecyclerView(View view){
        if(recyclerViewCompleted.getVisibility() == View.VISIBLE){
            recyclerViewCompleted.setVisibility(View.GONE);
            image_arrow2.setImageResource(android.R.drawable.arrow_up_float);
        } else{
            recyclerViewCompleted.setVisibility(View.VISIBLE);
            image_arrow2.setImageResource(android.R.drawable.arrow_down_float);
        }
    }


    // MÉTODOS PARA LLEGAR A OTRAS PANTALLAS

    /**
     * returnMainScreen se encarga de regresar a la pantalla principal
     */

    private void returnMainScreen(){
        Intent intent = new Intent(this, MainActivity.class);
        startActivity(intent);
        finish();
    }

    /**
     * showNewTaskScreen se encarga de mostrar la pantalla de creación de nueva tarea
     * @param view
     */

    public void showNewTaskScreen(View view){
        Intent intent = new Intent(this, pantallaNuevaTarea.class);
        startActivity(intent);

    }

    /**
     * showBottomDialog se encarga de mostrar el dialog con las opciones de la app
     * @param view
     */

    public void showBottomDialog(View view){
        if(settingsDialog == null || !settingsDialog.isShowing()){
            settingsDialog = new settingsDialog(this);
            settingsDialog.show();
        }
    }

    /**
     * showLeftDialog muestra el dialog del panel izquierdo con las opciones "Mi semana", "Mi registro" y "Desconectar"
     * @param view
     */

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

    /**
     * showSummaryScreen lleva a la pantalla "Mi registro"
     * @param view
     */

    public void showSummaryScreen(View view){

        Intent intent = new Intent(this, pantallaRegistros.class);
        startActivity(intent);
    }

    /**
     * showMyWeekScreen lleva a la pantalla "Mi semana"
     * @param view
     */

    public void showMyWeekScreen(View view){
        Intent intent = new Intent(this, pantallaMiSemana.class);
        startActivity(intent);
    }





}