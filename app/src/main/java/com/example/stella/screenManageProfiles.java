package com.example.stella;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;

import com.example.stella.reciclerViewsAdapters.listProfilesManagementAdapter;

public class screenManageProfiles extends AppCompatActivity {

    RecyclerView recyclerViewProfiles;
    listProfilesManagementAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.screen_manage_profiles);
        recyclerViewProfiles = findViewById(R.id.recycler_profiles_manage);
        adapter = new listProfilesManagementAdapter(this, this);
        setRecyclerViewProfiles();
    }

    private void setRecyclerViewProfiles(){
        adapter.fillProfiles();
        recyclerViewProfiles.setHasFixedSize(true);
        recyclerViewProfiles.setLayoutManager(new LinearLayoutManager(this));
        recyclerViewProfiles.setNestedScrollingEnabled(false);
        recyclerViewProfiles.setAdapter(adapter);

    }
}