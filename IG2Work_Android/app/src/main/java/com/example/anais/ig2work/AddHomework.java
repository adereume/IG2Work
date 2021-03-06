package com.example.anais.ig2work;

import android.app.DatePickerDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.app.TimePickerDialog;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.support.design.widget.TextInputLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.text.format.DateFormat;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.DatePicker;
import android.widget.TimePicker;
import android.widget.Toast;

import com.example.anais.ig2work.DataBase.RequestActivity;
import com.example.anais.ig2work.Model.Homework;
import com.example.anais.ig2work.Utils.StringUtils;

import org.json.JSONException;
import org.json.JSONObject;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

/**
 * La classe AddHomework gère l'activité d'ajout & d'édition d'un devoir.
 */
public class AddHomework extends AppCompatActivity {
    private SharedPreferences preferences;

    private TextInputLayout mTitleView;
    private static TextInputLayout mDueDateView;
    private TextInputLayout mDescriptionView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_homework);

        // Affichage de la flèche de retour
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        preferences = PreferenceManager.getDefaultSharedPreferences(this);

        mTitleView = (TextInputLayout) findViewById(R.id.titre);
        mDueDateView = (TextInputLayout) findViewById(R.id.dueDate);
        mDueDateView.getEditText().setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showTimePickerDialog(v);
                showDatePickerDialog(v);
            }
        });
        mDueDateView.getEditText().setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if(hasFocus) {
                    showTimePickerDialog(v);
                    showDatePickerDialog(v);
                }
            }
        });
        mDescriptionView = (TextInputLayout) findViewById(R.id.description);

        // Si on a un devoir en paramètre, on se place en mode Edition
        if(this.getIntent().getExtras() != null) {
            setTitle("Edit Devoir");

            Homework homeworkObject = (Homework) getIntent().getSerializableExtra("homeworkObject");

            mTitleView.getEditText().setText(homeworkObject.getTitre());
            mDescriptionView.getEditText().setText(homeworkObject.getDescription());
            mDueDateView.getEditText().setText(String.valueOf(homeworkObject.getDueDate()));
        } else {
            setTitle("Ajout Devoir");
        }
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
                attemptAddHomework();
                break;
            case android.R.id.home: // Retour à la page de séance (ou à l'accueil)
                this.finish();
                break;

        }

        return super.onOptionsItemSelected(item);
    }

    /*
    Tentative d'ajout/mise à jour du devoir
     */
    private void attemptAddHomework() {
        // Réinitialisation des erreurs
        mTitleView.setError(null);
        mDueDateView.setError(null);
        mDescriptionView.setError(null);

        boolean cancel = false;
        View focusView = null;

        // Stockage des valeurs au moment de la tentative d'ajout/mise à jour
        String title = mTitleView.getEditText().getText().toString();
        String date = mDueDateView.getEditText().getText().toString();
        String description = mDescriptionView.getEditText().getText().toString();

        // Vérification des champs
        if (TextUtils.isEmpty(title)) {
            mTitleView.setError(getString(R.string.error_field_required));
            focusView = mTitleView;
            cancel = true;
        }
        if (TextUtils.isEmpty(date)) {
            mDueDateView.setError(getString(R.string.error_field_required));
            focusView = mDueDateView;
            cancel = true;
        }
        if (TextUtils.isEmpty(description)) {
            mDescriptionView.setError(getString(R.string.error_field_required));
            focusView = mDescriptionView;
            cancel = true;
        }

        Date dueDate = null;
        try {
            dueDate = new SimpleDateFormat("d/MM/yyyy - h:m").parse(date);
        } catch (ParseException e) {
            e.printStackTrace();
        }

        // La date ne peut pas être dans le passé
        if(dueDate.before(new Date())) {
            mDueDateView.setError("La date ne doit pas être dans le passè");
            focusView = mDueDateView;
            cancel = true;
        }

        // La date ne peut pas être positionnée sur un week end
        String jour = new SimpleDateFormat("EEEE").format(dueDate);
        if (jour.equals("samedi") || jour.equals("dimanche")) {
            mDueDateView.setError("La date ne peux pas être durant le weekend");
            focusView = mDueDateView;
            cancel = true;
        }

        if (cancel) {
            focusView.requestFocus();
        } else {
            addHomework(title, description, new SimpleDateFormat("yyyy-MM-dd hh:mm:ss").format(dueDate));
        }
    }

    /*
    Ajout/Mise à jour du devoir
     */
    public void addHomework(final String title, final String description, final String dueDate) {
        new RequestActivity() {
            @Override
            public void traiteReponse(JSONObject json_data, String action) {
                try {
                    if(!json_data.isNull("retour")) {
                        json_data.getString("retour");
                        AddHomework.this.finish(); // Retour à la page précédente
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                    Toast.makeText(AddHomework.this, "Une erreur est survenue", Toast.LENGTH_LONG).show();
                }
            }
        }.envoiRequete("addHomeWork", "action=addHomeWork&idSeance="+preferences.getInt("idSeance", 0)+"&idUser="+preferences.getInt(StringUtils.IDUSER.toString(), 0)+"&titre="+title+"&description="+description+"&dueDate="+dueDate);
    }

    public void showDatePickerDialog(View v) {
        DialogFragment newFragment = new DatePickerFragment();
        newFragment.show(getFragmentManager(), "datePicker");
    }

    public void showTimePickerDialog(View v) {
        DialogFragment newFragment = new TimePickerFragment();
        newFragment.show(getFragmentManager(), "timePicker");
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
            mDueDateView.getEditText().setText(day + "/" + (month + 1) + "/" + year);
        }
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
            mDueDateView.getEditText().setText(mDueDateView.getEditText().getText() + " - " + hourOfDay + ":" + minute);
        }
    }

}
