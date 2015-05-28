package com.test.helloeeg;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.media.Ringtone;
import android.media.RingtoneManager;
import android.net.Uri;
import android.widget.Toast;

public class AlarmReceiver extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {

        // For our recurring task, we'll just display a message

        Intent notificationIntent = new Intent(context,NotificacionAlarma.class);
        notificationIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        context.startActivity(notificationIntent);




        // Toast.makeText(context, "NOTIFICACION", Toast.LENGTH_SHORT).show();

    }
}