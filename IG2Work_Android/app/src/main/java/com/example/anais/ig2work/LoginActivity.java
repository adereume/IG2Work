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
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.provider.ContactsContract;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.util.Log;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.WindowManager;
import android.view.inputmethod.EditorInfo;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.example.anais.ig2work.DataBase.RequestActivity;
import com.example.anais.ig2work.Utils.RestActivity;
import com.example.anais.ig2work.Utils.StringUtils;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;


/**
 * A login screen that offers login via pseudo/password.
 */
public class LoginActivity extends AppCompatActivity implements LoaderCallbacks<Cursor> {
   // private UserLoginTask mAuthTask = null;

    // UI references.
    private EditText mFirstnameView;
    private EditText mLastnameView;
    private EditText mPasswordView;
    private View mProgressView;
    private View mLoginFormView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        GlobalState gs = (GlobalState) getApplication();
        if(! gs.verifReseau()) {
            Toast.makeText(this, "Aucun réseau disponible", Toast.LENGTH_LONG).show();
            this.finish();
        }

        //Keyboard don't resize view
        this.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN);

        //Hidden the keyboard
        this.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);

        mLoginFormView = findViewById(R.id.login_form);
        mProgressView = findViewById(R.id.login_progress);

        mFirstnameView = (EditText) findViewById(R.id.firstname);
        mFirstnameView.setOnTouchListener(new View.OnTouchListener() {
            public boolean onTouch(View v, MotionEvent event) {
                mFirstnameView.requestLayout();
                LoginActivity.this.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_PAN);
                return false;
            }
        });
        mLastnameView = (EditText) findViewById(R.id.lastname);
        mLastnameView.setOnTouchListener(new View.OnTouchListener() {
            public boolean onTouch(View v, MotionEvent event) {
                mLastnameView.requestLayout();
                LoginActivity.this.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_PAN);
                return false;
            }
        });

        mPasswordView = (EditText) findViewById(R.id.password);
        mPasswordView.setOnTouchListener(new View.OnTouchListener() {
            public boolean onTouch(View v, MotionEvent event) {
                mPasswordView.requestLayout();
                LoginActivity.this.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_PAN);
                return false;
            }
        });

        Button mEmailSignInButton = (Button) findViewById(R.id.email_sign_in_button);
        mEmailSignInButton.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                attemptLogin();
            }
        });

        Button button = (Button) findViewById(R.id.incription);
        button.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(LoginActivity.this, SignInActivity.class);
                startActivity(intent);
                finish();
            }
        });
    }

    @Override
    protected void onStart() {
        super.onStart();

        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(this);
        String firstname = preferences.getString(StringUtils.FIRSTNAME.toString(), null);
        if(firstname != null)
            mFirstnameView.setText(firstname);

        String lastname = preferences.getString(StringUtils.LASTNAME.toString(), null);
        if(lastname != null)
            mLastnameView.setText(lastname);

        String lastPwd = preferences.getString(StringUtils.PASSWORD.toString(), null);
        if(lastPwd != null)
            mPasswordView.setText(lastPwd);
        mPasswordView.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView textView, int id, KeyEvent keyEvent) {
                if (id == R.id.login || id == EditorInfo.IME_NULL) {
                    attemptLogin();
                    return true;
                }
                return false;
            }
        });

        String attempt = preferences.getString(StringUtils.ATTEMPT_CONNEXION.toString(), null);

        if(firstname != null && lastname != null && lastPwd != null && attempt == null) {
            showProgress(true);
            userLogin(firstname, lastname, lastPwd);
        }
    }

    private void attemptLogin() {
        // Reset errors.
        mFirstnameView.setError(null);
        mLastnameView.setError(null);
        mPasswordView.setError(null);

        // Store values at the time of the login attempt.
        String firstName = mFirstnameView.getText().toString();
        String lastName = mLastnameView.getText().toString();
        String password = mPasswordView.getText().toString();

        boolean cancel = false;
        View focusView = null;

        // Vérifier si les champs sont remplie
        if (TextUtils.isEmpty(firstName)) {
            mFirstnameView.setError(getString(R.string.error_field_required));
            focusView = mFirstnameView;
            cancel = true;
        }

        if (TextUtils.isEmpty(lastName)) {
            mLastnameView.setError(getString(R.string.error_field_required));
            focusView = mLastnameView;
            cancel = true;
        }

        if (TextUtils.isEmpty(password)) {
            mPasswordView.setError(getString(R.string.error_field_required));
            focusView = mPasswordView;
            cancel = true;
        }

        if (cancel) {
            focusView.requestFocus();
        } else {
            showProgress(true);
            userLogin(firstName, lastName, password);
        }
    }

    /**
     * Shows the progress UI and hides the login form.
     */
    @TargetApi(Build.VERSION_CODES.HONEYCOMB_MR2)
    private void showProgress(final boolean show) {
        // On Honeycomb MR2 we have the ViewPropertyAnimator APIs, which allow
        // for very easy animations. If available, use these APIs to fade-in
        // the progress spinner.
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB_MR2) {
            int shortAnimTime = getResources().getInteger(android.R.integer.config_shortAnimTime);

            findViewById(R.id.incription).setVisibility(show ? View.GONE : View.VISIBLE);
            findViewById(R.id.incription).animate().setDuration(shortAnimTime).alpha(
                    show ? 0 : 1).setListener(new AnimatorListenerAdapter() {
                @Override
                public void onAnimationEnd(Animator animation) {
                    findViewById(R.id.incription).setVisibility(show ? View.GONE : View.VISIBLE);
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


    public void userLogin(final String firstname, final String lastname, final String password) {
        new RequestActivity() {
            @Override
            public void traiteReponse(JSONObject o, String action) {
                showProgress(false);

                try {
                    if(o.isNull("retour")) {
                        Toast.makeText(LoginActivity.this, "Erreur: le pseudo ou le mot de passe est incorrecte", Toast.LENGTH_LONG).show();
                        mFirstnameView.setError(StringUtils.error_champ.toString());
                        mLastnameView.setError(StringUtils.error_champ.toString());
                        mPasswordView.setError(StringUtils.error_champ.toString());
                        mPasswordView.requestFocus();
                        return;
                    }

                    JSONArray json = o.getJSONArray("retour");
                    JSONObject retour = json.getJSONObject(0);

                    String role = retour.getString("role");
                    int idUser = retour.getInt("id");

                    SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(LoginActivity.this);
                    SharedPreferences.Editor editor = preferences.edit();
                    editor.putInt(StringUtils.IDUSER.toString(), idUser);
                    editor.putString(StringUtils.FIRSTNAME.toString(), firstname);
                    editor.putString(StringUtils.LASTNAME.toString(), lastname);
                    editor.putString(StringUtils.PASSWORD.toString(), password);
                    editor.putString(StringUtils.ROLE.toString(), role);
                    editor.putString(StringUtils.ATTEMPT_CONNEXION.toString(), null);
                    editor.apply();

                    Toast.makeText(LoginActivity.this, "Connection en cours", Toast.LENGTH_SHORT).show();
                    Intent intent = new Intent(LoginActivity.this, HomeActivity.class);
                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                    LoginActivity.this.startActivity(intent);
                    LoginActivity.this.finish();

                } catch (JSONException e) {
                    e.printStackTrace();

                    Toast.makeText(LoginActivity.this, "Erreur: le pseudo ou le mot de passe est incorrecte", Toast.LENGTH_LONG).show();
                    mFirstnameView.setError(StringUtils.error_champ.toString());
                    mLastnameView.setError(StringUtils.error_champ.toString());
                    mPasswordView.setError(StringUtils.error_champ.toString());
                    mPasswordView.requestFocus();
                }
            }
        }.envoiRequete("login", "action=connexion&firstname="+firstname+"&lastname="+lastname+"&password="+password);
    }

}

