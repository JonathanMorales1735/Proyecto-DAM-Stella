package com.example.stella.dialogs;

import static android.content.ContentValues.TAG;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.ImageButton;

import androidx.annotation.NonNull;

import com.example.stella.R;

import java.util.ArrayList;
import java.util.List;

public class dayPickerDialog extends Dialog {

    Context c;
    List<String> dayList;
    boolean timeCheck, daysCheck;
    String daysSelection = "";
    OnDaySetListener listener;
    boolean isShowing;

    public dayPickerDialog(@NonNull Context context, OnDaySetListener listener) {
        super(context);
        this.c = context;
        this.listener = listener;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.dialog_daypicker);
        this.getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        this.setCancelable(false);

        CheckBox chkMonday, chkTuesday, chkWednesday, chkThursday, chkFriday, chkSaturday, chkSunday;
        Button btnApply;
        ImageButton btnClose;


        chkMonday = (CheckBox) findViewById(R.id.checkBoxMonday);
        chkTuesday = (CheckBox) findViewById(R.id.checkBoxTuesday);
        chkWednesday = (CheckBox) findViewById(R.id.checkBoxWednesday);
        chkThursday = (CheckBox) findViewById(R.id.checkBoxThursday);
        chkFriday = (CheckBox) findViewById(R.id.checkBoxFriday);
        chkSaturday = (CheckBox) findViewById(R.id.checkBoxSaturday);
        chkSunday = (CheckBox) findViewById(R.id.checkBoxSunday);
        btnApply = (Button) findViewById(R.id.applyBtnDayPicker);
        btnClose = (ImageButton) findViewById(R.id.closeBtnDayPicker);

        btnApply.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dayList = new ArrayList<>();
                dayList.clear();
                List<String> auxList = new ArrayList<>();
                if(chkMonday.isChecked()){
                    auxList.add(c.getResources().getString(R.string.monday));
                    dayList.add("monday");
                    Log.i(TAG, "Día añadido a la lista: " + c.getResources().getString(R.string.monday));
                }
                if(chkTuesday.isChecked()){
                    auxList.add(c.getResources().getString(R.string.tuesday));
                    dayList.add("tuesday");
                    Log.i(TAG, "Día añadido a la lista: " + c.getResources().getString(R.string.tuesday));
                }
                if(chkWednesday.isChecked()){
                    auxList.add(c.getResources().getString(R.string.wednesday));
                    dayList.add("wednesday");
                    Log.i(TAG, "Día añadido a la lista: " + c.getResources().getString(R.string.wednesday));
                }
                if(chkThursday.isChecked()){
                    auxList.add(c.getResources().getString(R.string.thursday));
                    dayList.add("thursday");
                    Log.i(TAG, "Día añadido a la lista: " + c.getResources().getString(R.string.thursday));
                }
                if(chkFriday.isChecked()){
                    auxList.add(c.getResources().getString(R.string.friday));
                    dayList.add("friday");
                    Log.i(TAG, "Día añadido a la lista: " + c.getResources().getString(R.string.friday));
                }
                if(chkSaturday.isChecked()){
                    auxList.add(c.getResources().getString(R.string.saturday));
                    dayList.add("saturday");
                    Log.i(TAG, "Día añadido a la lista: " + c.getResources().getString(R.string.saturday));
                }
                if(chkSunday.isChecked()){
                    auxList.add(c.getResources().getString(R.string.sunday));
                    dayList.add("sunday");
                    Log.i(TAG, "Día añadido a la lista: " + c.getResources().getString(R.string.sunday));
                }

                if(!dayList.isEmpty()){
                    daysCheck = true;
                } else {
                    daysCheck = false;
                }
                auxSetDaysText(auxList);
                listener.onDaySet(dayList, daysSelection);
                hide();
                dismiss();
            }

        });

        btnClose.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                hide();
                dismiss();
            }
        });


    }

    @Override
    public void show() {
        if (!isShowing) {
            super.show();
            isShowing = true;
        }
    }

    @Override
    public void dismiss() {
        super.dismiss();
        isShowing = false;
    }

    public interface OnDaySetListener {
        void onDaySet(List<String> dayList, String daysSelection);
    }

    private List<String> getDayList(){
        return dayList;
    }

    private String getDaysSelection(){
        return daysSelection;
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

            daysSelection = text;
        } else {
            daysSelection = c.getResources().getString(R.string.never);
        }

    }




}
