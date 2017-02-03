package com.example.anais.ig2work;

import android.app.DialogFragment;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import com.example.anais.ig2work.DataBase.RequestActivity;
import com.example.anais.ig2work.Utils.StringUtils;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

/**
 * PopUp d'ajout contenant les différents boutons
 * Created by Utilisateur on 31/01/2017.
 */

public class AjoutFragment extends DialogFragment {
    private String idUser;
    private String idSeance;

    private SeanceActivity callingActivity;

    protected View v;

    /**
     * Permet de récupérer le variable utile pour le dialog
     * @param idSeance
     * @param idUser
     * @param role
     * @return
     */
    /*static AjoutFragment newInstance(String idSeance, String idUser, String role) {
        AjoutFragment f = new AjoutFragment();

        //Transforme idIngredient en argument
        Bundle args = new Bundle();

        args.putString("role", role);
        args.putString("idUser", idUser);
        args.putString("idSeance", idSeance);

        f.setArguments(args);
        return f;
    }*/

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        Log.e("Argument", getArguments().getString("idUser")+" "+getArguments().getString("idSeance")
                +" "+getArguments().getString("role"));
        idUser = getArguments().getString("idUser");
        idSeance = getArguments().getString("idSeance");

        callingActivity = (SeanceActivity) getActivity();

        if(getArguments().getString("role").equals("teacher")) {
            v = inflater.inflate(R.layout.ajout_fragment_teacher, container, false);

            v.findViewById(R.id.addtask).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent();
                    intent = new Intent(callingActivity, AddTaskActivity.class);
                    startActivity(intent);
                }
            });

            v.findViewById(R.id.addHomework).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent();
                    intent = new Intent(callingActivity, AddHomework.class);
                    startActivity(intent);
                }
            });

            v.findViewById(R.id.addQuestion).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent();
                    intent = new Intent(callingActivity, AddQuestionActivity.class);
                    startActivity(intent);
                }
            });
        } else {
            v = inflater.inflate(R.layout.ajout_fragment, container, false);

            isLost(idSeance, idUser);

            v.findViewById(R.id.lost).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    setLost(idSeance, idUser);
                }
            });
        }

        v.findViewById(R.id.addNote).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent();
                intent = new Intent(callingActivity, AddNoteActivity.class);
                startActivity(intent);
            }
        });

        return v;
    }

    public void isLost(final String idSeance, final String idUser) {
        final boolean isLost = false;
        new RequestActivity() {
            @Override
            public void traiteReponse(JSONObject o, String action) {
                try {
                    JSONArray array = o.getJSONArray("retour");
                    if(array.getJSONObject(0) != null) {
                        //L'étudiant est déjà perdu
                        ((Button)v.findViewById(R.id.lost)).setEnabled(false);
                        ((Button)v.findViewById(R.id.lost)).setBackgroundColor(Color.GRAY);
                    }

                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }.envoiRequete("login", "action=isLost&idSeance="+idSeance+"&idUser="+idUser);
    }

    public void setLost(final String idSeance, final String idUser) {
        new RequestActivity() {
            @Override
            public void traiteReponse(JSONObject o, String action) {
                getDialog().dismiss();
            }
        }.envoiRequete("login", "action=setLost&idSeance="+idSeance+"&idUser="+idUser);
    }
}
