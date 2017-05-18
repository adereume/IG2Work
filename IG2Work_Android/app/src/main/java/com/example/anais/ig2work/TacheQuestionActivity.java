package com.example.anais.ig2work;

import android.support.design.widget.TextInputLayout;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.MenuItem;
import android.view.View;

import com.example.anais.ig2work.DataBase.RequestActivity;
import com.example.anais.ig2work.Utils.RestActivity;

import org.json.JSONObject;

public class TacheQuestionActivity extends RestActivity {
    public int idTask;

    public TextInputLayout question;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tache_question);

        setTitle("Ajouter une question");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        question = (TextInputLayout) findViewById(R.id.question);
    }

    @Override
    protected void onStart() {
        super.onStart();

        idTask = this.getIntent().getExtras().getInt("idTask");
    }

    public void checkTache() {
        // Reset errors.
        question.setError(null);

        // Store values at the time of the login attempt.
        String intitule = question.getEditText().getText().toString();

        boolean cancel = false;
        View focusView = null;

        // Vérifier si les champs sont remplie
        if (TextUtils.isEmpty(intitule)) {
            question.setError(getString(R.string.error_field_required));
            focusView = question;
            cancel = true;
        }

        if (cancel) {
            focusView.requestFocus();
        } else {
            addTache(intitule);
        }
    }

    public void addTache(String intitule) {
        new RequestActivity() {
            @Override
            public void traiteReponse(JSONObject o, String action) {
                if(!o.isNull("retour")) {
                    TacheQuestionActivity.this.finish();
                }
            }
        }.envoiRequete("getTacheById", "action=addTacheQuestion&idTache=" + idTask+"&question="+intitule);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        switch (id) {
            case R.id.menu_validate:
                //Ajoute la question
                checkTache();
                break;

            case android.R.id.home:
                this.finish();
                break;
        }

        return super.onOptionsItemSelected(item);
    }
}
