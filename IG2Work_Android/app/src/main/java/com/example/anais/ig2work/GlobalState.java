package com.example.anais.ig2work;

import android.app.Application;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

/**
 * Created by Utilisateur on 26/11/2016.
 */

public class GlobalState extends Application {

    @Override
    public void onCreate() {
        super.onCreate();
    }

    public String getOneString(int resID) {
        return getString(resID);
    }
    public boolean verifReseau() {
        // On vérifie si le réseau est disponible,
        // si oui on change le statut du bouton de connexion
        ConnectivityManager cnMngr = (ConnectivityManager) getSystemService(CONNECTIVITY_SERVICE);
        NetworkInfo netInfo = cnMngr.getActiveNetworkInfo();

        String sType = "Aucun réseau détecté";
        Boolean bStatut = false;
        if (netInfo != null) {
            NetworkInfo.State netState = netInfo.getState();

            if (netState.compareTo(NetworkInfo.State.CONNECTED) == 0) {
                bStatut = true;
                int netType= netInfo.getType();
                switch (netType) {
                    case ConnectivityManager.TYPE_MOBILE :
                        sType = "Réseau mobile détecté"; break;
                    case ConnectivityManager.TYPE_WIFI :
                        sType = "Réseau wifi détecté"; break;
                }
            }
        }

        return bStatut;
    }
}
