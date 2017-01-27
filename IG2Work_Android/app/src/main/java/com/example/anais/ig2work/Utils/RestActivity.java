package com.example.anais.ig2work.Utils;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;

import com.example.anais.ig2work.GlobalState;
import com.example.anais.ig2work.R;

/**
 * Created by clementruffin on 27/01/2017.
 */

public abstract class RestActivity extends AppCompatActivity {
    protected GlobalState gs;
    private FinishAllReceiver fa;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Instanciation de la classe GlobalState
        gs = (GlobalState) getApplication();

        // Instanciation de la classe FinishAllReceiver
        fa = new FinishAllReceiver(this);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

        // Fermeture de l'activité courante
        fa.unRegisterFinishAllReceiver();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        switch (id) {

            // Menu 'Se déconnecter'
            case R.id.action_logout:
                fa.closeAllActivities();
                return true;

        }

        return super.onOptionsItemSelected(item);
    }
}
