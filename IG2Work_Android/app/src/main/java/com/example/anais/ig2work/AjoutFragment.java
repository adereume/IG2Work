package com.example.anais.ig2work;

import android.app.DialogFragment;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.anais.ig2work.DataBase.RequestActivity;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

/**
 * La classe AjoutFragment gère la popup d'ajout au sein d'une séance.
 * Elle permet à un enseignant d'ajouter un élément de séance (tâche, question, devoir ou note).
 * Elle permet à un étudiant de signaler qu'il est perdu ou d'ajouter une note personnelle.
 */

public class AjoutFragment extends DialogFragment {
    private String idUser;
    private String idSeance;

    private SeanceActivity callingActivity;

    protected View v;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Récupération des paramètres
        idUser = getArguments().getString("idUser");
        idSeance = getArguments().getString("idSeance");

        callingActivity = (SeanceActivity) getActivity();

        // Affichage d'une popup différente selon le type de l'utilisateur
        if(getArguments().getString("role").equals("teacher")) {
            v = inflater.inflate(R.layout.ajout_fragment_teacher, container, false);

            // Ajouter un tâche
            v.findViewById(R.id.addtask).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(callingActivity, AddTaskActivity.class);
                    startActivity(intent);
                    getDialog().dismiss();
                }
            });

            // Ajouter un devoir
            v.findViewById(R.id.addHomework).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(callingActivity, AddHomework.class);
                    startActivity(intent);
                    getDialog().dismiss();
                }
            });

            // Ajouter une question
            v.findViewById(R.id.addQuestion).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(callingActivity, AddQuestionActivity.class);
                    startActivity(intent);
                    getDialog().dismiss();
                }
            });
        } else {
            v = inflater.inflate(R.layout.ajout_fragment, container, false);

            isLost(idSeance, idUser);

            // Signaler perdu
            v.findViewById(R.id.lost).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    setLost(idSeance, idUser);
                }
            });
        }

        // Ajouter une note
        v.findViewById(R.id.addNote).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(callingActivity, AddNoteActivity.class);
                startActivity(intent);
                getDialog().dismiss();
            }
        });

        return v;
    }

    /*
    Signale un étudiant comme perdu au sein de la séance
     */
    public void setLost(final String idSeance, final String idUser) {
        new RequestActivity() {
            @Override
            public void traiteReponse(JSONObject o, String action) {
                getDialog().dismiss(); // Fermeture de la popup
            }
        }.envoiRequete("login", "action=setLost&idSeance="+idSeance+"&idUser="+idUser);
    }

    /*
    Vérifie que l'étudiant ne s'est pas déjà signalé perdu.
    Tout le temps que l'enseignant n'a pas réinitialisé, l'étudiant ne peut renouveler le signalement.
     */
    public void isLost(final String idSeance, final String idUser) {
        new RequestActivity() {
            @Override
            public void traiteReponse(JSONObject o, String action) {
                try {
                    JSONArray array = o.getJSONArray("retour");
                    if(array.getJSONObject(0) != null) {
                        // L'étudiant est déjà perdu
                        // Le bouton est grisé
                        v.findViewById(R.id.lost).setEnabled(false);
                        v.findViewById(R.id.lost).setBackgroundColor(Color.GRAY);
                    }

                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }.envoiRequete("login", "action=isLost&idSeance="+idSeance+"&idUser="+idUser);
    }
}
