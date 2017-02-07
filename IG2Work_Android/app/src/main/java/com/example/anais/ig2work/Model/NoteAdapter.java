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
 * Created by clementruffin on 07/02/2017.
 */

public class NoteAdapter extends ArrayAdapter<Note> {

    public NoteAdapter(Context context, List<Note> notes) {
        super(context, 0, notes);
    }

    private class NoteViewHolder{
        public TextView description;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        if(convertView == null){
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.row_task_layout,parent, false);
        }

        NoteAdapter.NoteViewHolder viewHolder = (NoteAdapter.NoteViewHolder) convertView.getTag();
        if(viewHolder == null){
            viewHolder = new NoteAdapter.NoteViewHolder();
            viewHolder.description = (TextView) convertView.findViewById(R.id.title);
            convertView.setTag(viewHolder);
        }

        Note note = getItem(position);
        viewHolder.description.setText(note.getDescription());

        convertView.findViewById(R.id.checkBox).setVisibility(View.GONE);

        return convertView;
    }
}
