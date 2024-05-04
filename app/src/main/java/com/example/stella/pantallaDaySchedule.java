package com.example.stella;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.widget.TextView;

import com.example.stella.reciclerViewsAdapters.listWeeklyTasksAdapter;
import com.example.stella.utils.loadSettings;

/**
 * Esta clase muestra la pantalla de un dia especigido en "mi semana"
 */

public class pantallaDaySchedule extends AppCompatActivity {

    RecyclerView recyclerView;
    listWeeklyTasksAdapter adapter;
    TextView txt_dayName;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        loadSettings loadSettings = new loadSettings(this);
        loadSettings.loadSettings(this);
        setContentView(R.layout.pantalladiadelasemana);

        recyclerView = findViewById(R.id.recyclerWeeklyTasks);
        txt_dayName = findViewById(R.id.txt_dayName);
        adapter = new listWeeklyTasksAdapter(this, this.getWindow(), auxGetDayIntent());

        setRecyclerViewDaySchedule();


    }

    @Override
    public void onStart() {
        super.onStart();
        setRecyclerViewDaySchedule();
    }
    @Override
    public void onResume() {
        super.onResume();
        setRecyclerViewDaySchedule();
    }

    /**
     * setRecyclerViewDaySchedule prepara el adapter y el recyclerview que muestra las tareas de un dia en especifico
     */

    private void setRecyclerViewDaySchedule(){
        adapter.setSelectedDay(auxGetDayIntent());
        adapter.notifyDataSetChanged();
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setNestedScrollingEnabled(false);
        recyclerView.setAdapter(adapter);
        adapter.reSetItemList();
        adapter.notifyDataSetChanged();
    }

    /**
     * auxGetDayIntent metodo auxiliar para obtener el dia en especifico seleccionado por el usuario
     * @return
     */

    private String auxGetDayIntent(){
        Intent intent = getIntent();
        String day = intent.getStringExtra("day");
        txt_dayName.setText(getResources().getString(R.string.my) + " " + getDayTranslatioinInt(day));

        return day;
    }

    /**
     * getDayTranslatioinInt devuelve la traduccion del dia seleccionado ( ej: Lunes -> Monday)
     * @param day
     * @return
     */

    private String getDayTranslatioinInt(String day){
        String dayTranslation = "";
        switch(day){
            case "monday":
                dayTranslation = getResources().getString(R.string.monday);
                break;
            case "tuesday":
                dayTranslation = getResources().getString(R.string.tuesday);
                break;
            case "wednesday":
                dayTranslation = getResources().getString(R.string.wednesday);
                break;
            case "thursday":
                dayTranslation = getResources().getString(R.string.thursday);
                break;
            case "friday":
                dayTranslation = getResources().getString(R.string.friday);
                break;
            case "saturday":
                dayTranslation = getResources().getString(R.string.saturday);
                break;
            case "sunday":
                dayTranslation = getResources().getString(R.string.sunday);
                break;
            default:
                break;
        }

        return dayTranslation;
    }
}