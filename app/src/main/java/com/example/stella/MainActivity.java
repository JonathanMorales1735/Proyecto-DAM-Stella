package com.example.stella;

import static android.content.ContentValues.TAG;

import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.Toast;

import com.example.stella.db.dbLogic;
import com.example.stella.dialogs.createNewProfileDialog;
import com.example.stella.dialogs.settingsDialog;
import com.example.stella.recyclerViewsAdapters.listProfilesAdapter;
import com.example.stella.utils.loadSettings;
import com.example.stella.workManager.scheduleDailyAction;
import com.example.stella.db.DbHelper;
import com.example.stella.utils.settings;

import java.io.File;

/**
 * MainActivity es la actividad de la pantalla principal. En ella se puede seleccionar un perfil, acceder a la configuracion, acceder a la administracion de perfiles y la creacion de los mismos
 */

public class MainActivity extends AppCompatActivity {


    CardView cardView_addNewProfile, cardView_manageProfiles;
    RecyclerView recyclerViewProfiles;
    listProfilesAdapter adapter;
    ImageView logo;
    Button btn_help;
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
        btn_help = findViewById(R.id.btn_help);

        recyclerViewProfiles = findViewById(R.id.recycler_profiles);
        adapter = new listProfilesAdapter(this, this);

        cardView_addNewProfile = findViewById(R.id.cardView_addNewProfile);
        cardView_manageProfiles = findViewById(R.id.cardView_manageProfiles);
        dbLogic = new dbLogic(this);
        settings = new settings(this);

        createInitialDB(); // Se crea la base de datos por primera vez
        setDailyActionWork(); // Se inicia, si no lo está, dailyActionWorker
        onFirstRun(); // Se realizan ciertas acciones la primera vez que se enciende la app
        setRecyclerViewProfiles();  // Se prepara el recyclerview de los perfiles para listarlos
        enableManageProfilesBtn(); // Se hace visible o no el "boton" para administrar los perfiles
        enableAddProfileBtn(); // Se hace visible o no el "boton" para crear los perfiles
        changeLogoAndGearColor(); // Se cambia el color del logo y el boton de las opciones dependiendo del tema de la app

    }

    @Override
    public void onStart() {
        super.onStart();
        setRecyclerViewProfiles();
        changeLogoAndGearColor();
        enableManageProfilesBtn();
        enableAddProfileBtn();
        if(settings.isUserActive()){
            goToTasksScreen();
        }

    }

    /**
     * Método para mostrar el pdf de ayuda en línea
     * @param view
     */

    public void showHelpFile(View view){
        Intent intent = new Intent(this, helpFile.class);
        startActivity(intent);
        finish();
    }

    /**
     * enableAddProfileBtn habilita el boton de añadir perfil dependiendo de si se alcanzo el limite de 5 perfiles o no
     */

    private void enableAddProfileBtn(){
        if(adapter.getItemCount() < 5){
            cardView_addNewProfile.setVisibility(View.VISIBLE);
        } else{
            cardView_addNewProfile.setVisibility(View.GONE);
        }
    }

    /**
     * enableManageProfilesBtn habilita el boton de administrar perfiles siempre que haya un perfil minimo
     */

    private void enableManageProfilesBtn(){
        if(adapter.getItemCount() > 0){
            cardView_manageProfiles.setVisibility(View.VISIBLE);
        } else {
            cardView_manageProfiles.setVisibility(View.GONE);
        }
    }

    /**
     * changeLogoAndGearColor cambia el color del logo y el boton de configuracion dependiendo del tema de la aplicacion
     */

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

    /**
     * setRecyclerViewProfiles establece un adaptador al recyclerview que contiene los perfiles
     */

    private void setRecyclerViewProfiles(){
        adapter.fillProfiles();
        recyclerViewProfiles.setHasFixedSize(true);
        recyclerViewProfiles.setLayoutManager(new LinearLayoutManager(this));
        recyclerViewProfiles.setNestedScrollingEnabled(false);
        recyclerViewProfiles.setAdapter(adapter);

    }

    /**
     * setDailyActionWork inicia "dailyActionWorker"
     */

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

    /**
     * onFirstRun si es la primera vez que se inicia la aplicacion, realiza una serie de acciones
     */

    private void onFirstRun(){

        boolean mboolean = settings.isFirstRunPassed();

        if(!mboolean){
            settings.updateLastDailyAction();
            settings.setLanguage(0);
            settings.setAppTheme(0);
            settings.updateFirstRunPassed();
        }
    }

    /**
     * createInitialDB crea la base de datos
     */

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

    /**
     * showBottomDialog activa el dialog que contiene las opciones de configuracion para elegir el idioma y el tema de la aplicación
     * @param view
     */

    public void showBottomDialog(View view){
        if(settingsDialog == null || !settingsDialog.isShowing()){
            settingsDialog = new settingsDialog(this);
            settingsDialog.show();
        }
    }

    /**
     * goToManageProfileScreen metodo para dirigirse hacia la pantalla de administracion de perfiles
     * @param view
     */

    public void goToManageProfileScreen(View view){
        Intent intent = new Intent(this, screenManageProfiles.class);
        startActivity(intent);
    }

    /**
     * goToTasksScreen metodo para dirigirse a la pantalla de tareas
     */

    public void goToTasksScreen(){
        Intent intent = new Intent(this, screenTasks.class);
        startActivity(intent);
        finish();
    }

    /**
     * showNewProfileDialog muestra el dialog de "createNewProfileDialog"
     * @param view
     */

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