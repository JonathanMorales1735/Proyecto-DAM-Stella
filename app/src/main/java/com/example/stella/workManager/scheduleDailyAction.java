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
