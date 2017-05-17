package com.example.anais.ig2work;

import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.support.design.widget.TextInputLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import com.example.anais.ig2work.DataBase.RequestActivity;
import com.example.anais.ig2work.Utils.StringUtils;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

/**
 * La classe AddNoteActivity gère l'activité d'ajout & d'édition d'une note.
 */
public class AddNoteActivity extends AppCompatActivity {
    private SharedPreferences preferences;

    private TextInputLayout mNoteView;

    private int idNote = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_note);

        // Affichage de la flèche de retour
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        preferences = PreferenceManager.getDefaultSharedPreferences(this);

        mNoteView = (TextInputLayout) findViewById(R.id.note);

        // Si on a une note en paramètre, on se place en mode Edition
        if(this.getIntent().getExtras() != null) {
            setTitle("Edit Note");

            idNote = this.getIntent().getExtras().getInt("idNote");
            getNote();
        } else {
            setTitle("Ajout Note");
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
            case android.R.id.home: // Retour à la page de séance
                this.finish();
                break;

        }

        return super.onOptionsItemSelected(item);
    }

    /*
    Récupération du contenu de la note
     */
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

    /*
    Tentative d'ajout/mise à jour de la note
     */
    private void attemptAddNote() {
        // Réinitialisation des erreurs
        mNoteView.setError(null);

        // Stockage des valeurs au moment de la tentative d'ajout/mise à jour
        String note = mNoteView.getEditText().getText().toString();

        boolean cancel = false;
        View focusView = null;

        // Vérification des champs
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

    /*
    Ajout de la note
     */
    public void addNote(final String description) {
        new RequestActivity() {
            @Override
            public void traiteReponse(JSONObject json_data, String action) {
                if(!json_data.isNull("retour")) {
                    AddNoteActivity.this.finish(); // Retour à la page précédente
                }
            }
        }.envoiRequete("addNote", "action=addNote&idSeance="+preferences.getInt("idSeance", 0)+"&idUser="+preferences.getInt(StringUtils.IDUSER.toString(), 0)+"&description="+description);
    }

    /*
    Mise à jour de la note
     */
    public void updateNote(final String description) {
        new RequestActivity() {
            @Override
            public void traiteReponse(JSONObject json_data, String action) {
                if(!json_data.isNull("retour")) {
                    AddNoteActivity.this.finish(); // Retour à la page précédente
                }
            }
        }.envoiRequete("updateNote", "action=updateNote&idNote="+idNote+"&idUser="+preferences.getInt(StringUtils.IDUSER.toString(), 0)+"&description="+description);
    }
}
