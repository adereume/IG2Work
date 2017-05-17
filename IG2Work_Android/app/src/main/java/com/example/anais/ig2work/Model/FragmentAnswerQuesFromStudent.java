package com.example.anais.ig2work.Model;

import android.app.DialogFragment;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.design.widget.TextInputLayout;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;

import com.example.anais.ig2work.DataBase.RequestActivity;
import com.example.anais.ig2work.R;
import com.example.anais.ig2work.TaskActivity;
import com.example.anais.ig2work.Utils.StringUtils;

import org.json.JSONObject;

/**
 * La classe FragmentAnswerQuesFromStudent gère la popup d'ajout d'une réponse à une question
 */
public class FragmentAnswerQuesFromStudent extends DialogFragment {
    private TaskActivity callingActivity;
    private int idQuestion;
    private int idUser;

    private TextInputLayout mLibelleView;

    static FragmentAnswerQuesFromStudent newInstance(int idQuestion, String answer) {
        FragmentAnswerQuesFromStudent f = new FragmentAnswerQuesFromStudent();

        Bundle args = new Bundle();
        args.putInt("idQuestion", idQuestion);
        if(answer != null)
            args.putString("answer", answer);

        f.setArguments(args);
        return f;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_answer_ques_from_student, container, false);
        callingActivity = (TaskActivity) getActivity();


        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(getActivity());
        idUser = preferences.getInt(StringUtils.IDUSER.toString(), 0);

        // Enlève le titre
        getDialog().getWindow().requestFeature(Window.FEATURE_NO_TITLE);

        mLibelleView = (TextInputLayout) v.findViewById(R.id.inputIntitule);
        if(getArguments().getString("answer") != null)
            mLibelleView.getEditText().setText(getArguments().getString("answer"));

        idQuestion = getArguments().getInt("idQuestion");

        // Listener sur les buttons
        v.findViewById(R.id.add).setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                attemptEtape();
            }
        });
        v.findViewById(R.id.cancel).setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                callingActivity.retourPopUpAnswer(0);
                getDialog().dismiss();
            }
        });

        return v;
    }

    /*
    Tentative d'ajout de la réponse
     */
    private void attemptEtape() {
        // Réinitialisation des erreurs
        mLibelleView.setError(null);

        // Stockage des valeurs avant la tentative d'ajout
        String libelle = mLibelleView.getEditText().getText().toString();

        boolean cancel = false;
        View focusView = null;

        // Vérification des champs
        if(TextUtils.isEmpty(libelle)){
            mLibelleView.setError(getString(R.string.error_field_required));
            focusView = mLibelleView;
            cancel = true;
        }

        if (cancel) {
            focusView.requestFocus();
        } else {
            addAnswerQuestionStudent(libelle);
        }
    }

    /*
    Ajout de la réponse
     */
    public void addAnswerQuestionStudent(String answer) {
        new RequestActivity() {
            @Override
            public void traiteReponse(JSONObject o, String action) {
                if(!o.isNull("retour")) {
                    callingActivity.retourPopUpAnswer(1);
                    FragmentAnswerQuesFromStudent.this.getDialog().dismiss();
                }
            }
        }.envoiRequete("answerTacheQuestion", "action=answerTacheQuestion&idQuestion=" +idQuestion+"&idUser="+idUser+ "&answer="+answer);
    }

    // Retour sur l'application
    @Override
    public void onResume() {
        super.onResume();
    }

}