package com.example.stella;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.os.Build;
import android.os.Looper;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.Robolectric;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.Shadows;
import org.robolectric.annotation.Config;
import org.robolectric.annotation.LooperMode;
import static org.junit.Assert.assertTrue;

import androidx.test.core.app.ApplicationProvider;

import com.example.stella.db.DbHelper;
import com.example.stella.db.dbLogic;

import androidx.arch.core.executor.testing.InstantTaskExecutorRule;



@RunWith(RobolectricTestRunner.class)
@Config(sdk = Build.VERSION_CODES.P)
@LooperMode(LooperMode.Mode.PAUSED)
public class screenNewTaskIntegrationTest {

    private screenNewTask activity;

    private dbLogic dbLogic;
    private Context instrumentationContext;

    EditText name, description;
    CheckBox notifyCheckBox;
    Button btn_register2;

    @Rule
    public InstantTaskExecutorRule executorRule = new InstantTaskExecutorRule();

    @Before
    public void setUp() {
        activity = Robolectric.buildActivity(screenNewTask.class).create().get();

        instrumentationContext = ApplicationProvider.getApplicationContext();

        name = activity.findViewById(R.id.editTextEditName);
        description = activity.findViewById(R.id.editTextEditDescription);
        notifyCheckBox = activity.findViewById(R.id.editNotifyCheckBox);
        btn_register2 = activity.findViewById(R.id.btn_register2);

        dbLogic = new dbLogic(activity);

    }

    /**
     * Con este test se pretende comprobar que screenNewTask trabaja bien en conjunto con dbLogic y este con DbHelper
     */

    @Test
    public void testAddNewTask(){
        // Abrimos conexión con la base de datos para poder introducirle datos
        DbHelper dbh = new DbHelper(activity);
        SQLiteDatabase db = dbh.getWritableDatabase();

        // Establezco los valores que tendria escritos
        name.setText("Tarea de prueba");
        description.setText("Descripcion de prueba");
        notifyCheckBox.setChecked(false);

        // Simulamos el click del botón para insertar la tarea
        btn_register2.performClick();

        // Espera a que el looper principal se quede inactivo
        Shadows.shadowOf(Looper.getMainLooper()).idle();

        // Comprobamos que la tarea fue insertada. Como el siguiente ID tras crear la base de datos es 13 para las siguientes tareas, la tarea que hememos
        // creado deberia de tener el id 13
        boolean check = dbLogic.checkTaskInTable(13, "pendingtasks");

        // Cerramos conexion
        db.close();
        // Verifica que la inserción haya sido exitosa
        assertTrue(check); // SE ESPERA TRUE
    }


}
