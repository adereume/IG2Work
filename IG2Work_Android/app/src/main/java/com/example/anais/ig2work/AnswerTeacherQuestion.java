package com.example.anais.ig2work;

import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.support.design.widget.TextInputLayout;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import com.example.anais.ig2work.DataBase.RequestActivity;
import com.example.anais.ig2work.Utils.RestActivity;
import com.example.anais.ig2work.Utils.StringUtils;

import org.json.JSONObject;

/**
 * La classe AnswerTeacherQuestion gère l'activité qui permet à un étudiant de répondre
 * à une question au sein d'une séance
 */
public class AnswerTeacherQuestion extends RestActivity {
    private TextInputLayout answer;

    private int idQuestion;
    private int idUser;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_answer_teacher_question);

        // Affichage de la flèche de retour
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(AnswerTeacherQuestion.this);

        setTitle("Répondre");

        // Récupére l'ID de l'utilisateur courant
        idUser = preferences.getInt(StringUtils.IDUSER.toString(), 0);
        idQuestion = this.getIntent().getExtras().getInt("idQuestion");

        answer = (TextInputLayout) findViewById(R.id.answer);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        switch (id) {
            case R.id.menu_validate:
                checkAnswer();
                break;

            case android.R.id.home:
                this.finish(); // Retour à la vue de la question
                break;
        }

        return super.onOptionsItemSelected(item);
    }

    /*
    Vérifie la réponse avant de l'ajouter
     */
    public void checkAnswer() {
        // Réinitialisation des erreurs
        answer.setError(null);

        // Stockage des valeurs au moment de la tentative d'ajout
        String intitule = answer.getEditText().getText().toString();

        boolean cancel = false;
        View focusView = null;

        // Vérification des champs
        if (TextUtils.isEmpty(intitule)) {
            answer.setError(getString(R.string.error_field_required));
            focusView = answer;
            cancel = true;
        }

        if (cancel) {
            focusView.requestFocus();
        } else {
            setAnswer(intitule);
        }
    }

    /*
    Ajoute la réponse de l'étudiant
     */
    public void setAnswer(String answer) {
        new RequestActivity() {
            @Override
            public void traiteReponse(JSONObject o, String action) {
                if(!o.isNull("retour")) {
                    AnswerTeacherQuestion.this.finish(); // Retour à la page précédente
                } else {
                    Toast.makeText(AnswerTeacherQuestion.this, "La réponse n'a pas pu être ajouté", Toast.LENGTH_SHORT).show();
                }
            }
        }.envoiRequete("answerQuestion", "action=answerQuestion&idQuestion=" + idQuestion+"&idUser="+idUser+"&answer="+answer);
    }

}
