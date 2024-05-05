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
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import com.example.stella.db.DbHelper;
import com.example.stella.db.dbLogic;
import com.example.stella.dialogs.dayPickerDialog;
import com.example.stella.dialogs.successDialog;
import com.example.stella.dialogs.timePickerDialog;
import com.example.stella.utils.loadSettings;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

/**
 * Esta clase muestra la pantalla en donde se editan las tareas
 */

public class screenEditTask extends AppCompatActivity {

    Spinner spinner;
    EditText name, description;
    CheckBox checkNotify;
    TextView textSelectedTime, textSelectedDays, btn_reset;
    LinearLayout layer_timeAndDaysEdit;

    int taskId = 0;
    int profileId = 0;
    String table = "";

    List<String> dayList;
    int hora = 0;
    int minuto = 0;

    boolean dayCheck = false;
    boolean timeCheck = false;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        loadSettings loadSettings = new loadSettings(this);
        loadSettings.loadSettings(this);
        setContentView(R.layout.screen_edit_task);

        spinner = (Spinner) findViewById(R.id.spinnerTypesEdit);
        name = findViewById(R.id.editTextEditName);
        description = findViewById(R.id.editTextEditDescription);
        checkNotify = findViewById(R.id.editNotifyCheckBox);
        textSelectedTime = findViewById(R.id.textSelectedTime);
        textSelectedDays = findViewById(R.id.textSelectedDays);
        btn_reset = findViewById(R.id.btn_reset);
        layer_timeAndDaysEdit = findViewById(R.id.layer_timeAndDaysEdit);
        dayList = new ArrayList<>();


        fillTypesSpinner(spinner);

        Intent intent = getIntent();

        taskId = intent.getIntExtra("id", 0);
        table = intent.getStringExtra("table");
        profileId = intent.getIntExtra("profileId", 0);

        fillAllSections(intent);
        showTimeAndDaysLayer();

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

        checkNotify.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                if(b){
                    layer_timeAndDaysEdit.setVisibility(View.VISIBLE);
                } else {
                    layer_timeAndDaysEdit.setVisibility(View.GONE);
                }
            }
        });



    }

    /**
     * fillAllSections rellena todas las secciones de la pantalla de editar tarea con los campos de la tarea, como el nombre, descripcion...
     * @param intent
     */

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

    /**
     * showTimeAndDaysLayer verifica si el checkBox "checkNotify" esta activo. Si lo esta, muestra el layer en donde se encuentra la hora y los dias de la tarea
     */

    private void showTimeAndDaysLayer(){
        if(checkNotify.isChecked()){
            layer_timeAndDaysEdit.setVisibility(View.VISIBLE);
        } else {
            layer_timeAndDaysEdit.setVisibility(View.GONE);
        }
    }

    /**
     * showDayPickerDialog muestra el dialog "dayPickerDialog"
     */

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

    /**
     * showTimePickerDialog muestra el dialog "timePickerDialog"
     */

    private void showTimePickerDialog(){
        timePickerDialog dialog = new timePickerDialog(this, hora, minuto, true, new timePickerDialog.OnTimeSetListener() {
            @Override
            public void onTimeSet(TimePicker timePicker, int selectedHour, int selectedMinute) {
                hora = selectedHour;
                minuto = selectedMinute;
                textSelectedTime.setText(String.format(Locale.getDefault(), "%02d:%02d:00", hora, minuto));
                timeCheck = true;
            }
        });

        dialog.show();
    }

    /**
     * resetTime reestablece el la hora al pulsar el boton resetear
     */

    public void resetTime(){
        textSelectedTime.setText(getResources().getString(R.string.never));
        timeCheck = false;
    }

    /**
     * auxSetDaysText metodo auxiliar para mostrar los dias seleccionados en un textView
     * @param auxList
     */

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

    /**
     * editTask es el metodo que edita la tarea, recoge los valores de los campos e inserta la tarea  si no se encuentra en alguna tabla o la actualiza si se encuentra
     * @param view
     */

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
        boolean auxCheck = false;
        boolean check = false;

        // Si se encuentra en la tabla "weeklytasks", actualiza la tarea en ella
        if(checkTaskInTable(tableWeekly)){
            auxCheck = updateTaskInWeeklyTasks();
            if(auxCheck) check = true;
        }
        // Si se encuentra en la tabla "pendingtasks", actualiza la tarea en ella
        if(checkTaskInTable(tablePending)){
            auxCheck = updateTaskInPendingTasks();
            if(auxCheck) check = true;
        }
        // Si se encuentra en "weeklytasks" pero no en "pendingtasks", uno de los dias seleccionados es hoy y no se encuentra en "completedTasks", la inserta en pendingtasks
        if(checkTaskInTable(tableWeekly) && !checkTaskInTable(tablePending) && auxCheckCurrentDay() && !checkTaskInTable(tableCompleted)){
            auxCheck = insertInPendingtasks();
            if(auxCheck) check = true;
        }
        // Si no se encuentra en "weeklytasks" pero se seleccionaron dias, se inserta en weeklytasks
        if(!checkTaskInTable(tableWeekly) && dayCheck){
            auxCheck = insertInWeeklytasks();
            if(auxCheck) check = true;
        }
        // Si esta en "completedtasks" se actualiza la tarea en ella
        if(checkTaskInTable(tableCompleted)){
            auxCheck = updateTaskInCompletedTasks();
            if(auxCheck) check = true;
        }

        if(check) showSuccessDialog();
    }

    /**
     * clearTimeAndDaysIfNeeded se utiliza en casos especificos, resetea el tiempo y los dias seleccionados
     */

    private void clearTimeAndDaysIfNeeded(){
        if(!checkNotify.isChecked()){
            Log.i(TAG, "Se hace el clear");
            timeCheck = false;
            dayCheck = false;
            dayList.clear();
        }
    }

    /**
     * insertInWeeklytasks inserta la tarea en la tabla "weeklytasks"
     * @return
     */

    private boolean insertInWeeklytasks(){
        ContentValues task = getTaskForWeeklytasks();
        task.put("id", taskId);
        dbLogic dbLogic = new dbLogic(this);
        boolean insertSuccessfully = dbLogic.insertTask("weeklytasks",task);
        if(insertSuccessfully) {
            Log.i(TAG, "insertInWeeklytasks: La tarea se insertó correctamente en WEEKLYTASKS.");
            return true;
        } else{
            Log.e(TAG, "insertInPendingtasks: La tarea no pudo insertarse correctamente.");
            return false;
        }
    }

    /**
     * insertInPendingtasks inserta la tarea en la tabla "pendingtasks"
     * @return
     */

    private boolean insertInPendingtasks(){
        ContentValues task = getTaskForPendingCompletedTasks();
        task.put("id", taskId);
        dbLogic dbLogic = new dbLogic(this);
        boolean insertSuccessfully = dbLogic.insertTask("pendingtasks", task);
        if(insertSuccessfully) {
            Log.i(TAG, "insertInPendingtasks: La tarea se insertó correctamente en PENDINGTASKS.");
            return true;
        } else{
            Log.e(TAG, "insertInPendingtasks: La tarea no pudo insertarse correctamente.");
            return false;
        }
    }

    /**
     * updateTaskInWeeklyTasks actualiza la tarea en la tabla "weeklytasks"
     * @return
     */

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
        boolean check = dbLogic.updateTask(taskId, "weeklytasks", task);
        if (check){
            return true;
        } else {
            return false;
        }

    }

    /**
     * updateTaskInPendingTasks actualiza la tarea en la tabla "pendingtasks"
     * @return
     */

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
        boolean check = dbLogic.updateTask(taskId, "pendingtasks", task);

        if (check){
           return true;
        } else{
            return false;
        }


    }

    /**
     * updateTaskInCompletedTasks actualiza la tarea en la table "completedtasks"
     * @return
     */

    private boolean updateTaskInCompletedTasks(){

        dbLogic dbLogic = new dbLogic(this);
        ContentValues task = getTaskForPendingCompletedTasks();
        boolean check = dbLogic.updateTask(taskId, "completedtasks", task);
        if (check){
            return true;
        } else{
            return false;
        }
    }

    /**
     * getTaskForWeeklytasks obtiene la tarea de la tabla "weeklytasks"
     * @return
     */

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
        task.put("profileId", profileId);
        return task;
    }

    /**
     * getTaskForPendingCompletedTasks obtiene la tarea para las tablas "pendingtasks" y "completedtasks"
     * @return
     */

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
        task.put("profileId", profileId);

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

    /**
     * auxGetType metodo auxiliar para obtener el nombre en ingles (asi se encuentran nombrados en las tablas) del tipo seleccionado
     * @param position
     * @return
     */

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

    /**
     * checkTaskInTable verifica si existe la tarea en la tabla pasada por parametro
     * @param tableName
     * @return
     */

    private boolean checkTaskInTable(String tableName){
        dbLogic dbLogic = new dbLogic(this);
        boolean check = dbLogic.checkTaskInTable(taskId, tableName);
        return check;
    }

    /**
     * showSuccessDialog enseña un dialog con un mensaje de exito
     */

    private void showSuccessDialog(){
        successDialog dialog = new successDialog();
        dialog.showDialog(this, 1);
    }

    /**
     * fillTypesSpinner rellena los valores del spinener con los tipos de la tarea
     * @param spinner
     */

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