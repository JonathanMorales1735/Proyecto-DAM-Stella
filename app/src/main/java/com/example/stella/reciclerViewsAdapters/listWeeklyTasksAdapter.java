package com.example.stella.reciclerViewsAdapters;

import static android.content.ContentValues.TAG;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.ImageButton;
import android.widget.PopupMenu;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.stella.R;
import com.example.stella.db.DbHelper;
import com.example.stella.pantallaEditarTarea;

import java.util.ArrayList;
import java.util.List;

/**
 * listWeeklyTasksAdapter es el adapter de la recyclerview que muestra las tareas de la tabla "weeklytasks"
 */

public class listWeeklyTasksAdapter extends RecyclerView.Adapter<listWeeklyTasksAdapter.ViewHolder> {
    private List<taskElement> mData = new ArrayList<>();
    private LayoutInflater mInflater;
    private Context context;
    private adaptersLogic adapterLogic;
    public String selectedDay = "";

    public listWeeklyTasksAdapter(Context context, Window w, String selectedDay){
        this.mInflater = LayoutInflater.from(context);
        this.context = context;
        adapterLogic = new adaptersLogic(context);
        this.selectedDay =  selectedDay;
        setItem(adapterLogic.getWeeklytasksList(selectedDay));
        notifyDataSetChanged();
    }

    public void setSelectedDay(String day){
        selectedDay = day;
    }
    public void reSetItemList(){
        setItem(adapterLogic.getWeeklytasksList(selectedDay));
    }

    @NonNull
    @Override
    public listWeeklyTasksAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = mInflater.inflate(R.layout.cardview_task_layout2, parent, false);
        return new listWeeklyTasksAdapter.ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull listWeeklyTasksAdapter.ViewHolder holder, int position) {
        holder.bindData(mData.get(position));
    }

    public void setItem(List<taskElement> items){ mData = items;}

    @Override
    public int getItemCount() {
        return mData.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder{

        TextView name;
        ImageButton optionsButton;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            name = itemView.findViewById(R.id.cardText);
            optionsButton = itemView.findViewById(R.id.cardOptionButton);
        }

        void bindData(final taskElement item){
            name.setText(item.getName());

            optionsButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    PopupMenu popupMenu = new PopupMenu(context, optionsButton);
                    popupMenu.inflate(R.menu.menu_optionstask);
                    popupMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                        @Override
                        public boolean onMenuItemClick(MenuItem menuItem) {
                            switch (menuItem.getItemId()){
                                case R.id.optionEdit:
                                    // Logica del boton editar
                                    // Recoge los datos de la tarea y la traspasa a la pantalla editar tarea
                                    Intent intent = new Intent(context, pantallaEditarTarea.class);
                                    taskElement auxItem= adapterLogic.getTaskFullInfo(item.getId(), "weeklytasks");
                                    intent.putExtra("id", auxItem.getId());
                                    intent.putExtra("name", auxItem.getName());
                                    intent.putExtra("description", auxItem.getDescription());
                                    intent.putExtra("type", auxItem.getType());
                                    intent.putExtra("notify", auxItem.isNotify());
                                    intent.putExtra("time", auxItem.getTime());
                                    intent.putExtra("table", "weeklytasks");
                                    intent.putExtra("profileId", auxItem.getProfileId());
                                    context.startActivity(intent);
                                    break;
                                case R.id.optionDelete:
                                    // Logica del boton eliminar
                                    // Elimina la tarea
                                    adapterLogic.deleteTaskInWeeklyTasks(item.getId());
                                    reSetItemList();
                                    notifyDataSetChanged();
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
            // Muestra un dialog con la informacion de la tarea
            name.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    adapterLogic.showTaskInfo(item.getId(), "weeklytasks");
                }
            });
        }
    }






}
