package com.rendez_vous_geo;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.telephony.SmsManager;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.google.android.gms.maps.model.LatLng;


import static android.Manifest.permission.SEND_SMS;

/**
 * Activité permettant de d'accepter ou non un rdv
 */
public class ValidateRendezvousActivity extends AppCompatActivity {

    private static final int MY_PERMISSIONS_REQUEST_SEND_SMS = 0;
    private static final String ACCEPT = "Accepter";

    // Attributs récupérés du SMS
    private LatLng latLng;
    private String phoneNumber = null;
    private String adress = null;
    private String date = null;
    private String time = null;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_validate_rendezvous);

        // récupération des queryParams du lien cliqué
        Uri dataFromLink = getIntent().getData();
        this.latLng = new LatLng(
                Double.parseDouble(dataFromLink.getQueryParameter("latitude")),
                Double.parseDouble(dataFromLink.getQueryParameter("longitude")));
        this.phoneNumber = dataFromLink.getQueryParameter("phone");
        this.adress = dataFromLink.getQueryParameter("adress");
        this.date = dataFromLink.getQueryParameter("date");
        this.time = dataFromLink.getQueryParameter("time");

        // Affichage des attributs dans l'activité
        TextView tvPhoneNumber = findViewById(R.id.tvPhoneNumber);
        tvPhoneNumber.setText(this.phoneNumber);
        TextView tvAdress = findViewById(R.id.tvAdress);
        tvAdress.setText(this.adress);
        TextView tvDate = findViewById(R.id.tvDate);
        tvDate.setText(this.date);
        TextView tvTime = findViewById(R.id.tvTime);
        tvTime.setText(this.time);
    }

    /**
     * Fonction permettant d'envoyer le SMS de réponse selon le bouton cliqué
     * @param view
     */
    public void responseAction(View view) {
        Button clickedButton = findViewById(view.getId());
        String choix;
        // Récupération du numéro de téléphone de l'utilisateur actuel dans les préférences
        SharedPreferences userPhoneNumberPref = this.getSharedPreferences(getString(R.string.shared_preference), Context.MODE_PRIVATE);
        String userPhoneNumber = userPhoneNumberPref.getString(getString(R.string.settings_phone), "");

        // Construction du message
        if (clickedButton.getText().equals(ACCEPT)) {
            choix = "accepté";
        }
        else {
            choix = "refusé";
        }

        String message = "Le rendez-vous au " + this.adress +
                " le " + this.date +
                " à " + this.time + " a été " + choix + " par " + userPhoneNumber + ".";
        // Envoi du message
        sendSMS(message);
        Toast.makeText(getApplicationContext(), "Votre message a bien été envoyé.", Toast.LENGTH_LONG).show();
    }

    /**
     * Fonction appelée au clic sur le bouton "Ouvrir dans Google Maps" permettant d'ouvrir
     * l'application "Google Maps" avec la latitude et la longitude définies dans le rdv
     * @param v
     */
    public void openMaps(View v) {
        // Uri permettant de renseigner la latitude et la longitude
        Uri uriLatLong = Uri.parse("geo:0,0?q=" + latLng.latitude + "," + latLng.longitude);
        // Définition de l'intent
        Intent mapIntent = new Intent(Intent.ACTION_VIEW, uriLatLong);
        // On ouvre l'intent avec l'application Google Maps
        mapIntent.setPackage("com.google.android.apps.maps");
        // Démarrage de l'activité
        startActivity(mapIntent);
    }

    /**
     * Fonction permettant d'envoyer un SMS
     * @param message
     */
    public void sendSMS(String message) {
        checkSMSPermission();
        SmsManager sm = SmsManager.getDefault();
        sm.sendTextMessage(this.phoneNumber, null, message, null, null);
    }

    /**
     * Fonction permettant de vérifier si la permission d'envoi des SMS a été donnée
     */
    public void checkSMSPermission() {
        if (ContextCompat.checkSelfPermission(this, SEND_SMS) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{SEND_SMS}, MY_PERMISSIONS_REQUEST_SEND_SMS);
        }
    }
}
