package com.example.stella.dialogs;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.NumberPicker;

import androidx.annotation.NonNull;

import com.example.stella.R;

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
        monthPicker.setDisplayedValues(getMonthsArray());
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

    private String[] getMonthsArray(){
        String[] months = new String[12];
        months[0] = getContext().getResources().getString(R.string.january);
        months[1] = getContext().getResources().getString(R.string.february);
        months[2] = getContext().getResources().getString(R.string.march);
        months[3] = getContext().getResources().getString(R.string.april);
        months[4] = getContext().getResources().getString(R.string.may);
        months[5] = getContext().getResources().getString(R.string.june);
        months[6] = getContext().getResources().getString(R.string.july);
        months[7] = getContext().getResources().getString(R.string.august);
        months[8] = getContext().getResources().getString(R.string.september);
        months[9] = getContext().getResources().getString(R.string.october);
        months[10] = getContext().getResources().getString(R.string.november);
        months[11] = getContext().getResources().getString(R.string.december);

        return months;
    }
}
