package com.example.stella.dialogs;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.NumberPicker;

import androidx.annotation.NonNull;

import com.example.stella.R;

import java.util.Calendar;

public class YearPickerDialog extends Dialog {

    private OnYearSelectedListener listener;

    public YearPickerDialog(@NonNull Context context, OnYearSelectedListener listener) {
        super(context);
        this.listener = listener;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.dialog_year_picker);

        final NumberPicker yearPicker = findViewById(R.id.year_picker);
        Button selectButton = findViewById(R.id.select_button);

        // Configurar el NumberPicker para los años
        int currentYear = Calendar.getInstance().get(Calendar.YEAR);
        yearPicker.setMinValue(currentYear - 10); // Rango de 100 años hacia atrás
        yearPicker.setMaxValue(currentYear + 10); // Rango de 100 años hacia adelante
        yearPicker.setValue(currentYear);
        yearPicker.setDescendantFocusability(NumberPicker.FOCUS_BLOCK_DESCENDANTS);

        selectButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int selectedYear = yearPicker.getValue();
                listener.onYearSelected(selectedYear);
                dismiss();
            }
        });
    }

    public interface OnYearSelectedListener {
        void onYearSelected(int year);
    }
}
