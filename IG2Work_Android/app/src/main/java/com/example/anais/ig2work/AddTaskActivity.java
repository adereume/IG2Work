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
import com.example.anais.ig2work.Model.QuestionFromStudent;
import com.example.anais.ig2work.Model.QuestionFromStudentAdapter;
import com.example.anais.ig2work.Model.Task;
import com.example.anais.ig2work.Utils.RestActivity;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class AddTaskActivity extends AppCompatActivity {
    private SharedPreferences preferences;

    private TextInputLayout mTitleView;
    private TextInputLayout mDescriptionView;

    private int idTask = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_task);
        //Le bouton retour à gauche de la barre d'action
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        preferences = PreferenceManager.getDefaultSharedPreferences(AddTaskActivity.this);

        mTitleView = (TextInputLayout) findViewById(R.id.titre);
        mDescriptionView = (TextInputLayout) findViewById(R.id.description);

        if(this.getIntent().getExtras() != null) {
            setTitle("Edit Tache");

            idTask = this.getIntent().getExtras().getInt("idTask");
            getTache();
        } else {
            setTitle("Ajout Tache");
        }
    }

    public void getTache() {
        new RequestActivity() {
            @Override
            public void traiteReponse(JSONObject o, String action) {
                try {
                    JSONArray retour = o.getJSONArray("tache");
                    JSONObject task = retour.getJSONObject(0);

                    mTitleView.getEditText().setText(task.getString("titre"));
                    mDescriptionView.getEditText().setText(task.getString("description").replace("<br />", ""));
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }.envoiRequete("getTacheById", "action=getTacheById&idTache=" + idTask);
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
            if(idTask != 0)
                updateTask(title, description);
            else
                addTask(title, description);
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
                attemptAddTask();
                break;
            case android.R.id.home:
                this.finish();
                break;

        }

        return super.onOptionsItemSelected(item);
    }

    public void updateTask(final String title, final String description) {
        new RequestActivity() {
            @Override
            public void traiteReponse(JSONObject json_data, String action) {
                if(!json_data.isNull("retour")) {
                    AddTaskActivity.this.finish();
                }
            }
        }.envoiRequete("updateTache", "action=updateTache&idTache="+idTask+"&titre="+title+"&description="+description);
    }

    public void addTask(final String title, final String description) {
        new RequestActivity() {
            @Override
            public void traiteReponse(JSONObject json_data, String action) {
                if(!json_data.isNull("retour")) {
                    AddTaskActivity.this.finish();
                }
            }
        }.envoiRequete("addTache", "action=addTache&idSeance="+preferences.getInt("idSeance", 0)+"&titre="+title+"&description="+description);
    }
}
