package com.example.anais.ig2work;

import android.animation.ObjectAnimator;
import android.app.FragmentManager;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.view.animation.DecelerateInterpolator;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.ProgressBar;

import com.example.anais.ig2work.DataBase.RequestActivity;
import com.example.anais.ig2work.Model.Homework;
import com.example.anais.ig2work.Model.HomeworkAdapter;
import com.example.anais.ig2work.Model.Note;
import com.example.anais.ig2work.Model.NoteAdapter;
import com.example.anais.ig2work.Model.Task;
import com.example.anais.ig2work.Model.TaskAdapter;
import com.example.anais.ig2work.Utils.ListUtils;
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

/**
 * La classe SeanceActivity gère l'activité de visualisation d'une séance.
 * Elle contient l'ensemble des tâches, questions, devoirs & notes de la séance pour l'utilisateur.
 * S'il est enseignant, il a également accès au taux d'étudiants perdus.
 */
public class SeanceActivity extends RestActivity {
    private SharedPreferences preferences;

    private ProgressBar progressBar;
    private ListView listViewTasks;
    private ListView listViewHomeworks;
    private ListView listViewNotes;

    private int idSeance;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_seance);

        // Affichage de la flèche de retour
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        preferences = PreferenceManager.getDefaultSharedPreferences(SeanceActivity.this);

        progressBar = (ProgressBar) findViewById(R.id.progressBar);
        ImageButton resetButton = (ImageButton) findViewById(R.id.resetButton);
        listViewTasks = (ListView) findViewById(R.id.list_tasks);
        listViewHomeworks = (ListView) findViewById(R.id.list_homeworks);
        listViewNotes = (ListView) findViewById(R.id.list_notes);

        // Récupération de l'ID de la séance courante
        idSeance = this.getIntent().getExtras().getInt("idSeance");
        SharedPreferences.Editor editor = preferences.edit();
        editor.putInt("idSeance", idSeance);
        editor.apply();

        resetButton.setOnClickListener(new Button.OnClickListener() {
            @Override
            public void onClick(View view) {
                // Lorsque l'enseignant clique sur le bouton de réinitialisation de la barre
                // de progression du taux d'étudiants perdus, le taux est réinitialisé.
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
            case R.id.menu_seance: // Ouverture de la popup d'ajout
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
            case R.id.delete_seance:
                new AlertDialog.Builder(this)
                        .setIcon(android.R.drawable.ic_dialog_alert)
                        .setMessage("Voulez-vous supprimer cette séance")
                        .setPositiveButton("oui", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                new RequestActivity() {
                                    @Override
                                    public void traiteReponse(JSONObject o, String action) {
                                        SeanceActivity.this.finish();
                                    }
                                }.envoiRequete("deleteSeance", "action=deleteSeance&idSeance=" + idSeance + "&idUser=" + preferences.getInt(StringUtils.IDUSER.toString(), 0));
                            }
                        })
                        .setNegativeButton("non", null)
                        .show();
                break;
            case android.R.id.home: // Retour à l'accueil
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

    /*
    Récupération du contenu de la séance
     */
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

                    // ***** TÂCHES & QUESTIONS *****
                    List<Task> listTasks = new ArrayList<>();
                    JSONArray tasks = o.getJSONArray("seance");

                    for (int i = 0; i < tasks.length(); i++) {
                        JSONObject task = tasks.getJSONObject(i);

                        int id = task.getInt("id");
                        String type = task.getString("type"); // Désigne une tâche ou une question
                        String title = task.getString("titre");
                        Boolean isVisible = true;

                        if (!task.isNull("isVisible")) {
                            isVisible = task.getString("isVisible").equals("1");
                        }

                        Boolean isRealized = false;
                        if (!task.isNull("realized")) {
                            isRealized = task.getString("realized").equals("1");
                        }

                        Task t = new Task(id, title, null, type, isVisible, isRealized);

                        listTasks.add(t);
                    }

                    TaskAdapter adapterSeanceObject = new TaskAdapter(SeanceActivity.this, listTasks);
                    listViewTasks.setAdapter(adapterSeanceObject);

                    // Lorsque l'utilisateur clique sur une tâche/question, il accède à une vue détaillée
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

                    List<Homework> listHomeworks = new ArrayList<>();
                    JSONArray homeworks = o.getJSONArray("homework");

                    for (int i = 0; i < homeworks.length(); i++) {

                        JSONObject homework = homeworks.getJSONObject(i);

                        int id = homework.getInt("id");
                        String title = homework.getString("titre");
                        String dueDate = homework.getString("dueDate");
                        Boolean isVisible = true;
                        if (!homework.isNull("isVisible")) {
                            isVisible = homework.getString("isVisible").equals("1");
                        }

                        Boolean realized = false;
                        if (!homework.isNull("realized")) {
                            realized = homework.getString("realized").equals("1");
                        }

                        Log.e("Realized", realized+"");
                        Homework h = new Homework(id, moduleName, title, null, formatter.parse(dueDate), realized, isVisible);

                        listHomeworks.add(h);

                    }

                    HomeworkAdapter adapterHomeworks = new HomeworkAdapter(SeanceActivity.this, listHomeworks);
                    listViewHomeworks.setAdapter(adapterHomeworks);
                    ListUtils.setDynamicHeight(listViewHomeworks);

                    // Lorsque l'utilisateur clique sur un devoir, il accède à une vue détaillée
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
                    List<Note> listNotes = new ArrayList<>();
                    JSONArray notes = o.getJSONArray("note");

                    for (int i = 0; i < notes.length(); i++) {

                        JSONObject note = notes.getJSONObject(i);

                        int id = note.getInt("id");
                        String description = note.getString("description");
                        boolean isPrivate = false;

                        if (!note.isNull("private")) {
                            isPrivate = note.getString("private").equals("1");
                        }

                        Note n = new Note(id, description, isPrivate);

                        listNotes.add(n);
                    }

                    NoteAdapter adapterNotes = new NoteAdapter(SeanceActivity.this, listNotes);
                    listViewNotes.setAdapter(adapterNotes);
                    ListUtils.setDynamicHeight(listViewNotes);

                    // Lorsque l'utilisateur clique sur une note, il accède à une vue détaillée
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

    /*
    Récupération du taux d'étudiants perdus
     */
    public void getLostStudents() {
        new RequestActivity() {
            @Override
            public void traiteReponse(JSONObject o, String action) {
                try {
                    JSONArray retour = o.getJSONArray("retour");
                    JSONObject lost = retour.getJSONObject(0);

                    // Recupére les résultats (nombre d'étudiants perdus & nombre total)
                    int lostStudent = lost.getInt("Perdu");
                    int totalStudent = lost.getInt("Total");

                    // Calcul du taux
                    int taux = 0;
                    if (lostStudent != 0 && totalStudent != 0) {
                        taux = (lostStudent * 100) / totalStudent;
                    }

                    // Initialisation de la ProgressBar
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

    /*
    Réinitialisation du taux d'étudiants perdus
     */
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


}
