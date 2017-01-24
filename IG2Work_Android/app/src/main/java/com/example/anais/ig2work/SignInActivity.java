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
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.example.anais.ig2work.DataBase.RequestActivity;
import com.example.anais.ig2work.Utils.StringUtils;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;


/**
 * A login screen that offers login via pseudo/password.
 */
public class SignInActivity extends AppCompatActivity implements LoaderCallbacks<Cursor> {
    // UI references.
    private EditText mFirstnameView;
    private EditText mLastnameView;
    private EditText mPasswordView;
    private EditText mPasswordConfView;
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

        mFirstnameView = (EditText) findViewById(R.id.firstname);
        mFirstnameView.setOnTouchListener(new View.OnTouchListener() {
            public boolean onTouch(View v, MotionEvent event) {
                mFirstnameView.requestLayout();
                SignInActivity.this.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_PAN);
                return false;
            }
        });

        mLastnameView = (EditText) findViewById(R.id.lastname);
        mLastnameView.setOnTouchListener(new View.OnTouchListener() {
            public boolean onTouch(View v, MotionEvent event) {
                mLastnameView.requestLayout();
                SignInActivity.this.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_PAN);
                return false;
            }
        });

        mPasswordView = (EditText) findViewById(R.id.password);
        mPasswordView.setOnTouchListener(new View.OnTouchListener() {
            public boolean onTouch(View v, MotionEvent event) {
                //Keyboard don't resize view
                mPasswordView.requestLayout();
                SignInActivity.this.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_PAN);
                return false;
            }
        });

        mPasswordConfView = (EditText) findViewById(R.id.passwordConf);
        mPasswordConfView.setOnTouchListener(new View.OnTouchListener() {
            public boolean onTouch(View v, MotionEvent event) {
                //Keyboard don't resize view
                mPasswordConfView.requestLayout();
                SignInActivity.this.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_PAN);
                return false;
            }
        });

        Button mEmailSignInButton = (Button) findViewById(R.id.incription);
        mEmailSignInButton.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                attemptSignIn();
            }
        });

        mLoginFormView = findViewById(R.id.login_form);
        mProgressView = findViewById(R.id.login_progress);

        Button button = (Button) findViewById(R.id.login);
        button.setOnClickListener(new OnClickListener() {
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
        // Reset errors.
        mFirstnameView.setError(null);
        mLastnameView.setError(null);
        mPasswordView.setError(null);
        mPasswordConfView.setError(null);

        // Store values at the time of the login attempt.
        String firstname = mFirstnameView.getText().toString();
        String lastname = mLastnameView.getText().toString();
        String password = mPasswordView.getText().toString();
        String passwordConf = mPasswordConfView.getText().toString();

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
        if(TextUtils.isEmpty(passwordConf)) {
            mPasswordConfView.setError(getString(R.string.error_field_required));
            focusView = mPasswordConfView;
            cancel = true;
        }

        if(!password.equals(passwordConf)) {
            mPasswordConfView.setError(getString(R.string.error_confirmation_password));
            focusView = mPasswordConfView;
            cancel = true;
        }

        if (cancel) {
            focusView.requestFocus();
        } else {
            showProgress(true);
            userSignIn(firstname, lastname, password);
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

    public void userSignIn(final String firstname, final String lastname, final String password) {
        new RequestActivity() {
            @Override
            public void traiteReponse(JSONArray o, String action) {
                showProgress(false);

                try {
                    JSONObject json_data = o.getJSONObject(0);

                    if(json_data.getString("Pseudo") != null) {
                        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(SignInActivity.this);
                        SharedPreferences.Editor editor = preferences.edit();
                        editor.putString(StringUtils.FIRSTNAME.toString(), firstname);
                        editor.putString(StringUtils.LASTNAME.toString(), lastname);
                        editor.putString(StringUtils.PASSWORD.toString(), password);
                        editor.apply();
                    }

                    Toast.makeText(SignInActivity.this, "Connection en cours", Toast.LENGTH_LONG).show();
                    Intent intent = new Intent(SignInActivity.this, MainActivity.class);
                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                    SignInActivity.this.startActivity(intent);
                    finish();
                } catch (JSONException e) {
                    e.printStackTrace();
                Toast.makeText(SignInActivity.this, "Une erreur est survenu", Toast.LENGTH_LONG).show();
                }
            }
        }.envoiRequete("login", "action=inscription&firstname="+firstname+"&lastname="+lastname+"&password="+password);
    }

}

