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
import com.example.anais.ig2work.Model.Note;
import com.example.anais.ig2work.Utils.StringUtils;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class AddNoteActivity extends AppCompatActivity {
    private SharedPreferences preferences;

    private TextInputLayout mNoteView;
    private int idNote = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_note);
        //Le bouton retour à gauche de la barre d'action
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        preferences = PreferenceManager.getDefaultSharedPreferences(this);

        mNoteView = (TextInputLayout) findViewById(R.id.note);

        if(this.getIntent().getExtras() != null) {
            setTitle("Edit Note");

            idNote = this.getIntent().getExtras().getInt("idNote");
            getNote();
        } else {
            setTitle("Ajout Note");
        }
    }

    public void getNote() {
        new RequestActivity() {
            @Override
            public void traiteReponse(JSONObject o, String action) {
                try {
                    JSONArray retour = o.getJSONArray("note");
                    JSONObject note = retour.getJSONObject(0);

                    mNoteView.getEditText().setText(note.getString("description").replace("<br />", ""));
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }.envoiRequete("getNoteById", "action=getNoteById&idNote=" + idNote);
    }

    private void attemptAddNote() {
        mNoteView.setError(null);

        // Store values at the time of the login attempt.
        String note = mNoteView.getEditText().getText().toString();

        boolean cancel = false;
        View focusView = null;

        // Vérifier si les champs sont remplie
        if (TextUtils.isEmpty(note)) {
            mNoteView.setError(getString(R.string.error_field_required));
            focusView = mNoteView;
            cancel = true;
        }

        if (cancel) {
            focusView.requestFocus();
        } else {
            if(idNote != 0)
                updateNote(note);
            else
                addNote(note);
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
                attemptAddNote();
                break;
            case android.R.id.home:
                this.finish();
                break;

        }

        return super.onOptionsItemSelected(item);
    }

    public void updateNote(final String description) {
        new RequestActivity() {
            @Override
            public void traiteReponse(JSONObject json_data, String action) {
                if(!json_data.isNull("retour")) {
                    AddNoteActivity.this.finish();
                }
            }
        }.envoiRequete("updateNote", "action=updateNote&idNote="+idNote+"&idUser="+preferences.getInt(StringUtils.IDUSER.toString(), 0)+"&description="+description);
    }

    public void addNote(final String description) {
        new RequestActivity() {
            @Override
            public void traiteReponse(JSONObject json_data, String action) {
                if(!json_data.isNull("retour")) {
                    AddNoteActivity.this.finish();
                }
            }
        }.envoiRequete("addNote", "action=addNote&idSeance="+preferences.getInt("idSeance", 0)+"&idUser="+preferences.getInt(StringUtils.IDUSER.toString(), 0)+"&description="+description);
    }
}
