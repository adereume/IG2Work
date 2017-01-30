package com.example.anais.ig2work;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.annotation.TargetApi;
import android.app.LoaderManager.LoaderCallbacks;
import android.content.CursorLoader;
import android.content.Intent;
import android.content.Loader;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.graphics.Color;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.provider.ContactsContract;
import android.support.design.widget.TextInputEditText;
import android.support.design.widget.TextInputLayout;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.SimpleAdapter;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.example.anais.ig2work.DataBase.RequestActivity;
import com.example.anais.ig2work.Utils.RestActivity;
import com.example.anais.ig2work.Utils.StringUtils;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


/**
 * A login screen that offers login via pseudo/password.
 */
public class SignInActivity extends RestActivity implements LoaderCallbacks<Cursor> {
    // UI references.
    private TextInputLayout mFirstnameView;
    private TextInputLayout mLastnameView;
    private TextInputLayout mPasswordView;
    //Pour la vérification du rôle
    private Spinner mRoleView;
    private TextView mSpinnerText;
    private Spinner mTPView;

    private View mProgressView;
    private View mLoginFormView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signin);

        //Keyboard don't resize view
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN);

        //Hidden the keyboard
        this.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);

        mFirstnameView = (TextInputLayout) findViewById(R.id.firstname);
        mFirstnameView.setOnTouchListener(new View.OnTouchListener() {
            public boolean onTouch(View v, MotionEvent event) {
                mFirstnameView.requestLayout();
                SignInActivity.this.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_PAN);
                return false;
            }
        });

        mLastnameView = (TextInputLayout) findViewById(R.id.lastname);
        mLastnameView.setOnTouchListener(new View.OnTouchListener() {
            public boolean onTouch(View v, MotionEvent event) {
                mLastnameView.requestLayout();
                SignInActivity.this.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_PAN);
                return false;
            }
        });

        mPasswordView = (TextInputLayout) findViewById(R.id.password);
        mPasswordView.setOnTouchListener(new View.OnTouchListener() {
            public boolean onTouch(View v, MotionEvent event) {
                //Keyboard don't resize view
                mPasswordView.requestLayout();
                SignInActivity.this.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_PAN);
                return false;
            }
        });

        mRoleView = (Spinner) findViewById(R.id.role);
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this, R.array.role_array, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        mRoleView.setAdapter(adapter);
        mRoleView.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                mSpinnerText.setVisibility(View.GONE);
                if(position == 2) {
                    findViewById(R.id.layout_promo).setVisibility(View.VISIBLE);
                    getPromo();
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
        mSpinnerText = (TextView) findViewById(R.id.error_role);

        Button button = (Button) findViewById(R.id.incription);
        button.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                attemptSignIn();
            }
        });

        mTPView = (Spinner) findViewById(R.id.tp) ;

        mLoginFormView = findViewById(R.id.login_form);
        mProgressView = findViewById(R.id.login_progress);

        Button buttonLogin = (Button) findViewById(R.id.login);
        buttonLogin.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(SignInActivity.this, LoginActivity.class);
                startActivity(intent);
                finish();
            }
        });
    }

    /**
     * Attempts to sign in or register the account specified by the login form.
     * If there are form errors (invalid email, missing fields, etc.), the
     * errors are presented and no actual login attempt is made.
     */
    private void attemptSignIn() {
        mSpinnerText.setVisibility(View.GONE);
        // Reset errors.
        mFirstnameView.setError(null);
        mLastnameView.setError(null);
        mPasswordView.setError(null);
        mSpinnerText.setError(null);

        // Store values at the time of the login attempt.
        String firstname = mFirstnameView.getEditText().getText().toString();
        String lastname = mLastnameView.getEditText().getText().toString();
        String password = mPasswordView.getEditText().getText().toString();
        String role = mRoleView.getSelectedItem().toString();
        String idTp = null;

        boolean cancel = false;
        View focusView = null;

        // Vérifier si les champs sont remplie
        if (TextUtils.isEmpty(firstname)) {
            mFirstnameView.setError(getString(R.string.error_field_required));
            focusView = mFirstnameView;
            cancel = true;
        }
        if (TextUtils.isEmpty(lastname)) {
            mLastnameView.setError(getString(R.string.error_field_required));
            focusView = mLastnameView;
            cancel = true;
        }
        if (TextUtils.isEmpty(password)) {
            mPasswordView.setError(getString(R.string.error_field_required));
            focusView = mPasswordView;
            cancel = true;
        }
        if (role.equals(getString(R.string.ROLE_EMPTY))) {
            //mSpinnerText.setError(getString(R.string.error_field_required));
            mSpinnerText.setText(getString(R.string.error_role_required));
            mSpinnerText.setTextColor(Color.RED);
            mSpinnerText.setVisibility(View.VISIBLE);
            focusView = mSpinnerText;
            cancel = true;
        }
        if(role.equals("Etudiant")) {
            role = StringUtils.ETUDIANT.toString();
        } else {
            role = StringUtils.ENSEIGNANT.toString();
        }

        if (role.equals(StringUtils.ETUDIANT.toString())) {
            if(findViewById(R.id.layout_tp).getVisibility() == View.GONE) {
                new AlertDialog.Builder(this)
                        .setIcon(android.R.drawable.ic_dialog_alert)
                        .setMessage("Vous n'avez pas renseigner tous vos groupe.")
                        .setPositiveButton("OK", null)
                        .show();

                focusView = mTPView;
                cancel = true;
            } else {
                try {
                    JSONObject json = new JSONObject(mTPView.getSelectedItem().toString());
                    idTp = json.getString("Id");
                } catch (JSONException e) {
                    new AlertDialog.Builder(this)
                            .setIcon(android.R.drawable.ic_dialog_alert)
                            .setMessage("Vous n'avez pas renseigner tous vos groupe.")
                            .setPositiveButton("OK", null)
                            .show();

                    focusView = mTPView;
                    cancel = true;
                }

            }
        }

        if (cancel) {
            focusView.requestFocus();
        } else {
            showProgress(true);
            userSignIn(firstname, lastname, password, role, idTp);
        }
    }

    /**
     * Shows the progress UI and hides the login form.
     */
    @TargetApi(Build.VERSION_CODES.HONEYCOMB_MR2)
    private void showProgress(final boolean show) {

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB_MR2) {
            int shortAnimTime = getResources().getInteger(android.R.integer.config_shortAnimTime);

            findViewById(R.id.login).setVisibility(show ? View.GONE : View.VISIBLE);
            findViewById(R.id.login).animate().setDuration(shortAnimTime).alpha(
                    show ? 0 : 1).setListener(new AnimatorListenerAdapter() {
                @Override
                public void onAnimationEnd(Animator animation) {
                    findViewById(R.id.login).setVisibility(show ? View.GONE : View.VISIBLE);
                }
            });

            mLoginFormView.setVisibility(show ? View.GONE : View.VISIBLE);
            mLoginFormView.animate().setDuration(shortAnimTime).alpha(
                    show ? 0 : 1).setListener(new AnimatorListenerAdapter() {
                @Override
                public void onAnimationEnd(Animator animation) {
                    mLoginFormView.setVisibility(show ? View.GONE : View.VISIBLE);
                }
            });

            mProgressView.setVisibility(show ? View.VISIBLE : View.GONE);
            mProgressView.animate().setDuration(shortAnimTime).alpha(
                    show ? 1 : 0).setListener(new AnimatorListenerAdapter() {
                @Override
                public void onAnimationEnd(Animator animation) {
                    mProgressView.setVisibility(show ? View.VISIBLE : View.GONE);
                }
            });
        } else {
            // The ViewPropertyAnimator APIs are not available, so simply show
            // and hide the relevant UI components.
            mProgressView.setVisibility(show ? View.VISIBLE : View.GONE);
            mLoginFormView.setVisibility(show ? View.GONE : View.VISIBLE);
        }
    }

    @Override
    public Loader<Cursor> onCreateLoader(int i, Bundle bundle) {
        return new CursorLoader(this,
                // Retrieve data rows for the device user's 'profile' contact.
                Uri.withAppendedPath(ContactsContract.Profile.CONTENT_URI,
                        ContactsContract.Contacts.Data.CONTENT_DIRECTORY), ProfileQuery.PROJECTION,

                // Select only email addresses.
                ContactsContract.Contacts.Data.MIMETYPE +
                        " = ?", new String[]{ContactsContract.CommonDataKinds.Email
                .CONTENT_ITEM_TYPE},

                // Show primary email addresses first. Note that there won't be
                // a primary email address if the user hasn't specified one.
                ContactsContract.Contacts.Data.IS_PRIMARY + " DESC");
    }

    @Override
    public void onLoadFinished(Loader<Cursor> cursorLoader, Cursor cursor) {

    }

    @Override
    public void onLoaderReset(Loader<Cursor> cursorLoader) {
    }

    private interface ProfileQuery {
        String[] PROJECTION = {
                ContactsContract.CommonDataKinds.Nickname.NAME,
                ContactsContract.CommonDataKinds.Nickname.IS_PRIMARY,
        };
    }

    public void userSignIn(final String firstname, final String lastname, final String password, final String role, final String idTP) {
        new RequestActivity() {
            @Override
            public void traiteReponse(JSONObject json_data, String action) {
                showProgress(false);

                try {
                    if(!json_data.isNull("retour")) {
                        Log.i("Inscription", json_data.getString("retour"));
                        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(SignInActivity.this);
                        SharedPreferences.Editor editor = preferences.edit();
                        editor.putString(StringUtils.FIRSTNAME.toString(), firstname);
                        editor.putString(StringUtils.LASTNAME.toString(), lastname);
                        editor.putString(StringUtils.PASSWORD.toString(), password);
                        editor.putString(StringUtils.ROLE.toString(), role);
                        editor.putString(StringUtils.ATTEMPT_CONNEXION.toString(), null);
                        editor.apply();

                        Toast.makeText(SignInActivity.this, "Connection en cours", Toast.LENGTH_LONG).show();
                        Intent intent = new Intent(SignInActivity.this, HomeActivity.class);
                        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                        SignInActivity.this.startActivity(intent);
                        finish();
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                    Toast.makeText(SignInActivity.this, "Une erreur est survenu", Toast.LENGTH_LONG).show();
                }
            }
        }.envoiRequete("login", "action=inscription&firstname="+firstname+"&lastname="+lastname+"&password="+password+"&type="+role+"&idPromo="+idTP);
    }

    public void getPromo() {
        final Spinner promoSpinner = (Spinner) findViewById(R.id.promo);

        new RequestActivity() {
            @Override
            public void traiteReponse(JSONObject json_data, String action) {
                ArrayList<HashMap<String, String>> array = new ArrayList<>();
                HashMap nullItem = new HashMap<>();
                nullItem.put("Id", 0);
                nullItem.put("Nom", StringUtils.PROMO_EMPTY.toString());
                array.add(nullItem);

                try {
                    if(!json_data.isNull("retour")) {
                        JSONArray json = json_data.getJSONArray("retour");
                        for(int i = 0; i < json.length(); i++) {
                            JSONObject promo = json.getJSONObject(i);
                            HashMap map = new HashMap<>();
                            map.put("Id", promo.getString("id"));
                            map.put("Nom", promo.getString("name"));

                            array.add(map);
                        }

                        SimpleAdapter adapter = new SimpleAdapter(SignInActivity.this, array, R.layout.spinner_signin,
                                new String[]{"Nom", "Id"}, new int[]{ R.id.Nom, R.id.id});
                        adapter.setDropDownViewResource(R.layout.spinner_signin);
                        promoSpinner.setAdapter(adapter);
                        promoSpinner.setAdapter(adapter);
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                    Toast.makeText(SignInActivity.this, "Une erreur est survenu", Toast.LENGTH_LONG).show();
                }
            }
        }.envoiRequete("login", "action=getPromo");

        promoSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                if(position != 0) {
                    findViewById(R.id.layout_td).setVisibility(View.VISIBLE);

                    HashMap<String, String> item = (HashMap<String, String>) parent.getItemAtPosition(position);
                    getTD(item.get("Id"));
                } else {
                    findViewById(R.id.layout_td).setVisibility(View.GONE);
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
    }

    public void getTD(String idPromo) {
        final Spinner tdSpinner = (Spinner) findViewById(R.id.td);

        new RequestActivity() {
            @Override
            public void traiteReponse(JSONObject json_data, String action) {
                ArrayList<HashMap<String, String>> array = new ArrayList<>();
                HashMap nullItem = new HashMap<>();
                nullItem.put("Id", 0);
                nullItem.put("Nom", StringUtils.TD_EMPTY.toString());
                array.add(nullItem);

                try {
                    if(!json_data.isNull("retour")) {
                        JSONArray json = json_data.getJSONArray("retour");
                        for(int i = 0; i < json.length(); i++) {
                            JSONObject promo = json.getJSONObject(i);
                            HashMap map = new HashMap<>();
                            map.put("Id", promo.getString("id"));
                            map.put("Nom", promo.getString("name"));

                            array.add(map);
                        }

                        SimpleAdapter adapter = new SimpleAdapter(SignInActivity.this, array, R.layout.spinner_signin,
                                new String[]{"Nom", "Id"}, new int[]{ R.id.Nom, R.id.id});
                        adapter.setDropDownViewResource(R.layout.spinner_signin);
                        tdSpinner.setAdapter(adapter);
                        tdSpinner.setAdapter(adapter);
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                    Toast.makeText(SignInActivity.this, "Une erreur est survenu", Toast.LENGTH_LONG).show();
                }
            }
        }.envoiRequete("login", "action=getTD&idPromo="+idPromo);

        tdSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                if(position != 0) {
                    findViewById(R.id.layout_tp).setVisibility(View.VISIBLE);

                    HashMap<String, String> item = (HashMap<String, String>) parent.getItemAtPosition(position);
                    getTP(item.get("Id"));
                } else {
                    findViewById(R.id.layout_tp).setVisibility(View.GONE);
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
    }

    public void getTP(String idTD) {
        new RequestActivity() {
            @Override
            public void traiteReponse(JSONObject json_data, String action) {
                ArrayList<HashMap<String, String>> array = new ArrayList<>();
                HashMap nullItem = new HashMap<>();
                nullItem.put("Id", 0);
                nullItem.put("Nom", StringUtils.TP_EMPTY.toString());
                array.add(nullItem);

                try {
                    if(!json_data.isNull("retour")) {
                        JSONArray json = json_data.getJSONArray("retour");
                        for(int i = 0; i < json.length(); i++) {
                            JSONObject promo = json.getJSONObject(i);
                            HashMap map = new HashMap<>();
                            map.put("Id", promo.getString("id"));
                            map.put("Nom", promo.getString("name"));

                            array.add(map);
                        }

                        SimpleAdapter adapter = new SimpleAdapter(SignInActivity.this, array, R.layout.spinner_signin,
                                new String[]{"Nom", "Id"}, new int[]{ R.id.Nom, R.id.id});
                        adapter.setDropDownViewResource(R.layout.spinner_signin);
                        mTPView.setAdapter(adapter);
                        mTPView.setAdapter(adapter);
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                    Toast.makeText(SignInActivity.this, "Une erreur est survenu", Toast.LENGTH_LONG).show();
                }
            }
        }.envoiRequete("login", "action=getTP&idTD="+idTD);
    }
}

