package com.example.anais.ig2work.Utils;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;

import com.example.anais.ig2work.DataBase.RequestActivity;
import com.example.anais.ig2work.GlobalState;
import com.example.anais.ig2work.LoginActivity;
import com.example.anais.ig2work.R;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by clementruffin on 27/01/2017.
 */

public abstract class RestActivity extends AppCompatActivity {
    protected GlobalState gs;
    private FinishAllReceiver fa;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Instanciation de la classe GlobalState
        gs = (GlobalState) getApplication();

        // Instanciation de la classe FinishAllReceiver
        fa = new FinishAllReceiver(this);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

        // Fermeture de l'activité courante
        fa.unRegisterFinishAllReceiver();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        switch (id) {

            // Menu 'Se déconnecter'
            case R.id.action_logout:
                logoutUser();
                return true;

            // Menu 'Fermer'
            case R.id.action_exit:
                fa.closeAllActivities();
                return true;
        }

        return super.onOptionsItemSelected(item);
    }

    public void logoutUser() {
        new RequestActivity() {
            @Override
            public void traiteReponse(JSONObject o, String action) {

                try {
                    String connecte = o.getString("connecte");

                    if (connecte == "false") {

                        // Lors de la déconnexion, pour éviter que la page de Login ne relance une authentification,
                        // on modifie la valeur de ATTEMPT_CONNEXION à 'false'
                        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(RestActivity.this);
                        SharedPreferences.Editor editor = preferences.edit();
                        editor.putString(StringUtils.ATTEMPT_CONNEXION.toString(), "false");
                        editor.apply();

                        // Lancement de la page Login
                        Intent intent = new Intent(RestActivity.this, LoginActivity.class);
                        RestActivity.this.startActivity(intent);
                        RestActivity.this.finish();
                    }

                } catch (JSONException e) {
                    e.printStackTrace();
                }


            }
        }.envoiRequete("logout", "action=logout");
    }
}
