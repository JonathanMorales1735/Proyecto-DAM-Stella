package com.example.stella.reciclerViewsAdapters;

import static android.content.ContentValues.TAG;

import android.app.Dialog;
import android.content.ContentValues;
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
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.ImageButton;
import android.widget.PopupMenu;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.example.stella.R;
import com.example.stella.db.DbHelper;
import com.example.stella.pantallaEditarTarea;

import java.util.ArrayList;
import java.util.List;

public class listCompletedTasksAdapter extends RecyclerView.Adapter<listCompletedTasksAdapter.ViewHolder> {
    private List<taskElement> mData = new ArrayList<>();
    private LayoutInflater mInflater;
    private Context context;
    private Dialog dialogInfo = null;
    listPendingTasksAdapter pendingTasksAdapter;

    public listCompletedTasksAdapter(Context context){
        this.mInflater = LayoutInflater.from(context);
        this.context = context;
        fillCompletedTasks();

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
                                    Intent intent = new Intent(context, pantallaEditarTarea.class);
                                    context.startActivity(intent);
                                    break;
                                case R.id.optionDelete:
                                    deleteItem(item.getId());
                                    fillCompletedTasks();
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

                    if(b == false){
                        pendingTasksAdapter.uncompleteCompletedTask(item.getId());
                        deleteItem(item.getId());
                        fillCompletedTasks();
                    }

                }
            });

            name.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    showInfoDialog(item.getId());
                }
            });
        }
    }

    /**
     * Éste método sirve para eliminar de la base de datos la tarea seleccionada. Hace uso de la id del elemento "taskElement"
     * como condición para su eliminación.
     * @param id
     */

    private void deleteItem(int id){
        DbHelper dbhelper = new DbHelper(context);
        SQLiteDatabase db = dbhelper.getWritableDatabase();
        Cursor c = db.rawQuery("SELECT id from COMPLETEDTASKS where id = " + id, null);
        if(c.moveToFirst()){
            do{
                int rowsAffected = db.delete("COMPLETEDTASKS", "id = ?", new String[]{String.valueOf(id)});
                Log.i(TAG, "Delete rows affected in COMPLETEDTASKS: " + rowsAffected);


            }while(c.moveToNext());
            c.close();
            db.close();
        }
    }

    /**
     * Éste método sirve para rellenar la List del adaptador de objetos "taskElement". Para rellenar la información
     * de cada taskElement, se llama a la base de datos para recoger el nombre y el id. Finalmente notifica al adaptador
     * que la información ha cambiado mediante notifyDataSetChanged();
     *
     * Se usa principalmente en la app apara refrescar la lista de elementos dentro del recyclerView que haga uso de este adaptador.
     */

    public void fillCompletedTasks(){
        DbHelper dbHelper = new DbHelper(context);
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        Cursor cursor = db.rawQuery("Select name, id from completedtasks", null);
        taskElement task;
        if(mData != null){
            clear();
        }

        while(cursor.moveToNext()){
            task = new taskElement();
            String name = cursor.getString(0);
            int id = cursor.getInt(1);
            if(name.length() >= 42){
                name = name.substring(0, 42) + "...";
            }
            task.setName(name);
            task.setId(id);
            mData.add(task);
        }

        cursor.close();
        db.close();
        notifyDataSetChanged();

    }

    /**
     * Éste método sirve para vaciar la List del adaptador.
     */

    public void clear(){
        mData.clear();
    }

    private void showInfoDialog(int id){
        if((dialogInfo == null) || !dialogInfo.isShowing()){
            dialogInfo = new Dialog(context);
            dialogInfo.requestWindowFeature(Window.FEATURE_NO_TITLE);
            dialogInfo.setContentView(R.layout.pantallainfo);

            TextView name = dialogInfo.findViewById(R.id.textName);
            TextView description = dialogInfo.findViewById(R.id.textDescription);
            TextView type = dialogInfo.findViewById(R.id.textType);
            TextView time = dialogInfo.findViewById(R.id.textTime);
            TextView days = dialogInfo.findViewById(R.id.textDays);

            taskElement item = auxGetTaskFullInfo(id);

            //Debido a que la app tiene mas de un idioma, se consigue el nombre del tipo de tarea (la cual se introduce en inglés para que coincida con su id en strings.xml)
            //y se muestra en el idioma seleccionado por el usuario.
            int typeIdName = context.getResources().getIdentifier(item.getType(), "string", context.getPackageName());
            String typeName = context.getResources().getString(typeIdName);

            name.setText(item.getName());
            description.setText(item.getDescription());
            type.setText(typeName);
            time.setText(item.getTime());


            dialogInfo.show();
            dialogInfo.getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
            dialogInfo.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
            dialogInfo.getWindow().getAttributes().windowAnimations = R.style.DialogAnimationLeft;
            dialogInfo.getWindow().setGravity(Gravity.LEFT);

            ImageButton button = dialogInfo.findViewById(R.id.btn_close);
            button.setOnClickListener(new View.OnClickListener(){
                public void onClick(View view) {
                    dialogInfo.hide();
                    dialogInfo.dismiss();
                }
            });
        }


    }

    private taskElement auxGetTaskFullInfo(int id){
        DbHelper dbHelper = new DbHelper(context);
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        taskElement task = null;

        Cursor cursor = db.rawQuery("Select * from completedtasks where id = " + id, null);

        while(cursor.moveToNext()){
            task = new taskElement();
            task.setId(cursor.getInt(0));
            task.setName(cursor.getString(1));
            task.setDescription(cursor.getString(2));
            task.setType(cursor.getString(3));
            task.setNotify(cursor.getInt(4));
            task.setTime(cursor.getString(5));
        }
        try{
            db.close();
            cursor.close();
        } catch (SQLException e){
            e.printStackTrace();
        }

        return task;
    }

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
            fillCompletedTasks();
        }
    }

    public void auxSetListPendingTasksAdapter( listPendingTasksAdapter adapter){
        this.pendingTasksAdapter = adapter;
    }

}
