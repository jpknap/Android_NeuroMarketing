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

    IntentFilter filter;
    private PendingIntent pendingIntent;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.notificacion);

    }
    public void comenzarTimer(View view) {
        AlarmManager manager = (AlarmManager) getSystemService(Context.ALARM_SERVICE);
        Intent alarmIntent = new Intent(NotificationReceiverActivity.this,Alarm.class);
        pendingIntent = PendingIntent.getBroadcast(NotificationReceiverActivity.this, 0, alarmIntent, 0);


        manager.set(AlarmManager.RTC_WAKEUP, SystemClock.elapsedRealtime()+ (1000 * 2), pendingIntent);
        Toast.makeText(this, "Alarm Set", Toast.LENGTH_SHORT).show();

        findViewById(R.id.botonaceptar).setVisibility(View.INVISIBLE);
        findViewById(R.id.botonrechazar).setVisibility(View.INVISIBLE);
        TextView tx=(TextView)findViewById(R.id.texto);
        tx.setText("Le notificaremos cuando sea hora de volver a estudiar");

    }

}