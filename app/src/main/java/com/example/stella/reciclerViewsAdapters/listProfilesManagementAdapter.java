package com.example.stella.reciclerViewsAdapters;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.PopupMenu;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.stella.R;
import com.example.stella.db.dbLogic;
import com.example.stella.dialogs.newProfileNameDialog;

import java.util.ArrayList;
import java.util.List;

/**
 * listProfilesManagementAdapter es el adaptador de la recyclerview de perfiles que se encuentra en la pantalla de administracion de eprfiles
 */

public class listProfilesManagementAdapter extends RecyclerView.Adapter<listProfilesManagementAdapter.ViewHolder>{

    List<profiles> profilesList = new ArrayList<>();
    private LayoutInflater mInflater;
    private Context context;
    private Activity activity;
    dbLogic dbLogic;

    public listProfilesManagementAdapter(Context context, Activity activity) {
        this.mInflater = LayoutInflater.from(context);
        this.context = context;
        this.activity = activity;
        dbLogic = new dbLogic(context);
        fillProfiles();
    }

    @NonNull
    @Override
    public listProfilesManagementAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = mInflater.inflate(R.layout.cardview_profile_manage, parent, false);
        return new listProfilesManagementAdapter.ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull listProfilesManagementAdapter.ViewHolder holder, int position) {
        holder.bindData(profilesList.get(position));
    }

    @Override
    public int getItemCount() {
        return profilesList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        //LinearLayout layout_profile_chooser;
        TextView txt_profileName;
        ImageButton btn_options;

        ViewHolder(View itemView) {
            super(itemView);
            //layout_profile_chooser = itemView.findViewById(R.id.layout_profile_chooser);
            txt_profileName = itemView.findViewById(R.id.txt_profileName);
            btn_options = itemView.findViewById(R.id.btn_options);
        }

        void bindData(final profiles profile) {

            txt_profileName.setText(profile.getName());

            btn_options.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    PopupMenu popupMenu = new PopupMenu(context, btn_options);
                    popupMenu.inflate(R.menu.menu_optionstask);
                    popupMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                        @Override
                        public boolean onMenuItemClick(MenuItem menuItem) {
                            switch (menuItem.getItemId()){
                                case R.id.optionEdit:
                                    showNewProfileNameDialog(profile.getId(), profile.getName());
                                    break;
                                case R.id.optionDelete:
                                    DialogInterface.OnClickListener dialogClickListener = new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface dialog, int which) {
                                            switch (which){
                                                case DialogInterface.BUTTON_POSITIVE:
                                                    dbLogic.deleteProfile(profile.getId());
                                                    fillProfiles();
                                                    break;

                                                case DialogInterface.BUTTON_NEGATIVE:
                                                    break;
                                            }
                                        }
                                    };

                                    AlertDialog.Builder builder = new AlertDialog.Builder(context);
                                    builder.setMessage(context.getResources().getString(R.string.delete_profile_warning)).setPositiveButton(context.getResources().getString(R.string.yes), dialogClickListener)
                                            .setNegativeButton("No", dialogClickListener).show();
                                    break;
                                default:
                                    break;

                            }
                            return false;
                        }
                    });
                    popupMenu.show();
                }
            });
        }
    }

    /**
     * fillProfiles rellena la lista del adapter con los perfiles que hay en la tabla "profiles"
     */

    public void fillProfiles(){
        profilesList = dbLogic.getProfiles();
        notifyDataSetChanged();
    }

    /**
     * showNewProfileNameDialog muestra el dialog "newProfileNameDialog" para poder cambiar el nombre del perfil
     * @param id
     * @param originalName
     */

    private void showNewProfileNameDialog(int id, String originalName){
        newProfileNameDialog.show(context, originalName,  new newProfileNameDialog.OnDialogClickListener() {
            @Override
            public void onAccept(String text) {
                dbLogic.updateProfile(text, id);
                fillProfiles();
                //activity.recreate();
            }

            @Override
            public void onCancel() {
                // Nada
            }
        });
    }


}
