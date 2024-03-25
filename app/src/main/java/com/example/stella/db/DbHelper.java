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
        createtables(sqLiteDatabase, CONTEXT);
        insertdata(sqLiteDatabase, CONTEXT);
        initialID();
    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1) {
        //insertdata(sqLiteDatabase, CONTEXT);
    }



    private void createtables(SQLiteDatabase sqldb, Context context){

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

    private void insertdata(SQLiteDatabase sqldb, Context context){
        Log.i(TAG, "Iniciando insercción de datos");


        int resource = context.getResources().getIdentifier("insercciondedatossql", "raw", context.getPackageName());;

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

    // Al insertar datos manualmente, establezco el siguiente id que debería tener "nextID"

    private void initialID(){
        SharedPreferences preferences = CONTEXT.getSharedPreferences("nextID", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = preferences.edit();
        editor.putInt("id", 13);
        editor.commit();
    }

}
