package com.example.stella.workManager;

import static android.content.ContentValues.TAG;

import android.content.ContentValues;
import android.content.Context;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.work.Worker;
import androidx.work.WorkerParameters;

import com.example.stella.utils.Alarm;
import com.example.stella.db.DbHelper;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Locale;

public class dailyActionWorker extends Worker {

    Context context;

    public dailyActionWorker(@NonNull Context context, @NonNull WorkerParameters workerParams) {
        super(context, workerParams);
        this.context = context;
    }

    @NonNull
    @Override
    public Result doWork() {
        Log.i("ContentValues", "workeando a tope");
        setDailyAction(context);

        return Result.success();
    }
    @Override
    public void onStopped(){
        Log.i("ContentValues", "Worker parado");
    }

    private void setDailyAction(Context context){
        boolean mboolean = checkLastDailyAction(context);

        if(mboolean){
            Log.i("ContentValues", "Toca chambear");
            clearPendingTasks(context);
            setWeeklyTasksInPending(context);
            setAlarms(context);
            setPreviousRecords(context);
            clearCompletedTasks(context);
            updateLastDailyActionDate(context);
        }
    }



    private boolean checkLastDailyAction(Context context){

        boolean mboolean = false;

        SharedPreferences settings = context.getSharedPreferences("lastDayDailyAction", 0);
        String date = settings.getString("lastDailyAction", "DEFAULT");


        LocalDate localDate = LocalDate.now();
        String str_LocalDate = localDate.toString();
        if(!date.equals(str_LocalDate)){
            Log.i(TAG, "Las fechas no son iguales, hay que realizar las operaciones pertinentes.");

            SharedPreferences.Editor editor = settings.edit();
            editor.putString("operationInfo", "Hay que realizar operaciones");
            editor.commit();
            mboolean = true;
        } else {
            Log.i(TAG, "Las fechas coinciden, no hay que realizar ninguna operacion.");
            SharedPreferences.Editor editor = settings.edit();
            editor.putString("operationInfo", "No hay que realizar operaciones");
            editor.commit();
        }

        return mboolean;
    }
    private void updateLastDailyActionDate(Context context){
        SharedPreferences settings = context.getSharedPreferences("lastDayDailyAction", 0);
        SharedPreferences.Editor editor = settings.edit();

        LocalDate localDate = LocalDate.now();
        String str_LocalDate = localDate.toString();

        editor.putString("lastDailyAction", str_LocalDate);
        editor.commit();

    }

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

    private void setWeeklyTasksInPending(Context context){
        Locale enLocale=new Locale("en", "EN");
        LocalDate localDate = LocalDate.now();
        String dayName=localDate.format(DateTimeFormatter.ofPattern("EEEE",enLocale));
        dayName = dayName.toLowerCase();

        DbHelper dbHelper = new DbHelper(context);
        SQLiteDatabase db = dbHelper.getWritableDatabase();

        Cursor cursor = db.rawQuery("Select id, name, description, type, notify, time from WEEKLYTASKS where " + dayName + " = 1", null);
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



    private void setAlarms(Context context){
        Alarm alarm = new Alarm(context);
        alarm.setAllAlarms(context);

    }



    private void setPreviousRecords(Context context){
        DbHelper dbHelper = new DbHelper(context);
        SQLiteDatabase db = dbHelper.getReadableDatabase();

        int work = 0;
        int domestic = 0;
        int study = 0;
        int leisure = 0;

        ArrayList<String> types = new ArrayList<>();
        types.add("work");
        types.add("domestic");
        types.add("study");
        types.add("leisure");

        Cursor cursor;

        for (String type: types){
            String query = "Select * from completedtasks where type = '" + type + "'";
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


        LocalDate localDate = LocalDate.now();
        localDate = localDate.minusDays(1);
        String date = localDate.toString();

        ContentValues cv = new ContentValues();
        cv.put("date", date);
        cv.put("study", study);
        cv.put("domestic", domestic);
        cv.put("work", work);
        cv.put("leisure", leisure);



        db.insert("PREVIOUSRECORDS", null, cv);

        try {
            db.close();
        } catch (SQLException e){
            e.printStackTrace();
        }
    }

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
