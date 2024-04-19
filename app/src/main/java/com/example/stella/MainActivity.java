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
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import com.example.stella.reciclerViewsAdapters.listProfilesAdapter;
import com.example.stella.utils.loadSettings;
import com.example.stella.workManager.scheduleDailyAction;
import com.example.stella.db.DbHelper;
import com.google.firebase.auth.FirebaseAuth;

import java.time.LocalDate;

public class MainActivity extends AppCompatActivity {

    private FirebaseAuth mAuth;
    private Dialog bottomDialog = null;
    CardView cardView_addNewProfile, cardView_manageProfiles;
    RecyclerView recyclerViewProfiles;
    listProfilesAdapter adapter;
    com.example.stella.utils.loadSettings loadSettings;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        loadSettings = new loadSettings(this);
        loadSettings.loadSettings(this);
        setContentView(R.layout.activity_main);

        recyclerViewProfiles = findViewById(R.id.recycler_profiles);
        adapter = new listProfilesAdapter(this, this);

        cardView_addNewProfile = findViewById(R.id.cardView_addNewProfile);
        cardView_manageProfiles = findViewById(R.id.cardView_manageProfiles);

        createInitialDB();
        setDailyActionWork();
        onFirstRun();
        setRecyclerViewProfiles();
        enableManageProfilesBtn();
        enableAddProfileBtn();

    }

    @Override
    public void onStart() {
        super.onStart();
        // Check if user is signed in (non-null) and update UI accordingly.
        //FirebaseUser currentUser = mAuth.getCurrentUser();
        if(isUserActive()){
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

    private boolean isUserActive(){
        SharedPreferences isUserActivePref = getSharedPreferences("isUserActive", 0);
        boolean check = isUserActivePref.getBoolean("isActive", false);
        return check;
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
        boolean mboolean;

        SharedPreferences settings = getSharedPreferences("FIRST_RUN", 0);
        mboolean = settings.getBoolean("FIRST_RUN_BOOL", false);

        if(!mboolean){
            LocalDate localDate = LocalDate.now();

            String date = localDate.toString();
            SharedPreferences settingLastDate = getSharedPreferences("lastDayDailyAction", 0);
            SharedPreferences settingsGeneral = getSharedPreferences("generalSettings", 0);

            SharedPreferences.Editor editor = settingLastDate.edit();


            editor.putString("lastDailyAction", date);
            editor.commit();

            editor = settingsGeneral.edit();
            editor.putInt("language", 0);
            editor.putInt("appTheme", 0);
            editor.commit();

            editor = settings.edit();
            editor.putBoolean("FIRST_RUN_BOOL", true);
            editor.commit();
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



    public void prueba(){
        Intent intent = new Intent(this, pantallaTareas.class);
        startActivity(intent);
        finish();
    }


    public void showBottomDialog(View view){

        if ((bottomDialog == null) || !bottomDialog.isShowing()){
            bottomDialog = new Dialog(this);
            bottomDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
            bottomDialog.setContentView(R.layout.bottomsheet_settings_layout);

            // Se crea el adapter del spinner
            Spinner spinner = bottomDialog.findViewById(R.id.languageOptionsSpinner);
            ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this, R.array.language_array, android.R.layout.simple_spinner_item);
            adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
            spinner.setAdapter(adapter);

            Spinner spinnerTheme = bottomDialog.findViewById(R.id.themeOptionsSpinner);
            ArrayAdapter<CharSequence> adapterTheme = ArrayAdapter.createFromResource(this, R.array.themes_array, android.R.layout.simple_spinner_item);
            adapterTheme.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
            spinnerTheme.setAdapter(adapterTheme);


            bottomDialog.show();


            bottomDialog.getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
            bottomDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
            bottomDialog.getWindow().getAttributes().windowAnimations = R.style.DialogAnimationBottom;
            bottomDialog.getWindow().setGravity(Gravity.BOTTOM);
        }

    }

    public void goToRegisterScreen(View view){
        Intent intent = new Intent(this, pantallaCrearCuenta.class);
        startActivity(intent);
    }
}