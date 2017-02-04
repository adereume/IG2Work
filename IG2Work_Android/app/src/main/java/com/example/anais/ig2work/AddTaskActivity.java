package com.example.anais.ig2work;

import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.support.design.widget.TextInputLayout;
import android.support.v4.app.NavUtils;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import com.example.anais.ig2work.DataBase.RequestActivity;
import com.example.anais.ig2work.Utils.RestActivity;

import org.json.JSONException;
import org.json.JSONObject;

public class AddTaskActivity extends AppCompatActivity {
    private SharedPreferences preferences;

    private TextInputLayout mTitleView;
    private TextInputLayout mDescriptionView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_task);
        setTitle("Ajout Tache");

        preferences = PreferenceManager.getDefaultSharedPreferences(AddTaskActivity.this);

        mTitleView = (TextInputLayout) findViewById(R.id.titre);
        mDescriptionView = (TextInputLayout) findViewById(R.id.description);

    }

    private void attemptAddTask() {
        // Reset errors.
        mTitleView.setError(null);
        mDescriptionView.setError(null);

        // Store values at the time of the login attempt.
        String title = mTitleView.getEditText().getText().toString();
        String description = mDescriptionView.getEditText().getText().toString();

        boolean cancel = false;
        View focusView = null;

        // Vérifier si les champs sont remplie
        if (TextUtils.isEmpty(title)) {
            mTitleView.setError(getString(R.string.error_field_required));
            focusView = mTitleView;
            cancel = true;
        }
        if (TextUtils.isEmpty(description)) {
            mDescriptionView.setError(getString(R.string.error_field_required));
            focusView = mDescriptionView;
            cancel = true;
        }

        if (cancel) {
            focusView.requestFocus();
        } else {
            addTask(title, description);
        }
    }

    @Override
    protected void onStart() {
        super.onStart();
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        //getActionBar().setDisplayHomeAsUpEnabled(true);
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
                attemptAddTask();
                break;
            case android.R.id.home:
                this.finish();
                break;

        }

        return super.onOptionsItemSelected(item);
    }

    public void addTask(final String title, final String description) {
        new RequestActivity() {
            @Override
            public void traiteReponse(JSONObject json_data, String action) {
                try {
                    if(!json_data.isNull("retour")) {
                        json_data.getString("retour");
                        AddTaskActivity.this.finish();
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                    Toast.makeText(AddTaskActivity.this, "Une erreur est survenu", Toast.LENGTH_LONG).show();
                }
            }
        }.envoiRequete("login", "action=addTache&idSeance="+preferences.getInt("idSeance", 0)+"&titre="+title+"&description="+description);
    }
}
