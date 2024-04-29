package com.example.stella.reciclerViewsAdapters;

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

import com.example.stella.db.dbLogic;
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
    listCompletedTasksAdapter completedTasksAdapter;
    Alarm alarm;
    adaptersLogic adaptersLogic;


    public listPendingTasksAdapter( Context context){
        this.mInflater = LayoutInflater.from(context);
        this.context = context;
        alarm = new Alarm(context);
        adaptersLogic = new adaptersLogic(context);
        setItem(adaptersLogic.getPendingtasksList());
        notifyDataSetChanged();

    }

    public void reSetItemList(){
        setItem(adaptersLogic.getPendingtasksList());
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
                                    intent.putExtra("profileId", auxItem.getProfileId());
                                    context.startActivity(intent);
                                    break;
                                case R.id.optionDelete:
                                    adaptersLogic.deleteTask(item.getId(), "pendingtasks");
                                    setItem(adaptersLogic.getPendingtasksList());
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
                    if(b == true){
                        completedTasksAdapter.completePendingTask(item.getId());
                        alarm.cancelAlarm(item.getId());
                        adaptersLogic.deleteTask(item.getId(), "pendingtasks");
                        setItem(adaptersLogic.getPendingtasksList());
                        notifyDataSetChanged();
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
     * Método de comunicación con otro adapter, "listCompletedTasksAdapter", para el intercambio de tareas (completado -> pendiente)
     * @param id
     */

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
            cv.put("profileId", cursor.getInt(6));
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
            setItem(adaptersLogic.getPendingtasksList());
            notifyDataSetChanged();
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
