package com.test.helloeeg;

import android.app.Activity;
import android.app.Dialog;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.bluetooth.BluetoothAdapter;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.*;
import com.neurosky.thinkgear.*;

import java.util.ArrayList;

import lecho.lib.hellocharts.model.PointValue;

public class HelloEEGActivity extends Activity {
	BluetoothAdapter bluetoothAdapter;
    CircularProgressBar c1;
	ArrayList<Integer> meditacion= new ArrayList<Integer>();
    ArrayList<String> datosGrafico=new ArrayList<String>();
	TextView tv;
	Button b;
	boolean aviso=false;
    boolean calidad=false;
	TGDevice tgDevice;
	final boolean rawEnabled = false;
    //pop_up var
    Dialog dialog;
    //database
    BD dataBase;
    //pop_up historial
    Dialog historialGrafico;




    private void medirEstress(){
        //colocar el tamaño maximo de meditacion
        while(meditacion.size()>1000000){
           meditacion.remove(0);
        }
        //calcular estres mental
        int estress=0;
        int porcentaje=0;
        for(int i =0; i<meditacion.size();i++){
            if(meditacion.get(i)<40)
                estress++;
           // tv.append("datos "+meditacion.get(i)+"\n");
        }
        float estressFinal=estress*100f/meditacion.size();
        porcentaje=100-(int)(75.0*estressFinal/25.0);
        if(porcentaje>100)
            porcentaje=100;
        if(porcentaje<0)
            porcentaje=0;
        //fin calculo estress

        c1.setProgress(porcentaje);
        c1.setTitle(porcentaje+"%");

        //colocar el color adecuado para el grafico
        if(porcentaje<=30) {//rojo
            c1.mProgressColorPaint.setColor(Color.parseColor("#FF0700"));

        }
        else if(porcentaje> 30 && porcentaje <=50) {//anaranjado
            c1.mProgressColorPaint.setColor(Color.parseColor("#FF6700"));
        }
        else if(porcentaje>50 && porcentaje <=65){ //amarillento
            c1.mProgressColorPaint.setColor(Color.parseColor("#FFD100"));
        }
        else { //verde
            c1.mProgressColorPaint.setColor(Color.parseColor("#3fea74"));
        }
        c1.refreshDrawableState();

        //agregar porcentaje estress a la lista para los graficos.
        datosGrafico.add(porcentaje+"");
        //que el grafico posea hasta 100 datos
        while(datosGrafico.size()>100) {
            datosGrafico.remove(0);
        }
        if(estressFinal>=25&&meditacion.size()>60){
            View vista= getWindow().getDecorView();
            if(!aviso) {
                createNotification(vista);
                aviso=true;//comenzar el timer propuesto
                return;
            }
        }
        return;

    }
    private void createNotification(View view) {//(View view)
        // Prepare intent which is triggered if the
        // notification is selected
        Intent intent = new Intent(this, NotificationReceiverActivity.class);
        PendingIntent pIntent = PendingIntent.getActivity(this, 0, intent, 0);

        // Build notification
        // Actions are just fake
        Notification noti = new Notification.Builder(this)
                .setContentTitle("StudyBuddy")
                .setContentText("Rest").setSmallIcon(R.drawable.logo)
                .setContentIntent(pIntent).build();
                //.addAction(R.drawable.icon, "Call", pIntent)
                //.addAction(R.drawable.icon, "More", pIntent)
                //.addAction(R.drawable.icon, "And more", pIntent).build();
        NotificationManager notificationManager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
        // hide the notification after its selected
        noti.flags |= Notification.FLAG_AUTO_CANCEL;
        noti.defaults |= Notification.DEFAULT_SOUND;
        noti.defaults |= Notification.DEFAULT_VIBRATE;

        notificationManager.notify(0, noti);

    }
    private void comenzarLectura(View view) {
        setContentView(R.layout.leyendo);
        tv = (TextView)findViewById(R.id.textView1);
        tv.setText("");
    }

    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);
        tv = (TextView)findViewById(R.id.textView);
        tv.setText("");
        //declaracion del popUp, para poder cargarlo en pantalla o esconderlo utilizar  dialog.show(); dialog.hide(); destruirlo dialog.cancel()
        dialog = new Dialog(this, android.R.style.Theme_Translucent_NoTitleBar);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.pop_up);

        historialGrafico = new Dialog(this, android.R.style.Theme_NoTitleBar);
        historialGrafico.requestWindowFeature(Window.FEATURE_NO_TITLE);
        historialGrafico.setContentView(R.layout.historial_grafico);
        historialGrafico.hide();

        dataBase = new BD(this);


        tv.append("Android version: " + Integer.valueOf(android.os.Build.VERSION.SDK) + "\n");
        bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        if(bluetoothAdapter == null) {
        	// Alert user that Bluetooth is not available
        	Toast.makeText(this, "Bluetooth not available", Toast.LENGTH_LONG).show();
        	finish();
        	return;
        }else {

        	tgDevice = new TGDevice(bluetoothAdapter, handler);
        }
    }

    @Override
    public void onDestroy() {
    	tgDevice.close();
        super.onDestroy();
    }
    /**
     * Handles messages from TGDevice
     */
    private final Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
        	switch (msg.what) {
            case TGDevice.MSG_STATE_CHANGE:

                switch (msg.arg1) {
	                case TGDevice.STATE_IDLE:
	                    break;
	                case TGDevice.STATE_CONNECTING:		                	
	                	tv.append("Connecting...\n");
	                	break;		                    
	                case TGDevice.STATE_CONNECTED:
	                	tv.append("Connected.\n");
                        comenzarLectura(getWindow().getDecorView());
                        c1 = (CircularProgressBar) findViewById(R.id.circularprogressbar1);
                        c1.setProgress(0);
	                	tgDevice.start();
	                    break;
	                case TGDevice.STATE_NOT_FOUND:
	                	tv.append("Can't find\n");
	                	break;
	                case TGDevice.STATE_NOT_PAIRED:
	                	tv.append("not paired\n");
	                	break;
	                case TGDevice.STATE_DISCONNECTED:
	                	tv.append("Disconnected mang\n");
                }

                break;
            case TGDevice.MSG_POOR_SIGNAL:
            		//signal = msg.arg1;
                    if(msg.arg1==0) {
                        calidad=true;
                    }
                    else {
                        tv.append("PoorSignal: " + msg.arg1 + "\n");
                        calidad=false;
                    }
                break;
            case TGDevice.MSG_RAW_DATA:	  
            		//raw1 = msg.arg1;
            		//tv.append("Got raw: " + msg.arg1 + "\n");
            	break;
            case TGDevice.MSG_HEART_RATE:
        		    //tv.append("Heart rate: " + msg.arg1 + "\n");
                break;
            case TGDevice.MSG_ATTENTION:
            		//att = msg.arg1;
            		//tv.append("Attention: " + msg.arg1 + "\n");
            		//Log.v("HelloA", "Attention: " + att + "\n");
            	break;
            case TGDevice.MSG_MEDITATION:
                    tv.append("Meditation: " + msg.arg1 + "\n");
                    if(calidad) {
                        dialog.hide();
                        if(msg.arg1!=0) {
                            meditacion.add(msg.arg1);
                            medirEstress();
                        }
                    }
            	break;
            case TGDevice.MSG_BLINK:
            		tv.append("Blink: " + msg.arg1 + "\n");
            	break;
            case TGDevice.MSG_RAW_COUNT:
            		//tv.append("Raw Count: " + msg.arg1 + "\n");
            	break;
            case TGDevice.MSG_LOW_BATTERY:
            	Toast.makeText(getApplicationContext(), "Low battery!", Toast.LENGTH_SHORT).show();
            	break;
            case TGDevice.MSG_RAW_MULTI:
            	//TGRawMulti rawM = (TGRawMulti)msg.obj;
            	//tv.append("Raw1: " + rawM.ch1 + "\nRaw2: " + rawM.ch2);
            default:
            	break;
        }
        }
    };


    public void doStuff(View view) {
        if(tgDevice.getState() != TGDevice.STATE_CONNECTING && tgDevice.getState() != TGDevice.STATE_CONNECTED) {
            tgDevice.connect(rawEnabled);
            dialog.show();
        }

        //tgDevice.ena
    }
    public void llamarGrafico(View view) {
        //enviar aqui la lista de strings datosGrafico

        Intent act = new Intent(this, GraficoActivity.class);
        act.putStringArrayListExtra("listaString",datosGrafico);
        startActivity(act);
        //tgDevice.ena
    }
    public void setListBD (View view){
        String valores="";
        ArrayList<String> data = datosGrafico;
        for (int i=0; i<data.size() && i < 100;i++){
            valores+=data.get(i)+":";
        }
        // se agregan los valores obtenidos de datos graficos, en formato value0:valu1:value2:value3: ....
        dataBase.agregarElemento(valores);
        // retorna los datos en un ArrayList<String> en el orden del ultimo hasta el primero (DESC)

      // Toast.makeText(this, ""+datosAlmacenados.get(datosAlmacenados.size()-1), Toast.LENGTH_LONG).show();
    }
    public void llenarLista(View view) {
        historialGrafico.show();
        ArrayList<String> datosAlmacenados = dataBase.getGraficos();
        ListView listas =(ListView)historialGrafico.findViewById(R.id.listaHistorial);
        ArrayAdapter<String> adapter= new ArrayAdapter<String>(this,R.layout.simple_list_item_1,datosAlmacenados);

        listas.setAdapter(adapter);
       // adapter.notifyDataSetChanged();

        listas.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                //Toast.makeText(getApplicationContext(),""+position, Toast.LENGTH_LONG).show();
                cargarGraficoHistoria(position);
            }
        });
    }

    public void cargarGraficoHistoria(int posicion){
    /*segun la posicion lo buscas en la lista publica.*/
         Intent act = new Intent(this, GraficoActivity.class);
        ArrayList<String> datosAlmacenados = dataBase.getGraficos();
        String aux= datosAlmacenados.get(posicion);
        String[] datitos=aux.split(":");
        datosAlmacenados=new ArrayList<String>();
        for(int i =0; i< datitos.length;i++){
            datosAlmacenados.add(datitos[i]);
        }
        act.putStringArrayListExtra("listaString",datosAlmacenados);
        startActivity(act);

    }

}