package com.example.stella.recyclerViewsAdapters;

import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import com.example.stella.db.DbHelper;
import com.example.stella.db.dbLogic;
import com.example.stella.dialogs.taskInfoDialog;

import java.util.List;

/**
 * adaptersLogic guarda logica que se utiliza en los "adapter" de todos los recyclerViews de la app
 */

public class adaptersLogic {
    public static final String TAG = adaptersLogic.class.getName();
    Context c;
    taskInfoDialog taskInfoDialog = null;
    dbLogic dbLogic;

    public adaptersLogic(Context context){
        c = context;
        dbLogic = new dbLogic(c);
    }

    /**
     * deleteTaskInWeeklyTasks es un método que se encarga de borrar una tarea en la tabla weeklytasks
     * @param id
     */

    public void deleteTaskInWeeklyTasks(int id){
        // Se borra la tarea en la tabla weeklytasks
        boolean deleteSuccessfully = dbLogic.deleteTask(id, "weeklytasks");
        // Se verifica si tuvo exito
        if(deleteSuccessfully){
            Log.i(TAG, "Deleted task successfully in WEEKLYTASKS with id " + id);
        } else{
            Log.i(TAG, "Couldn't delete task in WEEKLYTASKS with id " + id);
        }
        // Si esta en pendiente se elimina tambien
        boolean checkTaskInPendingTasks = dbLogic.checkTaskInTable(id, "pendingtasks");
        if(checkTaskInPendingTasks){
            Log.i(TAG, "Task with id " + id + " is in PENDINGTASKS as well. Proceeding to its elimination");
            boolean deleteSuccInPending = dbLogic.deleteTask(id, "pendingtasks");
            if(deleteSuccInPending){
                Log.i(TAG, "Deleted task successfully in PENDINGTASKS with id " + id);
            }
        }
    }

    /**
     * showTaskInfo muestra un dialog con la información de la tarea
     * @param id
     * @param tableName
     */

    public void showTaskInfo(int id, String tableName){
        if(taskInfoDialog == null || !taskInfoDialog.isShowing()){
            taskInfoDialog = new taskInfoDialog(c ,id, tableName);;
            taskInfoDialog.show();
        }
    }

    /**
     * getTaskFullInfo obtiene todos los campos de la tarea, se busca por id y por nombre de la tabla
     * @param id
     * @param tableName
     * @return
     */

    public taskElement getTaskFullInfo(int id, String tableName){
        // Se obtiene conexión con base de datos
        DbHelper dbHelper = new DbHelper(c);
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        taskElement task = null;
        // Se ejecuta una query para obtener todos los datos de una tarea
        Cursor cursor = db.rawQuery("Select id, name, description, type, notify, time, profileId from " + tableName +" where id = " + id, null);
        // Se forma el objeto con los datos recogidos en el cursor
        while(cursor.moveToNext()){
            task = new taskElement();
            task.setId(cursor.getInt(0));
            task.setName(cursor.getString(1));
            task.setDescription(cursor.getString(2));
            task.setType(cursor.getString(3));
            task.setNotify(cursor.getInt(4));
            task.setTime(cursor.getString(5));
            task.setProfileId(cursor.getInt(6));
        }
        try{
            // Se cierra la conexión
            db.close();
            cursor.close();
        } catch (SQLException e){
            e.printStackTrace();
        }

        return task;
    }

    /**
     * getPendingTasksList obtiene una lista con todas las tareas de la tabla pendingtasks
     * @return
     */

    public List<taskElement> getPendingtasksList(){
        List<taskElement> list = dbLogic.getPendingTasksList();
        return list;
    }

    /**
     * getCompletedtasksList obtiene una lista con todas las tareas de la tabla completedtasks
     * @return
     */

    public List<taskElement> getCompletedtasksList(){
        List<taskElement> list = dbLogic.getCompletedTasksList();
        return list;
    }

    /**
     * getWeeklytasksList obtiene una lsita con todas las tareas de la tabla weeklytasks
     * @param day
     * @return
     */

    public List<taskElement> getWeeklytasksList(String day){
        List<taskElement> list = dbLogic.getWeeklyTasksList(day);
        return list;
    }

    /**
     * deleteTasks borra una tarea a partir de su id y el nombre de la tabla en la que se encuentra
     * @param id
     * @param tableName
     */

    public void deleteTask(int id, String tableName){
        dbLogic.deleteTask(id, tableName);
    }


}
