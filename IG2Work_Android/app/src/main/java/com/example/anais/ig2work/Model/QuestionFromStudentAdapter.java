package com.example.anais.ig2work.Model;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.anais.ig2work.DataBase.RequestActivity;
import com.example.anais.ig2work.R;
import com.example.anais.ig2work.Utils.StringUtils;

import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.List;
import java.util.Locale;

/**
 * Created by clementruffin on 26/01/2017.
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
    public View getView(int position, View convertView, ViewGroup parent) {

        if(convertView == null){
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.row_question_student_layout,parent, false);
        }

        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(convertView.getContext());
        final int idUser = preferences.getInt(StringUtils.IDUSER.toString(), 0);

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

        //Affichage en fonction du rôle
        if(StringUtils.ENSEIGNANT.toString().equals(preferences.getString(StringUtils.ROLE.toString(), ""))) {
            ((ImageButton) convertView.findViewById(R.id.btnAnswer)).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    //Vue pour répondre
                }
            });
        } else {
            convertView.findViewById(R.id.btnAnswer).setVisibility(View.GONE);
        }



        return convertView;
    }
}
