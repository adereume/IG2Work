package com.example.anais.ig2work;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Looper;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import com.example.anais.ig2work.DataBase.RequestActivity;
import com.example.anais.ig2work.Utils.StringUtils;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

/**
 * La classe SplashScreen gère l'activité d'introduction à l'ouverture de l'application
 */
public class SplashScreen extends AppCompatActivity {
    private SharedPreferences preferences;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash_screen);

        Thread myThread = new Thread(){
            @Override
            public void run() {
                Looper.prepare();
                try {
                    Thread.sleep(3000); // Affichage 3 secondes

                    SplashScreen.this.preferences = PreferenceManager.getDefaultSharedPreferences(SplashScreen.this);

                    String firstname = SplashScreen.this.preferences.getString(StringUtils.FIRSTNAME.toString(), null);
                    String lastname = SplashScreen.this.preferences.getString(StringUtils.LASTNAME.toString(), null);
                    String lastPwd = SplashScreen.this.preferences.getString(StringUtils.PASSWORD.toString(), null);
                    String attempt = SplashScreen.this.preferences.getString(StringUtils.ATTEMPT_CONNEXION.toString(), null);

                    // Si des identifiants sont sauvegardés, on lance une tentative de connexion
                    if(firstname != null && lastname != null && lastPwd != null && attempt == null) {
                        SplashScreen.this.userLogin(firstname, lastname, lastPwd);
                    } else {
                        // Sinon l'utilisateur est redirigé vers l'écran de connexion
                        Intent intent = new Intent(SplashScreen.this, LoginActivity.class);
                        SplashScreen.this.startActivity(intent);
                        SplashScreen.this.finish();
                    }
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }

                Looper.loop();
            }
        };

        myThread.start();
    }

    @Override
    protected void onStart() {
        super.onStart();
    }

    /*
    Connexion de l'utilisateur
     */
    protected void userLogin(final String firstname, final String lastname, final String password) {
        new RequestActivity() {
            @Override
            public void traiteReponse(JSONObject o, String action) {

                try {
                    if(o.isNull("retour")) {
                        Intent intent = new Intent(SplashScreen.this, LoginActivity.class);
                        SplashScreen.this.startActivity(intent);
                        SplashScreen.this.finish(); // Redirection vers l'écran de login
                        return;
                    }

                    JSONArray json = o.getJSONArray("retour");
                    JSONObject retour = json.getJSONObject(0);

                    String role = retour.getString("role");
                    int idUser = retour.getInt("id");

                    // Enregistrement des identifiants dans les préférences
                    SharedPreferences.Editor editor = SplashScreen.this.preferences.edit();
                    editor.putInt(StringUtils.IDUSER.toString(), idUser);
                    editor.putString(StringUtils.FIRSTNAME.toString(), firstname);
                    editor.putString(StringUtils.LASTNAME.toString(), lastname);
                    editor.putString(StringUtils.PASSWORD.toString(), password);
                    editor.putString(StringUtils.ROLE.toString(), role);
                    editor.putString(StringUtils.ATTEMPT_CONNEXION.toString(), null);
                    editor.apply();

                    Intent intent = new Intent(SplashScreen.this, HomeActivity.class);
                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                    SplashScreen.this.startActivity(intent);
                    SplashScreen.this.finish();

                } catch (JSONException e) {
                    e.printStackTrace();
                    Intent intent = new Intent(SplashScreen.this, LoginActivity.class);
                    SplashScreen.this.startActivity(intent);
                    SplashScreen.this.finish();
                }
            }
        }.envoiRequete("login", "action=connexion&firstname="+firstname+"&lastname="+lastname+"&password="+password);
    }
}
