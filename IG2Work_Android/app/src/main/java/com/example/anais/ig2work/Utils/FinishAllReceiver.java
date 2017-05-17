package com.example.anais.ig2work.Utils;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;

/**
 * La classe FinishAllReceiver gère l'arrêt des activités
 */

public class FinishAllReceiver extends BroadcastReceiver {
    RestActivity myAct;

    public static final String FINISH_ALL_ACTIVITIES_ACTIVITY_ACTION = "com.example.clementruffin.androidchat.FINISH_ALL_ACTIVITIES_ACTIVITY_ACTION";

    public FinishAllReceiver(RestActivity a) {
        myAct = a;

        IntentFilter INTENT_FILTER_FINISH_ALL_ACTIVITIES = new IntentFilter();
        INTENT_FILTER_FINISH_ALL_ACTIVITIES.addAction(FINISH_ALL_ACTIVITIES_ACTIVITY_ACTION);

        myAct.registerReceiver(this, INTENT_FILTER_FINISH_ALL_ACTIVITIES);
    }

    public void unRegisterFinishAllReceiver() {
        myAct.unregisterReceiver(this);
    }

    public void closeAllActivities() {
        myAct.sendBroadcast(new Intent(FINISH_ALL_ACTIVITIES_ACTIVITY_ACTION));
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        if (intent.getAction().equals(FINISH_ALL_ACTIVITIES_ACTIVITY_ACTION)) {
            myAct.finish();
        }
    }
}
