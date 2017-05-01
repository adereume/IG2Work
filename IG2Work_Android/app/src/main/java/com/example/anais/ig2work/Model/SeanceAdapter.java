package com.example.anais.ig2work.Model;

import android.content.Context;
import android.content.SharedPreferences;
import android.database.DataSetObserver;
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
import android.widget.ListAdapter;
import android.widget.TextView;

import com.example.anais.ig2work.R;
import com.example.anais.ig2work.Utils.StringUtils;

import java.text.SimpleDateFormat;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;

/**
 * Created by clementruffin on 26/01/2017.
 */

public class SeanceAdapter extends ArrayAdapter<Seance> {
    public SeanceAdapter(Context context, List<Seance> seances) {
        super(context, 0, seances);
    }

    private class SeanceViewHolder{
        public TextView module;
        public TextView teacherOrPromo;
        public TextView dayTime;
        public TextView room;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        if(convertView == null){
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.row_seance_layout,parent, false);
        }

        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(convertView.getContext());
        final int idUser = preferences.getInt(StringUtils.IDUSER.toString(), 0);

        SeanceAdapter.SeanceViewHolder viewHolder = (SeanceAdapter.SeanceViewHolder) convertView.getTag();
        if(viewHolder == null){
            viewHolder = new SeanceAdapter.SeanceViewHolder();
            viewHolder.module = (TextView) convertView.findViewById(R.id.module);
            viewHolder.teacherOrPromo = (TextView) convertView.findViewById(R.id.teacherOrPromo);
            viewHolder.dayTime = (TextView) convertView.findViewById(R.id.dayTime);
            viewHolder.room = (TextView) convertView.findViewById(R.id.room);
            convertView.setTag(viewHolder);
        }

        final Seance seance = getItem(position);
        viewHolder.module.setText(seance.getModule());

        switch (seance.getTarget()) {
            case "teacher":
                viewHolder.teacherOrPromo.setText(seance.getPromo());
                break;

            case "student":
                viewHolder.teacherOrPromo.setText(seance.getTeacher());
                break;
        }

        viewHolder.dayTime.setText(new SimpleDateFormat("dd MMMM yyyy à HH:mm", Locale.FRANCE).format(seance.getDayTime()));
        viewHolder.room.setText("Salle " + seance.getRoom());

        return convertView;
    }
}
