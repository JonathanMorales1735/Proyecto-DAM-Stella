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
import com.example.stella.utils.timeConvertCalendar;

import java.util.Calendar;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * bootBroadcastReceiver se encarga de establecer las alarmas de las tareas cuando el dispositivo se reinicia. La clase "dailyActionWorker" puede no establecer las alarmas hasta el dis
 * siguiente, asi que esta clase cubre ese fallo.
 */

public class BootBroadcastReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(Context pContext, Intent intent) {
        if(intent.getAction().equals(Intent.ACTION_BOOT_COMPLETED)){
            setAlarms(pContext);
        } else {
            Log.i("BootReceiver: ", "No ha sido posible crear dailyActionReceiver");
        }
    }

    /**
     * setAlarms es el método encargado de establecer las alarmas. Para ello recoge las tareas de la tabla "pendingtasks" en las cuales tengan una alarma establecida.
     * @param context
     */

    private void setAlarms(Context context) {
        // Se inicializa el objeto Alarm y Calendar
        Alarm alarm = new Alarm(context);
        Calendar calendar;

        // Obtenemos una conexión a la base de datos
        DbHelper dbHelper = new DbHelper(context);
        SQLiteDatabase db = dbHelper.getReadableDatabase();

        // Ejecutamos una query en la base de datos para obtener las tareas donde tengan una hora
        Cursor cursor = db.rawQuery("Select id, name, time from pendingtasks where time is not null", null);
        Log.i(TAG, "Creando nuevas alarmas.");

        // Establecemos una alarma para todas las tareas recogidas en el cursor
        while (cursor.moveToNext()) {
            int id = cursor.getInt(0);
            String name = cursor.getString(1);
            String time = cursor.getString(2);

            timeConvertCalendar setCalendar = new timeConvertCalendar();
            calendar = setCalendar.convertToCalendar(time);

            alarm.setAlarm(id, name, calendar);
            Log.i(TAG, "Alarma creada: " + id + " " + name + " " + time);
        }

        // Cerramos conexión con la base de datos
        try {
            db.close();
            cursor.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }




}