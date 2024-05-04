package com.example.stella.db;

import static android.content.ContentValues.TAG;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import com.example.stella.reciclerViewsAdapters.profiles;
import com.example.stella.reciclerViewsAdapters.taskElement;
import com.example.stella.utils.Alarm;
import com.example.stella.utils.settings;
import com.example.stella.utils.timeConvertCalendar;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

/**
 * En dbLogic se recogen métodos los cuales son usados en todas las clases que necesiten interactuar con la base de datos.
 */

public class dbLogic {

    Context context;
    settings settings;
    timeConvertCalendar timeConvertCalendar;

    public dbLogic(Context c){
        context = c;
        settings = new settings(c);
        timeConvertCalendar = new timeConvertCalendar();
    }

    //=============================================================================
    // INSERT METHODS
    //=============================================================================

    /**
     * inserTask se usa para insertar una tarea en la base de datos. Para ello se necesita un objeto "Calendar"
     * @param tableName
     * @param cv
     * @return
     */

    public boolean insertTask(String tableName, ContentValues cv){
        boolean check = false;

        long mid = 0;

        DbHelper dbH = new DbHelper(context);
        SQLiteDatabase db = dbH.getWritableDatabase();

        try {
            mid = db.insertOrThrow(tableName, null, cv);
            Log.i(TAG, "Inserción en " + tableName + " : " + String.valueOf(mid));
            boolean alarmTime = cv.containsKey("time");
            tableName = tableName.toLowerCase();
            if(alarmTime && tableName.equals("pendingtasks")){
                int id = cv.getAsInteger("id");
                String name = cv.getAsString("name");
                Calendar timeCalendar = timeConvertCalendar.convertToCalendar(cv.getAsString("time"));
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

    /**
     * createProfile se encarga de crear un perfil en la tabla "profiles". Tan solo se necesita un nombre
     * @param name
     * @return
     */

    public boolean createProfile(String name){
        DbHelper dbH = new DbHelper(context);
        SQLiteDatabase db = dbH.getWritableDatabase();
        ContentValues cv = new ContentValues();
        cv.put("name", name);
        try {
           long mid = db.insertOrThrow("profiles", null, cv);
           Log.i(TAG, "Insercion en PROFILES: " + mid);
        } catch (SQLException e){
            Log.e("Exception","SQLException"+String.valueOf(e.getMessage()));
            e.printStackTrace();
            return false;
        } finally {
            try {
                db.close();
                dbH.close();
            } catch (SQLException e){
                e.printStackTrace();
            }

         return true;
        }
    }

    //=============================================================================
    // UPDATE METHODS
    //=============================================================================

    /**
     * updateTask se encarga de actualizar una tarea en la base de datos. Usa la id de la tarea, el nombre de la tabla a la que apuntar y un contentValues con los valores a actualizar
     * @param taskId
     * @param tableName
     * @param cv
     * @return
     */

    public boolean updateTask(int taskId,  String tableName, ContentValues cv){
        DbHelper dbhelper = new DbHelper(context);
        SQLiteDatabase db = dbhelper.getWritableDatabase();
        Alarm alarm = new Alarm(context);

        alarm.cancelAlarm(taskId);

        int updateCount = 0;
        updateCount = db.update(tableName, cv, "id = " + taskId, null);


        String alarmTimeStr = cv.getAsString("time");
        tableName = tableName.toLowerCase();
        if(alarmTimeStr != null && tableName.equals("pendingtasks") ){
            Log.i(TAG, "updateTask: cambiando alarma.");
            int id = taskId;
            String name = cv.getAsString("name");
            Calendar timeCalendar = timeConvertCalendar.convertToCalendar(cv.getAsString("time"));
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

    /**
     * updateProfile se encarga de actualzar un perfil
     * @param text
     * @param id
     * @return
     */

    public boolean updateProfile(String text, int id){
        DbHelper dbhelper = new DbHelper(context);
        SQLiteDatabase db = dbhelper.getWritableDatabase();
        ContentValues cv = new ContentValues();
        cv.put("name", text);

        int count = db.update("profiles", cv, "id = " + id, null);

        try {
            db.close();
            dbhelper.close();
        } catch (SQLException e){
            e.printStackTrace();
        }

        if(count > 0){
            return true;
        } else {
            return false;
        }
    }


    //=============================================================================
    // DELETE METHODS
    //=============================================================================


    /**
     * deleteTask se encarga de eliminar una tarea. Se necesita el id de la tarea y la tabla en donde se encuentra
     * @param taskId
     * @param tableName
     * @return
     */

    public boolean deleteTask(int taskId, String tableName){

        DbHelper dbhelper = new DbHelper(context);
        SQLiteDatabase db = dbhelper.getWritableDatabase();

        int rowsAffected = db.delete(tableName, "id = " + taskId, null);
        Log.i(TAG, "Delete rows affected in " + tableName + ": " + rowsAffected);
        if(rowsAffected > 0 && !checkTaskInTable(taskId, "pendingtasks")){
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

    /**
     * deleteProfile se encarga de borrar un perfil
     * @param profileId
     * @return
     */

    public boolean deleteProfile(int profileId){
        DbHelper dbhelper = new DbHelper(context);
        SQLiteDatabase db = dbhelper.getWritableDatabase();

        int rowAffected = db.delete("profiles", "id = " + profileId, null);
        Log.i(TAG, "Delete row affected in PROFILES : " + rowAffected);

        try {
            db.close();
            dbhelper.close();
        } catch (SQLException e){
            e.printStackTrace();
        }

        if(rowAffected > 0){
            return true;
        } else {
            return false;
        }
    }

    //=============================================================================
    // SELECT METHODS
    //=============================================================================


    /**
     * checkTaskInTable se encarga de comprobar si una tarea existe en la tabla pasada por parámetro
     * @param id
     * @param tableName
     * @return
     */

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
        } catch (SQLException e){
            e.printStackTrace();
        }

        return check;
    }

    /**
     * getProfiles obtiene todos los perfiles de la tabla "profiles"
     * @return
     */

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

    /**
     * getPendingTaskList obtiene todas las tareas de la tabla "pendingtasks"
     * @return
     */

    public List<taskElement> getPendingTasksList(){
        DbHelper dbHelper = new DbHelper(context);
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        int currentProfileID = settings.getCurrentProfileID();
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

    /**
     * getCompeltedTasksList obtiene todas las tareas de la tabla "completedtasks"
     * @return
     */

    public List<taskElement> getCompletedTasksList(){
        DbHelper dbHelper = new DbHelper(context);
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        int currentProfileID = settings.getCurrentProfileID();
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

    /**
     * getWeeklyTasksList obtiene todas las tareas de la tabla "weeklytasks"
     * @param day
     * @return
     */

    public List<taskElement> getWeeklyTasksList(String day){
        DbHelper dbHelper = new DbHelper(context);
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        Log.i(TAG, "Obteniendo tareas de " + day);
        int currentProfileID = settings.getCurrentProfileID();
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





}
