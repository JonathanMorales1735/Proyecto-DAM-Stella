package com.example.stella.broadcastReceiver;

import static android.content.ContentValues.TAG;

import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Icon;
import android.util.Log;

import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;
import androidx.core.graphics.drawable.IconCompat;

import com.example.stella.MainActivity;
import com.example.stella.R;

import java.util.Random;

/**
 * Esta clase es usada junto con las alarmas de las tareas. Se encarga de recibir mediante un intent el id, nombre de la tarea y nombre del perfil y lo muestra en una notificacion
 */

public class alarmReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        Log.i(TAG, "Enviando notificacion");

        Intent nextActivity = new Intent (context, MainActivity.class);
        PendingIntent pendingIntent = PendingIntent.getActivity(context, 0, nextActivity, PendingIntent.FLAG_UPDATE_CURRENT);

        int id = intent.getExtras().getInt("id");
        String nameTask = intent.getExtras().getString("nameTask");
        String nameProfile = intent.getExtras().getString("nameProfile");
        // Aqui se coge el bitmap del icono pero no va
        Bitmap bitmap = getAppIconBitmap(context);

        NotificationCompat.Builder builder = new NotificationCompat.Builder(context, "channelNotification")
                .setLargeIcon(bitmap)
                .setSmallIcon(R.drawable.ic_stat_name)
                .setContentTitle("Tarea pendiente para " + nameProfile)
                .setDefaults(NotificationCompat.DEFAULT_ALL)
                .setPriority(NotificationCompat.PRIORITY_DEFAULT)
                .setContentIntent(pendingIntent)
                .setAutoCancel(true);

        //int idd = Integer.valueOf(id);

        showNotification(builder, id, nameTask, context);

        Log.i(TAG, "Id recibido: " + id);
        Log.i(TAG, "Nombre recibido: " + nameTask);
        //notificationManagerCompat.cancel(id);
    }

    private void showNotification(NotificationCompat.Builder builder, int id, String nameTask, Context context){
        NotificationManagerCompat notificationManagerCompat = NotificationManagerCompat.from(context);
        //notificationManagerCompat.cancel(id);
        builder.setContentText(nameTask);
        notificationManagerCompat.notify(id, builder.build());
    }

    private Bitmap getAppIconBitmap(Context context){
        Bitmap bm = BitmapFactory.decodeResource(context.getResources(),
                R.drawable.ic_stat_name);
        return bm;
    }
}
