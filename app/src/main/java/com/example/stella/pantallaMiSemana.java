package com.example.stella;

import static android.content.ContentValues.TAG;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.example.stella.db.DbHelper;

import java.util.ArrayList;
import java.util.List;

public class pantallaMiSemana extends AppCompatActivity {

    TextView mondayTasksAmount, tuesdayTasksAmount, wednesdayTasksAmount, thursdayTasksAmount, fridayTasksAmount, saturdayTasksAmount, sundayTasksAmount;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.pantallamisemana);

        mondayTasksAmount = findViewById(R.id.textMondayTasks);
        tuesdayTasksAmount = findViewById(R.id.textTuesdayTasks);
        wednesdayTasksAmount = findViewById(R.id.textWednesdayTasks);
        thursdayTasksAmount = findViewById(R.id.textThursdayTasks);
        fridayTasksAmount = findViewById(R.id.textFridayTasks);
        saturdayTasksAmount = findViewById(R.id.textSaturdayTasks);
        sundayTasksAmount = findViewById(R.id.textSundayTasks);



        getAmountTasks();

    }

    @Override
    public void onStart() {
        super.onStart();
        getAmountTasks();
    }

    private void getAmountTasks(){
        DbHelper dbHelper = new DbHelper(this);
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        String[] dayList = new String[]{"monday", "tuesday", "wednesday", "thursday", "friday", "saturday", "sunday"};
        TextView[] textViewList = new TextView[]{mondayTasksAmount, tuesdayTasksAmount, wednesdayTasksAmount,
                thursdayTasksAmount, fridayTasksAmount, saturdayTasksAmount, sundayTasksAmount};

        Cursor cursor = null;

        for(int i = 0; i <= dayList.length-1; i++){
            cursor = db.rawQuery("Select count(*) from weeklytasks where " + String.valueOf(dayList[i]) + " = 1", null);
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

    public void showDaySchedule(View view){

        String day = getResources().getResourceEntryName(view.getId());
        Log.i(TAG, "Dia seleccionado: " + day);
        Intent intent;

        switch (day){
            case "monday_layout":
                intent = new Intent(this, pantallaDaySchedule.class);
                intent.putExtra("day", "monday");
                startActivity(intent);
                break;
            case "tuesday_layout":
                intent = new Intent(this, pantallaDaySchedule.class);
                intent.putExtra("day", "tuesday");
                startActivity(intent);
                break;
            case "wednesday_layout":
                intent = new Intent(this, pantallaDaySchedule.class);
                intent.putExtra("day", "wednesday");
                startActivity(intent);
                break;
            case "thursday_layout":
                intent = new Intent(this, pantallaDaySchedule.class);
                intent.putExtra("day", "thursday");
                startActivity(intent);
                break;
            case "friday_layout":
                intent = new Intent(this, pantallaDaySchedule.class);
                intent.putExtra("day", "friday");
                startActivity(intent);
                break;
            case "saturday_layout":
                intent = new Intent(this, pantallaDaySchedule.class);
                intent.putExtra("day", "saturday");
                startActivity(intent);
                break;
            case "sunday_layout":
                intent = new Intent(this, pantallaDaySchedule.class);
                intent.putExtra("day", "sunday");
                startActivity(intent);
                break;
            default:
                break;
        }


    }
}