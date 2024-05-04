package com.example.stella.utils;

import android.util.Log;

import java.util.Calendar;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class timeConvertCalendar {


    /**
     * convertToCalendar es un método auxiliar que se encarga de verificar que la hora recogida del campo "time" de la tabla "pendingtasks". Si cumple el patrón, genera un objeto "calendar"
     * con la hora, minuto y segundo para usarlo al crear una alarma
     * @param time
     * @return
     */
    public Calendar convertToCalendar(String time) {
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
