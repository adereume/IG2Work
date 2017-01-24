package com.example.anais.ig2work.DataBase;

import android.support.v7.app.AppCompatActivity;

import org.json.JSONObject;

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
}
