package com.example.stella;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.DialogFragment;

import android.app.DatePickerDialog;
import android.app.Dialog;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.ImageButton;
import android.widget.PopupMenu;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;

import com.example.stella.db.DbHelper;
import com.example.stella.dialogs.MonthYearPickerDialog;
import com.example.stella.dialogs.YearPickerDialog;
import com.example.stella.utils.MyValueFormatter;
import com.example.stella.utils.loadSettings;
import com.example.stella.utils.settings;
import com.github.mikephil.charting.charts.PieChart;
import com.github.mikephil.charting.data.PieData;
import com.github.mikephil.charting.data.PieDataSet;
import com.github.mikephil.charting.data.PieEntry;
import com.github.mikephil.charting.utils.ColorTemplate;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Calendar;

/**
 * Clase que muestra la pantalla de los registros anteriores
 */

public class screenPreviousRecords extends AppCompatActivity {

    PieChart summary;
    ArrayList<PieEntry> enters;
    PieDataSet pieDataSet;
    PieData pieData;
    settings settings;

    static TextView txt_date;
    Button btn_applyFilter;
    ImageButton btn_filter;
    int filter = 0; // [0] Filtro por Fecha completa. [1] Filtro por Mes. [2] Filtro por Año.
    static String fullDate = "";
    String month = "";
    String year = "";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        loadSettings loadSettings = new loadSettings(this);
        loadSettings.loadSettings(this);
        setContentView(R.layout.screen_previous_records);
        settings = new settings(this);
        txt_date = (TextView) findViewById(R.id.txt_date);
        btn_filter = findViewById(R.id.btn_filter);
        summary = findViewById(R.id.summaryItem);

        //fullDateDialog();
        enters = new ArrayList<>();
        pieDataSet = new PieDataSet(enters, "");
        pieDataSet.setColors(ColorTemplate.MATERIAL_COLORS);
        pieDataSet.setValueTextSize(20);
        pieData = new PieData(pieDataSet);
        pieData.setValueFormatter(new MyValueFormatter(new DecimalFormat("####.##"), summary));
        summary.setUsePercentValues(true);
        summary.setData(pieData);
        summary.getLegend().setTextSize(20);
        summary.getDescription().setEnabled(false);
        summary.animateY(1000);
        summary.refreshDrawableState();

        summary.setEntryLabelTextSize(20f);
        filter = 0;
        setGraph(summary);



        btn_applyFilter = findViewById(R.id.btn_applyFilter);

    }

    /**
     * chooseDate metodo para escoger la fecha del registro anterior a ver
     * @param view
     */

    public void chooseDate(View view) {
        switch (filter){
            case 0:
                showFullDateDialog();
                break;
            case 1:
                showMonthYearPickerDialog();
                break;
            case 2:
                showYearPickerDialog();
                break;
            default:
                showFullDateDialog();
                break;
        }
    }

    /**
     * showFilterOption metodo para abrir el menu de seleccion de diferentes filtros
     * @param view
     */

    public void showFilterOption (View view){
        PopupMenu popupMenu = new PopupMenu(this, btn_filter);
        popupMenu.inflate(R.menu.menu_filter);
        popupMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem menuItem) {
                switch (menuItem.getItemId()){
                    case R.id.optionFilter:
                        showFilterDialog();
                        break;
                    default:
                        break;

                }
                return false;
            }
        });
        popupMenu.show();
    }

    /**
     * showYearPickerDialog muestra un dialog de "yearPickerDialog" para escoger un año
     */

    private void showYearPickerDialog(){
        YearPickerDialog dialog = new YearPickerDialog(this, new YearPickerDialog.OnYearSelectedListener() {
            @Override
            public void onYearSelected(int selectedYear) {
                // Aquí puedes hacer lo que quieras con el año seleccionado
                // Por ejemplo, mostrarlo en un TextView

                txt_date.setText(String.valueOf(selectedYear));
                year = String.valueOf(selectedYear);
                year =  String.format("%02d", selectedYear);
            }
        });
        dialog.show();
    }

    /**
     * showMonthYearPickerDialog muestra un dialog de "monthYearPickerDialog" para seleccionar un mes y un año
     */

    private void showMonthYearPickerDialog() {
        MonthYearPickerDialog dialog = new MonthYearPickerDialog(this, new MonthYearPickerDialog.OnMonthYearSetListener() {
            @Override
            public void onMonthYearSet(int selectedMonth, int selectedYear) {
                // Aquí puedes hacer lo que quieras con el mes y el año seleccionados
                // Por ejemplo, mostrarlos en un TextView

                String monthYearText = String.format("%02d/%02d", selectedMonth + 1, selectedYear);
                txt_date.setText(monthYearText);
                year = String.valueOf(selectedYear);
                year =  String.format("%02d", selectedYear);
                month = String.valueOf(selectedMonth);
                month =  String.format("%02d", selectedMonth + 1);
            }
        });
        dialog.show();
    }

    /**
     * showFullDateDialog muestra un dialog "DatePickerFragment" para escoger una fecha completa
     */

    private void showFullDateDialog(){
        DatePickerFragment newFragment = new DatePickerFragment();
        newFragment.show(getSupportFragmentManager(), "datePicker");
    }

    /**
     * setGraph establece la grafica con porcentajes de los tipos de tareas en la fecha seleccionada
     * @param view
     */

    public void setGraph(View view){
        DbHelper dbH = new DbHelper(this);
        SQLiteDatabase db = dbH.getReadableDatabase();
        int currentProfileID = settings.getCurrentProfileID();

        int work = 0;
        int domestic = 0;
        int study = 0;
        int leisure = 0;
        //String[] types ={"work", "domestic", "study", "leisure"};
        ArrayList<String> types = new ArrayList<>();
        types.add("work");
        types.add("domestic");
        types.add("study");
        types.add("leisure");

        Log.i("Set Graph", "Hago el clear");
        enters.clear();
        Log.i("Set Graph", "Clear hecho");


        Cursor cursor;

        Log.i("Set Graph", "Colocando query");

        // Recogemos de la base de datos, en la tabla PREVIOUSRECORDS, cuantas de las tareas tienen como "type"; work, study, domestic o leisure.

        for(String name: types){
            Log.i("Set Graph: ", name);
            String query = "";
            switch (filter){
                case 0:
                    query = "Select sum(" + name + ") as total from PREVIOUSRECORDS where date = '" + fullDate + "' and profileId = " + currentProfileID;
                    break;
                case 1:
                    query = "Select sum(" + name + ") as total from PREVIOUSRECORDS where date BETWEEN '" + year + "-" + month + "-01' and '" +  year + "-" + month + "-31' AND profileId = " + currentProfileID ;
                    break;
                case 2:
                    query = "Select sum(" + name + ") as total from PREVIOUSRECORDS where date BETWEEN '" + year + "-01-01' and '" +  year + "-12-31' AND profileId = " + currentProfileID ;
                    break;
                default:
                    break;
            }
            Log.i("Set Graph", "Buscando...");

            cursor = db.rawQuery(query, null);

            if(name.equals("work") && cursor.moveToFirst()){
                work = cursor.getInt(cursor.getColumnIndexOrThrow("total"));
                Log.i("Set Graph: ", "Tareas con work: " + work);
            } else if (name.equals("domestic") && cursor.moveToFirst()) {
                domestic = cursor.getInt(cursor.getColumnIndexOrThrow("total"));
                Log.i("Set Graph: ", "Tareas con domestic: " + domestic);
            } else if (name.equals("study") && cursor.moveToFirst()){
                study = cursor.getInt(cursor.getColumnIndexOrThrow("total"));
                Log.i("Set Graph: ", "Tareas con study: " + study);
            } else if (name.equals("leisure") && cursor.moveToFirst()){
                leisure = cursor.getInt(cursor.getColumnIndexOrThrow("total"));
                cursor.close();
                Log.i("Set Graph: ", "Tareas con leisure: " + leisure);
            }

        }

        // Una vez recogidos los datos, se introducen en una lista si tienen un valor mayor que 0

        //ArrayList<PieEntry> enters = new ArrayList<>();


        if(work > 0){
            String aux = this.getResources().getString(R.string.work);
            enters.add(new PieEntry(work, aux));
            Log.i("Set Graph: ", "Intoducido en la list del piechart: " + work + ", " + aux);
        }
        if(domestic > 0){
            String aux = this.getResources().getString(R.string.domestic);
            enters.add(new PieEntry(domestic, aux));
            Log.i("Set Graph: ", "Intoducido en la list del piechart: " + domestic + ", " + aux);
        }
        if(study > 0){
            String aux = this.getResources().getString(R.string.study);
            enters.add(new PieEntry(study, aux));
            Log.i("Set Graph: ", "Intoducido en la list del piechart: " + study + ", " + aux);
        }
        if(leisure > 0){
            String aux = this.getResources().getString(R.string.leisure);
            enters.add(new PieEntry(leisure, aux));
            Log.i("Set Graph: ", "Intoducido en la list del piechart: " + leisure + ", " + aux);
        }

        // Intoducimos la lista en un objeto PieDataSet, PieData y se configura el objeto PieChart (summary) para mostrar todos los datos en una gráfica.

        /**PieDataSet pieDataSet = new PieDataSet(enters, "");
        pieDataSet.setColors(ColorTemplate.MATERIAL_COLORS);
        pieDataSet.setValueTextSize(20);

        PieData pieData = new PieData(pieDataSet);
        pieData.setValueFormatter(new MyValueFormatter(new DecimalFormat("####.##"), summary));
        summary.setUsePercentValues(true);
        summary.setData(pieData);
        summary.getLegend().setTextSize(20);
        summary.getDescription().setEnabled(false);
        summary.animateY(1000);

        summary.setEntryLabelTextSize(20f);*/
        pieDataSet.notifyDataSetChanged();
        pieData.notifyDataChanged();
        summary.notifyDataSetChanged();
        summary.invalidate();



        try{
            db.close();
        } catch (SQLException e){
            e.printStackTrace();
        }
    }

    /**
     * showFilterDialog muestra un dialog con los filtros
     */

    private void showFilterDialog(){
        Dialog dialog = new Dialog(this);
        dialog.setContentView(R.layout.dialog_choose_filter);
        dialog.getWindow().setLayout(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        dialog.setCancelable(false);

        Button btn_setFilter = dialog.findViewById(R.id.btn_setFilter);
        ImageButton btn_cancel = dialog.findViewById(R.id.btn_closeChooseFilter);
        RadioGroup radioGroup = dialog.findViewById(R.id.radioGroupFilters);
        RadioButton rBFullDate= dialog.findViewById(R.id.radioButtonFullDate);
        RadioButton rBMonth = dialog.findViewById(R.id.radioButtonMonth);
        RadioButton rBYear = dialog.findViewById(R.id.radioButtonYear);

        btn_setFilter.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(rBFullDate.isChecked()){
                    filter = 0;
                }
                if(rBMonth.isChecked()){
                    filter = 1;
                }
                if(rBYear.isChecked()){
                    filter = 2;
                }

                dialog.hide();
                dialog.dismiss();
            }
        });

        btn_cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog.hide();
                dialog.dismiss();
            }
        });

        switch (filter){
            case 0:
                rBFullDate.setChecked(true);
                break;
            case 1:
                rBMonth.setChecked(true);
                break;
            case 2:
                rBYear.setChecked(true);
                break;
            default:
                break;
        }

        dialog.show();




    }



    public static class DatePickerFragment extends DialogFragment
            implements DatePickerDialog.OnDateSetListener {

        @Override
        public Dialog onCreateDialog(Bundle savedInstanceState) {

            final Calendar c = Calendar.getInstance();
            int year = c.get(Calendar.YEAR);
            int month = c.get(Calendar.MONTH);
            int day = c.get(Calendar.DAY_OF_MONTH);


            return new DatePickerDialog(getActivity(), this, year, month, day);
        }

        public void onDateSet(DatePicker view, int year, int month, int day) {
            txt_date.setText(String.format("%02d", day) + "/" + String.format("%02d", (month + 1)) + "/" + year);
            fullDate = year + "-" +String.format("%02d", (month + 1)) + "-" +  String.format("%02d", day);
        }

    }


}