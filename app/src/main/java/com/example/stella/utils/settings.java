package com.example.stella.utils;

import android.content.Context;
import android.content.SharedPreferences;

import java.time.LocalDate;

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

    public int getCurrentProfileID(){
        settings = context.getSharedPreferences("profile", 0);
        int id = settings.getInt("id", 0);
        return id;
    }

    public String getCurrentProfileName(){
        settings = context.getSharedPreferences("profile", 0);
        String profileName = settings.getString("name", "");
        return profileName;
    }

    public int getAppTheme(){
        settings = context.getSharedPreferences("generalSettings", 0);
        int theme = settings.getInt("appTheme", 0);
        return theme;
    }

    public int getAppLanguage(){
        settings = context.getSharedPreferences("generalSettings", 0);
        int language = settings.getInt("language", 0 );
        return language;
    }

    public String getLastDailyAction(){
        settings = context.getSharedPreferences("lastDayDailyAction", 0);
        String lastDailyAction = settings.getString("lastDailyAction", "DEFAULT");
        return lastDailyAction;
    }

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

    public boolean isFirstRunPassed(){
        settings = context.getSharedPreferences("FIRST_RUN", 0 );
        boolean check = settings.getBoolean("FIRST_RUN_BOOL", false);
        return check;
    }

    public boolean isUserActive(){
        SharedPreferences isUserActivePref = context.getSharedPreferences("isUserActive", 0);
        boolean check = isUserActivePref.getBoolean("isActive", false);
        return check;
    }

    //=============================================================================
    // SETTERS
    //=============================================================================

    public void setUserActivity(boolean check){
        settings = context.getSharedPreferences("isUserActive", 0);
        edit = settings.edit();
        edit.putBoolean("isActive", check);
        edit.commit();
    }

    public void updateFirstRunPassed(){
        settings = context.getSharedPreferences("FIRST_RUN", 0 );
        edit = settings.edit();
        edit.putBoolean("FIRST_RUN_BOOL", true);
        edit.commit();
    }

    public void updateLastDailyAction(){
        settings = context.getSharedPreferences("lastDayDailyAction", 0);
        LocalDate localDate = LocalDate.now();
        String date = localDate.toString();
        edit = settings.edit();
        edit.putString("lastDailyAction", date);
        edit.commit();
    }

    public void setInfoLastDailyAction(String info){
        settings = context.getSharedPreferences("lastdayDailyAction", 0);
        edit = settings.edit();
        edit.putString("operationInfo",info);
        edit.commit();
    }

    public void setAppTheme(int appTheme){
        settings = context.getSharedPreferences("generalSettings", 0);
        edit = settings.edit();
        edit.putInt("appTheme", appTheme);
        edit.commit();
    }

    public void setLanguage(int language){
        settings = context.getSharedPreferences("generalSettings", 0);
        edit = settings.edit();
        edit.putInt("language", language);
        edit.commit();
    }

    public void setProfileName(String name){
        settings = context.getSharedPreferences("profile", 0);
        edit = settings.edit();
        edit.putString("name", name);
        edit.commit();
    }

    public void setProfileID(int id){
        settings = context.getSharedPreferences("profile", 0);
        edit = settings.edit();
        edit.putInt("id", id);
        edit.commit();
    }

}
