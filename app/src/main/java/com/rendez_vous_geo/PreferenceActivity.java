package com.rendez_vous_geo;

import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.PorterDuff;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

public class PreferenceActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_preference);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setDisplayShowHomeEnabled(true);
        }

        setActualPreferences();
    }

    public void validateSettings(View view) {
        SharedPreferences.Editor editor = getSharedPreferences(getString(R.string.shared_preference), MODE_PRIVATE).edit();

        // Récupération du numéro de téléphone
        EditText editText = findViewById(R.id.phone_number);
        String phoneNumber = editText.getText().toString().replaceAll("\\s", "");

        // Ajout du nom de l'utilisateur
        EditText username = findViewById(R.id.username);
        editor.putString(getString(R.string.settings_username), username.getText().toString());

        /*
         * Si le téléphone n'est pas renseigné l'utilisateur ne peut pas quitté la page en sauvegardant
         */
        if (phoneNumber.matches("^[0-9]{10}$")) {
            editor.putString(getString(R.string.settings_phone), phoneNumber);
            editor.commit();

            Intent i = new Intent(this, MainActivity.class);
            i.putExtra(getString(R.string.extra_message_validation), getString(R.string.extra_message_preference));
            startActivity(i);
        } else {
            Toast t = Toast.makeText(getApplicationContext(), getString(R.string.error_number_size), Toast.LENGTH_SHORT);
            View toastView = t.getView();
            toastView.getBackground().setColorFilter(getResources().getColor(R.color.danger), PorterDuff.Mode.SRC_IN);

            TextView text = toastView.findViewById(android.R.id.message);
            text.setTextColor(getResources().getColor(R.color.white));
            t.show();
        }
    }

    /**
     * Récupére les préferences actuelles pour les ajouter aux inputs
     */
    private void setActualPreferences() {
        SharedPreferences sharedPreferences = getSharedPreferences(getString(R.string.shared_preference), MODE_PRIVATE);

        String phone = sharedPreferences.getString(getString(R.string.settings_phone), "");
        String username = sharedPreferences.getString(getString(R.string.settings_username), "");

        if (phone != null) {
            EditText phoneText = findViewById(R.id.phone_number);
            phoneText.setText(phone);
        }

        if (username != null) {
            EditText usernameText = findViewById(R.id.username);
            usernameText.setText(username);
        }
    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        finish();
        return true;
    }

}
