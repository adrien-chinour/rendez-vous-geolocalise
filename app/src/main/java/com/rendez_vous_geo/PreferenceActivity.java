package com.rendez_vous_geo;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

public class PreferenceActivity extends AppCompatActivity {


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_preference);
    }

    public void validateSettings(View view) {
        EditText editText = findViewById(R.id.phone_number);
        String phoneNumber = editText.getText().toString().replaceAll("\\s", "");
        SharedPreferences.Editor editor = getSharedPreferences(getString(R.string.shared_preference), MODE_PRIVATE).edit();

        if (phoneNumber.matches("^[0-9]{10}$")) {
            editor.putString(getString(R.string.settings_phone), phoneNumber);
            editor.commit();

            Intent i = new Intent(this, MainActivity.class);
            startActivity(i);
        } else {
            Toast.makeText(this, "Le numéro de téléphone saisi est invalide. Il doit contenir 10 numéros", Toast.LENGTH_LONG).show();
        }


    }

}
