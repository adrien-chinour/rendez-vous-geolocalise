package com.rendez_vous_geo;

import android.os.Bundle;
import android.view.View;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

/**
 * Activité de création d'un rdv géolocalisé
 * - Prise en compte des contacts + numéro avec choix multiple
 * - Prise en compte de la position actuelle ou possibilité d'en choisir une
 */
public class NewRendezvousActivity extends AppCompatActivity {

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // TODO créer le layout selon la maquette de NewRendezvousActivity
        setContentView(R.layout.activity_new_rendezvous);
    }

    /**
     * Action du bouton de l'envoi du rdv
     * @param view : Bouton envoyer
     */
    public void sendRendezvousAction(View view) {
        /*
         * TODO : récupération des éléments du formulaire
         *  et envoi d'un SMS comportant le lien de validation
         */
    }

}
