package com.example.anais.ig2work.DataBase;

import android.support.v7.app.AppCompatActivity;

import org.json.JSONArray;

/**
 * Created by Utilisateur on 24/11/2016.
 */

public abstract class RequestActivity extends AppCompatActivity {

    public void envoiRequete(String action, String paramPost) {

        ExecuteRequest req = new ExecuteRequest(this);
        req.execute(paramPost, action);
    }

    public abstract void traiteReponse(JSONArray o, String action);
    // devra être implémenté dans la classe fille
}
