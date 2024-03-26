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

import com.example.stella.utils.Alarm;
import com.example.stella.R;
import com.example.stella.db.DbHelper;
import com.example.stella.pantallaEditarTarea;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class listPendingTasksAdapter extends RecyclerView.Adapter<listPendingTasksAdapter.ViewHolder> {
    private List<taskElement> mData = new ArrayList<>();
    private LayoutInflater mInflater;
    private Context context;
    private Dialog dialogInfo = null;
    listCompletedTasksAdapter completedTasksAdapter;
    Alarm alarm;
    adaptersLogic adaptersLogic;


    public listPendingTasksAdapter( Context context){
        this.mInflater = LayoutInflater.from(context);
        this.context = context;
        alarm = new Alarm(context);
        fillPendingTasks();
        adaptersLogic = new adaptersLogic(context);

    }

    @Override
    public int getItemCount() {return mData.size();}

    @Override
    public listPendingTasksAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = mInflater.inflate(R.layout.cardview_task_layout, parent, false);
        return new listPendingTasksAdapter.ViewHolder(view);
    }

    @Override
    public void  onBindViewHolder(final listPendingTasksAdapter.ViewHolder holder, final  int position){
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
            radioButton.setChecked(false);

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
                                    taskElement auxItem= adaptersLogic.getTaskFullInfo(item.getId(), "pendingtasks");
                                    intent.putExtra("id", auxItem.getId());
                                    intent.putExtra("name", auxItem.getName());
                                    intent.putExtra("description", auxItem.getDescription());
                                    intent.putExtra("type", auxItem.getType());
                                    intent.putExtra("notify", auxItem.isNotify());
                                    intent.putExtra("time", auxItem.getTime());
                                    intent.putExtra("table", "pendingtasks");
                                    context.startActivity(intent);
                                    break;
                                case R.id.optionDelete:
                                    deleteItem(item.getId());
                                    fillPendingTasks();
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
                    if(b == true){
                        completedTasksAdapter.completePendingTask(item.getId());
                        alarm.cancelAlarm(item.getId());
                        deleteItem(item.getId());
                        fillPendingTasks();
                    }
                }
            });

            name.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    adaptersLogic.showTaskInfo(item.getId(), "pendingtasks");
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
        Cursor c = db.rawQuery("SELECT id from PENDINGTASKS where id = " + id, null);
        if(c.moveToFirst()){
            do{
                int rowsAffected = db.delete("PENDINGTASKS", "id = ?", new String[]{String.valueOf(id)});
                Log.i(TAG, "Delete rows affected in PENDINGTASKS: " + rowsAffected);
                if(rowsAffected > 0){
                alarm.cancelAlarm(id);
                }


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

    public void fillPendingTasks(){
        DbHelper dbHelper = new DbHelper(context);
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        Cursor cursor = db.rawQuery("Select name, id from pendingtasks", null);
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

        Cursor cursor = db.rawQuery("Select * from pendingtasks where id = " + id, null);

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

    public void uncompleteCompletedTask(int id){
        DbHelper dbHelper = new DbHelper(context);
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        long checkInsert = 0;
        ContentValues cv = new ContentValues();

        Cursor cursor = db.rawQuery("Select * from completedtasks where id = " + id, null);

        while (cursor.moveToNext()){
            cv.put("id", cursor.getInt(0));
            cv.put("name", cursor.getString(1));
            cv.put("description", cursor.getString(2));
            cv.put("type", cursor.getString(3));
            cv.put("notify", cursor.getInt(4));
            cv.put("time", cursor.getString(5));
        }
        try {
            if(cv.getAsString("time") != null){
                String name = cv.getAsString("name");
                Calendar calendar;
                calendar = auxSetCalendar(cv.getAsString("time"));
                alarm.setAlarm(id, name, calendar);
                Log.i("Reinserción en PendingTasks: ", "Alarma de la tarea reinsertada: " + calendar.get(Calendar.HOUR_OF_DAY) + ":" + calendar.get(Calendar.MINUTE) + ":00" );
            }
            checkInsert = db.insertOrThrow("PENDINGTASKS", null, cv);
            Log.i(TAG, "Inserción en PENDINGTASKS: " + String.valueOf(checkInsert));

        } catch (SQLException e){
            Log.e("Exception","SQLException"+String.valueOf(e.getMessage()));
            e.printStackTrace();
        } finally {
            db.close();
            cursor.close();
            fillPendingTasks();
        }
    }

    private Calendar auxSetCalendar(String time){
        Calendar calendar = Calendar.getInstance();
        Pattern p = Pattern.compile("(([0-9]{2,2}):([0-9]{2,2}):([0-9]{2,2}))");
        Matcher m = p.matcher(time);
        boolean mFound = m.find();
        Log.i("Adapter PendingTasks: ", "Hora recibida: " + time);
        if(mFound){
            int hour = Integer.valueOf(m.group(2));
            int minute = Integer.valueOf(m.group(3));
            calendar.set(Calendar.HOUR_OF_DAY, hour);
            calendar.set(Calendar.MINUTE, minute);
            calendar.set(Calendar.SECOND, 00);
            Log.i("Adapter PendingTasks:", "El patrón de time HIZO MATCH : " + hour + ":" + minute + ":00");
        } else {
            Log.i("Adapter PendingTasks:", "El patrón de time no hizo match");
        }

        return calendar;
    }

    public void auxSetListCompletedTasksAdapter( listCompletedTasksAdapter adapter){
        this.completedTasksAdapter = adapter;
    }


}
