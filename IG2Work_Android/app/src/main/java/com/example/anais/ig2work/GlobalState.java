package com.example.anais.ig2work;

import android.app.Application;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

public class GlobalState extends Application {

    @Override
    public void onCreate() {
        super.onCreate();
    }

    /*
    Vérifie la connectivité de l'appareil
     */
    public boolean verifReseau() {
        ConnectivityManager cnMngr = (ConnectivityManager) getSystemService(CONNECTIVITY_SERVICE);
        NetworkInfo netInfo = cnMngr.getActiveNetworkInfo();

        Boolean bStatut = false;

        if (netInfo != null) {
            NetworkInfo.State netState = netInfo.getState();

            if (netState.compareTo(NetworkInfo.State.CONNECTED) == 0) {
                bStatut = true; // L'appareil est connecté au réseau
            }
        }

        return bStatut;
    }
}
