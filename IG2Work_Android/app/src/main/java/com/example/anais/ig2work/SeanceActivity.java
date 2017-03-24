package com.example.anais.ig2work;

import android.animation.ObjectAnimator;
import android.app.FragmentManager;
import android.content.Intent;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.DecelerateInterpolator;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.example.anais.ig2work.DataBase.RequestActivity;
import com.example.anais.ig2work.Model.Homework;
import com.example.anais.ig2work.Model.HomeworkAdapter;
import com.example.anais.ig2work.Model.Note;
import com.example.anais.ig2work.Model.NoteAdapter;
import com.example.anais.ig2work.Model.Task;
import com.example.anais.ig2work.Model.TaskAdapter;
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
    private ImageButton resetButton;
    private ListView listViewTasks;
    private ListView listViewHomeworks;
    private ListView listViewNotes;

    private int idSeance;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_seance);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        preferences = PreferenceManager.getDefaultSharedPreferences(SeanceActivity.this);

        progressBar = (ProgressBar) findViewById(R.id.progressBar);
        resetButton = (ImageButton) findViewById(R.id.resetButton);
        listViewTasks = (ListView) findViewById(R.id.list_tasks);
        listViewHomeworks = (ListView) findViewById(R.id.list_homeworks);
        listViewNotes = (ListView) findViewById(R.id.list_notes);

        idSeance = this.getIntent().getExtras().getInt("idSeance");

        resetButton.setOnClickListener(new Button.OnClickListener() {
            @Override
            public void onClick(View view) {
                SeanceActivity.this.resetLostStudents();
            }
        });
    }

    @Override
    protected void onStart() {
        super.onStart();

        if (StringUtils.ETUDIANT.toString().equals(preferences.getString(StringUtils.ROLE.toString(), ""))) {
            findViewById(R.id.progress).setVisibility(View.GONE);
        } else {
            getLostStudents();
        }

        getSeance();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        switch (id) {
            case R.id.menu_seance:
                Log.d("action", "Séance");

                FragmentManager fragmentManager = getFragmentManager();

                Bundle data = new Bundle();
                data.putString("idSeance", String.valueOf(idSeance));
                data.putString("idUser", String.valueOf(preferences.getInt(StringUtils.IDUSER.toString(), 0)));
                data.putString("role", preferences.getString(StringUtils.ROLE.toString(), ""));

                AjoutFragment ajoutFragment = new AjoutFragment();
                ajoutFragment.setArguments(data);
                ajoutFragment.setRetainInstance(true);
                ajoutFragment.show(fragmentManager, "seance");

                return true;

            case android.R.id.home:
                this.finish();
                break;
        }

        return super.onOptionsItemSelected(item);
    }

    public void onClickChangeActivity(String activity, Bundle data) {
        Intent intent = new Intent();

        switch (activity) {
            case "task":
                intent = new Intent(SeanceActivity.this, TaskActivity.class);
                break;
            case "question":
                intent = new Intent(SeanceActivity.this, QuestionActivity.class);
                break;
            case "homework":
                intent = new Intent(SeanceActivity.this, HomeworkActivity.class);
                break;
            case "note":
                intent = new Intent(SeanceActivity.this, NoteActivity.class);
                break;
        }

        intent.putExtras(data);
        startActivity(intent);
    }

    public void getSeance() {
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
                            SeanceActivity.this.setTitle(moduleName + " - " + teacherName);
                            break;
                        case "teacher":
                            SeanceActivity.this.setTitle(moduleName + " - " + promoName);
                            break;
                    }

                    // ***** TÂCHES *****
                    List<Task> listTasks = new ArrayList<Task>();
                    JSONArray tasks = o.getJSONArray("seance");

                    for (int i = 0; i < tasks.length(); i++) {
                        JSONObject task = tasks.getJSONObject(i);

                        int id = task.getInt("id");
                        String type = task.getString("type");
                        String title = task.getString("titre");
                        Boolean isVisible = true;

                        if (!task.isNull("isVisible")) {
                            isVisible = task.getString("isVisible").equals("1") ? true : false;
                        }

                        Boolean isRealized = false;
                        if (!task.isNull("realized")) {
                            isRealized = task.getString("realized").equals("1") ? true : false;
                        }

                        Task t = new Task(id, title, null, type, isVisible, isRealized);

                        listTasks.add(t);
                    }

                    TaskAdapter adapterSeanceObject = new TaskAdapter(SeanceActivity.this, listTasks);
                    listViewTasks.setAdapter(adapterSeanceObject);

                    listViewTasks.setOnItemClickListener(new ListView.OnItemClickListener() {

                        @Override
                        public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {

                            Task taskChoice = (Task) listViewTasks.getAdapter().getItem(i);

                            Bundle data = new Bundle();

                            if(taskChoice.getType().equals("Tache")) {
                                data.putInt("idTask", taskChoice.getId());
                                SeanceActivity.this.onClickChangeActivity("task", data);
                            } else {
                                data.putInt("idQuestion", taskChoice.getId());
                                SeanceActivity.this.onClickChangeActivity("question", data);
                            }
                        }
                    });
                    ListUtils.setDynamicHeight(listViewTasks);

                    // ***** DEVOIRS *****

                    List<Homework> listHomeworks = new ArrayList<Homework>();
                    JSONArray homeworks = o.getJSONArray("homework");

                    for (int i = 0; i < homeworks.length(); i++) {

                        JSONObject homework = homeworks.getJSONObject(i);

                        int id = homework.getInt("id");
                        String title = homework.getString("titre");
                        String dueDate = homework.getString("dueDate");

                        Boolean isVisible = true;
                        if (!homework.isNull("isVisible")) {
                            isVisible = homework.getString("isVisible").equals("1") ? true : false;
                        }

                        Boolean realized = false;
                        if (!homework.isNull("realized")) {
                            realized = homework.getString("realized").equals("1") ? true : false;
                        }

                        Homework h = new Homework(id, moduleName, title, null, formatter.parse(dueDate), realized, isVisible);

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
                    List<Note> listNotes = new ArrayList<Note>();
                    JSONArray notes = o.getJSONArray("note");

                    for (int i = 0; i < notes.length(); i++) {

                        JSONObject note = notes.getJSONObject(i);

                        int id = note.getInt("id");
                        String description = note.getString("description");
                        boolean isPrivate = false;

                        if (!note.isNull("private")) {
                            isPrivate = note.getString("private").equals("1") ? true : false;
                        }

                        Note n = new Note(id, description, isPrivate);

                        listNotes.add(n);
                    }

                    NoteAdapter adapterNotes = new NoteAdapter(SeanceActivity.this, listNotes);
                    listViewNotes.setAdapter(adapterNotes);
                    ListUtils.setDynamicHeight(listViewNotes);

                    listViewNotes.setOnItemClickListener(new ListView.OnItemClickListener() {

                        @Override
                        public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {

                            Note noteChoice = (Note) listViewNotes.getAdapter().getItem(i);

                            Bundle data = new Bundle();
                            data.putInt("idNote", noteChoice.getId());

                            SeanceActivity.this.onClickChangeActivity("note", data);
                        }
                    });

                } catch (JSONException | ParseException e) {
                    e.printStackTrace();
                }
            }
        }.requetePeriodique(this, 30, "getSeanceById", "action=getSeanceById&idUser=" + preferences.getInt(StringUtils.IDUSER.toString(), 0) + "&idSeance=" + idSeance);
    }

    public void getLostStudents() {
        new RequestActivity() {
            @Override
            public void traiteReponse(JSONObject o, String action) {
                try {
                    JSONArray retour = o.getJSONArray("retour");
                    JSONObject lost = retour.getJSONObject(0);

                    //Recupére les résultats
                    int lostStudent = lost.getInt("Perdu");
                    int totalStudent = lost.getInt("Total");

                    //Calcul le poucentage
                    int taux = 0;
                    if (lostStudent != 0 && totalStudent != 0) {
                        taux = (lostStudent * 100) / totalStudent;
                    }

                    //Initialise la progressBar
                    ObjectAnimator animation = ObjectAnimator.ofInt(progressBar, "progress", taux);
                    animation.setDuration(500); // 0.5 second
                    animation.setInterpolator(new DecelerateInterpolator());
                    animation.start();

                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }.requetePeriodique(this, 30, "getAllLostBySeance", "action=getAllLostBySeance&idSeance=" + idSeance);
    }

    public void resetLostStudents() {
        new RequestActivity() {
            @Override
            public void traiteReponse(JSONObject o, String action) {
                ObjectAnimator animation = ObjectAnimator.ofInt(progressBar, "progress", 0);
                animation.setDuration(500); // 0.5 second
                animation.setInterpolator(new DecelerateInterpolator());
                animation.start();
            }
        }.envoiRequete("resetLostBySeance", "action=resetLostBySeance&idSeance=" + idSeance);
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
