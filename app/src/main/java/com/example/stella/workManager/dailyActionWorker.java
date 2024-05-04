package com.example.stella.workManager;

import static android.content.ContentValues.TAG;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.work.Worker;
import androidx.work.WorkerParameters;

import com.example.stella.db.dbLogic;
import com.example.stella.reciclerViewsAdapters.profiles;
import com.example.stella.utils.Alarm;
import com.example.stella.db.DbHelper;
import com.example.stella.utils.settings;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

/**
 * dailyActionWorker es una clase que extiende de WorkManager. Se encarga de realizar unas tareas cada dia.
 */

public class dailyActionWorker extends Worker {

    Context context;
    settings settings;

    public dailyActionWorker(@NonNull Context context, @NonNull WorkerParameters workerParams) {
        super(context, workerParams);
        this.context = context;
        this.settings = new settings(context);
    }

    @NonNull
    @Override
    public Result doWork() {
        setDailyAction(context);

        return Result.success();
    }
    @Override
    public void onStopped(){
        Log.i("ContentValues", "Worker parado");
    }

    /**
     * setDailyAction es un método que recoge todas las acciones que realiza DailyActionworker y las ejecuta
     * @param context
     */

    private void setDailyAction(Context context){
        boolean mboolean = checkLastDailyAction(context);

        if(mboolean){
            clearPendingTasks(context);
            setWeeklyTasksInPending(context);
            setAlarms(context);
            setPreviousRecords(context);
            clearCompletedTasks(context);
            updateLastDailyActionDate(context);
        }
    }

    /**
     * checkLastDailyAction verifica cuando fue la ultima vez que realizo una acción diaria. Devuelve true si la fecha de hoy no coincide con la guardada en lasDailyAction
     * @param context
     * @return
     */

    private boolean checkLastDailyAction(Context context){

        boolean mboolean = false;

        String date = settings.getLastDailyAction();
        LocalDate localDate = LocalDate.now();
        String str_LocalDate = localDate.toString();

        if(!date.equals(str_LocalDate)){
            Log.i(TAG, "Las fechas no son iguales, hay que realizar las operaciones pertinentes.");
            settings.setInfoLastDailyAction("Hay que realizar operaciones");
            mboolean = true;
        } else {
            Log.i(TAG, "Las fechas coinciden, no hay que realizar ninguna operacion.");
            settings.setInfoLastDailyAction("No hay que realizar operaciones");
        }

        return mboolean;
    }

    /**
     * updateLastDailyActionDate actualiza la fecha de la ultima acción diaria de dailyActionWorker
     * @param context
     */

    private void updateLastDailyActionDate(Context context){
        settings.updateLastDailyAction();
    }

    /**
     * clearPendingTasks se encarga de borrar todas las tareas de pendingtasks las cuales tienen una alarma o se encuentran en la tabla weeklytasks (notify en true)
     * @param context
     */

    private void clearPendingTasks(Context context){
        DbHelper dbHelper = new DbHelper(context);
        SQLiteDatabase db = dbHelper.getWritableDatabase();

        int rowsAffected = db.delete("PENDINGTASKS", "notify = 1", null);
        Log.i(TAG, "Delete rows affected in PENDINGTASKS: " + rowsAffected);

        try{
            db.close();
        } catch (SQLException e){
            e.printStackTrace();
        }
    }

    /**
     * setWeeklyTasksInPending se encarga de ir a weeklytasks, recoger las tareas del dia de hoy y las lleva a pendingtasks
     * @param context
     */

    private void setWeeklyTasksInPending(Context context){
        Locale enLocale=new Locale("en", "EN");
        LocalDate localDate = LocalDate.now();
        String dayName=localDate.format(DateTimeFormatter.ofPattern("EEEE",enLocale));
        dayName = dayName.toLowerCase();

        DbHelper dbHelper = new DbHelper(context);
        SQLiteDatabase db = dbHelper.getWritableDatabase();

        Cursor cursor = db.rawQuery("Select id, name, description, type, notify, time, profileId from WEEKLYTASKS where " + dayName + " = 1", null);
        int mid = 0;
        Log.i(TAG, "Insertando weeklytasks en pending");
        if(cursor.getExtras().isEmpty()){
            Log.i(TAG, "No hay nada en weeklytasks");
        }
        while(cursor.moveToNext()){
            ContentValues cv = new ContentValues();
            cv.put("id", cursor.getInt(0));
            cv.put("name", cursor.getString(1));
            cv.put("description", cursor.getString(2));
            cv.put("type", cursor.getString(3));
            cv.put("notify", cursor.getInt(4));
            cv.put("time", cursor.getString(5));
            cv.put("profileId", cursor.getInt(6));
            mid += db.insertOrThrow("pendingtasks", null, cv);
        }

        try{
            db.close();
            cursor.close();
        } catch (SQLException e){
            e.printStackTrace();
        }

        Log.i("DailyActionReceiver: ", "setWeeklyTasks inserts in pendingtasks: " + mid);

    }

    /**
     * setAlarms establece todas las alarmas de la tabla pendingtasks
     * @param context
     */

    private void setAlarms(Context context){
        Alarm alarm = new Alarm(context);
        alarm.setAllAlarms(context);

    }

    /**
     * setPreviousRecords se encarga de llevar los datos de las tareas de completedtasks a la tabla previousrecords
     * @param context
     */

    private void setPreviousRecords(Context context){
        DbHelper dbHelper = new DbHelper(context);
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        List<Integer> IDsList = new ArrayList<>();
        IDsList = auxGetProfilesIDs();


        ArrayList<String> types = new ArrayList<>();
        types.add("work");
        types.add("domestic");
        types.add("study");
        types.add("leisure");

        Cursor cursor;
        for(int id : IDsList){

            int work = 0;
            int domestic = 0;
            int study = 0;
            int leisure = 0;

            for (String type: types){
                String query = "Select * from completedtasks where type = '" + type + "' and profileId = " + id;
                cursor = db.rawQuery(query, null);

                if(type.equals("work")){
                    work += cursor.getCount();
                } else if (type.equals("domestic")){
                    domestic += cursor.getCount();
                } else if (type.equals("study")){
                    study += cursor.getCount();
                } else if (type.equals("leisure")){
                    leisure += cursor.getCount();
                    cursor.close();
                }
            }


            String date = settings.getLastDailyAction();

            ContentValues cv = new ContentValues();
            cv.put("date", date);
            cv.put("study", study);
            cv.put("domestic", domestic);
            cv.put("work", work);
            cv.put("leisure", leisure);
            cv.put("profileId", id);



            db.insert("PREVIOUSRECORDS", null, cv);
        }

        try {
            db.close();
        } catch (SQLException e){
            e.printStackTrace();
        }
    }

    /**
     * auxGetProfilesIDs es un metodo auxiliar que devuelve una lista de los id de todos los perfiles
     * @return
     */

    private List<Integer> auxGetProfilesIDs(){
        dbLogic dbLogic = new dbLogic(context);
        List<profiles> profiles = dbLogic.getProfiles();
        List<Integer> list = new ArrayList<>();
        for(profiles p : profiles){
            list.add(p.getId());
        }
        return list;
    }

    /**
     * clearCompletedTasks limpia toda la tabla de completedTasks
     * @param context
     */

    private void clearCompletedTasks(Context context){
        DbHelper dbHelper = new DbHelper(context);
        SQLiteDatabase db = dbHelper.getWritableDatabase();

        int rowsAffected = db.delete("COMPLETEDTASKS", null, null);
        Log.i(TAG, "Delete rows affected in COMPLETEDTASKS: " + rowsAffected);

        try{
            db.close();
        } catch (SQLException e){
            e.printStackTrace();
        }
    }
}
