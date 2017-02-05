package com.example.anais.ig2work;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Parcelable;
import android.preference.PreferenceManager;
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

    private int idHomework;
    private Homework homeworkObject;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_homework);

        preferences = PreferenceManager.getDefaultSharedPreferences(HomeworkActivity.this);

        moduleTextView = (TextView) findViewById(R.id.module);
        titleTextView = (TextView) findViewById(R.id.title);
        descriptionTextView = (TextView) findViewById(R.id.description);
        dueDateTextView = (TextView) findViewById(R.id.dueDate);
        state = (CheckBox) findViewById(R.id.state);

        if (preferences.getString(StringUtils.ROLE.toString(), "").equals("teacher")) {
            state.setVisibility(View.GONE);
        }

        idHomework = this.getIntent().getExtras().getInt("idHomework");
        getHomework(idHomework);
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
                deleteHomework(idHomework);
                return true;
        }

        return super.onOptionsItemSelected(item);
    }

    public void getHomework(final int idHomework) {
        new RequestActivity() {
            @Override
            public void traiteReponse(JSONObject o, String action) {

                try {
                    SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.FRANCE);

                    JSONArray info = o.getJSONArray("retour");
                    JSONObject homework = info.getJSONObject(0);

                    int id = homework.getInt("id");
                    String module = homework.getString("moduleName");
                    String title = homework.getString("titre");
                    String description = homework.getString("description");
                    String dueDate = homework.getString("dueDate");
                    Boolean realized = false;

                    if (!homework.isNull("realized")) {
                        realized = homework.getString("realized").equals("1") ? true : false;
                    }

                    homeworkObject = new Homework(id, module, title, description, formatter.parse(dueDate), realized);

                    moduleTextView.setText(homeworkObject.getModule());
                    titleTextView.setText(homeworkObject.getTitre());
                    descriptionTextView.setText(homeworkObject.getDescription());
                    dueDateTextView.setText(new SimpleDateFormat("dd MMMM yyyy à HH:mm", Locale.FRANCE).format(homeworkObject.getDueDate()));
                    state.setChecked(homeworkObject.isRealized());

                } catch (JSONException | ParseException e) {
                    e.printStackTrace();
                }
            }
        }.envoiRequete("getHomeWorkById", "action=getHomeWorkById&idUser=" + preferences.getInt(StringUtils.IDUSER.toString(), 0) + "&idHomeWork=" + idHomework);
    }

    public void deleteHomework(final int idHomework) {
        new RequestActivity() {
            @Override
            public void traiteReponse(JSONObject o, String action) {

                //TODO Retour lors de la suppression d'un devoir ?
                /*try {

                } catch (JSONException | ParseException e) {
                    e.printStackTrace();
                }*/
            }
        }.envoiRequete("deleteHomework", "action=deleteHomework&idHomeWork=" + idHomework);
    }
}
