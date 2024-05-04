package com.example.stella.utils;

import android.content.Context;
import android.content.SharedPreferences;

import java.time.LocalDate;

/**
 * settings es la clase encargada de establecer o recoger las preferencias
 */

public class settings {
    Context context;
    SharedPreferences settings;
    SharedPreferences.Editor edit;
    public settings(Context context){
        this.context = context;
    }

    //=============================================================================
    // GETTERS
    //=============================================================================

    /**
     * getCurrentProfileID recoge el id del perfil que está en uso
     * @return
     */

    public int getCurrentProfileID(){
        settings = context.getSharedPreferences("profile", 0);
        int id = settings.getInt("id", 0);
        return id;
    }

    /**
     * getCurrentProfilename obtiene el nombre del perfil que está en uso
     * @return
     */

    public String getCurrentProfileName(){
        settings = context.getSharedPreferences("profile", 0);
        String profileName = settings.getString("name", "");
        return profileName;
    }

    /**
     * getAppTheme obtiene el tema que escogió el usuario
     * @return
     */

    public int getAppTheme(){
        settings = context.getSharedPreferences("generalSettings", 0);
        int theme = settings.getInt("appTheme", 0);
        return theme;
    }

    /**
     * getAppLanguage obtiene el idioma escogido por el usuario
     * @return
     */

    public int getAppLanguage(){
        settings = context.getSharedPreferences("generalSettings", 0);
        int language = settings.getInt("language", 0 );
        return language;
    }

    /**
     * getLastDailyAction obtiene la fecha de la ultima vez que "dailyActionWorker" realizó sus acciones
     * @return
     */

    public String getLastDailyAction(){
        settings = context.getSharedPreferences("lastDayDailyAction", 0);
        String lastDailyAction = settings.getString("lastDailyAction", "DEFAULT");
        return lastDailyAction;
    }

    /**
     * getNextTaskID obtiene un id para asignarselo a una tarea, luego se suma 1 a si mismo
     * @return
     */

    public int getNextTaskID(){
        settings = context.getSharedPreferences("nextID", Context.MODE_PRIVATE);
        int id = settings.getInt("id", -1);
        edit = settings.edit();
        edit.putInt("id", id+1);
        edit.commit();
        return id;
    }

    //=============================================================================
    // CHECKERS
    //=============================================================================

    /**
     * isFirstRunPassed verifica si la app ya se utilizo por primera vez anteriormente
     * @return
     */

    public boolean isFirstRunPassed(){
        settings = context.getSharedPreferences("FIRST_RUN", 0 );
        boolean check = settings.getBoolean("FIRST_RUN_BOOL", false);
        return check;
    }

    /**
     * isUserActive verifica si el perfil que se uso por ultima vez sigue en estado activo
     * @return
     */

    public boolean isUserActive(){
        SharedPreferences isUserActivePref = context.getSharedPreferences("isUserActive", 0);
        boolean check = isUserActivePref.getBoolean("isActive", false);
        return check;
    }

    //=============================================================================
    // SETTERS
    //=============================================================================

    /**
     * setuserActivity establece si el perfil esta en uso o se cerró sesión con él
     * @param check
     */

    public void setUserActivity(boolean check){
        settings = context.getSharedPreferences("isUserActive", 0);
        edit = settings.edit();
        edit.putBoolean("isActive", check);
        edit.commit();
    }

    /**
     * updateFirstRunPassed establece que la app ya se abrió por primera vez
     */

    public void updateFirstRunPassed(){
        settings = context.getSharedPreferences("FIRST_RUN", 0 );
        edit = settings.edit();
        edit.putBoolean("FIRST_RUN_BOOL", true);
        edit.commit();
    }

    /**
     * updateLastDailyAction actualiza la fecha de la ultima acción diaria de "dailyActionWorker" a la fecha de hoy
     */

    public void updateLastDailyAction(){
        settings = context.getSharedPreferences("lastDayDailyAction", 0);
        LocalDate localDate = LocalDate.now();
        String date = localDate.toString();
        edit = settings.edit();
        edit.putString("lastDailyAction", date);
        edit.commit();
    }

    /**
     * setInfoLastDailyAction establece la información de la ultima acción diaria usada por "dailtActionWorker"
     * @param info
     */

    public void setInfoLastDailyAction(String info){
        settings = context.getSharedPreferences("lastdayDailyAction", 0);
        edit = settings.edit();
        edit.putString("operationInfo",info);
        edit.commit();
    }

    /**
     * setAppTheme guarda el tema de la app
     * @param appTheme
     */

    public void setAppTheme(int appTheme){
        settings = context.getSharedPreferences("generalSettings", 0);
        edit = settings.edit();
        edit.putInt("appTheme", appTheme);
        edit.commit();
    }

    /**
     * setLanguage guarda el idioma de la app
     * @param language
     */

    public void setLanguage(int language){
        settings = context.getSharedPreferences("generalSettings", 0);
        edit = settings.edit();
        edit.putInt("language", language);
        edit.commit();
    }

    /**
     * setProfileName guarda el nombre del perfil escogido
     * @param name
     */

    public void setProfileName(String name){
        settings = context.getSharedPreferences("profile", 0);
        edit = settings.edit();
        edit.putString("name", name);
        edit.commit();
    }

    /**
     * setProfileID guarda el id del perfil escogido
     * @param id
     */

    public void setProfileID(int id){
        settings = context.getSharedPreferences("profile", 0);
        edit = settings.edit();
        edit.putInt("id", id);
        edit.commit();
    }

}
