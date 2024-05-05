package com.example.stella.recyclerViewsAdapters;

import static android.content.ContentValues.TAG;

import android.app.Dialog;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.ImageButton;
import android.widget.PopupMenu;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.example.stella.R;
import com.example.stella.db.DbHelper;
import com.example.stella.screenEditTask;

import java.util.ArrayList;
import java.util.List;

/**
 * listCOmpletedTasksAdapter es el adapter utilizado en el recyclerview que muestra las tareas completadas de la pantalla tareas
 */

public class listCompletedTasksAdapter extends RecyclerView.Adapter<listCompletedTasksAdapter.ViewHolder> {
    private List<taskElement> mData = new ArrayList<>();
    private LayoutInflater mInflater;
    private Context context;
    private Dialog dialogInfo = null;
    listPendingTasksAdapter pendingTasksAdapter;
    adaptersLogic adapterLogic;

    public listCompletedTasksAdapter(Context context){
        this.mInflater = LayoutInflater.from(context);
        this.context = context;
        adapterLogic = new adaptersLogic(context);
        setItem(adapterLogic.getCompletedtasksList());
        notifyDataSetChanged();
    }

    /**
     * reSetItemList vuelve a insertar las tareas que se encuentran en la tabla completedtasks en el adapter
     */

    public void reSetItemList(){
        setItem(adapterLogic.getCompletedtasksList());
    }

    @Override
    public int getItemCount() {return mData.size();}

    @Override
    public listCompletedTasksAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = mInflater.inflate(R.layout.cardview_task_layout, parent, false);
        return new listCompletedTasksAdapter.ViewHolder(view);
    }

    @Override
    public void  onBindViewHolder(final listCompletedTasksAdapter.ViewHolder holder, final  int position){
        holder.bindData(mData.get(position));
    }

    public void setItem(List<taskElement> items){ mData = items;}

    public class ViewHolder extends RecyclerView.ViewHolder {

        TextView name;
        CheckBox radioButton;
        ImageButton optionsButton;

        ViewHolder(View itemView){
            super(itemView);
            name = itemView.findViewById(R.id.cardText);
            radioButton = itemView.findViewById(R.id.cardRadioButton);
            optionsButton = itemView.findViewById(R.id.cardOptionButton);


        }

        void bindData(final taskElement item) {
            name.setText(item.getName());
            radioButton.setChecked(true);

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
                                    // Logica del boton "editar"
                                    // obtiene la informacion de latarea y la pasa a la pantalla editar tarea
                                    Intent intent = new Intent(context, screenEditTask.class);
                                    taskElement auxItem= adapterLogic.getTaskFullInfo(item.getId(), "completedtasks");
                                    intent.putExtra("id", auxItem.getId());
                                    intent.putExtra("name", auxItem.getName());
                                    intent.putExtra("description", auxItem.getDescription());
                                    intent.putExtra("type", auxItem.getType());
                                    intent.putExtra("notify", auxItem.isNotify());
                                    intent.putExtra("time", auxItem.getTime());
                                    intent.putExtra("table", "completedtasks");
                                    intent.putExtra("profileId", auxItem.getProfileId());
                                    context.startActivity(intent);
                                    break;
                                case R.id.optionDelete:
                                    // Logica del boton "eliminar"
                                    // Elimina la tarea
                                    adapterLogic.deleteTask(item.getId(), "completedtasks");
                                    setItem(adapterLogic.getCompletedtasksList());
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

            radioButton.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                    // Cuando se pulsa el radioButton de la tarjetita de la tarea, la borra de la tabla completedtasks y la traspasa a pendingtasks
                    if(b == false){
                        pendingTasksAdapter.uncompleteCompletedTask(item.getId());
                        adapterLogic.deleteTask(item.getId(), "completedtasks");
                        setItem(adapterLogic.getCompletedtasksList());
                        notifyDataSetChanged();
                    }

                }
            });
            // Muestra la informacion de la tarea
            name.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    adapterLogic.showTaskInfo(item.getId(), "completedtasks");
                }
            });
        }
    }

    /**
     * Método de comunicación con otro adapter, "listPendingTasksAdapter", para el intercambio de tareas (pendiente -> completada)
     * @param id
     */
    

    public void completePendingTask(int id){
        DbHelper dbHelper = new DbHelper(context);
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        long checkInsert = 0;
        ContentValues cv = new ContentValues();

        Cursor cursor = db.rawQuery("Select * from pendingtasks where id = " + id, null);

        while (cursor.moveToNext()){
            cv.put("id", cursor.getInt(0));
            cv.put("name", cursor.getString(1));
            cv.put("description", cursor.getString(2));
            cv.put("type", cursor.getString(3));
            cv.put("notify", cursor.getInt(4));
            cv.put("time", cursor.getString(5));
            cv.put("profileId", cursor.getInt(6));
        }
        try {
            checkInsert = db.insertOrThrow("COMPLETEDTASKS", null, cv);
            Log.i(TAG, "Inserción en COMPLETEDTASKS: " + String.valueOf(checkInsert));
        } catch (SQLException e){
            Log.e("Exception","SQLException"+String.valueOf(e.getMessage()));
            e.printStackTrace();
        } finally {
            db.close();
            cursor.close();
            setItem(adapterLogic.getCompletedtasksList());
            notifyDataSetChanged();
        }
    }

    public void auxSetListPendingTasksAdapter( listPendingTasksAdapter adapter){
        this.pendingTasksAdapter = adapter;
    }

}
