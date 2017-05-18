package com.example.anais.ig2work.Model;

import android.app.DialogFragment;
import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageButton;
import android.widget.TextView;

import com.example.anais.ig2work.R;
import com.example.anais.ig2work.TaskActivity;
import com.example.anais.ig2work.Utils.StringUtils;

import java.util.List;

/**
 * La classe QuestionFromStudentAdapter permet de personnaliser les éléments d'une liste de questions
 * d'étudiants au sein d'une tâche.
 * Pour chaque question, on affiche l'intitulé et la réponse.
 */

public class QuestionFromStudentAdapter extends ArrayAdapter<QuestionFromStudent> {

    public QuestionFromStudentAdapter(Context context, List<QuestionFromStudent> questions) {
        super(context, 0, questions);
    }

    private class QuestionViewHolder{
        public TextView question;
        public TextView answer;
    }

    @Override
    public View getView(int position, View convertView, final ViewGroup parent) {

        if(convertView == null){
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.row_question_student_layout,parent, false);
        }

        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(convertView.getContext());

        // Affichage des informations relatives à la question
        QuestionViewHolder viewHolder = (QuestionViewHolder) convertView.getTag();
        if(viewHolder == null){
            viewHolder = new QuestionViewHolder();
            viewHolder.question = (TextView) convertView.findViewById(R.id.question);
            viewHolder.answer = (TextView) convertView.findViewById(R.id.answer);
            convertView.setTag(viewHolder);
        }

        final QuestionFromStudent question = getItem(position);
        viewHolder.question.setText(question.getQuestion());
        viewHolder.answer.setText(question.getAnwser());

        // Affichage en fonction du rôle
        if(StringUtils.ENSEIGNANT.toString().equals(preferences.getString(StringUtils.ROLE.toString(), ""))) {
            ((ImageButton) convertView.findViewById(R.id.btnAnswer)).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    // Vue pour répondre
                    DialogFragment newFragment = FragmentAnswerQuesFromStudent.newInstance(question.getId(), question.getAnwser());
                    newFragment.show(((TaskActivity) parent.getContext()).getFragmentManager(), "dialog");
                }
            });
        } else {
            convertView.findViewById(R.id.btnAnswer).setVisibility(View.GONE);
        }

        return convertView;
    }
}
