package com.example.stella.db;

import static android.content.ContentValues.TAG;

import android.content.ContentValues;
import android.content.Context;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import com.example.stella.reciclerViewsAdapters.profiles;
import com.example.stella.reciclerViewsAdapters.taskElement;
import com.example.stella.utils.Alarm;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

public class dbLogic {

    Context context;

    public dbLogic(Context c){
        context = c;
    }

    public int checkCurrentProfileID(){
        SharedPreferences settings = context.getSharedPreferences("profile", 0);
        int id = settings.getInt("id", 0);
        return id;
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

    //=============================================================================
    // SELECT METHODS
    //=============================================================================


    public boolean checkTaskInTable(int id, String tableName){
        DbHelper dbH = new DbHelper(context);
        SQLiteDatabase db = dbH.getReadableDatabase();

        String query = "Select * from " + tableName + " where id = " + id + "";

        Cursor cursor = db.rawQuery(query, null);

        Boolean check = cursor.moveToFirst();

        try {
            db.close();
            cursor.close();
            dbH.close();
            Log.i(TAG, "checkTaskInTable: DBH cerrado.");
        } catch (SQLException e){
            e.printStackTrace();
        }

        return check;
    }

    public List<profiles> getProfiles(){
        DbHelper dbH = new DbHelper(context);
        SQLiteDatabase db = dbH.getReadableDatabase();

        String query = "Select id, name from profiles";

        Cursor cursor = db.rawQuery(query, null);
        List<profiles> list = new ArrayList<>();
        profiles prof;
        while(cursor.moveToNext()){
            prof = new profiles(cursor.getInt(0), cursor.getString(1));
            list.add(prof);
        }

        try{
            db.close();
            dbH.close();
        } catch (SQLException e){
            e.printStackTrace();
        }

        return list;
    }

    public List<taskElement> getPendingTasksList(){
        DbHelper dbHelper = new DbHelper(context);
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        int currentProfileID = checkCurrentProfileID();
        Cursor cursor = db.rawQuery("Select name, id from pendingtasks where profileId = " + currentProfileID, null);
        taskElement task;
        List<taskElement> list = new ArrayList<>();

        while(cursor.moveToNext()){
            task = new taskElement();
            String name = cursor.getString(0);
            int id = cursor.getInt(1);
            if(name.length() >= 42){
                name = name.substring(0, 42) + "...";
            }

            task.setName(name);
            task.setId(id);
            Log.i(TAG, task.getName());
            list.add(task);
        }
        try{
            cursor.close();
            db.close();
        }catch (SQLException e){
            e.printStackTrace();
        }

        return list;

    }

    public List<taskElement> getCompletedTasksList(){
        DbHelper dbHelper = new DbHelper(context);
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        int currentProfileID = checkCurrentProfileID();
        Cursor cursor = db.rawQuery("Select name, id from completedtasks where profileId = " + currentProfileID, null);
        taskElement task;
        List<taskElement> list = new ArrayList<>();

        while(cursor.moveToNext()){
            task = new taskElement();
            String name = cursor.getString(0);
            int id = cursor.getInt(1);
            if(name.length() >= 42){
                name = name.substring(0, 42) + "...";
            }
            task.setName(name);
            task.setId(id);
            list.add(task);
        }
        try {
            cursor.close();
            db.close();
        }catch (SQLException e){
            e.printStackTrace();
        }

        return list;

    }

    public List<taskElement> getWeeklyTasksList(String day){
        DbHelper dbHelper = new DbHelper(context);
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        Log.i(TAG, "Obteniendo tareas de " + day);
        int currentProfileID = checkCurrentProfileID();
        Cursor cursor = db.rawQuery("Select id, name from WEEKLYTASKS where " + day + " = 1 and profileId = " + currentProfileID, null);
        taskElement task;
        List<taskElement> list = new ArrayList<>();

        while (cursor.moveToNext()){
            task = new taskElement();
            task.setId(cursor.getInt(0));
            task.setName(cursor.getString(1));
            Log.i(TAG, "Tarea recogida de weeklytasks: " + task.getId() + " " + task.getName());
            list.add(task);
        }
        try{
            db.close();
            cursor.close();
        } catch (SQLException e){
            e.printStackTrace();
        }
        return list;
    }
    
    


    // TODO: consulta PENDINGTASKS, COMPLETEDTASKS con el inner join a perfiles
    // TODO: consulta de WEEKLYTASKS con el inner join a perfiles
    // TODO: consulta a PREVIOUSRECORD

}
