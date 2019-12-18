package com.rendez_vous_geo;

import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

/**
 * Activité permettant de d'accepter ou non un rdv
 */
public class ValidateRendezvousActivity extends AppCompatActivity {

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // TODO créer le layout selon la maquette de ValidateRendezvousActivity
        setContentView(R.layout.activity_validate_rendezvous);
    }

    /**
     * Action du bouton "oui"
     */
    public void agreeAction() {
        // TODO envoi d'un SMS au demandeur pour accepter son invitation
    }

    /**
     * Action du bouton "non"
     */
    public void denyAction() {
        // TODO envoi d'un SMS au demandeur pour refuser son invitation
    }
}
