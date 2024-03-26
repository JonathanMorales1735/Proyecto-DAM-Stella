package com.example.stella;

import static android.content.ContentValues.TAG;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.AlarmManager;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.PendingIntent;
import android.app.TimePickerDialog;
import android.content.ContentValues;
import android.content.Context;
import android.content.SharedPreferences;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Color;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import com.example.stella.db.DbHelper;
import com.example.stella.dialogs.dayPickerDialog;
import com.example.stella.dialogs.timePickerDialog;
import com.example.stella.utils.Alarm;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;

public class pantallaNuevaTarea extends AppCompatActivity {

    int hora, minuto;
    TextView textSelectedTime, textSelectedDays;
    EditText name, description;
    CheckBox notifyCheckBox;
    LinearLayout layer_horario, layer_dias;
    Spinner spinner;
    List<String> dayList;
    Calendar timeCalendar;
    AlarmManager alarmManager;
    PendingIntent pendingIntent;
    Alarm alarm;
    boolean timeCheck, daysCheck; // Con estas booleanas se sabrá si el usuario ha escogido una hora y/o día o no.

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.pantallanuevatarea);

        textSelectedTime = (TextView) findViewById(R.id.textSelectedTime);
        textSelectedDays = (TextView) findViewById(R.id.textSelectedDays);
        name = (EditText) findViewById(R.id.editTextEditName);
        description = (EditText) findViewById(R.id.editTextEditDescription);
        notifyCheckBox = (CheckBox) findViewById(R.id.editNotifyCheckBox);
        layer_horario = (LinearLayout) findViewById(R.id.layer_horario);
        layer_dias = (LinearLayout) findViewById(R.id.layer_dias);
        spinner = (Spinner) findViewById(R.id.spinnerTypes);

        fillTypesSpinner(spinner);
        dayList = new ArrayList<>();
        timeCalendar = Calendar.getInstance();

        layer_horario.setVisibility(View.GONE); // GONE para quitarlo, VISIBLE para ponerlo
        layer_dias.setVisibility(View.GONE);

        textSelectedTime.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showTimePickerDialog();
            }
        });

        textSelectedDays.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showDayPickerDialog();
            }
        });


        notifyCheckBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener(){
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean isChecked) {
                if(isChecked){
                    layer_horario.setVisibility(View.VISIBLE);
                    layer_dias.setVisibility(View.VISIBLE);
                } else {
                    layer_horario.setVisibility(View.GONE);
                    layer_dias.setVisibility(View.GONE);
                }
            }

        });

    }

    public void createNewTask(View view){

        int notify = 0;
        String nameText;
        String descriptionText = null;

        long checkInsert = 0; // Variable para saber si se insertó bien la tarea o no


        if (TextUtils.isEmpty(String.valueOf(name.getText()))){
            Toast.makeText(this, getResources().getString(R.string.nameEmpty), Toast.LENGTH_SHORT).show();
            Log.i(TAG, "No hay nombre");
            return;
        }

        if(notifyCheckBox.isChecked()){
            notify = 1;
        }


        DbHelper dbH = new DbHelper(this);
        SQLiteDatabase db = dbH.getWritableDatabase();
        nameText = String.valueOf(name.getText());
        if(!TextUtils.isEmpty(String.valueOf(description))){
            descriptionText = String.valueOf(description.getText());
        }

        String typeName = auxGetType(spinner.getSelectedItemPosition());

        ContentValues task = new ContentValues();
        task.put("id", auxGetNextID());
        task.put("name", nameText);
        task.put("description", descriptionText);
        task.put("type", typeName);
        task.put("notify", notify);
        // Si no requiere de notificación, la tarea será de duración infinita y se meterá directa a la tabla PENDINGTASKS
        if(notify == 0){

            checkInsert = auxInsertTask(task, db, "PENDINGTASKS");
            clearNewTaskScreen();
            Toast.makeText(this, getResources().getString(R.string.newtaskcreatedsuccessfully), Toast.LENGTH_SHORT).show();
            Log.i(TAG, "CheckInsert: " + checkInsert);

        // Si requiere de notificación, la tarea será de una duración de solo un día, así que se ingresará: [1]En WEEKLYTASKS si el usuario
        // seleccinó algun día para repetir, [2] En WEEKLYTASKS si el usuario seleccionó algún día y en PENDINGTASKS si ese dia es hoy ó
        // [3] En PENDINGTASKS si el usuario solo seleccionó una hora para la notificación, pero no un día. Sin embargo, si el usuario seleccionó que quiere un aviso
        //  pero no seleccionó ningún dia y ninguna hora, se meterá en PENDINGTASKS como una tarea de duración infinita.
        } else {
            if(!timeCheck && !daysCheck){
                Toast.makeText(this, "No has seleccionado día u hora.", Toast.LENGTH_SHORT).show();
                Log.i(TAG, "No ha seleccionado dia u hora");
                return;
            }
            if(timeCheck){
                String time = String.format(Locale.getDefault(), "%02d:%02d:00", hora, minuto);
                task.put("time", time);
            }
            if(daysCheck){
                if(auxCheckCurrentDay()){
                    checkInsert = auxInsertTask(task, db, "PENDINGTASKS");
                    Log.i(TAG, "CheckInsert: " + checkInsert);
                }
                for(String day: dayList){
                    task.put(day, 1);
                }
                checkInsert = auxInsertTask(task, db, "WEEKLYTASKS");
                clearNewTaskScreen();
                Toast.makeText(this, getResources().getString(R.string.newtaskcreatedsuccessfully), Toast.LENGTH_SHORT).show();
                Log.i(TAG, "CheckInsert: " + checkInsert);
            } else {
                checkInsert = auxInsertTask(task, db, "PENDINGTASKS");
                clearNewTaskScreen();
                Toast.makeText(this, getResources().getString(R.string.newtaskcreatedsuccessfully), Toast.LENGTH_SHORT).show();
                Log.i(TAG, "CheckInsert: " + checkInsert);
            }
        }

        Log.i(TAG, "Id de la tarea: " + String.valueOf(task.get("id")));
        try {
            db.close();
        } catch (SQLException e){
            e.printStackTrace();
        }


    }



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

    private long auxInsertTask(ContentValues cv, @NonNull SQLiteDatabase db1, String tableName){

        long mid = 0;
        SQLiteDatabase db = db1;
        try {
            mid = db.insertOrThrow(tableName, null, cv);
            Log.i(TAG, "Inserción en " + tableName + " : " + String.valueOf(mid));
            boolean alarmTime = cv.containsKey("time");
            tableName = tableName.toLowerCase();
            if(alarmTime && tableName.equals("pendingtasks")){
                int id = cv.getAsInteger("id");
                String name = cv.getAsString("name");
                alarm = new Alarm(this);
                alarm.setAlarm(id, name, timeCalendar);
            }
        } catch (SQLException e){
            Log.e("Exception","SQLException"+String.valueOf(e.getMessage()));
            e.printStackTrace();
        } finally {
            //db.close();
        }

        return mid;

    }

    private int auxGetNextID(){
        SharedPreferences preferences = getSharedPreferences("nextID", Context.MODE_PRIVATE);
        int id = preferences.getInt("id", -1);
        SharedPreferences.Editor editor = preferences.edit();
        editor.putInt("id", id+1);
        editor.commit();
        return id;
    }

    private String auxGetType(int type){
        String aux = "";
        switch (type){
            case 0:
                aux = "work";
                return aux;
            case 1:
                aux = "domestic";
                return aux;
            case 2:
                aux = "study";
                return aux;
            case 3:
                aux = "leisure";
                return aux;
            default:
                return aux;
        }
    }

    private void clearNewTaskScreen(){
        name.setText("");
        description.setText("");

        textSelectedTime.setText(getResources().getString(R.string.never));
        textSelectedDays.setText(getResources().getString(R.string.never));
        dayList.clear();
        timeCalendar = Calendar.getInstance();
        timeCheck = false;
        daysCheck = false;
        if(notifyCheckBox.isChecked()) notifyCheckBox.setChecked(false);
    }

    private void showDayPickerDialog(){
        dayPickerDialog dialog = new dayPickerDialog(this, new dayPickerDialog.OnDaySetListener() {
            @Override
            public void onDaySet(List<String> list, String daysSelection) {
                dayList = list;
                textSelectedDays.setText(daysSelection);
                if(!dayList.isEmpty()){
                    daysCheck = true;
                } else {
                    daysCheck = false;
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

    public void resetTime(View view){
        textSelectedTime.setText(getResources().getString(R.string.never));
        timeCheck = false;
        timeCalendar.clear();
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