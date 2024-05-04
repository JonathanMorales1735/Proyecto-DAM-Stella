package com.example.stella.dialogs;

import android.app.TimePickerDialog;
import android.content.Context;
import android.widget.TimePicker;

/**
 * timePickerDialog muestra un dialog para seleccionar dia, mes y año. Se utiliza en registros anteriores para buscar registros
 */

public class timePickerDialog extends TimePickerDialog {
    public timePickerDialog(Context context, int hourOfDay, int minute, boolean is24HourView, OnTimeSetListener listener) {
        super(context, listener, hourOfDay, minute, is24HourView);
        this.setTitle("Seleccionar tiempo");
    }

    public interface OnTimeSetListener extends TimePickerDialog.OnTimeSetListener {

        @Override
        void onTimeSet(TimePicker timePicker, int selectedHour, int selectedMinute);

    }
}
