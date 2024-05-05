package com.example.stella.db;

import static android.content.ContentValues.TAG;

import android.content.Context;
import android.content.SharedPreferences;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import androidx.annotation.Nullable;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

/**
 * Esta clase es la encargada de formar la base de datos
 */

public class DbHelper extends SQLiteOpenHelper {

    private static final int DATABASE_VERSION = 1;
    private static final String DATABASE_NAME = "stella.db";
    private static Context CONTEXT = null;

    public DbHelper(@Nullable Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
        CONTEXT = context;
    }

    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {
        createTables(sqLiteDatabase, CONTEXT);
        insertData(sqLiteDatabase, CONTEXT);
        initialID();
    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1) {
        //insertdata(sqLiteDatabase, CONTEXT);
    }

    /**
     * createTables se encarga de la creación de las tablas de la base de datos . Para ello hace uso de un documento en res>raw, llamado
     * "creaciondelatablasql"
     * @param sqldb
     * @param context
     */

    private void createTables(SQLiteDatabase sqldb, Context context){

        int resource = context.getResources().getIdentifier("creaciondetablassql", "raw", context.getPackageName());;

        InputStream insertsStream = context.getResources().openRawResource(resource);
        BufferedReader insertReader = new BufferedReader(new InputStreamReader(insertsStream));

        try {
            while (insertReader.ready()) {
                String insertStmt = insertReader.readLine();
                Log.i(TAG, insertStmt);
                sqldb.execSQL(insertStmt);

            }
        }catch (IOException e){
            Log.e(TAG, "createdb: Error en la creacion de base de datos y sus tablas.");
            e.printStackTrace();
        } finally {
            try {
                insertReader.close();
            } catch (IOException e) {
                Log.e(TAG, "createdb: Error al cerrar el flujo de datos.");
                e.printStackTrace();
            }
        }

    }

    /**
     * insertData se encarga de la insercción en las tablas de la base de datos . Para ello hace uso de un documento en res>raw, llamado
     * "insercciondedatossql"
     * @param sqldb
     * @param context
     */

    private void insertData(SQLiteDatabase sqldb, Context context){
        Log.i(TAG, "Iniciando insercción de datos");


        int resource = context.getResources().getIdentifier("inserciondedatossql", "raw", context.getPackageName());;

        InputStream insertsStream = context.getResources().openRawResource(resource);
        BufferedReader insertReader = new BufferedReader(new InputStreamReader(insertsStream));

        try {
            while (insertReader.ready()) {
                String insertStmt = insertReader.readLine();
                Log.i(TAG, insertStmt);
                sqldb.execSQL(insertStmt);

            }
        }catch (IOException e){
            Log.e(TAG, "createdb: Error al insertar los datos iniciales en la base de datos.");
            e.printStackTrace();
        } finally {
            try {
                insertReader.close();
            } catch (IOException e) {
                Log.e(TAG, "createdb: Error al cerrar el flujo de datos.");
                e.printStackTrace();
            }
        }
    }

    /**
     * initialDb se usa para establecer un ID principal el cual se usara para las siguientes tareas que sean creadas. Es así debido a que la id en las tareas se introduce fuera de la DB
     * y ya insertamos 12 tareas en "insertData"
     */

    private void initialID(){
        SharedPreferences preferences = CONTEXT.getSharedPreferences("nextID", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = preferences.edit();
        editor.putInt("id", 13);
        editor.commit();
    }

}
