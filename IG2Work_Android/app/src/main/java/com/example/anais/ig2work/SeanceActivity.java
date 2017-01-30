package com.example.anais.ig2work;

import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.example.anais.ig2work.DataBase.RequestActivity;
import com.example.anais.ig2work.Utils.RestActivity;
import com.example.anais.ig2work.Utils.StringUtils;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

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
            progressBar.setVisibility(View.INVISIBLE);
        }

        int idSeance = this.getIntent().getExtras().getInt("idSeance");
        getSeance(idSeance);
    }

    public void getSeance(final int idSeance) {
        new RequestActivity() {
            @Override
            public void traiteReponse(JSONObject o, String action) {

                try {

                    JSONArray infos = o.getJSONArray("info");
                    JSONObject info = infos.getJSONObject(0);

                    String moduleName = info.getString("moduleName");
                    String teacherName = info.getString("teacherFirstName") + " " + info.getString("teacherLastName");
                    String promoName = info.getString("promoName");

                    textView.setText(moduleName + " - " + teacherName + " - " + promoName);

                    JSONArray tasks = o.getJSONArray("seance");

                    for (int i = 0; i < tasks.length(); i++) {

                        JSONObject task = tasks.getJSONObject(i);

                        // Instancier objet tâche
                        // Remplir liste de tâches
                        // Instancier Adapter -> listView
                    }

                    JSONArray homeworks = o.getJSONArray("homework");

                    for (int i = 0; i < homeworks.length(); i++) {

                        JSONObject homework = homeworks.getJSONObject(i);

                        // Instancier objet homework
                        // Remplir liste de homeworks
                        // Instancier Adapter -> listView
                    }

                    JSONArray notes = o.getJSONArray("note");

                    for (int i = 0; i < notes.length(); i++) {

                        JSONObject note = notes.getJSONObject(i);

                        // Instancier objet note
                        // Remplir liste de notes
                        // Instancier Adapter -> listView
                    }

                    List<String> listTasks = new ArrayList<String>();
                    listTasks.add("Tâche 1");
                    listTasks.add("Tâche 2");
                    listTasks.add("Tâche 3");

                    ArrayAdapter<String> adapterTasks = new ArrayAdapter<String>(SeanceActivity.this, android.R.layout.simple_list_item_1, listTasks);
                    listViewTasks.setAdapter(adapterTasks);

                    List<String> listHomeworks = new ArrayList<String>();
                    listHomeworks.add("Devoir 1");
                    listHomeworks.add("Devoir 2");
                    listHomeworks.add("Devoir 3");

                    ArrayAdapter<String> adapterHomeworks = new ArrayAdapter<String>(SeanceActivity.this, android.R.layout.simple_list_item_1, listHomeworks);
                    listViewHomeworks.setAdapter(adapterHomeworks);

                    List<String> listNotes = new ArrayList<String>();
                    listNotes.add("Note 1");
                    listNotes.add("Note 2");
                    listNotes.add("Note 3");

                    ArrayAdapter<String> adapterNotes = new ArrayAdapter<String>(SeanceActivity.this, android.R.layout.simple_list_item_1, listNotes);
                    listViewNotes.setAdapter(adapterNotes);

                    ListUtils.setDynamicHeight(listViewTasks);
                    ListUtils.setDynamicHeight(listViewHomeworks);
                    ListUtils.setDynamicHeight(listViewNotes);

                } catch (JSONException e) {
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
