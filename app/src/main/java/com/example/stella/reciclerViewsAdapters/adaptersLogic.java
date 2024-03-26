package com.example.stella.reciclerViewsAdapters;

import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import com.example.stella.db.DbHelper;
import com.example.stella.db.dbLogic;
import com.example.stella.dialogs.taskInfoDialog;

public class adaptersLogic {
    public static final String TAG = adaptersLogic.class.getName();
    Context c;
    taskInfoDialog taskInfoDialog = null;

    public adaptersLogic(Context context){
        c = context;
    }

    public void deleteTaskInWeeklyTasks(int id){
        dbLogic dbLogic = new dbLogic(c);
        boolean deleteSuccessfully = dbLogic.deleteTask(id, "weeklytasks");
        if(deleteSuccessfully){
            Log.i(TAG, "Deleted task successfully in WEEKLYTASKS with id " + id);
        } else{
            Log.i(TAG, "Couldn't delete task in WEEKLYTASKS with id " + id);
        }
        boolean checkTaskInPendingTasks = dbLogic.checkTaskInTable(id, "pendingtasks");
        if(checkTaskInPendingTasks){
            Log.i(TAG, "Task with id " + id + " is in PENDINGTASKS as well. Proceeding to its elimination");
            boolean deleteSuccInPending = dbLogic.deleteTask(id, "pendingtasks");
            if(deleteSuccInPending){
                Log.i(TAG, "Deleted task successfully in PENDINGTASKS with id " + id);
            }
        }
    }

    public void showTaskInfo(int id, String tableName){
        if(taskInfoDialog == null || !taskInfoDialog.isShowing()){
            taskInfoDialog = new taskInfoDialog(c ,id, tableName);;
            taskInfoDialog.show();
        }
    }

    public taskElement getTaskFullInfo(int id, String tableName){
        DbHelper dbHelper = new DbHelper(c);
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        taskElement task = null;

        Cursor cursor = db.rawQuery("Select id, name, description, type, notify, time from " + tableName +" where id = " + id, null);

        while(cursor.moveToNext()){
            task = new taskElement();
            task.setId(cursor.getInt(0));
            task.setName(cursor.getString(1));
            task.setDescription(cursor.getString(2));
            task.setType(cursor.getString(3));
            task.setNotify(cursor.getInt(4));
            task.setTime(cursor.getString(5));
        }
        try{
            db.close();
            cursor.close();
        } catch (SQLException e){
            e.printStackTrace();
        }

        return task;
    }

}
