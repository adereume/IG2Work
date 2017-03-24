package com.example.anais.ig2work.DataBase;

import android.app.Activity;
import android.app.ActivityManager;
import android.content.ComponentName;
import android.content.Context;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;

import org.json.JSONObject;

import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

/**
 * Created by Anais on 24/01/2016.
 */

public abstract class RequestActivity extends AppCompatActivity {

    public void envoiRequete(String action, String paramPost) {

        ExecuteRequest req = new ExecuteRequest(this);
        req.execute(paramPost, action);
    }

    public abstract void traiteReponse(JSONObject o, String action);
    // devra être implémenté dans la classe fille

    public void requetePeriodique(final Activity act, int periode, final String action, final String url) {

        TimerTask doAsynchronousTask;
        final Handler handler = new Handler();
        final Timer timer = new Timer();

        doAsynchronousTask = new TimerTask() {
            @Override
            public void run() {
                ActivityManager am = (ActivityManager) act.getApplicationContext().getSystemService(Context.ACTIVITY_SERVICE);
                List<ActivityManager.RunningTaskInfo> taskInfo = am.getRunningTasks(1);
                ComponentName componentInfo = taskInfo.get(0).topActivity;

                if(componentInfo.getShortClassName().substring(1).equals(act.getLocalClassName()))
                    handler.post(new Runnable() {
                    public void run() {
                        envoiRequete(action, url);
                    }
                });
                else {
                    timer.cancel();
                }
            }
        };

        timer.schedule(doAsynchronousTask, 0, 1000 * periode);
    }

}
