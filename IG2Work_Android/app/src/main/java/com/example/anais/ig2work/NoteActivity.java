package com.example.anais.ig2work;

import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.CheckBox;
import android.widget.TextView;

import com.example.anais.ig2work.DataBase.RequestActivity;
import com.example.anais.ig2work.Model.Homework;
import com.example.anais.ig2work.Model.Note;
import com.example.anais.ig2work.Utils.RestActivity;
import com.example.anais.ig2work.Utils.StringUtils;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Locale;

public class NoteActivity extends RestActivity {

    private SharedPreferences preferences;
    private TextView noteTextView;

    private int idUser;
    private int idNote;
    private Note noteObject;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_note);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        preferences = PreferenceManager.getDefaultSharedPreferences(NoteActivity.this);
        idUser = preferences.getInt(StringUtils.IDUSER.toString(), 0);

        noteTextView = (TextView) findViewById(R.id.note);

        idNote = this.getIntent().getExtras().getInt("idNote");
        getNote();
    }

    @Override
    protected void onStart() {
        super.onStart();
        getNote();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        switch (id) {
            case R.id.menu_edit_note:
                Intent intent = new Intent();
                intent = new Intent(NoteActivity.this, AddNoteActivity.class);
                intent.putExtra("idNote", idNote);
                startActivity(intent);
                return true;

            case R.id.menu_delete_note:
                deleteNote();
                return true;

            case android.R.id.home:
                this.finish();
                break;
        }

        return super.onOptionsItemSelected(item);
    }

    public void getNote() {
        new RequestActivity() {
            @Override
            public void traiteReponse(JSONObject o, String action) {

                try {
                    SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.FRANCE);

                    JSONArray info = o.getJSONArray("note");
                    JSONObject note = info.getJSONObject(0);

                    int id = note.getInt("id");
                    String description = note.getString("description").replace("<br />", "");

                    Boolean isPrivate = false;
                    if (!note.isNull("isPrivate")) {
                        isPrivate = note.getString("isPrivate").equals("1") ? true : false;
                    }
                    noteObject = new Note(id, description, isPrivate);

                    noteTextView.setText(noteObject.getDescription());

                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }.envoiRequete("getNoteById", "action=getNoteById&idNote=" + idNote);
    }

    public void deleteNote() {
        new AlertDialog.Builder(this)
                .setTitle("Supprimer la note")
                .setMessage("Etes-vous sur de vouloir supprimer cette note ?")
                .setNegativeButton("Non", null)
                .setPositiveButton("Oui", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        //Supprimer la note
                        new RequestActivity() {
                            @Override
                            public void traiteReponse(JSONObject o, String action) {
                                if (!o.isNull("retour"))
                                    NoteActivity.this.finish();
                            }
                        }.envoiRequete("deleteNote", "action=deleteNote&idUser=" + idUser + "&idNote=" + idNote);
                    }
                }).show();
    }
}
