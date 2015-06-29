package com.test.helloeeg;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteDatabase.CursorFactory;
import android.database.sqlite.SQLiteOpenHelper;

import java.util.ArrayList;
import java.util.List;
/* llamadas
        dataBase = new BD(this);
        dataBase.agregarElemento("penee");
        String hola = dataBase.getGraficos();
 */
public class BD extends SQLiteOpenHelper {

    private static int version = 1;
    private static String name = "baseDatos" ;
    private static CursorFactory factory = null;
    SQLiteDatabase db;
    //Sentencia SQL para crear la tabla de Usuarios
    String sqlCreateGrafico = "CREATE TABLE grafico (id INTEGER PRIMARY KEY, nombre TEXT , fecha TEXT)";

    public BD(Context contexto) {
        super(contexto, name, factory, version);

    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(sqlCreateGrafico);

    }
    public void agregarElemento(String lista, String fecha){
        db = this.getWritableDatabase();
        db.execSQL("INSERT INTO grafico (nombre, fecha) VALUES (\""+lista+"\",\""+fecha+"\")");
        db.close();

    }
    public ArrayList<String>  getGraficos () {
        db = this.getWritableDatabase();
        ArrayList<String> datosGrafico=new ArrayList<String>();
        String elementDemo="";
        if (db != null)
        {
            // agregarmos ciudad
            Cursor c = db.rawQuery("SELECT id,nombre FROM grafico ORDER BY id DESC ", null);
            if (c.moveToFirst()) {
                //Recorremos el cursor hasta que no haya más registros
                do {
                    int id = c.getInt(0);
                    String nombre = c.getString(1);
                    datosGrafico.add(nombre);
                } while (c.moveToNext());
            }
        }
        db.close();
        return datosGrafico;
    }

    public ArrayList<String>  getFecha () {
        db = this.getWritableDatabase();
        ArrayList<String> datosGrafico=new ArrayList<String>();
        String elementDemo="";
        if (db != null)
        {
            // agregarmos ciudad
            Cursor c = db.rawQuery("SELECT id,fecha FROM grafico ORDER BY id DESC ", null);
            if (c.moveToFirst()) {
                //Recorremos el cursor hasta que no haya más registros
                do {
                    int id = c.getInt(0);
                    String nombre = c.getString(1);
                    datosGrafico.add(nombre);
                } while (c.moveToNext());
            }
        }
        db.close();
        return datosGrafico;
    }
    @Override
    public void onUpgrade(SQLiteDatabase db, int versionAnterior, int versionNueva) {
        //NOTA: Por simplicidad del ejemplo aquí utilizamos directamente la opción de
        //      eliminar la tabla anterior y crearla de nuevo vacía con el nuevo formato.
        //      Sin embargo lo normal será que haya que migrar datos de la tabla antigua
        //      a la nueva, por lo que este método debería ser más elaborado.

        //Se elimina la versión anterior de la tabla



        //db.execSQL("DROP TABLE IF EXISTS ciudades");

        //Se crea la nueva versión de la tabla
        //db.execSQL(sqlCreate);
    }
}
