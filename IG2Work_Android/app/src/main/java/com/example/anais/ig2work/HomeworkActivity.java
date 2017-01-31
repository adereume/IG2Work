package com.example.anais.ig2work;

import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.TextView;

import com.example.anais.ig2work.DataBase.RequestActivity;
import com.example.anais.ig2work.Model.Homework;
import com.example.anais.ig2work.Utils.StringUtils;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class HomeworkActivity extends AppCompatActivity {

    private SharedPreferences preferences;
    private TextView titleTextView;
    private TextView descriptionTextView;
    private CheckBox state;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_homework);

        preferences = PreferenceManager.getDefaultSharedPreferences(HomeworkActivity.this);

        titleTextView = (TextView) findViewById(R.id.title);
        descriptionTextView = (TextView) findViewById(R.id.description);
        state = (CheckBox) findViewById(R.id.state);

        if (preferences.getString(StringUtils.ROLE.toString(), "").equals("teacher")) {
            state.setVisibility(View.GONE);
        }

        int idHomework = this.getIntent().getExtras().getInt("idHomework");
        //getHomework(idHomework);

        titleTextView.setText("Titre du devoir");
        descriptionTextView.setText("Ceci est la description du devoir. Il s'agit d'un texte long qui décrit le devoir à réaliser...");
    }

    public void getHomework(final int idHomework) {
        new RequestActivity() {
            @Override
            public void traiteReponse(JSONObject o, String action) {

                try {
                    SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.FRANCE);

                    JSONObject info = o.getJSONObject("info");

                    String title = info.getString("title");
                    String description = info.getString("description");
                    Boolean realized = info.getBoolean("realized");

                    titleTextView.setText(title);
                    descriptionTextView.setText(description);
                    state.setChecked(realized);

                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }.envoiRequete("getHomeworkById", "action=getHomeworkById&idUser=" + preferences.getInt(StringUtils.IDUSER.toString(), 0) + "&idHomework=" + idHomework);
    }
}
