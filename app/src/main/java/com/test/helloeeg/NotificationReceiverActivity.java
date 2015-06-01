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

import java.util.Calendar;

/**
 * Created by Felipe on 22/04/2015.
 */

public class NotificationReceiverActivity extends Activity  {

    IntentFilter filter;
    private PendingIntent pendingIntent;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.notificacion);

        Intent alarmIntent = new Intent(NotificationReceiverActivity.this, AlarmReceiver.class);
        pendingIntent = PendingIntent.getBroadcast(NotificationReceiverActivity.this, 0, alarmIntent, PendingIntent.FLAG_CANCEL_CURRENT );

    }
    public void comenzarTimer(View view) {
        AlarmManager manager = (AlarmManager) getSystemService(Context.ALARM_SERVICE);

        manager.set(AlarmManager.RTC_WAKEUP, System.currentTimeMillis()+30*1000, pendingIntent);
        Toast.makeText(this, "Alarm Set", Toast.LENGTH_SHORT).show();

        findViewById(R.id.botonaceptar).setVisibility(View.INVISIBLE);
        findViewById(R.id.botonrechazar).setVisibility(View.INVISIBLE);
        TextView tx=(TextView)findViewById(R.id.texto);
        tx.setText("Le notificaremos cuando sea hora de volver a estudiar");

    }

}