package com.example.anais.ig2work.Model;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.example.anais.ig2work.R;
import com.example.anais.ig2work.Utils.StringUtils;

import java.util.List;

/**
 * Created by clementruffin on 26/01/2017.
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

        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(convertView.getContext());
        final int idUser = preferences.getInt(StringUtils.IDUSER.toString(), 0);

        AnswersViewHolder viewHolder = (AnswersViewHolder) convertView.getTag();
        if(viewHolder == null){
            viewHolder = new AnswersViewHolder();
            viewHolder.answer = (TextView) convertView.findViewById(R.id.question);
            viewHolder.textPourcentage = (TextView) convertView.findViewById(R.id.textPourcentage);
            viewHolder.pourcentage = (ProgressBar) convertView.findViewById(R.id.progressBar2);
            convertView.setTag(viewHolder);
        }

        final AnswerFromQuestionStat answerStat = getItem(position);
        viewHolder.answer.setText(answerStat.getAnswer());
        viewHolder.textPourcentage.setText(answerStat.getPourcentage()+"%");

        //Initalisation de la progressBar
        viewHolder.pourcentage.setMax(100);
        viewHolder.pourcentage.setProgress(answerStat.getPourcentage());

        //Affichage en fonction du rôle
        /*if(StringUtils.ENSEIGNANT.toString().equals(preferences.getString(StringUtils.ROLE.toString(), ""))) {
            ((ImageButton) convertView.findViewById(R.id.btnAnswer)).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    //Vue pour répondre
                    DialogFragment newFragment = FragmentAnswerQuesFromStudent.newInstance(question.getId(), question.getAnwser());
                    newFragment.show(((TaskActivity) parent.getContext()).getFragmentManager(), "dialog");
                }
            });
        } else {
            convertView.findViewById(R.id.btnAnswer).setVisibility(View.GONE);
        }*/

        return convertView;
    }
}
