package com.example.stella;

import static android.content.ContentValues.TAG;

import androidx.appcompat.app.AppCompatActivity;

import android.content.ContentValues;
import android.graphics.Color;
import android.os.Bundle;
import android.text.TextUtils;
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

import com.example.stella.db.dbLogic;
import com.example.stella.dialogs.dayPickerDialog;
import com.example.stella.dialogs.successDialog;
import com.example.stella.dialogs.timePickerDialog;
import com.example.stella.utils.loadSettings;
import com.example.stella.utils.settings;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;

/**
 * Esta clase muestra la pantalla para insertar una tarea nueva
 */

public class screenNewTask extends AppCompatActivity {

    int hora, minuto;
    TextView textSelectedTime, textSelectedDays;
    EditText name, description;
    CheckBox notifyCheckBox;
    LinearLayout layer_horario, layer_dias;
    Spinner spinner;
    List<String> dayList;
    Calendar timeCalendar;
    settings settings;
    dbLogic dbLogic;
    boolean timeCheck, daysCheck; // Con estas booleanas se sabrá si el usuario ha escogido una hora y/o día o no.

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        loadSettings loadSettings = new loadSettings(this);
        loadSettings.loadSettings(this);
        setContentView(R.layout.screen_new_task);

        settings = new settings(this);
        dbLogic = new dbLogic(this);

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

    /**
     * createNewTask es el método que se encarga de crear la tarea e insertarla en la base de datos
     * @param view
     */

    public void createNewTask(View view){

        int notify = 0;
        String nameText;
        String descriptionText = null;

        boolean checkInsert = false;


        if (TextUtils.isEmpty(String.valueOf(name.getText()))){
            Toast.makeText(this, getResources().getString(R.string.nameEmpty), Toast.LENGTH_SHORT).show();
            Log.i(TAG, "No hay nombre");
            return;
        }

        if(notifyCheckBox.isChecked()){
            notify = 1;
        }

        nameText = String.valueOf(name.getText());
        if(!TextUtils.isEmpty(String.valueOf(description))){
            descriptionText = String.valueOf(description.getText());
        }

        String typeName = auxGetType(spinner.getSelectedItemPosition());

        ContentValues task = new ContentValues();
        task.put("id", settings.getNextTaskID());
        task.put("name", nameText);
        task.put("description", descriptionText);
        task.put("type", typeName);
        task.put("notify", notify);
        task.put("profileId", settings.getCurrentProfileID());
        // Si no requiere de notificación, la tarea será de duración infinita y se meterá directa a la tabla PENDINGTASKS
        if(notify == 0){

            checkInsert = dbLogic.insertTask("PENDINGTASKS", task);
            clearNewTaskScreen();
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
                    checkInsert = dbLogic.insertTask("PENDINGTASKS", task);
                    Log.i(TAG, "CheckInsert: " + checkInsert);
                }
                for(String day: dayList){
                    task.put(day, 1);
                }
                checkInsert = dbLogic.insertTask("WEEKLYTASKS", task);
                clearNewTaskScreen();
                Log.i(TAG, "CheckInsert: " + checkInsert);
            } else {
                checkInsert = dbLogic.insertTask("PENDINGTASKS", task);
                clearNewTaskScreen();
                Log.i(TAG, "CheckInsert: " + checkInsert);
            }
        }

        if(checkInsert)showSuccessDialog();

    }

    /**
     * showSuccessDialog muestra un dialog con un mensaje de éxito
     */

    private void showSuccessDialog(){
        successDialog dialog = new successDialog();
        dialog.showDialog(this, 0);
    }

    /**
     * auxCheckCurrentDay es un método auxiliar que detecta que dia es hoy
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
     * auxGetType es un metodo auxiliar que obtiene la traduccion del dia seleccionado en el spinner
     * @param type
     * @return
     */

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

    /**
     * clearNewTaskScreen este metodo se encarga de limpiar todos los campos de la pantalla
     */

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

    /**
     * showDayPickerDialog muestra el dialog "dayPickerDialog" para seleccionar los dias y meter la tarea en weeklytasks
     */

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

    /**
     *  showTimePickerDialog muestra el dialog "timePickerDialog" para escoger la hora de la alarma
     */

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

    /**
     * resetTime resetea la hora
     * @param view
     */

    public void resetTime(View view){
        textSelectedTime.setText(getResources().getString(R.string.never));
        timeCheck = false;
        timeCalendar.clear();
    }

    /**
     * fillTypesSpinner rellena los valores del spinner de los tipos
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