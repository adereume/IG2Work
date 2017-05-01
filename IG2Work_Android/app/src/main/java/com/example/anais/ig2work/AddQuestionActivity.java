package com.example.anais.ig2work;

import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.support.design.widget.TextInputLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import com.example.anais.ig2work.DataBase.RequestActivity;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;

public class AddQuestionActivity extends AppCompatActivity {
    private SharedPreferences preferences;

    private TextInputLayout mDescriptionView;
    private TextInputLayout mCorrectionView;

    private int idQuestion = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_question);
        //Le bouton retour à gauche de la barre d'action
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        preferences = PreferenceManager.getDefaultSharedPreferences(AddQuestionActivity.this);

        mDescriptionView = (TextInputLayout) findViewById(R.id.description);
        mCorrectionView = (TextInputLayout) findViewById(R.id.correction);

        if(this.getIntent().getExtras() != null) {
            setTitle("Edit Question");

            idQuestion = this.getIntent().getExtras().getInt("idQuestion");
            getQuestion();
        } else {
            setTitle("Ajout Question");
        }
    }

    public void getQuestion() {
        new RequestActivity() {
            @Override
            public void traiteReponse(JSONObject o, String action) {
                try {
                    JSONArray retour = o.getJSONArray("question");
                    JSONObject question = retour.getJSONObject(0);

                    mDescriptionView.getEditText().setText(question.getString("description").replace("<br />", ""));
                    mCorrectionView.getEditText().setText(question.getString("answer"));
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }.envoiRequete("getQuestionById", "action=getQuestionById&idQuestion=" + idQuestion);
    }

    private void attemptAddQuestion() {
        // Reset errors.
        mDescriptionView.setError(null);
        mCorrectionView.setError(null);

        // Store values at the time of the login attempt.
        String description = mDescriptionView.getEditText().getText().toString();
        String correct = mCorrectionView.getEditText().getText().toString();

        boolean cancel = false;
        View focusView = null;

        // Vérifier si les champs sont remplie
        if (TextUtils.isEmpty(description)) {
            mDescriptionView.setError(getString(R.string.error_field_required));
            focusView = mDescriptionView;
            cancel = true;
        }
        if (TextUtils.isEmpty(correct)) {
            mCorrectionView.setError(getString(R.string.error_field_required));
            focusView = mCorrectionView;
            cancel = true;
        }

        if (cancel) {
            focusView.requestFocus();
        } else {
            if(idQuestion == 0)
                addQuestion(description, correct);
            else
                updateQuestion(description, correct);
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_ajout, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.menu_add:
                Log.d("action", "Ajouter");
                attemptAddQuestion();
                break;
            case android.R.id.home:
                this.finish();
                break;

        }

        return super.onOptionsItemSelected(item);
    }

    public void addQuestion(String description, String correct) {
        try {
            description = URLEncoder.encode(description, "utf-8");
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }

        new RequestActivity() {
            @Override
            public void traiteReponse(JSONObject json_data, String action) {
                try {
                    if(!json_data.isNull("retour")) {
                        json_data.getString("retour");
                        AddQuestionActivity.this.finish();
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                    Toast.makeText(AddQuestionActivity.this, "Une erreur est survenu", Toast.LENGTH_LONG).show();
                }
            }
        }.envoiRequete("addQuestion", "action=addQuestion&idSeance="+preferences.getInt("idSeance", 0)+"&description="+description+"&correct="+correct);
    }

    public void updateQuestion(String description, String correct) {

        new RequestActivity() {
            @Override
            public void traiteReponse(JSONObject json_data, String action) {
                try {
                    Log.e("Retour", json_data.toString());
                    if(!json_data.isNull("retour")) {
                        json_data.getString("retour");
                        AddQuestionActivity.this.finish();
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                    Toast.makeText(AddQuestionActivity.this, "Une erreur est survenu", Toast.LENGTH_LONG).show();
                }
            }
        }.envoiRequete("updateQuestion", "action=updateQuestion&idQuestion="+idQuestion+"&description="+description+"&correct="+correct);
    }
}
