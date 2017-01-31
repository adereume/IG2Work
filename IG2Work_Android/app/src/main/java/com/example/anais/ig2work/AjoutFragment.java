package com.example.anais.ig2work;

import android.app.DialogFragment;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.anais.ig2work.DataBase.RequestActivity;
import com.example.anais.ig2work.Utils.StringUtils;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * PopUp d'ajout contenant les différents boutons
 * Created by Utilisateur on 31/01/2017.
 */

public class AjoutFragment extends DialogFragment {
    private String idUser;
    private String idSeance;
    protected View v;

    /**
     * Permet de récupérer le variable utile pour le dialog
     * @param idSeance
     * @param idUser
     * @param role
     * @return
     */
    static AjoutFragment newInstance(String idSeance, String idUser, String role) {
        AjoutFragment f = new AjoutFragment();

        //Transforme idIngredient en argument
        Bundle args = new Bundle();

        args.putString("role", role);
        args.putString("idUser", idUser);
        args.putString("idSeance", idSeance);

        f.setArguments(args);
        return f;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        idUser = getArguments().getString("idUser");
        idSeance = getArguments().getString("idSeance");

        if(getArguments().getString("role").equals(StringUtils.ENSEIGNANT)) {
            v = inflater.inflate(R.layout.ajout_fragment_teacher, container, false);
        } else {
            v = inflater.inflate(R.layout.ajout_fragment, container, false);

            isLost(idSeance, idUser);
        }

        return v;
    }

    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.addtask:
                break;
            case R.id.addHomework:
                break;
            case R.id.addQuestion:
                break;
            case R.id.addNote:
                break;
            case R.id.lost:
                setLost(idSeance, idUser);
                break;
        }
    }

    public void isLost(final String idSeance, final String idUser) {
        final boolean isLost = false;
        new RequestActivity() {
            @Override
            public void traiteReponse(JSONObject o, String action) {

                try {
                    if(!o.isNull("retour")) {
                        //L'étudiant est déjà perdu
                        Log.e("Retour", o.getString("retour"));
                        v.findViewById(R.id.lost).setEnabled(true);
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
