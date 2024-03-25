package com.example.stella.db;

import static android.content.ContentValues.TAG;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import com.example.stella.Alarm;

import java.util.Calendar;

public class dbLogic {

    Context context;

    public dbLogic(Context c){
        context = c;
    }


    //=============================================================================
    // INSERT METHODS
    //=============================================================================

    public boolean insertTask(Calendar timeCalendar, String tableName, ContentValues cv){
        boolean check = false;

        long mid = 0;

        DbHelper dbH = new DbHelper(context);
        SQLiteDatabase db = dbH.getWritableDatabase();

        try {
            mid = db.insertOrThrow(tableName, null, cv);
            Log.i(TAG, "Inserción en " + tableName + " : " + String.valueOf(mid));
            boolean alarmTime = cv.containsKey("time");
            tableName = tableName.toLowerCase();
            if(alarmTime && tableName.equals("pendingtasks") && timeCalendar.getTimeInMillis() > 0){
                int id = cv.getAsInteger("id");
                String name = cv.getAsString("name");
                Alarm alarm = new Alarm(context);
                alarm.setAlarm(id, name, timeCalendar);
            }
        } catch (SQLException e){
            Log.e("Exception","SQLException"+String.valueOf(e.getMessage()));
            e.printStackTrace();
        } finally {
            try {
                db.close();
                dbH.close();
            } catch (SQLException e){
                e.printStackTrace();
            }

            check = true;
        }

        return check;
    }

    //=============================================================================
    // UPDATE METHODS
    //=============================================================================


    public boolean updateTask(int taskId, Calendar timeCalendar, String tableName, ContentValues cv){
        DbHelper dbhelper = new DbHelper(context);
        SQLiteDatabase db = dbhelper.getWritableDatabase();
        Alarm alarm = new Alarm(context);

        alarm.cancelAlarm(taskId);

        int updateCount = 0;
        updateCount = db.update(tableName, cv, "id = " + taskId, null);


        String alarmTimeStr = cv.getAsString("time");
        tableName = tableName.toLowerCase();
        if(alarmTimeStr != null && tableName.equals("pendingtasks") && timeCalendar.getTimeInMillis() > 0){
            Log.i(TAG, "updateTask: cambiando alarma.");
            int id = taskId;
            String name = cv.getAsString("name");
            Log.i(TAG, "updateTask: EL NOMBRE: " + name);
            alarm.setAlarm(id, name, timeCalendar);
        }

        try {
            db.close();
            dbhelper.close();
        } catch (SQLException e){
            e.printStackTrace();
        }

        if(updateCount > 0){
            return true;
        } else {
            return false;
        }
    }


    //=============================================================================
    // DELETE METHODS
    //=============================================================================



    public boolean deleteTask(int taskId, String tableName){

        DbHelper dbhelper = new DbHelper(context);
        SQLiteDatabase db = dbhelper.getWritableDatabase();

        int rowsAffected = db.delete(tableName, "id = " + taskId, null);
        Log.i(TAG, "Delete rows affected in " + tableName + ": " + rowsAffected);
        if(rowsAffected > 0){
            Alarm alarm = new Alarm(context);
            alarm.cancelAlarm(taskId);
        } else{
            return false;
        }

        try {
            db.close();
            dbhelper.close();
        } catch (SQLException e){
            e.printStackTrace();
        }

        return true;

    }



}
