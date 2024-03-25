package com.example.stella;

import static android.content.ContentValues.TAG;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

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

import com.example.stella.workManager.scheduleDailyAction;
import com.example.stella.db.DbHelper;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import java.time.LocalDate;

public class MainActivity extends AppCompatActivity {

    private FirebaseAuth mAuth;
    private Dialog bottomDialog = null;
    EditText email, password;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        //ActionBar toolbar = getSupportActionBar();

        //toolbar.hide();

        mAuth = FirebaseAuth.getInstance();

        email = findViewById(R.id.editTextEmail);
        password = findViewById(R.id.editTextPassword);

        createInitialDB();
        setDailyActionWork();
        onFirstRun();

    }

    @Override
    public void onStart() {
        super.onStart();
        // Check if user is signed in (non-null) and update UI accordingly.
        FirebaseUser currentUser = mAuth.getCurrentUser();
        if(currentUser != null && currentUser.isEmailVerified()){
            prueba();
        }
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
            SharedPreferences settingLastDate = getSharedPreferences("lastDayDailyAction", 0);
            LocalDate localDate = LocalDate.now();
            String date = localDate.toString();
            SharedPreferences.Editor editor = settingLastDate.edit();
            editor.putString("lastDailyAction", date);
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

    /**
     * Método "logIn" tiene como función el inicio de sesión del usuario.
     * Para ello hace uso del email y contraseña introducida por el usuario.
     * @param view
     */

    public void logIn(View view){
        // Se recoge el imail y contraseña introducida por el usuario
        String emailText, passwordText;
        emailText = String.valueOf(email.getText());
        passwordText = String.valueOf(password.getText());

        //  A continuación, se hacen las comprobaciones pertinenetes del email y contraseña

        if(TextUtils.isEmpty(emailText)){
            Toast.makeText(this, R.string.emailEmpty, Toast.LENGTH_SHORT).show();
            return;
        }
        if(TextUtils.isEmpty(passwordText)){
            Toast.makeText(this, R.string.passwordEmpty, Toast.LENGTH_SHORT).show();
            return;
        }

        // Una vez hecho las comprobaciones, se procede al inicio de sesión con FireBase
        mAuth.signInWithEmailAndPassword(emailText, passwordText)
                .addOnCompleteListener(task -> {
                    // Su el inicio de sesión tuvo éxito, se procede a ir a la siguiente pantalla
                    if(task.isSuccessful() && task.getResult().getUser().isEmailVerified()){
                        prueba();
                    } else {
                        Toast.makeText(MainActivity.this, "Usuario no válido", Toast.LENGTH_SHORT).show();
                    }
                });

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