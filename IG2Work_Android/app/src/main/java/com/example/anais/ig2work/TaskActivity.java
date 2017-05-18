package com.example.anais.ig2work;

import android.content.DialogInterface;
import android.content.Intent;
import android.support.v7.app.AlertDialog;
import android.os.Bundle;
import android.view.MenuItem;
import android.widget.ListView;
import android.widget.TextView;

import com.example.anais.ig2work.DataBase.RequestActivity;
import com.example.anais.ig2work.Model.QuestionFromStudent;
import com.example.anais.ig2work.Model.QuestionFromStudentAdapter;
import com.example.anais.ig2work.Utils.RestActivity;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

/**
 * La classe TaskActivity gère l'activité de visualisation d'une tâche
 */
public class TaskActivity extends RestActivity {
    private TextView titre;
    private TextView description;
    private ListView listQuestion;

    private int idTask;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_task);

        // Affichage de la flèche de retour
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        setTitle("Tâche");

        // Récupère l'ID de la tâche
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

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        Bundle data = new Bundle();
        Intent intent = new Intent();

        switch (id) {
            case R.id.menu_delete_task:
                new AlertDialog.Builder(this)
                        .setTitle("Supprimer la tâche")
                        .setMessage("Etes-vous sur de vouloir supprimer cette tâche ?")
                        .setNegativeButton("Non", null)
                        .setPositiveButton("Oui", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                //Supprimer la tâche
                                new RequestActivity() {
                                    @Override
                                    public void traiteReponse(JSONObject o, String action) {
                                        if(!o.isNull("retour"))
                                            TaskActivity.this.finish();
                                    }
                                }.envoiRequete("deleteTache", "action=deleteTache&idTache=" + idTask);
                            }
                        })
                        .show();
                break;
            case R.id.menu_edit_task:
                intent = new Intent(TaskActivity.this, AddTaskActivity.class);
                data.putInt("idTask", idTask);
                intent.putExtras(data);
                startActivity(intent);
                break;
            case R.id.menu_question_task:
                intent = new Intent(TaskActivity.this, TacheQuestionActivity.class);
                data.putInt("idTask", idTask);
                intent.putExtras(data);
                startActivity(intent);
                break;

            case android.R.id.home: // Retour à la page de s"ance
                this.finish();
                break;
        }

        return super.onOptionsItemSelected(item);
    }

    public void retourPopUpAnswer(int CODE_RETOUR) {
        if(CODE_RETOUR == 1) {
            getTache();
        }
    }

    /*
    Récupération de la tâche
     */
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

}
