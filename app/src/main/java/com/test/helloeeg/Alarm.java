package com.test.helloeeg;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.PowerManager;
import android.os.SystemClock;
import android.widget.Toast;


/**
 * Created by Felipe on 19/05/2015.
 */
public class Alarm extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent){
        Toast toast = new Toast(context);
        toast.setText("hola");
        toast.show();
    }
   /* public void SetAlarm(Context context) {
        AlarmManager am =( AlarmManager)context.getSystemService(Context.ALARM_SERVICE);
        Intent i = new Intent(context, Alarm.this.getClass());
        PendingIntent pi = PendingIntent.getBroadcast(context, 0, i, 0);
        am.set(AlarmManager.RTC_WAKEUP, SystemClock.elapsedRealtime()+ (1000 * 2), pi);

    }*/

}
