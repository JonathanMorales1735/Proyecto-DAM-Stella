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

public class listWeeklyTasksAdapter extends RecyclerView.Adapter<listWeeklyTasksAdapter.ViewHolder> {
    private List<taskElement> mData = new ArrayList<>();
    private LayoutInflater mInflater;
    private Context context;
    private Dialog dialogInfo = null;
    private adaptersLogic adapterLogic;

    public listWeeklyTasksAdapter(Context context, Window w){
        this.mInflater = LayoutInflater.from(context);
        this.context = context;
        adapterLogic = new adaptersLogic(context);
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
                                    Intent intent = new Intent(context, pantallaEditarTarea.class);
                                    taskElement auxItem= adapterLogic.getTaskFullInfo(item.getId(), "weeklytasks");
                                    intent.putExtra("id", auxItem.getId());
                                    intent.putExtra("name", auxItem.getName());
                                    intent.putExtra("description", auxItem.getDescription());
                                    intent.putExtra("type", auxItem.getType());
                                    intent.putExtra("notify", auxItem.isNotify());
                                    intent.putExtra("time", auxItem.getTime());
                                    intent.putExtra("table", "weeklytasks");
                                    context.startActivity(intent);
                                    break;
                                case R.id.optionDelete:
                                    adapterLogic.deleteTaskInWeeklyTasks(item.getId());
                                    //fillPendingTasks();
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

            name.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    adapterLogic.showTaskInfo(item.getId(), "weeklytasks");
                }
            });
        }
    }

    public void fillWeeklyTasks(String day){
        DbHelper dbHelper = new DbHelper(context);
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        Log.i(TAG, "Obteniendo tareas de " + day);
        Cursor cursor = db.rawQuery("Select id, name from WEEKLYTASKS where " + day + " = 1", null);
        taskElement task;

        while (cursor.moveToNext()){
            task = new taskElement();
            task.setId(cursor.getInt(0));
            task.setName(cursor.getString(1));
            Log.i(TAG, "Tarea recogida de weeklytasks: " + task.getId() + " " + task.getName());
            mData.add(task);
        }
        try{
            db.close();
            cursor.close();
        } catch (SQLException e){
            e.printStackTrace();
        }
        notifyDataSetChanged();
    }




}
