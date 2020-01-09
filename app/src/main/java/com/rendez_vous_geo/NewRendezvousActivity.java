package com.rendez_vous_geo;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.PorterDuff;
import android.location.Address;
import android.net.Uri;
import android.os.Bundle;
import android.provider.ContactsContract.CommonDataKinds.Phone;
import android.telephony.SmsManager;
import android.text.Editable;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.maps.model.LatLng;
import com.schibstedspain.leku.LocationPickerActivity;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.DialogFragment;

import static android.Manifest.permission.SEND_SMS;
import static android.view.View.VISIBLE;

public class NewRendezvousActivity extends AppCompatActivity {

    private static final int MY_PERMISSIONS_REQUEST_SEND_SMS = 0; // Permission SEND_SMS

    private static final int PICK_CONTACT_REQUEST = 1; // Code requete pour l'intent de choix contact
    private static final int PICK_LOCATION_REQUEST = 2; // Code requete pour l'intent de choix lieu

    // coordonnées GPS du RDV
    private LatLng latLng;

    // Liste des numéros des destinataires
    private String[] phoneNumbers;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_rendezvous);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setDisplayShowHomeEnabled(true);
        }
    }

    /**
     * Action du bouton pour l'ajout d'un contact
     *
     * @param view : bouton d'ajout
     */
    public void addContactAction(View view) {
        Intent intent = new Intent(Intent.ACTION_PICK);
        intent.setType(Phone.CONTENT_TYPE);
        startActivityForResult(intent, PICK_CONTACT_REQUEST);
    }

    /**
     * Action du bouton pour l'ajout d'une localisation
     * Ouvre l'activité de LocationPicker
     *
     * @param view : bouton localisation
     */
    public void addLocationAction(View view) {
        Intent locationPickerIntent = new LocationPickerActivity.Builder()
                .withDefaultLocaleSearchZone()
                .shouldReturnOkOnBackPressed()
                .build(getApplicationContext());

        startActivityForResult(locationPickerIntent, PICK_LOCATION_REQUEST);
    }

    /**
     * Action liée à l'ouverture du time picker
     *
     * @param view button du time picker
     */
    public void showTimePickerDialog(View view) {
        DialogFragment newFragment = new PickerActivityFragment.TimePickerFragment();
        newFragment.show(getSupportFragmentManager(), "timePicker");
    }

    /**
     * Action liée à l'ouverture du date picker
     *
     * @param view button du date picker
     */
    public void showDatePickerDialog(View view) {
        DialogFragment newFragment = new PickerActivityFragment.DatePickerFragment();
        newFragment.show(getSupportFragmentManager(), "datePicker");
    }

    /**
     * Action du bouton de prévisualisation
     * Génère le message à envoyer et l'affiche dans une TextView
     *
     * @param view : bouton previsualisation
     */
    public void previewAction(View view) {
        if (formIsInvalid()) {
            return;
        }

        Toast.makeText(this, getMessage(), Toast.LENGTH_LONG).show();
    }

    /**
     * Action du bouton de l'envoi du rdv
     *
     * @param view : Bouton envoyer
     */
    public void sendRendezvousAction(View view) {
        if (formIsInvalid()) {
            return;
        }

        EditText contacts = findViewById(R.id.contact_list);
        this.phoneNumbers = contacts.getText().toString().replaceAll("\\s", "").split(";");

        if (ContextCompat.checkSelfPermission(this, SEND_SMS) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{SEND_SMS}, MY_PERMISSIONS_REQUEST_SEND_SMS);
        } else {
            sendSms();
        }
    }

    /**
     * Envoi des SMS de rendez-vous
     */
    private void sendSms() {
        for (String phoneNumber : this.phoneNumbers) {
            if (!phoneNumber.matches("^[+]?[0-9]{4,13}$")) {
                displayErrorToast();
                return;
            } else {
                SmsManager sm = SmsManager.getDefault();
                ArrayList<String> parts = sm.divideMessage(getMessage());
                sm.sendMultipartTextMessage(phoneNumber, null, parts, null, null);
            }
        }

        Intent i = new Intent(this, MainActivity.class);
        i.putExtra(getString(R.string.extra_message_validation), "Votre message a bien été envoyé.");
        startActivity(i);
        finish();
    }

    private void displayErrorToast() {
        Toast t = Toast.makeText(getApplicationContext(), getString(R.string.error_contact_list), Toast.LENGTH_SHORT);
        View view = t.getView();
        view.getBackground().setColorFilter(getResources().getColor(R.color.danger), PorterDuff.Mode.SRC_IN);

        TextView text = view.findViewById(android.R.id.message);
        text.setTextColor(getResources().getColor(R.color.white));
        t.show();
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NotNull String[] permissions, @NotNull int[] grantResults) {
        if (requestCode == MY_PERMISSIONS_REQUEST_SEND_SMS) {

            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                sendSms();
            } else {
                Toast.makeText(this, getString(R.string.error_permission_sms), Toast.LENGTH_LONG).show();
            }
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {

        super.onActivityResult(requestCode, resultCode, data);
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

        if (requestCode == PICK_LOCATION_REQUEST) {
            if (resultCode == RESULT_OK) {

                this.latLng = new LatLng(
                        data.getDoubleExtra("latitude", 0.0),
                        data.getDoubleExtra("longitude", 0.0)
                );

                // récupération de l'adresse complète pour l'affichage
                Address address = data.getParcelableExtra("address");
                TextView addressOutput = findViewById(R.id.location);
                addressOutput.setText(address.getAddressLine(0));
            }
        }
    }

    /**
     * Écriture du SMS à envoyer au(x) récepteur(s)
     *
     * @return String message
     */
    private String getMessage() {
        TextView message = findViewById(R.id.message);
        TextView address = findViewById(R.id.location);
        Button date = findViewById(R.id.date_picker);
        Button time = findViewById(R.id.time_picker);

        // Ajout d'un message personnalisé avant le contenu du SMS si l'utilisateur en a saisi un
        String content = "";
        if (message.getText().length() != 0) {
            content = message.getText() + System.lineSeparator();
        }

        // On retourne le message à envoyer au(x) récepteur(s)
        return content +
                "Rendez-vous au " + address.getText() +
                " le " + date.getText() +
                " à " + time.getText() + "." +
                System.lineSeparator() +
                "Pour valider le rendez-vous et avoir plus d'informations : " + getValidationLink().toString();
    }

    /**
     * Construction de l'uri pour le(s) destinataire(s) du rdv
     *
     * @return String uri à envoyer au(x) destinataire(s)
     */
    private Uri getValidationLink() {
        Button location = findViewById(R.id.location);
        Button date = findViewById(R.id.date_picker);
        Button time = findViewById(R.id.time_picker);

        SharedPreferences sharedPref = this.getSharedPreferences(
                getString(R.string.shared_preference), Context.MODE_PRIVATE);

        String phone = sharedPref.getString(getString(R.string.settings_phone), "");

        Uri.Builder builder = new Uri.Builder();
        builder.scheme("http")
                .authority("rendez-vous-geo.com")
                .appendQueryParameter("latitude", String.valueOf(latLng.latitude))
                .appendQueryParameter("longitude", String.valueOf(latLng.longitude))
                .appendQueryParameter("adress", String.valueOf(location.getText()))
                .appendQueryParameter("phone", phone)
                .appendQueryParameter("date", String.valueOf(date.getText()))
                .appendQueryParameter("time", String.valueOf(time.getText()));

        return builder.build();
    }

    /**
     * Vérification des champs obligatoires avant l'envoi ou la previsualisation
     *
     * @return validité du formulaire (oui/non)
     */
    private boolean formIsInvalid() {
        Button location = findViewById(R.id.location);
        if (location.getText() == getResources().getString(R.string.default_location)) {
            findViewById(R.id.error_location).setVisibility(VISIBLE);
            return true;
        }

        Button date = findViewById(R.id.date_picker);
        if (date.getText() == getResources().getString(R.string.default_date)) {
            findViewById(R.id.error_date).setVisibility(VISIBLE);
            return true;
        }

        Button time = findViewById(R.id.time_picker);
        if (time.getText() == getResources().getString(R.string.default_time)) {
            findViewById(R.id.error_time).setVisibility(VISIBLE);
            return true;
        }

        return false;
    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        finish();
        return true;
    }
}
