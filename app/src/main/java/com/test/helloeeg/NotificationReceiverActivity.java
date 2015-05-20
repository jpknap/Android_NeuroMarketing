package com.test.helloeeg;

import android.app.Activity;
import android.app.AlarmManager;
import android.content.IntentFilter;
import android.app.Notification;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Intent;
import android.os.Bundle;
import android.os.SystemClock;
import android.view.View;
import android.content.Context;
import android.widget.TextView;
import android.widget.Toast;

/**
 * Created by Felipe on 22/04/2015.
 */

public class NotificationReceiverActivity extends Activity  {
    private Alarm alarma;
    IntentFilter filter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.notificacion);

    }
    public void comenzarTimer(View view) {
        alarma =new Alarm();
        alarma.comenzarAlarma(this,1);
        findViewById(R.id.botonaceptar).setVisibility(View.INVISIBLE);
        findViewById(R.id.botonrechazar).setVisibility(View.INVISIBLE);
        TextView tx=(TextView)findViewById(R.id.texto);
        tx.setText("Le notificaremos cuando sea hora de volver a estudiar");
        return;
    }

}