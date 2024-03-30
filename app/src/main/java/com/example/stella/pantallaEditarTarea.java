package com.example.stella;

import static android.content.ContentValues.TAG;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.ContentValues;
import android.content.Intent;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import com.example.stella.db.DbHelper;
import com.example.stella.db.dbLogic;
import com.example.stella.dialogs.dayPickerDialog;
import com.example.stella.dialogs.timePickerDialog;
import com.example.stella.utils.loadSettings;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;

public class pantallaEditarTarea extends AppCompatActivity {

    Spinner spinner;
    EditText name, description;
    CheckBox checkNotify;
    TextView textSelectedTime, textSelectedDays, btn_reset;

    int taskId = 0;
    String table = "";

    List<String> dayList;
    Calendar timeCalendar;
    int hora = 0;
    int minuto = 0;

    boolean dayCheck = false;
    boolean timeCheck = false;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        loadSettings loadSettings = new loadSettings(this);
        loadSettings.loadSettings(this);
        setContentView(R.layout.pantallaeditartarea);

        spinner = (Spinner) findViewById(R.id.spinnerTypesEdit);
        name = findViewById(R.id.editTextEditName);
        description = findViewById(R.id.editTextEditDescription);
        checkNotify = findViewById(R.id.editNotifyCheckBox);
        textSelectedTime = findViewById(R.id.textSelectedTime);
        textSelectedDays = findViewById(R.id.textSelectedDays);
        btn_reset = findViewById(R.id.btn_reset);
        dayList = new ArrayList<>();
        timeCalendar = Calendar.getInstance();


        fillTypesSpinner(spinner);

        Intent intent = getIntent();

        taskId = intent.getIntExtra("id", 0);
        table = intent.getStringExtra("table");

        fillAllSections(intent);

        textSelectedDays.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showDayPickerDialog();
            }
        });

        textSelectedTime.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showTimePickerDialog();
            }
        });

        btn_reset.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                resetTime();
            }
        });



    }

    private void fillAllSections(Intent intent){
        String auxName = intent.getStringExtra("name");
        String auxDescription = intent.getStringExtra("description");
        int auxNotify = intent.getIntExtra("notify", 0);
        String auxType = intent.getStringExtra("type");
        String auxTime = intent.getStringExtra("time");



        name.setText(auxName);
        description.setText(auxDescription);

        if(auxNotify == 1){
            checkNotify.setChecked(true);
        } else {
            checkNotify.setChecked(false);
        }
        // TODO CUANDO QUIERES EDITAR UNA TAREA EN COMPLETEDTASK SE CIERRA LA APP
        switch (auxType){
            case "work":
                spinner.setSelection(0);
                break;
            case "domestic":
                spinner.setSelection(1);
                break;
            case "study":
                spinner.setSelection(2);
                break;
            case "leisure":
                spinner.setSelection(3);
                break;
        }

        if(checkNotify.isChecked()){
            // Colocamos la hora en su textview
            if(auxTime == null) {
                auxTime = getResources().getString(R.string.never);
            } else {
                timeCheck = true;
            }
            textSelectedTime.setText(auxTime);

            // A continuación, colocamos los dias que tiene la tarea en la tabla weeklytasks

            DbHelper dbH = new DbHelper(this);
            SQLiteDatabase db = dbH.getWritableDatabase();
            boolean aux = false;
            dayList.clear();

            String query = "select monday, tuesday, wednesday, thursday, friday, saturday, sunday from weeklytasks where id = " + taskId + "";

            Cursor cursor = db.rawQuery(query, null);

            while (cursor.moveToFirst()){


                List<String> list = new ArrayList<>();
                list.add("Monday");
                list.add("Tuesday");
                list.add("Wednesday");
                list.add("Thursday");
                list.add("Friday");
                list.add("Saturday");
                list.add("Sunday");

                List<String> daysListAppLanguage = new ArrayList<>();
                daysListAppLanguage.add(getResources().getString(R.string.monday));
                daysListAppLanguage.add(getResources().getString(R.string.tuesday));
                daysListAppLanguage.add(getResources().getString(R.string.wednesday));
                daysListAppLanguage.add(getResources().getString(R.string.thursday));
                daysListAppLanguage.add(getResources().getString(R.string.friday));
                daysListAppLanguage.add(getResources().getString(R.string.saturday));
                daysListAppLanguage.add(getResources().getString(R.string.sunday));

                List<String> listForText = new ArrayList<>();

                int order = 0;

                for(String day: list){
                    int getCursorInt = cursor.getInt(order);
                    if(getCursorInt == 1){
                        dayList.add(day);
                        listForText.add(daysListAppLanguage.get(order));
                        Log.i(TAG, "Dia cargado " + order + ": " + day);
                        dayCheck = true;
                    }
                    order++;
                }

                auxSetDaysText(listForText);
                break;

            }

            try {
                db.close();
                cursor.close();
                dbH.close();
            } catch (SQLException e){
                e.printStackTrace();
            }

        }



    }

    private void showDayPickerDialog(){
        dayPickerDialog dialog = new dayPickerDialog(this, new dayPickerDialog.OnDaySetListener() {
            @Override
            public void onDaySet(List<String> list, String daysSelection) {
                dayList = list;
                textSelectedDays.setText(daysSelection);
                if(!dayList.isEmpty()){
                    dayCheck = true;
                } else {
                    dayCheck = false;
                }
            }
        });
        dialog.show();
    }

    private void showTimePickerDialog(){
        timePickerDialog dialog = new timePickerDialog(this, hora, minuto, true, new timePickerDialog.OnTimeSetListener() {
            @Override
            public void onTimeSet(TimePicker timePicker, int selectedHour, int selectedMinute) {
                hora = selectedHour;
                minuto = selectedMinute;
                textSelectedTime.setText(String.format(Locale.getDefault(), "%02d:%02d", hora, minuto));
                timeCalendar.set(Calendar.HOUR_OF_DAY, hora);
                timeCalendar.set(Calendar.MINUTE, minuto);
                timeCalendar.set(Calendar.SECOND, 00);
                timeCheck = true;
            }
        });

        dialog.show();
    }

    public void resetTime(){
        textSelectedTime.setText(getResources().getString(R.string.never));
        timeCheck = false;
        timeCalendar.clear();
    }

    private void auxSetDaysText(List<String> auxList){
        String text = "";
        if(!auxList.isEmpty()){
            for(String s: auxList){
                s = s.substring(0, 2);
                text = text + "," + s;
                Log.i(TAG, text);
            }
            text = text.substring(1, text.length());
            Log.i(TAG, text);

            this.textSelectedDays.setText(text);
        } else {
            this.textSelectedDays.setText(getResources().getString(R.string.never));
        }

    }

    public void editTask(View view){

        if( checkNotify.isChecked() && (!timeCheck && !dayCheck)){
            Toast.makeText(this, "No has seleccionado día u hora.", Toast.LENGTH_SHORT).show();
            Log.i(TAG, "No ha seleccionado dia u hora");
            return;
        }

        clearTimeAndDaysIfNeeded();

        String tablePending = "pendingtasks";
        String tableWeekly = "weeklytasks";
        String tableCompleted = "completedtasks";


        if(checkTaskInTable(tableWeekly)){
            updateTaskInWeeklyTasks();
        }
        if(checkTaskInTable(tablePending)){
            updateTaskInPendingTasks();
        }
        if(checkTaskInTable(tableWeekly) && !checkTaskInTable(tablePending) && auxCheckCurrentDay()){
            insertInPendingtasks();
        }
        if(!checkTaskInTable(tableWeekly) && dayCheck){
            insertInWeeklytasks();
        }
        if(checkTaskInTable(tableCompleted)){
            updateTaskInCompletedTasks();
        }
    }

    private void clearTimeAndDaysIfNeeded(){
        if(!checkNotify.isChecked()){
            Log.i(TAG, "Se hace el clear");
            timeCheck = false;
            dayCheck = false;
            dayList.clear();
            timeCalendar.clear();
        }
    }

    private void insertInWeeklytasks(){
        ContentValues task = getTaskForWeeklytasks();
        task.put("id", taskId);
        dbLogic dbLogic = new dbLogic(this);
        boolean insertSuccessfully = dbLogic.insertTask(timeCalendar, "weeklytasks",task);
        if(insertSuccessfully) {
            Log.i(TAG, "insertInWeeklytasks: La tarea se insertó correctamente en WEEKLYTASKS.");
        } else{
            Log.e(TAG, "insertInPendingtasks: La tarea no pudo insertarse correctamente.");
        }
    }

    private void insertInPendingtasks(){
        ContentValues task = getTaskForPendingCompletedTasks();
        task.put("id", taskId);
        dbLogic dbLogic = new dbLogic(this);
        boolean insertSuccessfully = dbLogic.insertTask(timeCalendar, "pendingtasks", task);
        if(insertSuccessfully) {
            Log.i(TAG, "insertInPendingtasks: La tarea se insertó correctamente en PENDINGTASKS.");
        } else{
            Log.e(TAG, "insertInPendingtasks: La tarea no pudo insertarse correctamente.");
        }
    }

    private boolean updateTaskInWeeklyTasks(){

        dbLogic dbLogic = new dbLogic(this);

        if(dayList.isEmpty()){
            Log.i(TAG, "updateTaskInWeekletasksTasks: La lista de días de la tarea está vacía, se procederá a eliminarla de WEEKLYTASKS.");
            boolean  deletesuccessfully = dbLogic.deleteTask(taskId, "weeklytasks");
            if(deletesuccessfully) {
                Log.i(TAG, "updateTaskInWeekletasksTasks: La tarea se eliminó correctamente de WEEKLYTASKS.");
            } else{
                Log.e(TAG, "updateTaskInWeekletasksTasks: La tarea no pudo eliminarse correctamente.");
            }
            return true;
        }

        Log.i(TAG, "updateTaskInWeeklyTasks: La lista de dias tiene días, se hará el update.");
        ContentValues task = getTaskForWeeklytasks();
        boolean check = dbLogic.updateTask(taskId, timeCalendar, "weeklytasks", task);
        if (check){
            return true;
        } else {
            return false;
        }

    }


    private boolean updateTaskInPendingTasks(){

        dbLogic dbLogic = new dbLogic(this);

        if(checkTaskInTable("weeklytasks") && !auxCheckCurrentDay()){
            Log.i(TAG, "updateTaskInPendingTasks: La tarea requiere día en específico y no es hoy. Se procederá a su eliminación de PENDINGTASKS.");
            boolean  deletesuccessfully = dbLogic.deleteTask(taskId, "pendingtasks");
            if(deletesuccessfully) {
                Log.i(TAG, "updateTaskInPendingTasks: La tarea se eliminó correctamente de PENDINGTASKS.");
            } else{
                Log.e(TAG, "updateTaskInPendingTasks: La tarea no pudo eliminarse correctamente.");
            }

            return true;
        }

        ContentValues task = getTaskForPendingCompletedTasks();
        boolean check = dbLogic.updateTask(taskId, timeCalendar, "pendingtasks", task);

        if (check){
           return true;
        } else{
            return false;
        }


    }

    private boolean updateTaskInCompletedTasks(){

        dbLogic dbLogic = new dbLogic(this);
        ContentValues task = getTaskForPendingCompletedTasks();
        boolean check = dbLogic.updateTask(taskId, timeCalendar, "completedtasks", task);
        if (check){
            return true;
        } else{
            return false;
        }
    }


    @NonNull
    private ContentValues getTaskForWeeklytasks(){
        int taskNotify = 1;
        String taskTime = null;
        if(timeCheck) {
            taskTime = textSelectedTime.getText().toString();
        }
        String taskType = auxGetType(spinner.getSelectedItemPosition());
        int monday = 0, tuesday = 0, wednesday = 0, thursday = 0, friday = 0, saturday = 0, sunday = 0;

        for(String day: dayList){
            Log.i(TAG, "Dia del getTaskForWeeklytasks: " + day);
            switch (day.toLowerCase()){
                case "monday":
                    monday = 1;
                    break;
                case "tuesday":
                    tuesday = 1;
                    break;
                case "wednesday":
                    wednesday = 1;
                    break;
                case "thursday":
                    thursday = 1;
                    break;
                case "friday":
                    friday = 1;
                    break;
                case "saturday":
                    saturday = 1;
                    break;
                case "sunday":
                    sunday = 1;
                    break;

            }
        }

        ContentValues task = new ContentValues();
        task.put("name", name.getText().toString());
        task.put("description", description.getText().toString());
        task.put("type", taskType);
        task.put("notify", taskNotify);
        task.put("time", taskTime);
        task.put("monday", monday);
        task.put("tuesday", tuesday);
        task.put("wednesday", wednesday);
        task.put("thursday", thursday);
        task.put("friday", friday);
        task.put("saturday", saturday);
        task.put("sunday", sunday);
        return task;
    }

    @NonNull
    private ContentValues getTaskForPendingCompletedTasks(){
        int taskNotify = checkNotify.isChecked() ? 1 : 0;
        String taskTime = null;
        if(timeCheck) {
            taskTime = textSelectedTime.getText().toString();
        }
        String taskType = auxGetType(spinner.getSelectedItemPosition());

        ContentValues task = new ContentValues();
        task.put("name", name.getText().toString());
        task.put("description", description.getText().toString());
        task.put("type", taskType);
        task.put("notify", taskNotify);
        task.put("time", taskTime);

        return task;
    }

    /**
     * Retorna true si el dia de hoy se encuentra en la lista "dayList", false si no lo está.
     * @return
     */

    private boolean auxCheckCurrentDay(){
        String day = LocalDate.now().getDayOfWeek().name();
        day = day.toLowerCase();
        boolean b = false;
        for(String d : dayList){
            if(d.toLowerCase().equals(day)){
                b = true;
            }
        }

        return b;
    }

    private String auxGetType(int position){
        String type = "";

        switch (position){
            case 0:
                type = "work";
                break;
            case 1:
                type = "domestic";
                break;
            case 2:
                type = "study";
                break;
            case 3:
                type = "leisure";
                break;
        }

        return type;
    }

    private boolean checkTaskInTable(String tableName){
        dbLogic dbLogic = new dbLogic(this);
        boolean check = dbLogic.checkTaskInTable(taskId, tableName);
        return check;
    }



    private void fillTypesSpinner(Spinner spinner){
        ArrayAdapter<CharSequence> adapterTheme = ArrayAdapter.createFromResource(this, R.array.types_array, android.R.layout.simple_spinner_item);
        adapterTheme.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(adapterTheme);

        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parentView, View selectedItemView, int position, long id) {
                ((TextView)parentView.getChildAt(0)).setTextColor(Color.WHITE);
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });
    }
}