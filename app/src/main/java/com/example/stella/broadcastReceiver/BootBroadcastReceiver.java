package com.example.stella.broadcastReceiver;

import static android.content.ContentValues.TAG;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import com.example.stella.utils.Alarm;
import com.example.stella.db.DbHelper;

import java.util.Calendar;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class BootBroadcastReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(Context pContext, Intent intent) {
        if(intent.getAction().equals(Intent.ACTION_BOOT_COMPLETED)){
            setAlarms(pContext);
        } else {
            Log.i("BootReceiver: ", "No ha sido posible crear dailyActionReceiver");
        }
    }

    private void setAlarms(Context context) {
        Alarm alarm = new Alarm(context);
        Calendar calendar;

        DbHelper dbHelper = new DbHelper(context);
        SQLiteDatabase db = dbHelper.getReadableDatabase();

        Cursor cursor = db.rawQuery("Select id, name, time from pendingtasks where time is not null", null);
        Log.i(TAG, "Creando nuevas alarmas.");

        while (cursor.moveToNext()) {
            int id = cursor.getInt(0);
            String name = cursor.getString(1);
            String time = cursor.getString(2);

            calendar = auxSetCalendar(time);

            alarm.setAlarm(id, name, calendar);
            Log.i(TAG, "Alarma creada: " + id + " " + name + " " + time);
        }

        try {
            db.close();
            cursor.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private Calendar auxSetCalendar(String time) {
        Calendar calendar = Calendar.getInstance();
        Pattern p = Pattern.compile("(([0-9]{2,2}):([0-9]{2,2}):([0-9]{2,2}))");
        Matcher m = p.matcher(time);
        boolean mFound = m.find();
        Log.i("Adapter PendingTasks: ", "Hora recibida: " + time);
        if (mFound) {
            int hour = Integer.valueOf(m.group(2));
            int minute = Integer.valueOf(m.group(3));
            calendar.set(Calendar.HOUR_OF_DAY, hour);
            calendar.set(Calendar.MINUTE, minute);
            calendar.set(Calendar.SECOND, 00);
            Log.i("Adapter PendingTasks:", "El patrón de time HIZO MATCH : " + hour + ":" + minute + ":00");
        } else {
            Log.i("Adapter PendingTasks:", "El patrón de time no hizo match");
        }

        return calendar;
    }
}