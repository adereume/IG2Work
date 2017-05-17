package com.example.anais.ig2work;

import android.content.Intent;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.example.anais.ig2work.DataBase.RequestActivity;
import com.example.anais.ig2work.Utils.StringUtils;

import org.json.JSONObject;

/**
 * La classe AccountActivity gère l'activité "Mon Compte" qui permet à un utilisateur
 * de visualiser ses informations personnelles (prénom & nom) et de modifier son mot de passe.
 */
public class AccountActivity extends AppCompatActivity {
    private SharedPreferences preferences;

    EditText edtFirstname;
    EditText edtLastName;
    EditText edtOldPassword;
    EditText edtNewPassword;
    EditText edtNewPassword2;
    Button btnOk;

    int idUser;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_account);

        // Affichage de la flèche de retour
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        preferences = PreferenceManager.getDefaultSharedPreferences(AccountActivity.this);

        edtFirstname = (EditText) findViewById(R.id.compte_edtFirstName);
        edtLastName = (EditText) findViewById(R.id.compte_edtLastName);
        edtOldPassword = (EditText) findViewById(R.id.compte_edtOldPasse);
        edtNewPassword = (EditText) findViewById(R.id.compte_edtNewPasse);
        edtNewPassword2 = (EditText) findViewById(R.id.compte_edtNewPasse2);
        btnOk = (Button) findViewById(R.id.compte_btnOK);

        btnOk.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // Lorsque l'utilisateur clique sur le bouton OK, on tente de mettre à jour
                // son mot de passe
                attemptChangePassword();
            }
        });
    }

    @Override
    protected void onStart() {
        super.onStart();

        preferences = PreferenceManager.getDefaultSharedPreferences(AccountActivity.this);
        idUser = PreferenceManager.getDefaultSharedPreferences(AccountActivity.this.getApplicationContext()).getInt(StringUtils.IDUSER.toString(), 0);

        // Les champs 'Prénom' et 'Nom' sont remplis avec les données enregistrées dans les préférences
        String firstname = preferences.getString(StringUtils.FIRSTNAME.toString(), null);
        String lastname = preferences.getString(StringUtils.LASTNAME.toString(), null);
        edtFirstname.setText(firstname);
        edtLastName.setText(lastname);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        switch (id) {
            case android.R.id.home: // Retour à l'accueil
                Intent intent = new Intent(AccountActivity.this, HomeActivity.class);
                startActivity(intent);
                return true;
        }

        return super.onOptionsItemSelected(item);
    }

    /*
    Tentative de changement de mot de passe
     */
    public void attemptChangePassword() {
        // Réinitialisation des erreurs
        edtOldPassword.setError(null);
        edtNewPassword.setError(null);
        edtNewPassword2.setError(null);

        String oldPasswordSaved = preferences.getString(StringUtils.PASSWORD.toString(), null);

        // Stockage des valeurs avant la tentative de mise à jour
        String oldPassword = edtOldPassword.getText().toString();
        String newPassword = edtNewPassword.getText().toString();
        String newPassword2 = edtNewPassword2.getText().toString();

        // On vérifie d'abord que les champs "Nouveau mot de passe" et "Confirmer mot de passe"
        // sont identiques. Ensuite, on vérifie que le champ "Ancien mot de passe" est correct,
        // c'est à dire qu'il est identique au mot de passe actuel de l'utilisateur.
        if (!newPassword.equals(newPassword2)) {
            edtNewPassword2.setError("Les mots de passes entrés sont différents");
        } else if (!oldPassword.equals(oldPasswordSaved)) {
            edtOldPassword.setError("Mot de passe erroné");
        } else {
            changePassword(oldPassword, newPassword);
        }
    }

    /*
    Requête de changement du mot de passe
     */
    public void changePassword(final String oldPassword, final String newPassword) {

        new RequestActivity() {
            @Override
            public void traiteReponse(JSONObject o, String action) {

                if(o.isNull("retour")) {
                    Toast.makeText(AccountActivity.this, "Erreur lors de la mise à jour", Toast.LENGTH_LONG).show();
                    edtOldPassword.requestFocus();
                    return;
                } else {
                    // Le nouveau mot de passe est enregistré dans les préférences
                    SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(AccountActivity.this);
                    SharedPreferences.Editor editor = preferences.edit();
                    editor.putString(StringUtils.PASSWORD.toString(), newPassword);
                    editor.apply();
                }

                Toast.makeText(AccountActivity.this, "Mot de passe mis à jour", Toast.LENGTH_SHORT).show();

            }
        }.envoiRequete("changePassword", "action=updatePassword&idUser="+idUser+"&oldPassword="+oldPassword+"&newPassword="+newPassword);
    }
}
