package com.example.stella;

import static android.content.ContentValues.TAG;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.example.stella.db.DbHelper;
import com.example.stella.utils.loadSettings;
import com.example.stella.utils.settings;

import java.util.ArrayList;
import java.util.List;

/**
 * Clase que muestra la pantalla con los dias de la semana y el numero de tareas en ellos
 */

public class pantallaMiSemana extends AppCompatActivity {

    TextView mondayTasksAmount, tuesdayTasksAmount, wednesdayTasksAmount, thursdayTasksAmount, fridayTasksAmount, saturdayTasksAmount, sundayTasksAmount;
   settings settings;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        loadSettings loadSettings = new loadSettings(this);
        loadSettings.loadSettings(this);
        setContentView(R.layout.pantallamisemana);

        mondayTasksAmount = findViewById(R.id.textMondayTasks);
        tuesdayTasksAmount = findViewById(R.id.textTuesdayTasks);
        wednesdayTasksAmount = findViewById(R.id.textWednesdayTasks);
        thursdayTasksAmount = findViewById(R.id.textThursdayTasks);
        fridayTasksAmount = findViewById(R.id.textFridayTasks);
        saturdayTasksAmount = findViewById(R.id.textSaturdayTasks);
        sundayTasksAmount = findViewById(R.id.textSundayTasks);
        settings = new settings(this);


        getAmountTasks();

    }

    @Override
    public void onStart() {
        super.onStart();
        getAmountTasks();
    }

    /**
     * getAmountTasks obtiene el numero de tareas que hay en cada dia
     */

    private void getAmountTasks(){
        DbHelper dbHelper = new DbHelper(this);
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        String[] dayList = new String[]{"monday", "tuesday", "wednesday", "thursday", "friday", "saturday", "sunday"};
        TextView[] textViewList = new TextView[]{mondayTasksAmount, tuesdayTasksAmount, wednesdayTasksAmount,
                thursdayTasksAmount, fridayTasksAmount, saturdayTasksAmount, sundayTasksAmount};

        int profileId = settings.getCurrentProfileID();

        Cursor cursor = null;

        for(int i = 0; i <= dayList.length-1; i++){
            cursor = db.rawQuery("Select count(*) from weeklytasks where " + String.valueOf(dayList[i]) + " = 1 and profileId = " + profileId, null);
            while (cursor.moveToNext()){
                textViewList[i].setText(cursor.getString(0));
            }
        }


        try{
            db.close();
            cursor.close();
        } catch (SQLException e){
            e.printStackTrace();
        }
    }

    /**
     * showDaySchedule lleva a la pantalla del dia en especifico seleccionado
     * @param view
     */

    public void showDaySchedule(View view){

        String day = getResources().getResourceEntryName(view.getId());
        Log.i(TAG, "Dia seleccionado: " + day);
        Intent intent = new Intent(this, pantallaDaySchedule.class);
        switch (day){
            case "monday_layout":
                intent.putExtra("day", "monday");
                startActivity(intent);
                break;
            case "tuesday_layout":
                intent.putExtra("day", "tuesday");
                startActivity(intent);
                break;
            case "wednesday_layout":
                intent.putExtra("day", "wednesday");
                startActivity(intent);
                break;
            case "thursday_layout":
                intent.putExtra("day", "thursday");
                startActivity(intent);
                break;
            case "friday_layout":
                intent.putExtra("day", "friday");
                startActivity(intent);
                break;
            case "saturday_layout":
                intent.putExtra("day", "saturday");
                startActivity(intent);
                break;
            case "sunday_layout":
                intent.putExtra("day", "sunday");
                startActivity(intent);
                break;
            default:
                break;
        }


    }
}