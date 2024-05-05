package com.example.stella.dialogs;

import android.app.Dialog;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.view.Gravity;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.ImageButton;
import android.widget.TextView;

import com.example.stella.R;
import com.example.stella.db.DbHelper;
import com.example.stella.recyclerViewsAdapters.taskElement;

/**
 * textInfoDialog muestra un dialog con información de una tarea, como su nombre, descripcion, hora...
 */

public class taskInfoDialog extends Dialog {
    private Context context;
    private boolean isShowing = false;
    int taskID = 0;
    String tableName = "";

    public taskInfoDialog(Context context, int id, String name) {
        super(context);
        this.context = context;
        taskID = id;
        tableName = name;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        if(!isShowing || !this.isShowing()) {
            super.onCreate(savedInstanceState);
            requestWindowFeature(Window.FEATURE_NO_TITLE);
            setContentView(R.layout.dialog_task_info);

            TextView name = findViewById(R.id.textName);
            TextView description = findViewById(R.id.textDescription);
            TextView type = findViewById(R.id.textType);
            TextView time = findViewById(R.id.textTime);
            // Se obtiene la tarea para reflejar sus valores en los textView
            taskElement item = auxGetTaskFullInfo(taskID);

            int typeIdName = context.getResources().getIdentifier(item.getType(), "string", context.getPackageName());
            String typeName = context.getResources().getString(typeIdName);

            name.setText(item.getName());
            description.setText(item.getDescription());
            type.setText(typeName);
            time.setText(item.getTime());

            getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
            getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
            getWindow().getAttributes().windowAnimations = R.style.DialogAnimationLeft;
            getWindow().setGravity(Gravity.LEFT);
            // boton para cerrar el dialog
            ImageButton button = findViewById(R.id.btn_close);
            button.setOnClickListener(new android.view.View.OnClickListener() {
                public void onClick(android.view.View view) {
                    hide();
                    dismiss();
                    isShowing = false;
                }
            });
        }
    }

    @Override
    public void show() {
        if (!isShowing) {
            super.show();
            isShowing = true;
        }
    }

    @Override
    public void dismiss() {
        super.dismiss();
        isShowing = false;
    }

    /**
     * Método usado para obtener el valor de todos los campos de una tarea y devolverlos en un objeto taskElement
     * @param id
     * @return
     */

    private taskElement auxGetTaskFullInfo(int id){
        // Se abre conexión con base de datos
        DbHelper dbHelper = new DbHelper(this.getContext());
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        taskElement task = null;
        // Se ejecuta una query para obtener la tarea con id pasado por parametro
        Cursor cursor = db.rawQuery("Select * from " + tableName + " where id = " + id, null);
        // Se forma el objeto con los datos obtenidos
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
            // Se cierra la conexión con BD
            db.close();
            cursor.close();
        } catch (SQLException e){
            e.printStackTrace();
        }

        return task;
    }
}
