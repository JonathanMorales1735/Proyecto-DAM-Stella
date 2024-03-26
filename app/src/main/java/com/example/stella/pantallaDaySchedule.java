package com.example.stella;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;

import com.example.stella.reciclerViewsAdapters.listWeeklyTasksAdapter;

public class pantallaDaySchedule extends AppCompatActivity {

    RecyclerView recyclerView;
    listWeeklyTasksAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.pantalladiadelasemana);

        recyclerView = findViewById(R.id.recyclerWeeklyTasks);
        adapter = new listWeeklyTasksAdapter(this, this.getWindow());

        setRecyclerViewCompleted();


    }

    private void setRecyclerViewCompleted(){
        adapter.fillWeeklyTasks(auxGetDayIntent());
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setNestedScrollingEnabled(false);
        recyclerView.setAdapter(adapter);

    }

    private String auxGetDayIntent(){
        Intent intent = getIntent();
        String day = intent.getStringExtra("day");

        return day;
    }
}