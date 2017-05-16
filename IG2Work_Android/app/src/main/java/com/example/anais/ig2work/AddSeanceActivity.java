package com.example.anais.ig2work;

import android.app.DatePickerDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.app.TimePickerDialog;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.text.format.DateFormat;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.CheckedTextView;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import com.example.anais.ig2work.DataBase.RequestActivity;
import com.example.anais.ig2work.Utils.StringUtils;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

public class AddSeanceActivity extends AppCompatActivity {

    private SharedPreferences preferences;

    private Spinner selectModule;
    private Spinner selectType;
    private Spinner selectGroupe;
    private EditText room;
    private static EditText startDate;
    private static EditText endDate;
    private static boolean start;

    private List<Integer> listModuleId;
    private List<String> listModuleNames;
    private List<Integer> listPromoId;
    private List<String> listPromoNames;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_seance);

        setTitle("Ajout Séance");

        //Le bouton retour à gauche de la barre d'action
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        preferences = PreferenceManager.getDefaultSharedPreferences(this);

        selectModule = (Spinner) findViewById(R.id.select_module);
        selectType = (Spinner) findViewById(R.id.select_type);
        selectGroupe = (Spinner) findViewById(R.id.select_groupe);
        room = (EditText) findViewById(R.id.room);
        startDate = (EditText) findViewById(R.id.startDate);
        endDate = (EditText) findViewById(R.id.endDate);

        selectType.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                switch (i) {
                    case 0 :    getListPromos();    break;
                    case 1 :    getListTDs();       break;
                    case 2 :    getListTPs();       break;
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {
                selectGroupe.setAdapter(null);
            }
        });

        startDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                start = true;
                showTimePickerDialog(v);
                showDatePickerDialog(v);
            }
        });
        startDate.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if(hasFocus) {
                    start = true;
                    showTimePickerDialog(v);
                    showDatePickerDialog(v);
                }
            }
        });

        endDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                start = false;
                showTimePickerDialog(v);
                showDatePickerDialog(v);
            }
        });
        endDate.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if(hasFocus) {
                    start = false;
                    showTimePickerDialog(v);
                    showDatePickerDialog(v);
                }
            }
        });

        listModuleId = new ArrayList<>();
        listModuleNames = new ArrayList<>();
        listPromoId = new ArrayList<>();
        listPromoNames = new ArrayList<>();

        getListModules();
        getListPromos();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_ajout, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.menu_add:
                Log.d("action", "Ajouter");
                attemptAddSeance();
                break;
            case android.R.id.home:
                this.finish();
                break;

        }

        return super.onOptionsItemSelected(item);
    }

    public void getListModules() {
        new RequestActivity() {
            @Override
            public void traiteReponse(JSONObject json_data, String action) {
                try {
                    if(!json_data.isNull("retour")) {

                        JSONArray modules = json_data.getJSONArray("retour");

                        listModuleId.add(0);
                        listModuleNames.add("Sélectionner le module ...");
                        for (int i = 0; i < modules.length(); i++) {
                            JSONObject o = modules.getJSONObject(i);

                            listModuleId.add(o.getInt("id"));
                            listModuleNames.add(o.getString("name"));
                        }

                        ArrayAdapter<String> adapter = new ArrayAdapter<>(AddSeanceActivity.this, android.R.layout.simple_spinner_item, listModuleNames);
                        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                        selectModule.setAdapter(adapter);

                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                    Toast.makeText(AddSeanceActivity.this, "Une erreur est survenu", Toast.LENGTH_LONG).show();
                }
            }
        }.envoiRequete("getModules", "action=getModule");
    }

    public void getListPromos() {
        new RequestActivity() {
            @Override
            public void traiteReponse(JSONObject json_data, String action) {
                try {
                    if(!json_data.isNull("retour")) {

                        JSONArray promotions = json_data.getJSONArray("retour");
                        listPromoId = new ArrayList<>();
                        listPromoNames = new ArrayList<>();

                        listPromoId.add(0);
                        listPromoNames.add("Sélectionner la promo ...");
                        for (int i = 0; i < promotions.length(); i++) {
                            JSONObject o = promotions.getJSONObject(i);

                            listPromoId.add(o.getInt("id"));
                            listPromoNames.add(o.getString("name"));
                        }

                        ArrayAdapter<String> adapter = new ArrayAdapter<>(AddSeanceActivity.this, android.R.layout.simple_spinner_item, listPromoNames);
                        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                        selectGroupe.setAdapter(adapter);

                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                    Toast.makeText(AddSeanceActivity.this, "Une erreur est survenu", Toast.LENGTH_LONG).show();
                }
            }
        }.envoiRequete("getPromo", "action=getPromo");
    }

    public void getListTDs() {
        new RequestActivity() {
            @Override
            public void traiteReponse(JSONObject json_data, String action) {
                try {
                    if(!json_data.isNull("retour")) {

                        JSONArray tds = json_data.getJSONArray("retour");
                        listPromoId = new ArrayList<>();
                        listPromoNames = new ArrayList<>();

                        listPromoId.add(0);
                        listPromoNames.add("Sélectionner le groupe TD ...");
                        for (int i = 0; i < tds.length(); i++) {
                            JSONObject o = tds.getJSONObject(i);

                            listPromoId.add(o.getInt("id"));
                            listPromoNames.add(o.getString("name"));
                        }

                        ArrayAdapter<String> adapter = new ArrayAdapter<>(AddSeanceActivity.this, android.R.layout.simple_spinner_item, listPromoNames);
                        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                        selectGroupe.setAdapter(adapter);

                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                    Toast.makeText(AddSeanceActivity.this, "Une erreur est survenu", Toast.LENGTH_LONG).show();
                }
            }
        }.envoiRequete("getAllTD", "action=getAllTD");
    }

    public void getListTPs() {
        new RequestActivity() {
            @Override
            public void traiteReponse(JSONObject json_data, String action) {
                try {
                    if(!json_data.isNull("retour")) {

                        JSONArray tps = json_data.getJSONArray("retour");
                        listPromoId = new ArrayList<>();
                        listPromoNames = new ArrayList<>();

                        listPromoId.add(0);
                        listPromoNames.add("Sélectionner le groupe TP ...");
                        for (int i = 0; i < tps.length(); i++) {
                            JSONObject o = tps.getJSONObject(i);

                            listPromoId.add(o.getInt("id"));
                            listPromoNames.add(o.getString("name"));
                        }

                        ArrayAdapter<String> adapter = new ArrayAdapter<>(AddSeanceActivity.this, android.R.layout.simple_spinner_item, listPromoNames);
                        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                        selectGroupe.setAdapter(adapter);

                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                    Toast.makeText(AddSeanceActivity.this, "Une erreur est survenu", Toast.LENGTH_LONG).show();
                }
            }
        }.envoiRequete("getAllTP", "action=getAllTP");
    }

    public void showTimePickerDialog(View v) {
        DialogFragment newFragment = new AddSeanceActivity.TimePickerFragment();
        newFragment.show(getFragmentManager(), "timePicker");
    }

    public void showDatePickerDialog(View v) {
        DialogFragment newFragment = new AddSeanceActivity.DatePickerFragment();
        newFragment.show(getFragmentManager(), "datePicker");
    }

    public static class TimePickerFragment extends DialogFragment implements TimePickerDialog.OnTimeSetListener {
        @Override
        public Dialog onCreateDialog(Bundle savedInstanceState) {
            final Calendar c = Calendar.getInstance();
            int hour = c.get(Calendar.HOUR_OF_DAY);
            int minute = c.get(Calendar.MINUTE);

            return new TimePickerDialog(getActivity(), this, hour, minute,
                    DateFormat.is24HourFormat(getActivity()));
        }

        public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
            if (start) {
                startDate.setText(startDate.getText() + " - " + hourOfDay + ":" + minute);
            } else {
                endDate.setText(endDate.getText() + " - " + hourOfDay + ":" + minute);
            }
        }
    }

    public static class DatePickerFragment extends DialogFragment implements DatePickerDialog.OnDateSetListener {

        @Override
        public Dialog onCreateDialog(Bundle savedInstanceState) {
            final Calendar c = Calendar.getInstance();
            int year = c.get(Calendar.YEAR);
            int month = c.get(Calendar.MONTH);
            int day = c.get(Calendar.DAY_OF_MONTH);

            return new DatePickerDialog(getActivity(), this, year, month, day);
        }

        public void onDateSet(DatePicker view, int year, int month, int day) {
            if (start) {
                startDate.setText(day + "/" + (month + 1) + "/" + year);
            } else {
                endDate.setText(day + "/" + (month + 1) + "/" + year);
            }
        }
    }

    private void attemptAddSeance() {
        // Reset errors.
        ((TextView)selectModule.getSelectedView()).setError(null);
        ((TextView)selectGroupe.getSelectedView()).setError(null);
        room.setError(null);
        startDate.setError(null);
        endDate.setError(null);

        boolean cancel = false;
        View focusView = null;

        Date startDateTmp = null;
        Date endDateTmp = null;

        int moduleId = listModuleId.get(selectModule.getSelectedItemPosition());
        int promoId = listPromoId.get(selectGroupe.getSelectedItemPosition());
        String roomTxt = room.getText().toString();
        String startDateTxt = startDate.getText().toString();
        String endDateTxt = endDate.getText().toString();

        // Vérifier si les champs sont remplie
        if(selectModule.getSelectedItemPosition() == 0) {
            ((TextView) selectModule.getSelectedView()).setError(getString(R.string.error_field_required));
            focusView = selectModule;
            cancel = true;
        }

        if(selectGroupe.getSelectedItemPosition() == 0) {
            ((TextView) selectGroupe.getSelectedView()).setError(getString(R.string.error_field_required));
            focusView = selectGroupe;
            cancel = true;
        }

        if (TextUtils.isEmpty(startDateTxt)) {
            startDate.setError(getString(R.string.error_field_required));
            focusView = startDate;
            cancel = true;
        } else {
            try {
                startDateTmp = new SimpleDateFormat("d/MM/yyyy - h:m").parse(startDateTxt);

                //Vérifier si la date n'est pas dans le passè
                if(startDateTmp.before(new Date())) {
                    startDate.setError("La date ne doit pas être dans le passè");
                    focusView = startDate;
                    cancel = true;
                }

                //Ne pas mettre l'échéance le week-end
                String startJour = new SimpleDateFormat("EEEE").format(startDateTmp);
                if (startJour.equals("samedi") || startJour.equals("dimanche")) {
                    startDate.setError("La date ne peux pas être durant le weekend");
                    focusView = startDate;
                    cancel = true;
                }
            } catch (ParseException e) {
                e.printStackTrace();
            }
        }

        if (TextUtils.isEmpty(endDateTxt)) {
            endDate.setError(getString(R.string.error_field_required));
            focusView = endDate;
            cancel = true;
        } else {
            try {
                endDateTmp = new SimpleDateFormat("d/MM/yyyy - h:m").parse(endDateTxt);
                Log.e("Error", endDateTmp.toString());
                //Vérifier si la date n'est pas dans le passè
                if(endDateTmp.before(new Date())) {
                    endDate.setError("La date ne doit pas être dans le passè");
                    focusView = endDate;
                    cancel = true;
                }

                //Ne pas mettre l'échéance le week-end
                String endJour = new SimpleDateFormat("EEEE").format(endDateTmp);
                if (endJour.equals("samedi") || endJour.equals("dimanche")) {
                    endDate.setError("La date ne peux pas être durant le weekend");
                    focusView = endDate;
                    cancel = true;
                }
            } catch (ParseException e) {
                e.printStackTrace();
            }
        }

        if (TextUtils.isEmpty(roomTxt)) {
            room.setError(getString(R.string.error_field_required));
            focusView = room;
            cancel = true;
        }

        if (cancel) {
            focusView.requestFocus();
        } else {
            addSeance(moduleId,
                    promoId,
                    roomTxt,
                    new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(startDateTmp),
                    new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(endDateTmp));
        }
    }

    public void addSeance(final int idModule, final int idPromo, final String room, final String startDate, final String endDate) {
        new RequestActivity() {
            @Override
            public void traiteReponse(JSONObject json_data, String action) {
                try {
                    if(!json_data.isNull("retour")) {
                        json_data.getString("retour");
                        AddSeanceActivity.this.finish();
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                    Toast.makeText(AddSeanceActivity.this, "Une erreur est survenu", Toast.LENGTH_LONG).show();
                }
            }
        }.envoiRequete("addSeance", "action=addSeance&idTeacher=" + preferences.getInt(StringUtils.IDUSER.toString(), 0) + "&idModule=" + idModule + "&idPromo=" + idPromo + "&dayTime=" + startDate + "&dayTimeEnd=" + endDate + "&room=" + room);
    }
}
