package com.example.stella;

import static android.content.ContentValues.TAG;

import android.content.ContentValues;
import android.content.Context;
import android.content.SharedPreferences;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import org.junit.runner.RunWith;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.mockito.MockitoAnnotations.initMocks;

import androidx.annotation.Nullable;
import androidx.test.core.app.ApplicationProvider;

import org.junit.Before;
import org.junit.Test;
import org.robolectric.RobolectricTestRunner;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;


@RunWith(RobolectricTestRunner.class)
public class dbTests  {

    DbHelperForTest dbHelperTest;
    dbLogicTest dbLogicTest;
    private Context instrumentationContext;
    settingsTest settingsTest;


    @Before
    public void setup() {
        initMocks(this);
        instrumentationContext = ApplicationProvider.getApplicationContext();
        dbHelperTest = new DbHelperForTest(instrumentationContext);
        dbLogicTest = new dbLogicTest(instrumentationContext, dbHelperTest);
        settingsTest = new settingsTest(instrumentationContext);
    }

    /**
     * Método test para probar la inserción de datos en una tabla. En su caso, insertar una tarea en la tabla "pendingtasks"
     */

    @Test
    public void testInsertDB() {
        // Obtenemos el nombre de la tabla
        String tableName = "pendingtasks";

        // Formamos un contentValues para editar el registro con id 1
        ContentValues task = new ContentValues();
        task.put("id", 900);
        task.put("name", "Prueba");
        task.put("description", "Descripcion de ejemplo");
        task.put("type", "leisure");
        task.put("notify", 0);
        task.put("profileId", 1);

        // Ejecutamos el método para insertar la tarea y obtenemos el valor boolean para comprobar si se logro hacer o no
        boolean result = dbLogicTest.insertTask(tableName, task, dbHelperTest);

        // Verifica que la inserción haya sido exitosa
        assertTrue(result); // SE ESPERA TRUE
    }

    /**
     * Método test para probar la actualización de registros en una tabla. En su caso actualiza la tarea con id 1 de la tabla "pendingtasks"
     */

    @Test
    public void testUpdateDB(){
        // Obtenemos el id de la tarea que queremos actualizar
        int taskId = 1;
        // Obtenemos el nombre de la tabla
        String tableName = "pendingtasks";
        // Formamos un contentValues para editar el registro con id 1
        ContentValues task = new ContentValues();
        task.put("name", "Prueba");
        task.put("description", "Descripcion de ejemplo");
        task.put("type", "leisure");
        task.put("notify", 0);
        task.put("profileId", 1);

        // Ejecutamos el método para actualizar la tarea con id 1 y obtenemos el valor boolean para comprobar si se logró hacer o no
        boolean result = dbLogicTest.updateTask(taskId, tableName, task);

        // Verifica que la inserción haya sido exitosa
        assertTrue(result); // SE ESPERA TRUE
    }

    /**
     * Método test para probar la eliminación de registros en una tabla. En su caso elimina una tarea con id 1 de la tabla "pendingtasks"
     */

    @Test
    public void testDeleteDB(){
        // Obtenemos el id de la tarea que queremos eliminar
        int taskId = 1;
        // Obtenemos el nombre de la tabla
        String tableName = "pendingtasks";

        // Ejecutamos el método para eliminar la tarea 1 y obtenemos el valor boolean para comprobar si se logró hacer
        boolean result = dbLogicTest.deleteTask(taskId, tableName);

        // Verifica que la inserción haya sido exitosa
        assertTrue(result); // SE ESPERA TRUE
    }


    /**
     * Método test para verificar si existe un registro en una tabla. En su caso verifica si la tarea con id 1 se encuentra en la tabla "pendingtasks"
     */
    @Test
    public void testCheckTaskInTable(){
        // Obtenemos el id de la tarea
        int taskId = 1;
        // Obtenemos el nombre de la tabla
        String tableName = "pendingtasks";

        // Ejecutamos el método para verificar si se encuentra la tarea en la tabla o no y recogemos el valor boolean
        boolean result = dbLogicTest.checkTaskInTable(taskId, tableName);

        // Verifica que la inserción haya sido exitosa
        assertTrue(result); // SE ESPERA TRUE
    }

    /**
     * Método test para la creación de un nuevo perfil. En su caso verifica si el perfil se creo correctamente
     */

    @Test
    public void testCreateProfileDB(){
        // Obtenemos el nombre del perfil que queremos crear
        String profileName = "Prueba1";

        // Ejecutamos el método para insertar el perfil y recogemos el valor boolean
        boolean result = dbLogicTest.createProfile(profileName);

        // Verifica que la inserción haya sido exitosa
        assertTrue(result); // SE ESPERA TRUE
    }

    /**
     * Método test para la actualización de un perfil existente. En su caso actualiza el perfil con id 1
     */

    @Test
    public void testUpdateProfileDB(){
        //Obtenemos el id del perfil a actualizar
        int id = 1;

        // Obtenemos el nombre que le queremos poner nuevo
        String newName = "Prueba2";

        // Ejecutamos el método para actualizar el perfil y recogemos el resultado
        boolean result = dbLogicTest.updateProfile(newName, id);

        // Verifica que la inserción haya sido exitosa
        assertTrue(result); // SE ESPERA TRUE
    }

    /**
     * Método test para ingresar un tema de la app y obtenerlo de vuelta
     */

    @Test
    public void testSetAndGetAppTheme(){
        // Obtenemos el id del tema que queramos
        int themeId = 2;

        settingsTest.setAppTheme(themeId);

        int check = settingsTest.getAppTheme();

        assertEquals(2, check);
    }

    /**
     * Método test para ingresar un idioma en la app y obtenerlo de vuelta
     */

    @Test
    public void testSetAndGetLanguage(){
        // Obtenemos el id del idioma que queramos
        int languageId = 2;

        settingsTest.setLanguage(languageId);

        int check = settingsTest.getAppLanguage();

        assertEquals(2, check);
    }

    /**
     * Método test para ingresar el nombre del perfil en uso y obtenerlo de vuelta
     */

    @Test
    public void testSetAndGetProfileName(){
        // Obtenemos el nombre del perfil
        String name = "Prueba";

        // Ingresamos el nombre del perfil
        settingsTest.setProfileName(name);

        // Obtenemos de vuelta el nombre del perfil
        String check = settingsTest.getCurrentProfileName();

        assertEquals("Prueba", check);
    }

    /**
     * Método test para ingresar un id del perfil en uso y obtenerlo de vuelta
     */

    @Test
    public void testSetAndGetProfileID(){
        // Obtenemos el id del perfil
        int id = 1;

        // Ingresamos el id del perfil
        settingsTest.setProfileID(id);

        // Obtenemos de vuelta el id del perfil
        int check = settingsTest.getCurrentProfileID();

        assertEquals(1, check);
    }

    /**
     * Clase para simular la base de datos real pero sin afectarla
     */

    public static class DbHelperForTest extends SQLiteOpenHelper {

        private static final int DATABASE_VERSION = 1;
        private static final String DATABASE_NAME = "stella_test2.db";
        private  Context CONTEXT = null;

        public DbHelperForTest(@Nullable Context context) {
            super(context, DATABASE_NAME, null, DATABASE_VERSION);
            CONTEXT = context;
        }

        @Override
        public void onCreate(SQLiteDatabase sqLiteDatabase) {
            createTablesTestDB(sqLiteDatabase, CONTEXT);
            insertDataTestDB(sqLiteDatabase, CONTEXT);
            initialIDTestDB();
            System.out.println("CREDA PASE DE DATOS");
        }

        @Override
        public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1) {
            //insertdata(sqLiteDatabase, CONTEXT);
        }

        /**
         * createTables se encarga de la creación de las tablas de la base de datos . Para ello hace uso de un documento en res>raw, llamado
         * "creaciondelatablasql"
         * @param sqldb
         * @param context
         */

        private void createTablesTestDB(SQLiteDatabase sqldb, Context context){

            List<String> queries = new ArrayList<>();
            queries.add("CREATE TABLE IF NOT EXISTS PROFILES ( id INTEGER PRIMARY KEY AUTOINCREMENT, name VARCHAR(16) NOT NULL)");
            queries.add("CREATE TABLE IF NOT EXISTS PENDINGTASKS (id INT NOT NULL, name VARCHAR(60) NOT NULL, description VARCHAR(255), type VARCHAR(30) NOT NULL, notify BOOLEAN NOT NULL, time TIME, profileId int, primary key(id), foreign key(profileId) references profiles(id) ON DELETE CASCADE)");
            queries.add("CREATE TABLE IF NOT EXISTS COMPLETEDTASKS (id INT NOT NULL, name VARCHAR(60) NOT NULL, description VARCHAR(255), type VARCHAR(30) NOT NULL, notify BOOLEAN NOT NULL, time TIME, profileId int, primary key(id), foreign key(profileId) references profiles(id) ON DELETE CASCADE)");
            queries.add("CREATE TABLE IF NOT EXISTS PREVIOUSRECORDS (id INTEGER NOT NULL PRIMARY KEY AUTOINCREMENT, date DATE NOT NULL, study INT, domestic INT, work INT, leisure INT, profileId int,  foreign key(profileId) references profiles(id) ON DELETE CASCADE)");
            queries.add("CREATE TABLE IF NOT EXISTS WEEKLYTASKS ( id INT NOT NULL, name VARCHAR(60) NOT NULL, description VARCHAR(255), type VARCHAR(30) NOT NULL, notify BOOLEAN NOT NULL, time TIME, profileId int, monday BOOLEAN, tuesday BOOLEAN, wednesday BOOLEAN, thursday BOOLEAN, friday BOOLEAN, saturday BOOLEAN, sunday BOOLEAN, primary key(id), foreign key(profileId) references profiles(id) ON DELETE CASCADE)");


            for(int i = 0; i < queries.size(); i++){
                sqldb.execSQL(queries.get(i));
            }

        }

        /**
         * insertData se encarga de la insercción en las tablas de la base de datos . Para ello hace uso de un documento en res>raw, llamado
         * "insercciondedatossql"
         * @param sqldb
         * @param context
         */

        private void insertDataTestDB(SQLiteDatabase sqldb, Context context){
            List<String> queries = new ArrayList<>();

            queries.add("INSERT INTO PROFILES (id, name) VALUES (1, 'Jonathan')");
            queries.add("INSERT INTO PROFILES (id, name) VALUES (2, 'Morales')");
            queries.add("INSERT INTO PENDINGTASKS (id, name, description, type, notify, time, profileId) VALUES (1, 'Hacer ejercicios de programacion', 'Ejercicios 1,2 y 4 del tema 4, pagina 54', 'study', 0, NULL, 1)");
            queries.add("INSERT INTO PENDINGTASKS (id, name, description, type, notify, time, profileId) VALUES (2, 'Revisar correo', NULL, 'work', 1, '17:00:00', 1)");
            queries.add("INSERT INTO COMPLETEDTASKS (id, name, description, type, notify, time, profileId) VALUES (3, 'Tarea de estudiar', 'Estudiar temas 1 y 2', 'study', 0, NULL, 1)");
            queries.add("INSERT INTO COMPLETEDTASKS (id, name, description, type, notify, time, profileId) VALUES (4, 'Tarea de estudiar2', 'Estudiar temas 3 y 4', 'study', 0, NULL, 1)");
            queries.add("INSERT INTO COMPLETEDTASKS (id, name, description, type, notify, time, profileId) VALUES (5, 'Tarea de trabajo', 'Resolver bug de la linea 52', 'work', 0, NULL, 1)");
            queries.add("INSERT INTO COMPLETEDTASKS (id, name, description, type, notify, time, profileId) VALUES (6, 'Tarea de trabajo2', 'Resolver bug de la linea 354', 'work', 0, NULL, 1)");
            queries.add("INSERT INTO COMPLETEDTASKS (id, name, description, type, notify, time, profileId) VALUES (7, 'Tarea de trabajo3', 'Meeting de las 12', 'work', 1, '12:00:00', 1)");
            queries.add("INSERT INTO COMPLETEDTASKS (id, name, description, type, notify, time, profileId) VALUES (8, 'Tarea de domestica', 'Barrer el suelo', 'domestic', 1, '16:30:00', 1)");
            queries.add("INSERT INTO WEEKLYTASKS (id, name, description, type, notify, time, profileId, monday, tuesday, wednesday, thursday, friday, saturday, sunday) VALUES (9, 'Ir al gym', 'Toca pecho y triceps', 'leisure', 1, '19:20:00', 1, 1,0,0,0,1,0,0)");
            queries.add("INSERT INTO WEEKLYTASKS (id, name, description, type, notify, time, profileId, monday, tuesday, wednesday, thursday, friday, saturday, sunday) VALUES (10, 'Ir al gym', 'Toca dorsales y biceps', 'leisure', 1, '19:20:00', 1, 0,1,0,0,0,1,0)");
            queries.add("INSERT INTO WEEKLYTASKS (id, name, description, type, notify, time, profileId, monday, tuesday, wednesday, thursday, friday, saturday, sunday) VALUES (11, 'Ir al gym', 'Toca hombros y trapecio', 'leisure', 1, '19:20:00', 1, 0,0,1,0,0,0,1)");
            queries.add("INSERT INTO WEEKLYTASKS (id, name, description, type, notify, time, profileId, monday, tuesday, wednesday, thursday, friday, saturday, sunday) VALUES (12, 'Ir al gym', 'Toca pierna', 'leisure', 1, '19:20:00', 1, 0,0,0,1,0,0,0)");
            queries.add("INSERT INTO PREVIOUSRECORDS (date, study, domestic, work, leisure, profileId) VALUES ('2024-01-01', 1, 2, 1, 4, 1)");
            queries.add("INSERT INTO PREVIOUSRECORDS (date, study, domestic, work, leisure, profileId) VALUES ('2024-01-02', 0, 1, 3, 1, 1)");
            queries.add("INSERT INTO PREVIOUSRECORDS (date, study, domestic, work, leisure, profileId) VALUES ('2024-01-03', 3, 0, 0, 5, 1)");
            queries.add("INSERT INTO PREVIOUSRECORDS (date, study, domestic, work, leisure, profileId) VALUES ('2024-01-04', 0, 0, 5, 2, 1)");


            for (int i = 0; i < queries.size(); i++){
                sqldb.execSQL(queries.get(i));
            }
        }

        /**
         * initialDb se usa para establecer un ID principal el cual se usara para las siguientes tareas que sean creadas. Es así debido a que la id en las tareas se introduce fuera de la DB
         * y ya insertamos 12 tareas en "insertData"
         */

        private void initialIDTestDB(){
            SharedPreferences preferences = CONTEXT.getSharedPreferences("nextIDTestDB", Context.MODE_PRIVATE);
            SharedPreferences.Editor editor = preferences.edit();
            editor.putInt("id", 13);
            editor.commit();
        }

    }

}
