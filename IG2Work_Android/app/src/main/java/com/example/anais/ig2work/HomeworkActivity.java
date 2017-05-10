package com.example.anais.ig2work;

import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Parcelable;
import android.preference.PreferenceManager;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.TextView;

import com.example.anais.ig2work.DataBase.RequestActivity;
import com.example.anais.ig2work.Model.Homework;
import com.example.anais.ig2work.Utils.RestActivity;
import com.example.anais.ig2work.Utils.StringUtils;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class HomeworkActivity extends RestActivity {

    private SharedPreferences preferences;
    private TextView moduleTextView;
    private TextView titleTextView;
    private TextView descriptionTextView;
    private TextView dueDateTextView;
    private CheckBox state;

    private int idUser;
    private int idHomework;
    private Homework homeworkObject;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_homework);

        setTitle("Devoir");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        preferences = PreferenceManager.getDefaultSharedPreferences(HomeworkActivity.this);
        idUser = preferences.getInt(StringUtils.IDUSER.toString(), 0);

        moduleTextView = (TextView) findViewById(R.id.module);
        titleTextView = (TextView) findViewById(R.id.title);
        descriptionTextView = (TextView) findViewById(R.id.description);
        dueDateTextView = (TextView) findViewById(R.id.dueDate);
        state = (CheckBox) findViewById(R.id.state);

        if (preferences.getString(StringUtils.ROLE.toString(), "").equals("teacher")) {
            state.setVisibility(View.GONE);
        }

        idHomework = this.getIntent().getExtras().getInt("idHomework");
        getHomework();
    }

    @Override
    protected void onStart() {
        super.onStart();
        getHomework();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        switch (id) {
            case R.id.menu_edit_homework:
                Intent intent = new Intent();
                intent = new Intent(HomeworkActivity.this, AddHomework.class);
                intent.putExtra("homeworkObject", homeworkObject);
                startActivity(intent);
                return true;

            case R.id.menu_delete_homework:
                deleteHomework();
                return true;

            case android.R.id.home:
                this.finish();
                break;
        }

        return super.onOptionsItemSelected(item);
    }

    public void getHomework() {
        new RequestActivity() {
            @Override
            public void traiteReponse(JSONObject o, String action) {

                try {
                    SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.FRANCE);

                    JSONArray info = o.getJSONArray("homework");
                    JSONObject homework = info.getJSONObject(0);

                    int id = homework.getInt("id");
                    String title = homework.getString("titre");
                    String description = homework.getString("description");
                    String dueDate = homework.getString("dueDate");

                    Boolean isVisible = false;
                    if (!homework.isNull("isVisible")) {
                        isVisible = homework.getString("isVisible").equals("1");
                    }

                    Boolean realized = false;
                    if (!homework.isNull("realized")) {
                        realized = homework.getString("realized").equals("1");
                    }

                    homeworkObject = new Homework(id, "", title, description, formatter.parse(dueDate), realized, isVisible);

                    moduleTextView.setVisibility(View.GONE);
                    titleTextView.setText(homeworkObject.getTitre());
                    descriptionTextView.setText(homeworkObject.getDescription());
                    dueDateTextView.setText(new SimpleDateFormat("dd MMMM yyyy à HH:mm", Locale.FRANCE).format(homeworkObject.getDueDate()));
                    state.setChecked(homeworkObject.isRealized());

                } catch (JSONException | ParseException e) {
                    e.printStackTrace();
                }
            }
        }.envoiRequete("getHomeworkById", "action=getHomeworkById&idUser=" + preferences.getInt(StringUtils.IDUSER.toString(), 0) + "&idHomeWork=" + idHomework);
    }

    public void deleteHomework() {
        new AlertDialog.Builder(this)
                .setTitle("Supprimer le devoir")
                .setMessage("Etes-vous sur de vouloir supprimer ce devoir ?")
                .setNegativeButton("Non", null)
                .setPositiveButton("Oui", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        //Supprimer le devoir
                        new RequestActivity() {
                            @Override
                            public void traiteReponse(JSONObject o, String action) {
                                if (!o.isNull("retour"))
                                    HomeworkActivity.this.finish();
                            }
                        }.envoiRequete("deleteHomeWork", "action=deleteHomeWork&idUser=" + idUser + "&idHomeWork=" + idHomework);
                    }
                }).show();
    }
}
