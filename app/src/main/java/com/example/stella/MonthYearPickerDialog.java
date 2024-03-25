package com.example.stella;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.NumberPicker;

import androidx.annotation.NonNull;

public class MonthYearPickerDialog extends Dialog {

    private OnMonthYearSetListener listener;
    private NumberPicker monthPicker;
    private NumberPicker yearPicker;

    public MonthYearPickerDialog(@NonNull Context context, OnMonthYearSetListener listener) {
        super(context);
        this.listener = listener;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.dialog_month_year_picker);

        monthPicker = findViewById(R.id.month_picker);
        yearPicker = findViewById(R.id.year_picker);
        Button selectButton = findViewById(R.id.select_button);

        // Configurar el NumberPicker del mes
        monthPicker.setMinValue(0);
        monthPicker.setMaxValue(11);
        monthPicker.setDisplayedValues(new String[]{"January", "February", "March", "April", "May", "June", "July", "August", "September", "October", "November", "December"});
        monthPicker.setDescendantFocusability(NumberPicker.FOCUS_BLOCK_DESCENDANTS);

        // Configurar el NumberPicker del año
        int currentYear = java.util.Calendar.getInstance().get(java.util.Calendar.YEAR);
        yearPicker.setMinValue(currentYear - 10);
        yearPicker.setMaxValue(currentYear + 10);
        yearPicker.setValue(currentYear);
        yearPicker.setDescendantFocusability(NumberPicker.FOCUS_BLOCK_DESCENDANTS);

        selectButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int selectedMonth = monthPicker.getValue();
                int selectedYear = yearPicker.getValue();
                listener.onMonthYearSet(selectedMonth, selectedYear);
                dismiss();
            }
        });
    }

    public interface OnMonthYearSetListener {
        void onMonthYearSet(int selectedMonth, int selectedYear);
    }
}
