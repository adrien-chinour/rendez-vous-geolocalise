package com.rendez_vous_geo;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Resources;
import android.os.Bundle;
import android.view.View;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;

import java.util.Objects;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentManager;

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
    }
}
