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

import org.json.JSONException;
import org.json.JSONObject;

public class AddQuestionActivity extends AppCompatActivity {
    private SharedPreferences preferences;

    private TextInputLayout mDescriptionView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_question);
        setTitle("Ajout Question");
        //Le bouton retour à gauche de la barre d'action
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        preferences = PreferenceManager.getDefaultSharedPreferences(AddQuestionActivity.this);

        mDescriptionView = (TextInputLayout) findViewById(R.id.description);
    }

    private void attemptAddQuestion() {
        // Reset errors.
        mDescriptionView.setError(null);

        // Store values at the time of the login attempt.
        String description = mDescriptionView.getEditText().getText().toString();

        boolean cancel = false;
        View focusView = null;

        // Vérifier si les champs sont remplie
        if (TextUtils.isEmpty(description)) {
            mDescriptionView.setError(getString(R.string.error_field_required));
            focusView = mDescriptionView;
            cancel = true;
        }

        if (cancel) {
            focusView.requestFocus();
        } else {
            addQuestion(description);
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

    public void addQuestion(final String description) {
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
        }.envoiRequete("login", "action=addQuestion&idSeance="+preferences.getInt("idSeance", 0)+"&description="+description);
    }
}
