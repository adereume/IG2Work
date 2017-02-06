package com.example.anais.ig2work.Model;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.TextView;

import com.example.anais.ig2work.R;

import java.util.List;

/**
 * Created by clementruffin on 06/02/2017.
 */

public class TaskAdapter extends ArrayAdapter<Task> {

    public TaskAdapter(Context context, List<Task> tasks) {
        super(context, 0, tasks);
    }

    private class TaskViewHolder{
        public TextView title;
        public CheckBox realized;
    }

    private class QuestionViewHolder {
        public TextView title;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        if(convertView == null){
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.row_task_layout,parent, false);
        }

        TaskAdapter.TaskViewHolder viewHolder = (TaskAdapter.TaskViewHolder) convertView.getTag();
        if(viewHolder == null){
            viewHolder = new TaskAdapter.TaskViewHolder();
            viewHolder.title = (TextView) convertView.findViewById(R.id.title);
            viewHolder.realized = (CheckBox) convertView.findViewById(R.id.checkBox);
            convertView.setTag(viewHolder);
        }

        Task task = getItem(position);
        viewHolder.title.setText(task.getTitre());

        if (task.getType().equals("Tache")) {
            viewHolder.realized.setChecked(task.isRealized());
        } else {
            viewHolder.realized.setVisibility(View.INVISIBLE);
        }

        return convertView;
    }
}
