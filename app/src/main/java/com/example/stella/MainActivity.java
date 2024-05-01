package com.example.stella;

import static android.content.ContentValues.TAG;

import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.Dialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.Toast;

import com.example.stella.db.dbLogic;
import com.example.stella.dialogs.createNewProfileDialog;
import com.example.stella.dialogs.settingsDialog;
import com.example.stella.reciclerViewsAdapters.listProfilesAdapter;
import com.example.stella.utils.loadSettings;
import com.example.stella.workManager.scheduleDailyAction;
import com.example.stella.db.DbHelper;
import com.example.stella.utils.settings;
import com.google.firebase.auth.FirebaseAuth;

import java.time.LocalDate;

public class MainActivity extends AppCompatActivity {

    private FirebaseAuth mAuth;
    private Dialog bottomDialog = null;
    CardView cardView_addNewProfile, cardView_manageProfiles;
    RecyclerView recyclerViewProfiles;
    listProfilesAdapter adapter;
    ImageView logo;
    ImageButton btn_optionsGear;
    com.example.stella.utils.loadSettings loadSettings;
    settingsDialog settingsDialog = null;
    dbLogic dbLogic;
    settings settings;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        loadSettings = new loadSettings(this);
        loadSettings.loadSettings(this);
        setContentView(R.layout.activity_main);
        logo = findViewById(R.id.image_logo);
        btn_optionsGear = findViewById(R.id.btn_optionsGear);

        recyclerViewProfiles = findViewById(R.id.recycler_profiles);
        adapter = new listProfilesAdapter(this, this);

        cardView_addNewProfile = findViewById(R.id.cardView_addNewProfile);
        cardView_manageProfiles = findViewById(R.id.cardView_manageProfiles);
        dbLogic = new dbLogic(this);
        settings = new settings(this);

        createInitialDB();
        setDailyActionWork();
        onFirstRun();
        setRecyclerViewProfiles();
        enableManageProfilesBtn();
        enableAddProfileBtn();
        changeLogoAndGearColor();

    }

    @Override
    public void onStart() {
        super.onStart();
        setRecyclerViewProfiles();
        changeLogoAndGearColor();
        enableManageProfilesBtn();
        enableAddProfileBtn();
        if(settings.isUserActive()){
            prueba();
        }

    }
    private void enableAddProfileBtn(){
        if(adapter.getItemCount() < 5){
            cardView_addNewProfile.setVisibility(View.VISIBLE);
        } else{
            cardView_addNewProfile.setVisibility(View.GONE);
        }
    }

    private void enableManageProfilesBtn(){
        if(adapter.getItemCount() > 0){
            cardView_manageProfiles.setVisibility(View.VISIBLE);
        } else {
            cardView_manageProfiles.setVisibility(View.GONE);
        }
    }

    private void changeLogoAndGearColor(){
        int appTheme = settings.getAppTheme();
        if(appTheme == 0){
            logo.setImageResource(R.drawable.logo2);
            btn_optionsGear.setImageResource(R.drawable.optionsicon);
        } else{
            logo.setImageResource(R.drawable.logo1);
            btn_optionsGear.setImageResource(R.drawable.optionsiconwhite);
        }
    }



    private void setRecyclerViewProfiles(){
        adapter.fillProfiles();
        recyclerViewProfiles.setHasFixedSize(true);
        recyclerViewProfiles.setLayoutManager(new LinearLayoutManager(this));
        recyclerViewProfiles.setNestedScrollingEnabled(false);
        recyclerViewProfiles.setAdapter(adapter);

    }


    private void setDailyActionWork(){
        scheduleDailyAction scheduleDailyAction = new scheduleDailyAction(this);
        String tag = scheduleDailyAction.getTag();
        boolean check = scheduleDailyAction.isWorkScheduled(tag);
        if(!check){
            scheduleDailyAction.scheduleWork();
            Log.i(TAG, "setDailyActionWork: Se ha colocado el dailyActionWork");
        } else{
            Log.i(TAG, "setDailyActionWork: dailyActionWork ya está activo");
        }
    }

    private void onFirstRun(){

        boolean mboolean = settings.isFirstRunPassed();

        if(!mboolean){
            settings.updateLastDailyAction();
            settings.setLanguage(0);
            settings.setAppTheme(0);
            settings.updateFirstRunPassed();
        }
    }

    private void createInitialDB(){
        DbHelper dbHelper = new DbHelper(MainActivity.this);
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        if(db!=null){
            Log.i(TAG, "Base de datos ya creada.");
        }
        try{
            db.close();
        } catch (SQLException e){
            e.printStackTrace();
        }

    }

    public void showBottomDialog(View view){
        if(settingsDialog == null || !settingsDialog.isShowing()){
            settingsDialog = new settingsDialog(this);
            settingsDialog.show();
        }
    }


    public void goToManageProfileScreen(View view){
        Intent intent = new Intent(this, screenManageProfiles.class);
        startActivity(intent);
    }

    public void prueba(){
        Intent intent = new Intent(this, pantallaTareas.class);
        startActivity(intent);
        finish();
    }

    public void showNewProfileDialog(View view){
        createNewProfileDialog.show(this,   new createNewProfileDialog.OnDialogClickListener() {
            @Override
            public void onAccept(String text) {
                dbLogic.createProfile(text);
                adapter.fillProfiles();
                Toast.makeText(getApplicationContext(), getResources().getString(R.string.new_profile_created), Toast.LENGTH_SHORT).show();
                enableAddProfileBtn();
                enableManageProfilesBtn();
            }

            @Override
            public void onCancel() {
                // Nada
            }
        });
    }




}