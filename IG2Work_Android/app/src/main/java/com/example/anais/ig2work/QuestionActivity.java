package com.example.anais.ig2work;

import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.ListViewCompat;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.anais.ig2work.DataBase.RequestActivity;
import com.example.anais.ig2work.Model.AnswerFromQuestionAdapter;
import com.example.anais.ig2work.Model.AnswerFromQuestionStat;
import com.example.anais.ig2work.Model.QuestionFromStudent;
import com.example.anais.ig2work.Model.QuestionFromStudentAdapter;
import com.example.anais.ig2work.Model.TaskAdapter;
import com.example.anais.ig2work.Utils.RestActivity;
import com.example.anais.ig2work.Utils.StringUtils;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class QuestionActivity extends RestActivity {
    private SharedPreferences preferences;

    private int idQuestion;
    private int idUser;

    private TextView intitule;
    private TextView titleAnswer;
    private TextView answer;
    private TextView titleCorrect;
    private TextView correct;
    private Button visibility;
    private CheckBox etat;

    private TextView detailAnswers;
    private ListView listAnswers;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_question);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        setTitle("Question");

        //Récupére les champs de la vue
        intitule = (TextView) findViewById(R.id.question);
        titleAnswer = (TextView) findViewById(R.id.textView2);
        answer = (TextView) findViewById(R.id.answer);
        titleCorrect = (TextView) findViewById(R.id.textView3);
        correct = (TextView) findViewById(R.id.correct);
        visibility = (Button) findViewById(R.id.changeVisibility);
        etat = (CheckBox) findViewById(R.id.state);
        etat.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new RequestActivity() {
                    @Override
                    public void traiteReponse(JSONObject o, String action) {
                        if(!o.isNull("retour"))  {
                            Toast.makeText(QuestionActivity.this, "Etat mis à jour", Toast.LENGTH_SHORT).show();
                        }
                    }
                }.envoiRequete("realizedQuestion", "action=realizedQuestion&idQuestion=" + idQuestion+"&idStudent="+idUser+"&realized="+(etat.isChecked() ? 1 : 0));
            }
        });

        //Récupére l'id de la tâche
        idQuestion = this.getIntent().getExtras().getInt("idQuestion");
        //Récupére l'id de l'utilisateur en cours
        preferences = PreferenceManager.getDefaultSharedPreferences(QuestionActivity.this);
        idUser = preferences.getInt(StringUtils.IDUSER.toString(), 0);
    }

    @Override
    protected void onStart() {
        super.onStart();

        //Initialisation de la question
        if (StringUtils.ETUDIANT.toString().equals(preferences.getString(StringUtils.ROLE.toString(), ""))) {
            visibility.setVisibility(View.GONE);
            getQuestionForStudent();
        } else {
            //Affichage de l'enseignant
            findViewById(R.id.titleAnswers).setVisibility(View.VISIBLE);
            detailAnswers = (TextView) findViewById(R.id.detailAnswers);
            listAnswers = (ListView) findViewById(R.id.listAnswers);

            getQuestion();
        }
    }

    public void getQuestionForStudent() {
        new RequestActivity() {
            @Override
            public void traiteReponse(JSONObject o, String action) {
                try {
                    JSONArray retour = o.getJSONArray("question");
                    JSONObject question = retour.getJSONObject(0);

                    intitule.setText(question.getString("description").replace("<br />", ""));

                    //Si la réponse est affiché
                    if(question.getInt("answerIsVisible") == 1) {
                        titleCorrect.setVisibility(View.VISIBLE);
                        correct.setVisibility(View.VISIBLE);
                        correct.setText(question.getString("answer"));
                        //L'étudiant ne peut pas répondre
                        MenuItem item = getMenu().findItem(R.id.action_answer);
                        item.setVisible(false);
                    } else {
                        titleCorrect.setVisibility(View.GONE);
                        correct.setVisibility(View.GONE);
                    }

                    //Affichage de la réponse de l'étudiant
                    JSONArray reponses = o.getJSONArray("reponses");

                    if(reponses.length() > 0) {
                        JSONObject reponse = reponses.getJSONObject(0);

                        if(!reponse.isNull("answer")) {
                            titleAnswer.setVisibility(View.VISIBLE);
                            answer.setText(reponse.getString("answer"));
                            //L'étudiant ne peut plus répondre
                            MenuItem item = getMenu().findItem(R.id.action_answer);
                            item.setVisible(false);
                        } else {
                            titleAnswer.setVisibility(View.GONE);
                            answer.setText("Vous n'avez pas encore répondu");
                        }

                        if(!reponse.isNull("realized")) {
                            etat.setChecked(reponse.getString("realized").equals("1"));
                        }
                    } else {
                        titleAnswer.setVisibility(View.GONE);
                        answer.setText("Vous n'avez pas encore répondu");
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }.requetePeriodique(this, 30, "getTacheById", "action=getQuestionByIdForStudent&idQuestion=" + idQuestion+"&idUser="+idUser);
    }

    public void getQuestion() {
        final String role = preferences.getString(StringUtils.ROLE.toString(), "");

        new RequestActivity() {
            @Override
            public void traiteReponse(JSONObject o, String action) {
                try {
                    JSONArray retour = o.getJSONArray("question");
                    JSONObject question = retour.getJSONObject(0);

                    intitule.setText(question.getString("description").replace("<br />", ""));
                    etat.setVisibility(View.GONE);

                    //Le prof ne vois pas la zone "votre réponse"
                    titleAnswer.setVisibility(View.GONE);
                    answer.setVisibility(View.GONE);

                    titleCorrect.setVisibility(View.VISIBLE);
                    correct.setVisibility(View.VISIBLE);
                    correct.setText(question.getString("answer"));

                    if(question.getInt("answerIsVisible") == 1) {
                        //Set not visible
                        visibility.setCompoundDrawablesWithIntrinsicBounds(R.drawable.is_visible, 0, 0, 0);
                        visibility.setText("Faire Disparaitre");
                        visibility.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                changeAnswerVisibility(false);
                            }
                        });
                    }else {
                        //Set visible
                        visibility.setCompoundDrawablesWithIntrinsicBounds(R.drawable.not_visible, 0, 0, 0);
                        visibility.setText("Afficher");
                        visibility.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                changeAnswerVisibility(true);
                            }
                        });
                    }

                    if(question.isNull("totalAnswer") || question.getInt("totalAnswer") == 0)
                        detailAnswers.setText("Aucun étudiant n'a répondu");
                    else {
                        if (question.getInt("totalAnswer") > 1)
                            detailAnswers.setText(question.getInt("totalAnswer") + " étudiants ont répondu");
                        else
                            detailAnswers.setText(question.getInt("totalAnswer") + " étudiant a répondu");
                    }

                    JSONArray reponses = o.getJSONArray("reponses");
                    if(reponses.length() > 0) {
                        listAnswers.setVisibility(View.VISIBLE);

                        List<AnswerFromQuestionStat> listNotes = new ArrayList<>();
                        for (int i = 0; i < reponses.length(); i++) {
                            JSONObject reponse = reponses.getJSONObject(i);

                            String answer = reponse.getString("answer");
                            Double calcul = reponse.getDouble("Pourcentage") * 100 / reponse.getDouble("Total");

                            AnswerFromQuestionStat a = new AnswerFromQuestionStat(answer, (int) Math.round(calcul));
                            listNotes.add(a);
                        }
                        AnswerFromQuestionAdapter adapterNotes = new AnswerFromQuestionAdapter(QuestionActivity.this, listNotes);
                        listAnswers.setAdapter(adapterNotes);
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }.requetePeriodique(this, 30, "getQuestionById", "action=getQuestionById&idQuestion=" + idQuestion);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        Intent intent;
        Bundle data = new Bundle();

        switch (id) {
            case R.id.action_answer:
                intent = new Intent(QuestionActivity.this, AnswerTeacherQuestion.class);
                data.putInt("idQuestion", idQuestion);
                intent.putExtras(data);
                startActivity(intent);
                break;
            case R.id.action_edit:
                intent = new Intent(QuestionActivity.this, AddQuestionActivity.class);
                data.putInt("idQuestion", idQuestion);
                intent.putExtras(data);
                startActivity(intent);
                break;
            case R.id.action_delete:
                new AlertDialog.Builder(this)
                        .setTitle("Supprimer la question")
                        .setMessage("Etes-vous sur de vouloir supprimer cette question ?")
                        .setNegativeButton("Non", null)
                        .setPositiveButton("Oui", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                //Supprimer la tâche
                                new RequestActivity() {
                                    @Override
                                    public void traiteReponse(JSONObject o, String action) {
                                        if(!o.isNull("retour"))
                                            QuestionActivity.this.finish();
                                    }
                                }.envoiRequete("deleteQuestion", "action=deleteQuestion&idQuestion=" + idQuestion);
                            }
                        })
                        .show();
                break;
            case android.R.id.home:
                this.finish();
                break;

        }

        return super.onOptionsItemSelected(item);
    }

    public void changeAnswerVisibility(final boolean isVisible) {
        new RequestActivity() {
            @Override
            public void traiteReponse(JSONObject o, String action) {
                if(!o.isNull("retour")) {
                    Toast.makeText(QuestionActivity.this, "Etat de la correction mise à jour", Toast.LENGTH_SHORT).show();
                    visibility.setCompoundDrawablesWithIntrinsicBounds((isVisible ? R.drawable.is_visible : R.drawable.not_visible), 0, 0, 0);
                    visibility.setText((isVisible ? "Faire Disparaitre" : "Afficher"));
                    visibility.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            changeAnswerVisibility(!isVisible);
                        }
                    });
                }
            }
        }.envoiRequete("setCorrectionVisible", "action=setCorrectionVisible&idQuestion=" + idQuestion+"&isVisible="+(isVisible ? 1 : 0));
    }
}
