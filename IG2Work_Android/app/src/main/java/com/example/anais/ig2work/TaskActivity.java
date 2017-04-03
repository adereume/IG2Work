package com.example.anais.ig2work;

import android.app.FragmentManager;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;

import com.example.anais.ig2work.DataBase.RequestActivity;
import com.example.anais.ig2work.Model.QuestionFromStudent;
import com.example.anais.ig2work.Model.QuestionFromStudentAdapter;
import com.example.anais.ig2work.Model.Task;
import com.example.anais.ig2work.Model.TaskAdapter;
import com.example.anais.ig2work.Utils.ListUtils;
import com.example.anais.ig2work.Utils.RestActivity;
import com.example.anais.ig2work.Utils.StringUtils;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class TaskActivity extends RestActivity {
    private int idTask;

    private TextView titre;
    private TextView description;
    private ListView listQuestion;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_task);

        setTitle("Tâche");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        //Récupére l'id de la tâche
        idTask = this.getIntent().getExtras().getInt("idTask");

        titre = (TextView) findViewById(R.id.title);
        description = (TextView) findViewById(R.id.description);
        listQuestion = (ListView) findViewById(R.id.questions);
    }

    @Override
    protected void onStart() {
        super.onStart();
        getTache();
    }

    public void getTache() {
        new RequestActivity() {
            @Override
            public void traiteReponse(JSONObject o, String action) {
                try {
                    JSONArray retour = o.getJSONArray("tache");
                    JSONObject task = retour.getJSONObject(0);

                    titre.setText(task.getString("titre"));
                    description.setText(task.getString("description").replace("<br />", ""));

                    List<QuestionFromStudent> listTasks = new ArrayList<>();
                    JSONArray questions = o.getJSONArray("question");

                    for (int i = 0; i < questions.length(); i++) {
                        JSONObject question = questions.getJSONObject(i);

                        int id = question.getInt("id");
                        String intitule = question.getString("question");

                        String anwser = null;
                        if(!question.isNull("answer"))
                            anwser = question.getString("answer").replace("<br />", "");

                        QuestionFromStudent q = new QuestionFromStudent(id, idTask, intitule, anwser);

                        listTasks.add(q);
                    }

                    QuestionFromStudentAdapter adapterSeanceObject = new QuestionFromStudentAdapter(TaskActivity.this, listTasks);
                    listQuestion.setAdapter(adapterSeanceObject);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }.requetePeriodique(this, 30, "getTacheById", "action=getTacheById&idTache=" + idTask);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        switch (id) {
            case R.id.menu_delete_task:
                /*Log.d("action", "Séance");

                FragmentManager fragmentManager = getFragmentManager();

                Bundle data = new Bundle();
                data.putString("idSeance", String.valueOf(idSeance));
                data.putString("idUser", String.valueOf(preferences.getInt(StringUtils.IDUSER.toString(), 0)));
                data.putString("role", preferences.getString(StringUtils.ROLE.toString(), ""));

                AjoutFragment ajoutFragment = new AjoutFragment();
                ajoutFragment.setArguments(data);
                ajoutFragment.setRetainInstance(true);
                ajoutFragment.show(fragmentManager, "seance");

                return true;*/

            case R.id.menu_question_task:
                Intent intent = new Intent();
                intent = new Intent(TaskActivity.this, TacheQuestionActivity.class);
                Bundle data = new Bundle();
                data.putInt("idTask", idTask);
                intent.putExtras(data);
                startActivity(intent);
                break;
            case android.R.id.home:
                this.finish();
                break;
        }

        return super.onOptionsItemSelected(item);
    }
}
