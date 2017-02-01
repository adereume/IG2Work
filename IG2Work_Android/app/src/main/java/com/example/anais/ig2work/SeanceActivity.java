package com.example.anais.ig2work;

import android.app.FragmentManager;
import android.content.Intent;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ExpandableListView;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.example.anais.ig2work.DataBase.RequestActivity;
import com.example.anais.ig2work.Model.Homework;
import com.example.anais.ig2work.Model.HomeworkAdapter;
import com.example.anais.ig2work.Utils.RestActivity;
import com.example.anais.ig2work.Utils.StringUtils;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class SeanceActivity extends RestActivity {

    private SharedPreferences preferences;
    private ProgressBar progressBar;
    private TextView textView;
    private ListView listViewTasks;
    private ListView listViewHomeworks;
    private ListView listViewNotes;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_seance);

        preferences = PreferenceManager.getDefaultSharedPreferences(SeanceActivity.this);

        progressBar = (ProgressBar) findViewById(R.id.progressBar);
        textView = (TextView) findViewById(R.id.title);
        listViewTasks = (ListView) findViewById(R.id.list_tasks);
        listViewHomeworks = (ListView) findViewById(R.id.list_homeworks);
        listViewNotes = (ListView) findViewById(R.id.list_notes);

        if (preferences.getString(StringUtils.ROLE.toString(), "").equals("student")) {
            progressBar.setVisibility(View.GONE);
        }

        int idSeance = this.getIntent().getExtras().getInt("idSeance");
        getSeance(idSeance);
    }

    public void onClickChangeActivity(String activity, Bundle data) {
        Intent intent = new Intent();

        switch (activity) {
            case "homework":
                intent = new Intent(SeanceActivity.this, HomeworkActivity.class);
                break;
        }

        intent.putExtras(data);
        startActivity(intent);
    }

    public void getSeance(final int idSeance) {
        new RequestActivity() {
            @Override
            public void traiteReponse(JSONObject o, String action) {

                try {
                    SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.FRANCE);

                    JSONArray infos = o.getJSONArray("info");
                    JSONObject info = infos.getJSONObject(0);

                    String moduleName = info.getString("moduleName");
                    String teacherName = info.getString("teacherFirstName") + " " + info.getString("teacherLastName");
                    String promoName = info.getString("promoName");

                    switch (preferences.getString(StringUtils.ROLE.toString(), "")) {
                        case "student":
                            textView.setText(moduleName + " - " + teacherName);
                            break;
                        case "teacher":
                            textView.setText(moduleName + " - " + promoName);
                            break;
                    }

                    // ***** TÂCHES *****

                    List<String> listTasks = new ArrayList<String>();
                    JSONArray tasks = o.getJSONArray("seance");

                    for (int i = 0; i < tasks.length(); i++) {

                        JSONObject task = tasks.getJSONObject(i);

                        // Instancier objet tâche
                        // Remplir liste de tâches
                    }

                    listTasks.add("Tâche 1");
                    listTasks.add("Tâche 2");
                    listTasks.add("Tâche 3");

                    ArrayAdapter<String> adapterTasks = new ArrayAdapter<String>(SeanceActivity.this, android.R.layout.simple_list_item_1, listTasks);
                    listViewTasks.setAdapter(adapterTasks);
                    ListUtils.setDynamicHeight(listViewTasks);

                    // ***** DEVOIRS *****

                    List<Homework> listHomeworks = new ArrayList<Homework>();
                    JSONArray homeworks = o.getJSONArray("homework");

                    for (int i = 0; i < homeworks.length(); i++) {

                        JSONObject homework = homeworks.getJSONObject(i);

                        int id = homework.getInt("id");
                        String title = homework.getString("titre");
                        String description = homework.getString("description");
                        String dueDate = homework.getString("dueDate");
                        Boolean realized = false;

                        if (!homework.isNull("realized")) {
                            realized = homework.getString("realized").equals("1") ? true : false;
                        }

                        Homework h = new Homework(id, moduleName, title, description, formatter.parse(dueDate), realized);

                        listHomeworks.add(h);
                    }

                    HomeworkAdapter adapterHomeworks = new HomeworkAdapter(SeanceActivity.this, listHomeworks);
                    listViewHomeworks.setAdapter(adapterHomeworks);
                    ListUtils.setDynamicHeight(listViewHomeworks);

                    listViewHomeworks.setOnItemClickListener(new ListView.OnItemClickListener() {

                        @Override
                        public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {

                            Homework homeworkChoice = (Homework) listViewHomeworks.getAdapter().getItem(i);

                            Bundle data = new Bundle();
                            data.putInt("idHomework", homeworkChoice.getId());

                            SeanceActivity.this.onClickChangeActivity("homework", data);
                        }
                    });


                    // ***** NOTES *****

                    List<String> listNotes = new ArrayList<String>();
                    JSONArray notes = o.getJSONArray("note");

                    for (int i = 0; i < notes.length(); i++) {

                        JSONObject note = notes.getJSONObject(i);

                        // Instancier objet note
                        // Remplir liste de notes
                    }

                    listNotes.add("Note 1");
                    listNotes.add("Note 2");
                    listNotes.add("Note 3");

                    ArrayAdapter<String> adapterNotes = new ArrayAdapter<String>(SeanceActivity.this, android.R.layout.simple_list_item_1, listNotes);
                    listViewNotes.setAdapter(adapterNotes);
                    ListUtils.setDynamicHeight(listViewNotes);

                } catch (JSONException | ParseException e) {
                    e.printStackTrace();
                }
            }
        }.envoiRequete("getSeanceById", "action=getSeanceById&idUser=" + preferences.getInt(StringUtils.IDUSER.toString(), 0) + "&idSeance=" + idSeance);
    }

    public static class ListUtils {
        public static void setDynamicHeight(ListView mListView) {

            ListAdapter mListAdapter = mListView.getAdapter();
            if (mListAdapter == null) {
                // when adapter is null
                return;
            }

            int height = 0;
            int desiredWidth = View.MeasureSpec.makeMeasureSpec(mListView.getWidth(), View.MeasureSpec.UNSPECIFIED);

            for (int i = 0; i < mListAdapter.getCount(); i++) {
                View listItem = mListAdapter.getView(i, null, mListView);
                listItem.measure(desiredWidth, View.MeasureSpec.UNSPECIFIED);
                height += listItem.getMeasuredHeight();
            }

            ViewGroup.LayoutParams params = mListView.getLayoutParams();
            params.height = height + (mListView.getDividerHeight() * (mListAdapter.getCount() - 1));
            mListView.setLayoutParams(params);
            mListView.requestLayout();
        }
    }
}
