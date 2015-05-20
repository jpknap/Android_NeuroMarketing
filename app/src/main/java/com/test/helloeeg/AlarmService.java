package com.test.helloeeg;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import java.util.Timer;
import java.util.TimerTask;
import android.app.Notification;
/**
 * Created by Felipe on 19/05/2015.
 */
public class AlarmService extends Service {
    private Alarm alarma;
    private Timer mTimer = null;
    @Override
    public IBinder onBind(Intent arg0) {
        return null;
    }

    @Override
    public void onCreate(){
        super.onCreate();


    }
    /*  @Override
    public int onStartCommand(Intent intent, int flags, int startId)
    {
        alarm.SetAlarm(this);
        return START_STICKY;
    }

   @Override
   public void onStart(Intent intent, int startId)
    {
        alarm.SetAlarm(this);
    }*/

    /*public void ejecutarTarea(int tiempo){
        alarma=new Alarm();
        alarma.comenzarAlarma(this);

    }*/
}
