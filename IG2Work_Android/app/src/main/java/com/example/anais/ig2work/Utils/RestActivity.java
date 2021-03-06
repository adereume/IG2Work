package com.example.anais.ig2work.Utils;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;

import com.example.anais.ig2work.AccountActivity;
import com.example.anais.ig2work.DataBase.RequestActivity;
import com.example.anais.ig2work.GlobalState;
import com.example.anais.ig2work.LoginActivity;
import com.example.anais.ig2work.R;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * La classe RestActivity gère les interactions globales (menus, arrêts d'activités, déconnexion)
 */

public abstract class RestActivity extends AppCompatActivity {
    protected GlobalState gs;
    private SharedPreferences preferences;
    private FinishAllReceiver fa;

    private Menu activeMenu;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Instanciation de la classe GlobalState
        gs = (GlobalState) getApplication();

        // Récupération des préférences
        preferences = PreferenceManager.getDefaultSharedPreferences(RestActivity.this);

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
        activeMenu = menu;

        String role = preferences.getString(StringUtils.ROLE.toString(), "");

        // Affichage du menu en fonction de l'utilisateur et de la vue courante

        switch (this.getClass().getSimpleName()) {
            case "HomeActivity" :
                switch (role) {
                    case "student":
                        getMenuInflater().inflate(R.menu.menu_home_student, menu);
                        break;
                    case "teacher":
                        getMenuInflater().inflate(R.menu.menu_home_teacher, menu);
                        break;
                }
                break;

            case "HomeworkActivity" :
                if (role.equals("teacher")) {
                    getMenuInflater().inflate(R.menu.menu_homework, menu);
                }
                break;

            case "SeanceActivity" :
                if (role.equals("teacher"))
                    getMenuInflater().inflate(R.menu.menu_seance_teacher, menu);
                else
                    getMenuInflater().inflate(R.menu.menu_seance, menu);
                break;

            case "TaskActivity" :
                if (role.equals("teacher"))
                    getMenuInflater().inflate(R.menu.menu_task_teacher, menu);
                else
                    getMenuInflater().inflate(R.menu.menu_task_student, menu);
                break;

            case "QuestionActivity":
                if (role.equals("teacher"))
                    getMenuInflater().inflate(R.menu.menu_question_teacher, menu);
                else
                    getMenuInflater().inflate(R.menu.menu_question_student, menu);
                break;

            case "TacheQuestionActivity":
                getMenuInflater().inflate(R.menu.menu_validate, menu);
                break;

            case "AnswerTeacherQuestion":
                getMenuInflater().inflate(R.menu.menu_validate, menu);
                break;

            case "NoteActivity":
                getMenuInflater().inflate(R.menu.menu_note, menu);
                break;
        }

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        switch (id) {
            // Menu 'Mon compte'
            case R.id.action_account:
                Intent intent = new Intent(RestActivity.this, AccountActivity.class);
                startActivity(intent);
                return true;

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

    public Menu getMenu() {
        return activeMenu;
    }

    /*
    Déconnexion de l'utilisateur
     */
    public void logoutUser() {
        new RequestActivity() {
            @Override
            public void traiteReponse(JSONObject o, String action) {
                try {
                    String connecte = o.getString("connecte");

                    if (connecte.equals("false")) {
                        // Lors de la déconnexion, pour éviter que la page de login ne relance
                        // une authentification, on modifie la valeur de ATTEMPT_CONNEXION à 'false'
                        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(RestActivity.this);
                        SharedPreferences.Editor editor = preferences.edit();
                        editor.putString(StringUtils.ATTEMPT_CONNEXION.toString(), "false");
                        editor.apply();

                        // Retour vers la page de login
                        Intent intent = new Intent(RestActivity.this, LoginActivity.class);
                        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
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
