package com.example.stella.recyclerViewsAdapters;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.stella.R;
import com.example.stella.db.dbLogic;
import com.example.stella.screenTasks;
import com.example.stella.utils.settings;

import java.util.ArrayList;
import java.util.List;

/**
 * listProfilesAdapter es el adapter del recyclerview que muesrta los perfiles, se encuentra en la pantalla principal
 */

public class listProfilesAdapter extends RecyclerView.Adapter<listProfilesAdapter.ViewHolder>{

    List<profiles> profilesList = new ArrayList<>();
    private LayoutInflater mInflater;
    private Context context;
    private Activity activity;
    settings settings;

    public listProfilesAdapter(Context context, Activity activity) {
        this.mInflater = LayoutInflater.from(context);
        this.context = context;
        this.activity = activity;
        this.settings = new settings(context);
        fillProfiles();
    }

    @NonNull
    @Override
    public listProfilesAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = mInflater.inflate(R.layout.cardview_profile_chooser, parent, false);
        return new listProfilesAdapter.ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull listProfilesAdapter.ViewHolder holder, int position) {
        holder.bindData(profilesList.get(position));
    }

    @Override
    public int getItemCount() {
        return profilesList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        LinearLayout layout_profile_chooser;
        TextView txt_profileName;

        ViewHolder(View itemView) {
            super(itemView);
            layout_profile_chooser = itemView.findViewById(R.id.layout_profile_chooser);
            txt_profileName = itemView.findViewById(R.id.txt_profileName);
        }

        void bindData(final profiles profile) {

            txt_profileName.setText(profile.getName());

            layout_profile_chooser.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    setChosenProfile(profile.getId(), profile.getName());
                    Intent intent = new Intent(context, screenTasks.class);
                    context.startActivity(intent);
                    setUserActive();
                    activity.finish();
                }
            });
        }
    }

    /**
     * fillProfiles rellena la lista del adapter con los perfiles existentes de la tabla "profiles"
     */

    public void fillProfiles(){
        dbLogic dbLogic = new dbLogic(context);
        profilesList = dbLogic.getProfiles();
        notifyDataSetChanged();
    }

    /**
     * setChosenProfile guarda el id y el nombre del perfil que ha escogido usar el usuario
     * @param id
     * @param name
     */

    private void setChosenProfile(int id, String name){
        settings.setProfileID(id);
        settings.setProfileName(name);
    }

    /**
     * setUserActive establece que el perfil seleccionado se encuentra en estado activo. Cuando el usuario regrese a la app le entrara directamente al perfil mientras siga en activo
     */

    private void setUserActive(){
        settings.setUserActivity(true);
    }


}
