package com.example.anais.ig2work.Model;

import android.animation.ObjectAnimator;
import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.DecelerateInterpolator;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.example.anais.ig2work.DataBase.RequestActivity;
import com.example.anais.ig2work.R;
import com.example.anais.ig2work.Utils.StringUtils;

import org.json.JSONObject;

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
        public ImageView view;
    }

    private class QuestionViewHolder {
        public TextView title;
        public CheckBox realized;
        public ImageView view;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        if(convertView == null){
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.row_task_layout,parent, false);
        }

        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(convertView.getContext());
        final int idUser = preferences.getInt(StringUtils.IDUSER.toString(), 0);

        TaskAdapter.TaskViewHolder viewHolder = (TaskAdapter.TaskViewHolder) convertView.getTag();
        if(viewHolder == null){
            viewHolder = new TaskAdapter.TaskViewHolder();
            viewHolder.title = (TextView) convertView.findViewById(R.id.title);
            viewHolder.realized = (CheckBox) convertView.findViewById(R.id.checkBox);
            viewHolder.view = (ImageView) convertView.findViewById(R.id.visible);
            convertView.setTag(viewHolder);
        }

        final Task task = getItem(position);
        viewHolder.title.setText(task.getTitre());
        viewHolder.realized.setChecked(task.isRealized());
        final CheckBox cb = viewHolder.realized;
        viewHolder.realized.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setChecked(cb, task.getType(), task.getId(), idUser, !task.isRealized());
            }
        });

        ((ImageView)convertView.findViewById(R.id.logo)).setImageResource(R.drawable.logo_homework);

        //Affichage en fonction du rôle
        if(StringUtils.ENSEIGNANT.toString().equals(preferences.getString(StringUtils.ROLE.toString(), ""))) {
            final ImageView img = (ImageView) convertView.findViewById(R.id.visible);

            viewHolder.realized.setVisibility(View.GONE);
            //Reduit la zone de texte
            ViewGroup.MarginLayoutParams p = (ViewGroup.MarginLayoutParams) viewHolder.title.getLayoutParams();
            p.setMarginEnd(170);
            viewHolder.title.setLayoutParams(p);
            if(task.isVisible()) {
                viewHolder.view.setImageResource(R.drawable.is_visible);

            } else {
                viewHolder.view.setImageResource(R.drawable.not_visible);
            }
            viewHolder.view.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    setVisible(img,  task.getId(), task.getType(), !task.isVisible(), idUser);
                }
            });
        }

        //Affichage en fonction du type
        if (task.getType().equals("Tache")) {
            viewHolder.realized.setChecked(task.isRealized());
            ((ImageView)convertView.findViewById(R.id.logo)).setImageResource(R.drawable.logo_task);
        } else {
            viewHolder.realized.setChecked(task.isRealized());
            ((ImageView)convertView.findViewById(R.id.logo)).setImageResource(R.drawable.logo_quest);
        }

        return convertView;
    }

    public void setVisible(final ImageView img, final int idTask, final String type, final boolean isVisible, final int idUser) {
        if(type.equals("Tache")) {
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
                                TaskAdapter.this.setVisible(img,  idTask, type, !isVisible, idUser);
                            }
                        });
                    }
                }
            }.envoiRequete("setVisibleTache", "action=setTacheVisible&idTache="+idTask+"&isVisible="+isVisible+"&idUser="+idUser);
        } else {
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
                                TaskAdapter.this.setVisible(img,  idTask, type, !isVisible, idUser);
                            }
                        });
                    }
                }
            }.envoiRequete("setVisibleTache", "action=setQuestionVisible&idQuestion="+idTask+"&isVisible="+isVisible+"&idUser="+idUser);
        }
    }

    public void setChecked(final CheckBox cb, final String type, final int idTask, final int idUser, final boolean realized) {
        if(type.equals("Tache")) {
            new RequestActivity() {
                @Override
                public void traiteReponse(JSONObject o, String action) {
                    if(!o.isNull("retour"))  {
                        cb.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                TaskAdapter.this.setChecked(cb, type, idTask, idUser, !realized);
                            }
                        });
                    }
                }
            }.envoiRequete("realizedTache", "action=realizedTache&idTache="+idTask+"&realized="+realized+"&idStudent="+idUser);
        } else {
            new RequestActivity() {
                @Override
                public void traiteReponse(JSONObject o, String action) {
                    if(!o.isNull("retour"))  {
                        cb.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                TaskAdapter.this.setChecked(cb, type, idTask, idUser, !realized);
                            }
                        });
                    }
                }
            }.envoiRequete("realizedQuestion", "action=realizedQuestion&idQuestion="+idTask+"&realized="+realized+"&idStudent="+idUser);
        }
    }
}
