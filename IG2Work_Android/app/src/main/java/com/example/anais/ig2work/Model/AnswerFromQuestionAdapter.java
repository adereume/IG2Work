package com.example.anais.ig2work.Model;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.example.anais.ig2work.R;

import java.util.List;

/**
 * La classe AnswerFromQuestionAdapter permet de personnaliser les éléments d'une liste de réponses
 * faites par les étudiants à une question au sein d'une séance
 * Pour chaque réponse, on affiche l'intitulé et le pourcentage (barre de progression)
 */

public class AnswerFromQuestionAdapter extends ArrayAdapter<AnswerFromQuestionStat> {

    public AnswerFromQuestionAdapter(Context context, List<AnswerFromQuestionStat> answerStat) {
        super(context, 0, answerStat);
    }

    private class AnswersViewHolder{
        public TextView answer;
        public TextView textPourcentage;
        public ProgressBar pourcentage;
    }

    @Override
    public View getView(int position, View convertView, final ViewGroup parent) {

        if(convertView == null){
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.row_answer_question_layout,parent, false);
        }

        AnswersViewHolder viewHolder = (AnswersViewHolder) convertView.getTag();
        if(viewHolder == null){
            viewHolder = new AnswersViewHolder();
            viewHolder.answer = (TextView) convertView.findViewById(R.id.question);
            viewHolder.textPourcentage = (TextView) convertView.findViewById(R.id.textPourcentage);
            viewHolder.pourcentage = (ProgressBar) convertView.findViewById(R.id.progressBar2);
            convertView.setTag(viewHolder);
        }

        // Affichage des informations relatives à la réponse
        final AnswerFromQuestionStat answerStat = getItem(position);
        viewHolder.answer.setText(answerStat.getAnswer());
        viewHolder.textPourcentage.setText(answerStat.getPourcentage()+"%");

        // Initalisation de la ProgressBar
        viewHolder.pourcentage.setMax(100);
        viewHolder.pourcentage.setProgress(answerStat.getPourcentage());

        return convertView;
    }
}
