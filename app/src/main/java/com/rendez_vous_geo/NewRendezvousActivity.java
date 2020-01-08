package com.rendez_vous_geo;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.location.Address;
import android.net.Uri;
import android.os.Build;
import android.os.Build.VERSION_CODES;
import android.os.Bundle;
import android.provider.ContactsContract.CommonDataKinds.Phone;
import android.telephony.SmsManager;
import android.telephony.TelephonyManager;
import android.text.Editable;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.maps.model.LatLng;
import com.schibstedspain.leku.LocationPickerActivity;

import java.util.ArrayList;

import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.DialogFragment;

import static android.Manifest.permission.READ_PHONE_NUMBERS;
import static android.Manifest.permission.READ_PHONE_STATE;
import static android.Manifest.permission.READ_SMS;
import static android.Manifest.permission.SEND_SMS;
import static android.os.Build.VERSION_CODES.*;
import static android.view.View.VISIBLE;

public class NewRendezvousActivity extends AppCompatActivity {

    private static final int MY_PERMISSIONS_REQUEST_SEND_SMS = 0; // Permission SEND_SMS
    private static final int MY_PERMISSIONS_REQUEST_READ_PHONE_STATE = 1; // Permission READ_PHONE_STATE

    private static final int PICK_CONTACT_REQUEST = 1; // Code requete pour l'intent de choix contact
    private static final int PICK_LOCATION_REQUEST = 2; // Code requete pour l'intent de choix lieu

    // coordonnées GPS du RDV
    private LatLng latLng;

    // Liste des numéros des destinataires
    private String[] phoneNumbers;

    // Numéro de l'utilisateur
    private String phone;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_rendezvous);

        /*
         * Vérification de la permission pour la lecture du numéro de téléphone.
         */
        if (checkPhonePermissions()) {
            if (Build.VERSION.SDK_INT >= O) {
                ActivityCompat.requestPermissions(this, new String[]{READ_SMS, READ_PHONE_NUMBERS, READ_PHONE_STATE}, MY_PERMISSIONS_REQUEST_READ_PHONE_STATE);
            } else {
                ActivityCompat.requestPermissions(this, new String[]{READ_SMS, READ_PHONE_STATE}, MY_PERMISSIONS_REQUEST_READ_PHONE_STATE);
            }
        } else {
            phone = getPhone();
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

        TextView preview = findViewById(R.id.msg_preview);
        preview.setVisibility(VISIBLE);
        preview.setText(getMessage());
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
                Toast.makeText(getApplicationContext(), "Le numéro de téléphone " + phoneNumber + " n'est pas valide.", Toast.LENGTH_SHORT).show();
                return;
            } else {
                SmsManager sm = SmsManager.getDefault();
                ArrayList<String> parts = sm.divideMessage(getMessage());
                sm.sendMultipartTextMessage(phoneNumber, null, parts, null, null);
            }
        }

        Toast.makeText(getApplicationContext(), "Votre message a bien été envoyé.", Toast.LENGTH_LONG).show();
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        if (grantResults.length <= 0 || grantResults[0] != PackageManager.PERMISSION_GRANTED) {
            finish(); // L'activité est stoppée car les droits sont nécessaires pour utiliser l'application
            return;
        }
        switch (requestCode) {
            case MY_PERMISSIONS_REQUEST_SEND_SMS:
                sendSms();
                break;
            case MY_PERMISSIONS_REQUEST_READ_PHONE_STATE:
                phone = getPhone();
                break;
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

    /**
     * Récupération du numéro de téléphone de l'utlisateur
     *
     * @return : numéro de téléphone utilisateur
     */
    @SuppressLint("MissingPermission")
    private String getPhone() {
        String number = "";
        TelephonyManager phoneMgr = (TelephonyManager) getSystemService(Context.TELEPHONY_SERVICE);
        if (checkPhonePermissions()) {
            Log.w("test", "isnumbered");
            number = phoneMgr.getLine1Number();
        }

        Log.w("test", number);
        return number;
    }

    /**
     * Vérification des permissions par version android
     *
     * @return
     */
    private boolean checkPhonePermissions() {
        if (Build.VERSION.SDK_INT >= O) {
            return ActivityCompat.checkSelfPermission(this, READ_SMS) != PackageManager.PERMISSION_GRANTED
                    && ActivityCompat.checkSelfPermission(this, READ_PHONE_STATE) != PackageManager.PERMISSION_GRANTED
                    && ActivityCompat.checkSelfPermission(this, READ_PHONE_NUMBERS) != PackageManager.PERMISSION_GRANTED;
        } else if (Build.VERSION.SDK_INT >= M) {
            return ActivityCompat.checkSelfPermission(this, READ_SMS) != PackageManager.PERMISSION_GRANTED
                    && ActivityCompat.checkSelfPermission(this, READ_PHONE_STATE) != PackageManager.PERMISSION_GRANTED;
        } else {
            return true;
        }
    }
}
