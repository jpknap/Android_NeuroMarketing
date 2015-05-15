package com.test.helloeeg;

import android.app.Activity;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.bluetooth.BluetoothAdapter;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;
import com.neurosky.thinkgear.*;

import java.util.ArrayList;

public class HelloEEGActivity extends Activity {
	BluetoothAdapter bluetoothAdapter;
    CircularProgressBar c1;
	ArrayList<Integer> meditacion= new ArrayList<Integer>();

	TextView tv;
	Button b;
	boolean aviso=false;
    boolean calidad=false;
	TGDevice tgDevice;
	final boolean rawEnabled = false;

    public void medirEstress(){
        int estress=0;
        int porcentaje=0;
        for(int i =0; i<meditacion.size();i++){
            if(meditacion.get(i)<40)
                estress++;
            tv.append("datos "+meditacion.get(i)+"\n");
        }

        float estressFinal=estress*100f/meditacion.size();
        porcentaje=100-(int)estressFinal;
       // tv.append("porcentaje: "+porcentaje+"\n");
        c1.setProgress(porcentaje);
        c1.setTitle(porcentaje+"%");
        // c1.refreshDrawableState();
        if(estressFinal>=25){
            View vista= getWindow().getDecorView();

            if(!aviso) {
                createNotification(vista);
                aviso=true;//comenzar el timer propuesto
                return;
            }
        }
        return;

    }
    public void createNotification(View view) {//(View view)
        // Prepare intent which is triggered if the
        // notification is selected
        Intent intent = new Intent(this, NotificationReceiverActivity.class);
        PendingIntent pIntent = PendingIntent.getActivity(this, 0, intent, 0);

        // Build notification
        // Actions are just fake
        Notification noti = new Notification.Builder(this)
                .setContentTitle("Te recomendamos dejar de estudiar por 5 min")
                .setContentText("Subject").setSmallIcon(R.drawable.icon)
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
    public void comenzarLectura(View view) {
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



        tv.append("Android version: " + Integer.valueOf(android.os.Build.VERSION.SDK) + "\n" );
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
    // hola felipe este es un comentario
    
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
        if(tgDevice.getState() != TGDevice.STATE_CONNECTING && tgDevice.getState() != TGDevice.STATE_CONNECTED)
            tgDevice.connect(rawEnabled);
        //tgDevice.ena
    }
    public void llamarGrafico(View view) {
        Intent act = new Intent(this, GraficoActivity.class);
        startActivity(act);
        //tgDevice.ena
    }
}