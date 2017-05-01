package com.example.anais.ig2work;

import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.support.design.widget.TextInputLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import com.example.anais.ig2work.DataBase.RequestActivity;
import com.example.anais.ig2work.Utils.RestActivity;
import com.example.anais.ig2work.Utils.StringUtils;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class AnswerTeacherQuestion extends RestActivity {
    private TextInputLayout answer;
    private int idQuestion;
    private int idUser;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_answer_teacher_question);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        setTitle("Répondre");

        idQuestion = this.getIntent().getExtras().getInt("idQuestion");
        //Récupére l'id de l'utilisateur en cours
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(AnswerTeacherQuestion.this);
        idUser = preferences.getInt(StringUtils.IDUSER.toString(), 0);

        answer = (TextInputLayout) findViewById(R.id.answer);
    }

    public void checkAnswer() {
        // Reset errors.
        answer.setError(null);

        // Store values at the time of the login attempt.
        String intitule = answer.getEditText().getText().toString();

        boolean cancel = false;
        View focusView = null;

        // Vérifier si les champs sont remplie
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

    public void setAnswer(String answer) {
        //Ajouter la réponse:
        new RequestActivity() {
            @Override
            public void traiteReponse(JSONObject o, String action) {
                if(!o.isNull("retour")) {
                    AnswerTeacherQuestion.this.finish();
                } else {
                    Toast.makeText(AnswerTeacherQuestion.this, "La réponse n'a pas pu être ajouté", Toast.LENGTH_SHORT).show();
                }
            }
        }.envoiRequete("answerQuestion", "action=answerQuestion&idQuestion=" + idQuestion+"&idUser="+idUser+"&answer="+answer);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        switch (id) {
            case R.id.menu_validate:
                checkAnswer();
                break;

            case android.R.id.home:
                this.finish();
                break;
        }

        return super.onOptionsItemSelected(item);
    }
}
