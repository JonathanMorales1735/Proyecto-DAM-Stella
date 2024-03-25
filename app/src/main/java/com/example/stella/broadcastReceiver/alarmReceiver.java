package com.example.stella.broadcastReceiver;

import static android.content.ContentValues.TAG;

import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;

import com.example.stella.MainActivity;
import com.example.stella.R;

import java.util.Random;

public class alarmReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        Log.i(TAG, "Enviando notificacion");


        Intent nextActivity = new Intent (context, MainActivity.class);
        PendingIntent pendingIntent = PendingIntent.getActivity(context, 0, nextActivity, PendingIntent.FLAG_UPDATE_CURRENT);

        int id = intent.getExtras().getInt("id");
        String name = intent.getExtras().getString("name");

        NotificationCompat.Builder builder = new NotificationCompat.Builder(context, "channelNotification")
                .setSmallIcon(R.drawable.ic_stat_name)
                .setContentTitle("Tarea pendiente")

                .setDefaults(NotificationCompat.DEFAULT_ALL)
                .setPriority(NotificationCompat.PRIORITY_DEFAULT)
                .setContentIntent(pendingIntent)
                .setAutoCancel(true);

        //int idd = Integer.valueOf(id);

        showNotification(builder, id, name, context);

        Log.i(TAG, "Id recibido: " + id);
        Log.i(TAG, "Nombre recibido: " + name);
        //notificationManagerCompat.cancel(id);
    }

    private void showNotification(NotificationCompat.Builder builder, int id, String name, Context context){
        NotificationManagerCompat notificationManagerCompat = NotificationManagerCompat.from(context);
        //notificationManagerCompat.cancel(id);
        builder.setContentText(name);
        notificationManagerCompat.notify(id, builder.build());
    }
}
