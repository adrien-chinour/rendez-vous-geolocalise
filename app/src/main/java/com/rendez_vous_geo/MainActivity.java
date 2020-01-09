package com.rendez_vous_geo;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.PorterDuff;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.Objects;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

/**
 * Activité de démarrage de l'application
 * - Affiche les informations sur le fonctionnement de l'application ainsi que les crédits
 * - Permet de créer un nouveau rdv géolocalisé
 */
public class MainActivity extends AppCompatActivity {

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        FloatingActionButton fab = findViewById(R.id.btn_new_rdv);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(view.getContext(), NewRendezvousActivity.class);
                startActivity(i);
            }
        });

        SharedPreferences sharedPref = this.getSharedPreferences(
                getString(R.string.shared_preference), Context.MODE_PRIVATE);

        if (Objects.equals(sharedPref.getString(getString(R.string.settings_phone), ""), "")) {
            Intent i = new Intent(this, PreferenceActivity.class);
            startActivity(i);
        }

        displayToast();
    }

    private void displayToast() {
        String validation = getIntent().getStringExtra(getString(R.string.extra_message_validation));
        if (validation != null) {
            Toast t = Toast.makeText(getApplicationContext(), validation, Toast.LENGTH_LONG);
            View view = t.getView();
            view.getBackground().setColorFilter(getResources().getColor(R.color.success), PorterDuff.Mode.SRC_IN);

            TextView text = view.findViewById(android.R.id.message);
            text.setTextColor(getResources().getColor(R.color.white));
            t.show();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.toolbar_main_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_preference:
                Intent i = new Intent(this, PreferenceActivity.class);
                startActivity(i);
        }

        return super.onOptionsItemSelected(item);
    }
}
