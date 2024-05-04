package com.example.stella.utils;

import static android.content.ContentValues.TAG;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.os.Build;
import android.util.Log;

import com.example.stella.broadcastReceiver.alarmReceiver;
import com.example.stella.db.DbHelper;
import com.example.stella.db.dbLogic;

import java.util.Calendar;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Alarm es la clase encargada de la creacion de alarmas o la eliminación de estas
 */

public class Alarm {

    AlarmManager alarmManager;
    PendingIntent pendingIntent;
    Context context;
    settings settings;

    public Alarm(Context context){
        this.context = context;
        alarmManager = (AlarmManager) this.context.getSystemService(Context.ALARM_SERVICE);
        settings = new settings(context);
    }

    /**
     * setAlarm se encarga de establecer una alarma
     * @param id
     * @param nameTask
     * @param timeCalendar
     */

    public void setAlarm(int id, String nameTask, Calendar timeCalendar){

        Calendar auxCalendar = Calendar.getInstance();
        String nameProfile = settings.getCurrentProfileName();

        int hour = timeCalendar.get(Calendar.HOUR_OF_DAY);
        int minute = timeCalendar.get(Calendar.MINUTE);
        int currentHour = auxCalendar.get(Calendar.HOUR_OF_DAY);
        int currentMinute = auxCalendar.get(Calendar.MINUTE);
        if((hour <= currentHour) && (minute <= currentMinute)){
            Log.i("Alarm: " , "La hora establecida de la tarea es menor a la actual");
            return;
        }

        Intent intent = new Intent(context, alarmReceiver.class);
        intent.putExtra("id", id);
        intent.putExtra("nameTask", nameTask);
        intent.putExtra("nameProfile", nameProfile);
        Log.i(TAG, nameTask);
        pendingIntent = PendingIntent.getBroadcast(context, id, intent, PendingIntent.FLAG_UPDATE_CURRENT);


        //alarmManager.setExact(AlarmManager.RTC_WAKEUP, timeCalendar.getTimeInMillis(), pendingIntent);
            alarmManager.setExactAndAllowWhileIdle(AlarmManager.RTC_WAKEUP, timeCalendar.getTimeInMillis(), pendingIntent);


        //alarmManager.setRepeating(AlarmManager.RTC_WAKEUP, timeCalendar.getTimeInMillis(), AlarmManager.INTERVAL_DAY, pendingIntent);

        Log.i(TAG, "Alarma creada");
    }

    /**
     * cancelAlarm se encarga de eliminar una alarma en curso
     * @param id
     */

    public void cancelAlarm(int id){
        Intent intent = new Intent(context, alarmReceiver.class);
        PendingIntent pendingIntent = PendingIntent.getBroadcast(context, id, intent, 0);

        alarmManager.cancel(pendingIntent);
        Log.i(TAG, "Alarma cancelada: id " + id);
    }

    /**
     * setAllAlarms se encarga de establecer las alarmas de todas las tareas que se encuentran en la tabla pendingtasks
     * @param context
     */

    public void setAllAlarms(Context context) {
        Calendar calendar;

        DbHelper dbHelper = new DbHelper(context);
        SQLiteDatabase db = dbHelper.getReadableDatabase();

        Cursor cursor = db.rawQuery("Select id, name, time from pendingtasks where time is not null", null);
        Log.i(TAG, "Creando nuevas alarmas.");

        while (cursor.moveToNext()) {
            int id = cursor.getInt(0);
            String name = cursor.getString(1);
            String time = cursor.getString(2);
            timeConvertCalendar timeConvert = new timeConvertCalendar();
            calendar = timeConvert.convertToCalendar(time);

            setAlarm(id, name, calendar);
            Log.i(TAG, "Alarma creada: " + id + " " + name + " " + time);
        }

        try {
            db.close();
            cursor.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}
