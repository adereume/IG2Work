package com.example.anais.ig2work.Model;

import android.content.Context;
import android.graphics.Typeface;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.BaseExpandableListAdapter;
import android.widget.CheckBox;
import android.widget.ExpandableListView;
import android.widget.TextView;

import com.example.anais.ig2work.R;

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
            convertView.setTag(viewHolder);
        }

        Homework homework = getItem(position);
        viewHolder.title.setText(homework.getTitre());
        viewHolder.dueDate.setText(new SimpleDateFormat("dd MMMM yyyy à HH:mm", Locale.FRANCE).format(homework.getDueDate()));
        viewHolder.realized.setChecked(homework.isRealized());

        return convertView;
    }
}
