package com.example.stella.db;

import static android.content.ContentValues.TAG;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import com.example.stella.recyclerViewsAdapters.profiles;
import com.example.stella.recyclerViewsAdapters.taskElement;
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
    DbHelper dbH;

    public dbLogic(Context c){
        context = c;
        settings = new settings(c);
        timeConvertCalendar = new timeConvertCalendar();
        dbH = new DbHelper(context);
    }

    public dbLogic(Context c, DbHelper dbHelper){
        context = c;
        settings = new settings(c);
        timeConvertCalendar = new timeConvertCalendar();
        dbH = dbHelper;
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
        long mid = 0;
        // Se obtiene conexión con la base de datos
        SQLiteDatabase db = dbH.getWritableDatabase();
        // Se hace la inserción del ContentValues
        try {
            mid = db.insertOrThrow(tableName, null, cv);
            Log.i(TAG, "Inserción en " + tableName + " : " + String.valueOf(mid));
            // Se comprueba si el ContentValues tenia una hora ("time") para establecer una alarma
            boolean alarmTime = cv.containsKey("time");
            tableName = tableName.toLowerCase();
            if(alarmTime && tableName.equals("pendingtasks")){
                // Si tenia una hora para establecer una alarma, se establece con la clase Alarm.
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
            // Cerramos conexion con la base de datos
            try {
                db.close();
            } catch (SQLException e){
                e.printStackTrace();
            }
        }
        // Si hubo una insercion en la DB, retornará true. False de lo contrario
        if(mid != 0){
            return true;
        } else {
            return false;
        }
    }

    /**
     * createProfile se encarga de crear un perfil en la tabla "profiles". Tan solo se necesita un nombre
     * @param name
     * @return
     */

    public boolean createProfile(String name){
        // Se obtiene conexión con la base de datos
        SQLiteDatabase db = dbH.getWritableDatabase();
        // Se coloca el nombre en un ContentValues
        ContentValues cv = new ContentValues();
        cv.put("name", name);
        // Se hace la inserción en la base de datos
        try {
           long mid = db.insertOrThrow("profiles", null, cv);
           Log.i(TAG, "Insercion en PROFILES: " + mid);
        } catch (SQLException e){
            Log.e("Exception","SQLException"+String.valueOf(e.getMessage()));
            e.printStackTrace();
            return false;
        } finally {
            // Cerramos conexion con la base de datos
            try {
                db.close();
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
        // Se obtiene conexión con la base de datos
        SQLiteDatabase db = dbH.getWritableDatabase();
        // Obtenemos el objeto Alarm y quitamos la alarma que tuviera asociada la tarea
        Alarm alarm = new Alarm(context);

        alarm.cancelAlarm(taskId);

        int updateCount = 0;
        // Actualizamos la tarea
        updateCount = db.update(tableName, cv, "id = " + taskId, null);

        // Si al actualizar tenia tiempo para establecer una alarma, se establece la alarma
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
            // Cerramos conexion con la base de datos
            db.close();
        } catch (SQLException e){
            e.printStackTrace();
        }

        // Si hubo una insercion en la DB, retornará true. False de lo contrario
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
        // Se obtiene conexión con la base de datos
        SQLiteDatabase db = dbH.getWritableDatabase();
        // Se coloca el nombre en un ContentValues
        ContentValues cv = new ContentValues();
        cv.put("name", text);
        // Se hace la actualización del perfil
        int count = db.update("profiles", cv, "id = " + id, null);

        try {
            // Cerramos conexión de la BD
            db.close();
        } catch (SQLException e){
            e.printStackTrace();
        }
        // Si hubo una insercion en la DB, retornará true. False de lo contrario
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
        // Se obtiene conexión con la base de datos
        SQLiteDatabase db = dbH.getWritableDatabase();
        // Se procede a eliminar la tarea en su tabla
        int rowsAffected = db.delete(tableName, "id = " + taskId, null);
        Log.i(TAG, "Delete rows affected in " + tableName + ": " + rowsAffected);
        // Si ya no esta en pendiente se elimina la alarma que tenga asociada
        if(rowsAffected > 0 && !checkTaskInTable(taskId, "pendingtasks")){
            Alarm alarm = new Alarm(context);
            alarm.cancelAlarm(taskId);
        } else{
            return false;
        }

        try {
            // Cerramos conexión de la BD
            db.close();
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
        // Se obtiene conexión con la base de datos
        SQLiteDatabase db = dbH.getWritableDatabase();
        // Se procede a eliminar el perfil
        int rowAffected = db.delete("profiles", "id = " + profileId, null);

        try {
            // Cerramos conexión de la BD
            db.close();
        } catch (SQLException e){
            e.printStackTrace();
        }
        // Si hubo una insercion en la DB, retornará true. False de lo contrario
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
        // Se obtiene conexión con la base de datos
        SQLiteDatabase db = dbH.getReadableDatabase();
        // Forma una query en donde obtiene la tarea con cierta id
        String query = "Select * from " + tableName + " where id = " + id + "";
        // Un cursor ejecuta la query
        Cursor cursor = db.rawQuery(query, null);
        // Se comprueba si tiene al menos un registro o no
        Boolean check = cursor.moveToFirst();

        try {
            // Cerramos conexión de la BD
            db.close();
            cursor.close();
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
        // Se obtiene conexión con la base de datos
        SQLiteDatabase db = dbH.getReadableDatabase();
        // Se forma una query para obtener todos los perfiles
        String query = "Select id, name from profiles";
        // un cursor ejecuta la query
        Cursor cursor = db.rawQuery(query, null);
        List<profiles> list = new ArrayList<>();
        profiles prof;
        // Se añaden todos los perfiles en una lista para retornarla
        while(cursor.moveToNext()){
            prof = new profiles(cursor.getInt(0), cursor.getString(1));
            list.add(prof);
        }

        try{
            // Cerramos conexión de la BD
            db.close();
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
        // Se obtiene conexión con la base de datos
        SQLiteDatabase db = dbH.getReadableDatabase();
        // Se obtiene el id del perfil en uso
        int currentProfileID = settings.getCurrentProfileID();
        // Ejecuta la query en donde se obtienen todas las tareas con el id del perfil en uso
        Cursor cursor = db.rawQuery("Select name, id from pendingtasks where profileId = " + currentProfileID, null);
        taskElement task;
        List<taskElement> list = new ArrayList<>();
        // Se guardan todas las tareas en una lista para retornarla
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
            // Cierra conexión con base de datos
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
        // Se obtiene conexión con la base de datos
        SQLiteDatabase db = dbH.getReadableDatabase();
        // Se obtiene el id del perfil en uso
        int currentProfileID = settings.getCurrentProfileID();
        // Ejecuta la query en donde se obtienen todas las tareas con el id del perfil en uso
        Cursor cursor = db.rawQuery("Select name, id from completedtasks where profileId = " + currentProfileID, null);
        taskElement task;
        List<taskElement> list = new ArrayList<>();
        // Se guardan todas las tareas en una lista para retornarla
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
            // Cierra conexión con base de datos
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
        // Se obtiene conexión con la base de datos
        SQLiteDatabase db = dbH.getReadableDatabase();
        // Se obtiene el id del perfil en uso
        int currentProfileID = settings.getCurrentProfileID();
        // Ejecuta la query en donde se obtienen todas las tareas con el id del perfil en uso
        Cursor cursor = db.rawQuery("Select id, name from WEEKLYTASKS where " + day + " = 1 and profileId = " + currentProfileID, null);
        taskElement task;
        List<taskElement> list = new ArrayList<>();
        // Se guardan todas las tareas en una lista para retornarla
        while (cursor.moveToNext()){
            task = new taskElement();
            task.setId(cursor.getInt(0));
            task.setName(cursor.getString(1));
            Log.i(TAG, "Tarea recogida de weeklytasks: " + task.getId() + " " + task.getName());
            list.add(task);
        }
        try{
            // Cierra conexión con base de datos
            db.close();
            cursor.close();
        } catch (SQLException e){
            e.printStackTrace();
        }
        return list;
    }





}
