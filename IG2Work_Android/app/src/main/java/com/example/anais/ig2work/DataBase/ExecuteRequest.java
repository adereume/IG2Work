package com.example.anais.ig2work.DataBase;

import android.os.AsyncTask;
import android.util.Log;

import com.example.anais.ig2work.Utils.StringUtils;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.EOFException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;

/**
 * Created by Anais on 24/01/2016.
 */

public class ExecuteRequest extends AsyncTask<String, Void, JSONObject> {
    private RequestActivity mAct;
    private String action = null;
    // Une tâche ne peut être exécutée qu'une seule fois

    public ExecuteRequest( RequestActivity act) {
        mAct = act;
    }

    @Override
    protected void onPreExecute() {
        // S'exécute dans l'UI Thread
        super.onPreExecute();
    }

    @Override
    protected JSONObject doInBackground(String... qs) {

        action = qs[1];

        //Récupére les paramétres
        byte[] postData       = qs[0].getBytes( StandardCharsets.UTF_8 );
        int    postDataLength = postData.length;

        String result = "";

        try {
            URL url = new URL(StringUtils.URL.toString());
            Log.i("ExecuteRequest","url utilisée : " + url.toString());
            Log.i("ExecuteRequest","param utilisée : " + qs[0]);
            HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();

            urlConnection.setDoOutput( true );
            urlConnection.setInstanceFollowRedirects( false );
            urlConnection.setRequestMethod( "POST" );
            urlConnection.setRequestProperty( "Content-Type", "application/x-www-form-urlencoded");
            urlConnection.setRequestProperty( "charset", "utf-8");
            urlConnection.setRequestProperty( "Content-Length", Integer.toString( postDataLength ));
            urlConnection.setUseCaches( false );
            urlConnection.getOutputStream().write(postData);

            InputStream in = new BufferedInputStream(urlConnection.getInputStream());
            result = convertStreamToString(in);
            urlConnection.disconnect();

        } catch (EOFException e) {
            Log.e("ExecuteRequest", "Une erreur est survenu : " + e);
        } catch (Exception e) {
            Log.e("ExecuteRequest", "Une erreur est survenu : " + e);
        }

        //Convertion en JSON
        JSONObject json;
        try {
            json = new JSONObject(result);

        } catch (JSONException e) {
            e.printStackTrace();
            json = new JSONObject();
        }

        return json;
    }

    protected void onPostExecute(JSONObject result) {
        mAct.traiteReponse(result, action);
    }

    private String convertStreamToString(InputStream in) throws IOException {
        try {
            BufferedReader reader = new BufferedReader(new InputStreamReader(in));
            StringBuilder sb = new StringBuilder();
            String line = null;
            while ((line = reader.readLine()) != null) {
                sb.append(line + "\n");
            }
            return sb.toString();
        }finally {
            try {
                in.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}
