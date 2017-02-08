package com.example.anais.ig2work.DataBase;

import android.os.Bundle;
import android.os.Handler;
import android.os.PersistableBundle;
import android.support.v7.app.AppCompatActivity;

import org.json.JSONObject;

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

    public void requetePeriodique(int periode, final String action, final String url) {

        TimerTask doAsynchronousTask;
        final Handler handler = new Handler();
        Timer timer = new Timer();

        doAsynchronousTask = new TimerTask() {
            @Override
            public void run() {
                handler.post(new Runnable() {
                    public void run() {
                        envoiRequete(action, url);
                    }
                });
            }
        };

        timer.schedule(doAsynchronousTask, 0, 1000 * periode);
    }
}
