package com.example.anais.ig2work.Model;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.anais.ig2work.R;

import java.util.List;

/**
 * La classe NoteAdapter permet de personnaliser les éléments d'une liste de notes.
 * Pour chaque note, on affiche la description.
 */

public class NoteAdapter extends ArrayAdapter<Note> {

    public NoteAdapter(Context context, List<Note> notes) {
        super(context, 0, notes);
    }

    private class NoteViewHolder{
        public TextView description;
        public ImageView view;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        if(convertView == null){
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.row_note_layout,parent, false);
        }

        NoteAdapter.NoteViewHolder viewHolder = (NoteAdapter.NoteViewHolder) convertView.getTag();
        if(viewHolder == null){
            viewHolder = new NoteAdapter.NoteViewHolder();
            viewHolder.description = (TextView) convertView.findViewById(R.id.description);
            viewHolder.view = (ImageView) convertView.findViewById(R.id.visible);
            convertView.setTag(viewHolder);
        }

        // Affichage des informations relatives à la note
        Note note = getItem(position);
        viewHolder.description.setText(note.getDescription().replace("<br />", ""));
        ((ImageView)convertView.findViewById(R.id.logo)).setImageResource(R.drawable.logo_note_list);

        return convertView;
    }
}
