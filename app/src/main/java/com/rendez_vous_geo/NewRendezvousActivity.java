package com.rendez_vous_geo;

import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.provider.ContactsContract.CommonDataKinds.Phone;
import android.text.Editable;
import android.view.View;
import android.widget.EditText;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

/**
 * Activité de création d'un rdv géolocalisé
 * - Prise en compte des contacts + numéro avec choix multiple
 * - Prise en compte de la position actuelle ou possibilité d'en choisir une
 */
public class NewRendezvousActivity extends AppCompatActivity {

    static final int PICK_CONTACT_REQUEST = 1; // Code requete pour l'intent de choix contact

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // TODO créer le layout selon la maquette de NewRendezvousActivity
        setContentView(R.layout.activity_new_rendezvous);
    }

    /**
     * Action du bouton pour l'ajout d'un contact
     *
     * @param view : Bouton d'ajout
     */
    public void addContactAction(View view) {
        Intent intent = new Intent(Intent.ACTION_PICK);
        intent.setType(Phone.CONTENT_TYPE);
        startActivityForResult(intent, PICK_CONTACT_REQUEST);
    }

    /**
     * Action du bouton de l'envoi du rdv
     *
     * @param view : Bouton envoyer
     */
    public void sendRendezvousAction(View view) {
        /*
         * TODO : récupération des éléments du formulaire
         *  et envoi d'un SMS comportant le lien de validation
         */
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {

        /*
         * Request choix du contact
         */
        if (requestCode == PICK_CONTACT_REQUEST) {
            if (resultCode == RESULT_OK) {

                // récupération du numéro du contact sélectionné
                Uri contact = data.getData();
                String[] projection = {Phone.NUMBER};
                Cursor cursor = getContentResolver()
                        .query(contact, projection, null, null, null);
                cursor.moveToFirst();
                int column = cursor.getColumnIndex(Phone.NUMBER);
                String number = cursor.getString(column);

                // ajout à la liste le contact sélectionné
                EditText contactList = findViewById(R.id.contact_list);
                Editable content = contactList.getText();
                content.append(number).append(";");
                contactList.setSelection(content.length());
            }
        }
    }
}
