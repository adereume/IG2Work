package com.example.anais.ig2work.Model;

import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Typeface;
import android.preference.PreferenceManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.BaseExpandableListAdapter;
import android.widget.CheckBox;
import android.widget.ExpandableListView;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.anais.ig2work.DataBase.RequestActivity;
import com.example.anais.ig2work.HomeActivity;
import com.example.anais.ig2work.R;
import com.example.anais.ig2work.Utils.StringUtils;

import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;

/**
 * Created by clementruffin on 26/01/2017.
 */

public class HomeworkAdapter extends ArrayAdapter<Homework> {

    public HomeworkAdapter(Context context, List<Homework> homeworks) {
        super(context, 0, homeworks);
    }

    private class HomeworkViewHolder{
        public TextView title;
        public TextView dueDate;
        public CheckBox realized;
        public ImageView view;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        if(convertView == null){
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.row_homework_layout,parent, false);
        }

        HomeworkViewHolder viewHolder = (HomeworkViewHolder) convertView.getTag();
        if(viewHolder == null){
            viewHolder = new HomeworkViewHolder();
            viewHolder.title = (TextView) convertView.findViewById(R.id.title);
            viewHolder.dueDate = (TextView) convertView.findViewById(R.id.dueDate);
            viewHolder.realized = (CheckBox) convertView.findViewById(R.id.checkBox);
            viewHolder.view = (ImageView) convertView.findViewById(R.id.visible);
            convertView.setTag(viewHolder);
        }

        final Homework homework = getItem(position);
        viewHolder.title.setText(homework.getTitre());
        viewHolder.dueDate.setText(new SimpleDateFormat("dd MMMM yyyy à HH:mm", Locale.FRANCE).format(homework.getDueDate()));
        viewHolder.realized.setChecked(homework.isRealized());

        ((ImageView)convertView.findViewById(R.id.logo)).setImageResource(R.drawable.logo_homework);

        //Affichage en fonction du rôle
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(convertView.getContext());
        if(StringUtils.ENSEIGNANT.toString().equals(preferences.getString(StringUtils.ROLE.toString(), ""))) {
            final int idUser = preferences.getInt(StringUtils.IDUSER.toString(), 0);
            final ImageView img = (ImageView) convertView.findViewById(R.id.visible);

            viewHolder.realized.setVisibility(View.GONE);
            //Reduit la zone de texte
            ViewGroup.MarginLayoutParams p = (ViewGroup.MarginLayoutParams) viewHolder.title.getLayoutParams();
            p.setMarginEnd(170);
            viewHolder.title.setLayoutParams(p);
            if(homework.isVisible()) {
                viewHolder.view.setImageResource(R.drawable.is_visible);
            } else {
                viewHolder.view.setImageResource(R.drawable.not_visible);
            }
            viewHolder.view.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    setVisible(img,  homework.getId(), !homework.isVisible(), idUser);
                }
            });
        }

        return convertView;
    }

    public void setVisible(final ImageView img, final int idHomeWork, final boolean isVisible, final int idUser) {
        new RequestActivity() {
            @Override
            public void traiteReponse(JSONObject o, String action) {
                if(!o.isNull("retour"))  {
                    if(isVisible) {
                        img.setImageResource(R.drawable.is_visible);
                    } else {
                        img.setImageResource(R.drawable.not_visible);
                    }
                    img.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            HomeworkAdapter.this.setVisible(img,  idHomeWork, !isVisible, idUser);
                        }
                    });
                }
            }
        }.envoiRequete("setVisibleHomework", "action=setHomeWorkVisible&idHomeWork="+idHomeWork+"&isVisible="+isVisible+"&idUser="+idUser);
    }
}
