package com.example.stella;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.util.Log;
import android.view.View;

import com.example.stella.db.DbHelper;
import com.example.stella.utils.MyValueFormatter;
import com.example.stella.utils.loadSettings;
import com.github.mikephil.charting.charts.PieChart;
import com.github.mikephil.charting.data.PieData;
import com.github.mikephil.charting.data.PieDataSet;
import com.github.mikephil.charting.data.PieEntry;
import com.github.mikephil.charting.utils.ColorTemplate;

import java.text.DecimalFormat;
import java.util.ArrayList;

public class pantallaRegistros extends AppCompatActivity {

    PieChart summary;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        loadSettings loadSettings = new loadSettings(this);
        loadSettings.loadSettings(this);
        setContentView(R.layout.pantallaregistros);

        summary = findViewById(R.id.summaryItem);



        setGraph();

    }

    private void setGraph(){
        DbHelper dbH = new DbHelper(this);
        SQLiteDatabase db = dbH.getReadableDatabase();

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


        Cursor cursor;

        // Recogemos de la base de datos, en la tabla COMPLETEDTASKS, cuantas de las tareas tienen como "type"; work, study, domestic o leisure.

        for(String name: types){
            Log.i("Set Graph: ", name);
            String query = "Select * from completedtasks where type = '" + name + "'";
            cursor = db.rawQuery(query, null);

            if(name.equals("work")){
                work = cursor.getCount();
                Log.i("Set Graph: ", "Tareas con work: " + work);
            } else if (name.equals("domestic")) {
                domestic = cursor.getCount();
                Log.i("Set Graph: ", "Tareas con domestic: " + domestic);
            } else if (name.equals("study")){
                study = cursor.getCount();
                Log.i("Set Graph: ", "Tareas con study: " + study);
            } else if (name.equals("leisure")){
                leisure = cursor.getCount();
                cursor.close();
                Log.i("Set Graph: ", "Tareas con leisure: " + leisure);
            }

        }

        // Una vez recogidos los datos, se introducen en una lista si tienen un valor mayor que 0

        ArrayList<PieEntry> enters = new ArrayList<>();

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

        PieDataSet pieDataSet = new PieDataSet(enters, "");
        pieDataSet.setColors(ColorTemplate.MATERIAL_COLORS);
        pieDataSet.setValueTextSize(20);

        PieData pieData = new PieData(pieDataSet);
        pieData.setValueFormatter(new MyValueFormatter(new DecimalFormat("####.##"), summary));
        summary.setUsePercentValues(true);
        summary.setData(pieData);
        summary.getLegend().setTextSize(20);
        summary.getDescription().setEnabled(false);
        summary.animateY(1000);

        summary.setEntryLabelTextSize(20f);
        summary.invalidate();

        try{
            db.close();
        } catch (SQLException e){
            e.printStackTrace();
        }
    }



    public void showPreviousRecordsScreen(View view){
        Intent intent = new Intent(this, pantallaRegistrosAnteriores.class);
        startActivity(intent);
    }


}