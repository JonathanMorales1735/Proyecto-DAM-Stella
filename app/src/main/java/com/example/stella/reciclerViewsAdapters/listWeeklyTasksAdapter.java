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

    public listWeeklyTasksAdapter(Context context){
        this.mInflater = LayoutInflater.from(context);
        this.context = context;
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
                                    context.startActivity(intent);
                                    break;
                                case R.id.optionDelete:
                                    //deleteItem(item.getId());
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
                    showInfoDialog(item.getId());
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

        Cursor cursor = db.rawQuery("Select * from weeklytasks where id = " + id, null);

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
}
