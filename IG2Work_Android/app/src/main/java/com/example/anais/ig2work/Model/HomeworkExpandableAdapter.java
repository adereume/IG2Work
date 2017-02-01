package com.example.anais.ig2work.Model;

import android.content.Context;
import android.graphics.Typeface;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
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

public class HomeworkExpandableAdapter extends BaseExpandableListAdapter {
    private Context context;
    private ExpandableListView listView;
    private List<String> listDataHeader;
    private HashMap<String, List<Homework>> listDataChild;

    private int lastExpandedGroupPosition = -1;

    public HomeworkExpandableAdapter(Context context, ExpandableListView listView, List<String> listDataHeader, HashMap<String, List<Homework>> listChildData) {
        this.context = context;
        this.listView = listView;
        this.listDataHeader = listDataHeader;
        this.listDataChild = listChildData;
    }

    private class HomeworkViewHolder{
        public TextView title;
        public TextView dueDate;
        public CheckBox realized;
    }

    @Override
    public View getGroupView(int groupPosition, boolean isExpanded, View convertView, ViewGroup parent) {
        String headerTitle = (String) getGroup(groupPosition);

        if (convertView == null) {
            LayoutInflater infalInflater = (LayoutInflater) this.context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = infalInflater.inflate(R.layout.list_group, null);
        }

        TextView lblListHeader = (TextView) convertView.findViewById(R.id.lblListHeader);
        lblListHeader.setTypeface(null, Typeface.BOLD);
        lblListHeader.setText(headerTitle);

        return convertView;
    }

    @Override
    public View getChildView(int groupPosition, final int childPosition, boolean isLastChild, View convertView, ViewGroup parent) {

        if (convertView == null) {
            LayoutInflater infalInflater = (LayoutInflater) this.context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = infalInflater.inflate(R.layout.row_homework_layout, null);
        }

        HomeworkViewHolder viewHolder = (HomeworkViewHolder) convertView.getTag();

        if(viewHolder == null){
            viewHolder = new HomeworkViewHolder();
            viewHolder.title = (TextView) convertView.findViewById(R.id.title);
            viewHolder.dueDate = (TextView) convertView.findViewById(R.id.dueDate);
            viewHolder.realized = (CheckBox) convertView.findViewById(R.id.checkBox);
            convertView.setTag(viewHolder);
        }

        Homework homework = getChild(groupPosition, childPosition);

        viewHolder.title.setText(homework.getModule() + " - " + homework.getTitre());
        viewHolder.dueDate.setText(new SimpleDateFormat("dd MMMM yyyy à HH:mm", Locale.FRANCE).format(homework.getDueDate()));
        viewHolder.realized.setChecked(homework.isRealized());

        return convertView;
    }

    @Override
    public Homework getChild(int groupPosition, int childPosititon) {
        return this.listDataChild.get(this.listDataHeader.get(groupPosition)).get(childPosititon);
    }

    @Override
    public long getChildId(int groupPosition, int childPosition) {
        return childPosition;
    }

    @Override
    public int getChildrenCount(int groupPosition) {
        return this.listDataChild.get(this.listDataHeader.get(groupPosition)).size();
    }

    @Override
    public Object getGroup(int groupPosition) {
        return this.listDataHeader.get(groupPosition);
    }

    @Override
    public int getGroupCount() {
        return this.listDataHeader.size();
    }

    @Override
    public long getGroupId(int groupPosition) {
        return groupPosition;
    }

    @Override
    public boolean hasStableIds() {
        return false;
    }

    @Override
    public boolean isChildSelectable(int groupPosition, int childPosition) {
        return true;
    }

    @Override
    public void onGroupExpanded(int groupPosition) {

        // Lorsque l'on clique sur un groupe (et donc qu'on l'ouvre)
        // si un autre groupe était ouvert, on le collapse.
        // On ne permet d'afficher qu'un seul groupe à la fois.
        if(groupPosition != lastExpandedGroupPosition){
            listView.collapseGroup(lastExpandedGroupPosition);
        }

        super.onGroupExpanded(groupPosition);
        lastExpandedGroupPosition = groupPosition;
    }
}
