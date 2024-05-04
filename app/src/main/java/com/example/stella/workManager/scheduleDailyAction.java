package com.example.stella.workManager;

import android.content.Context;
import android.util.Log;

import androidx.work.PeriodicWorkRequest;
import androidx.work.WorkInfo;
import androidx.work.WorkManager;

import com.google.common.util.concurrent.ListenableFuture;

import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;

public class scheduleDailyAction {

    private static final String TAG = "dailyActionWork";
    Context context;

    public scheduleDailyAction(Context c){
     context = c;
    }

    public String getTag(){
        return TAG;
    }


    public void scheduleWork() {
       /** // Calcula el tiempo de la próxima ejecución después de las 00:00 am
        Calendar currentTime = Calendar.getInstance();
        Calendar nextMidnight = Calendar.getInstance();
        nextMidnight.set(Calendar.HOUR_OF_DAY, 0);
        nextMidnight.set(Calendar.MINUTE, 0);
        nextMidnight.set(Calendar.SECOND, 0);

            nextMidnight.add(Calendar.DATE, 1);


        long delay = nextMidnight.getTimeInMillis() - currentTime.getTimeInMillis();

        // BORRAR MAS ADELANTE

        long horas = delay / (1000 * 60 * 60);
        long minutos = (delay / (1000 * 60)) % 60;
        long segundos = (delay / 1000) % 60;

        Log.i("ContentValues", "Tiempo hasta las 12: HORAS: " + horas + ", MINUTOS: " + minutos + ", SEGUNDOS: " + segundos);*/

        // Construye la solicitud de trabajo periódico con el intervalo calculado
        PeriodicWorkRequest periodicWorkRequest =
                new PeriodicWorkRequest.Builder(dailyActionWorker.class, 1, TimeUnit.HOURS)
                        //.setInitialDelay(delay, TimeUnit.MILLISECONDS)
                        .addTag(TAG)
                        .build();

        // Programa la solicitud de trabajo periódico
        WorkManager.getInstance(context)
                .enqueue(periodicWorkRequest);


    }

    public boolean isWorkScheduled(String tag) {
        WorkManager instance = WorkManager.getInstance(context);
        ListenableFuture<List<WorkInfo>> statuses = instance.getWorkInfosByTag(tag);
        try {
            List<WorkInfo> workInfos = statuses.get();
            for (WorkInfo workInfo : workInfos) {
                if (workInfo.getState() == WorkInfo.State.RUNNING || workInfo.getState() == WorkInfo.State.ENQUEUED) {
                    return true;
                }
            }
        } catch (ExecutionException | InterruptedException e) {
            e.printStackTrace();
        }
        return false;
    }


}
