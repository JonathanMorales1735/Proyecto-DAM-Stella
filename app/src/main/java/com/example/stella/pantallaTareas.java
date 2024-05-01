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
        //FirebaseAuth.getInstance().signOut();
        setUserInactive();
        returnMainScreen();
    }

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

    public void setRecyclerViewPending(){
        adapterPendingTasks.notifyDataSetChanged();
        recyclerViewPending.setHasFixedSize(true);
        recyclerViewPending.setLayoutManager(new LinearLayoutManager(this)); // antes lm
        recyclerViewPending.setAdapter(adapterPendingTasks);
        adapterPendingTasks.notifyDataSetChanged();
        adapterPendingTasks.reSetItemList();
    }


    private void setUserInactive(){
        settings.setUserActivity(false);
    }

    private void setCurrentProfileName(){
        userName = settings.getCurrentProfileName();
    }

    private void changeAddCircleBtnColor(){
        //btnNewTask
        int appTheme = settings.getAppTheme();
        if(appTheme == 1){
            btnNewTask.setImageResource(R.drawable.ic_baseline_add_circle_white_24);
        } else{
            btnNewTask.setImageResource(R.drawable.ic_baseline_add_circle_24);
        }
    }

    public void retractPendingRecyclerView(View view){
        if(recyclerViewPending.getVisibility() == View.VISIBLE){
            recyclerViewPending.setVisibility(View.GONE);
            image_arrow1.setImageResource(android.R.drawable.arrow_up_float);
        } else{
            recyclerViewPending.setVisibility(View.VISIBLE);
            image_arrow1.setImageResource(android.R.drawable.arrow_down_float);
        }
    }

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
        if(settingsDialog == null || !settingsDialog.isShowing()){
            settingsDialog = new settingsDialog(this);
            settingsDialog.show();
        }
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