package com.rendez_vous_geo;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

/**
 * Activité de démarrage de l'application
 * - Affiche les informations sur le fonctionnement de l'application ainsi que les crédits
 * - Permet de créer un nouveau rdv géolocalisé
 */
public class MainActivity extends AppCompatActivity {

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // TODO créer le layout selon la maquette de MainActivity
        setContentView(R.layout.activity_main);
    }

    /**
     * Action du bouton création d'un rdv
     * @param view : Bouton créer
     */
    public void newRendezvousAction(View view) {
        Intent i = new Intent(this, NewRendezvousActivity.class);
        startActivity(i);
    }
}
