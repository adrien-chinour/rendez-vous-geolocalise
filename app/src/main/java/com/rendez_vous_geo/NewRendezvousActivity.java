package com.rendez_vous_geo;

import android.app.DatePickerDialog;
import android.app.Dialog;
import android.app.TimePickerDialog;
import android.content.Intent;
import android.database.Cursor;
import android.location.Address;
import android.net.Uri;
import android.os.Bundle;
import android.provider.ContactsContract.CommonDataKinds.Phone;
import android.text.Editable;
import android.text.format.DateFormat;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.TimePicker;

import com.google.android.gms.maps.model.LatLng;
import com.schibstedspain.leku.LocationPickerActivity;

import java.util.Calendar;
import java.util.Locale;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.DialogFragment;

import static android.view.View.VISIBLE;

/**
 * Activité de création d'un rdv géolocalisé
 * - Prise en compte des contacts + numéro avec choix multiple
 * - Prise en compte de la position actuelle ou possibilité d'en choisir une
 */
public class NewRendezvousActivity extends AppCompatActivity {

    static final int PICK_CONTACT_REQUEST = 1; // Code requete pour l'intent de choix contact
    static final int PICK_LOCATION_REQUEST = 2; // Code requete pour l'intent de choix lieu

    private LatLng latLng;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
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

    public void addLocationAction(View view) {
        Intent locationPickerIntent = new LocationPickerActivity.Builder()
                .withDefaultLocaleSearchZone()
                .shouldReturnOkOnBackPressed()
                .build(getApplicationContext());

        startActivityForResult(locationPickerIntent, PICK_LOCATION_REQUEST);
    }

    public void previewAction(View view) {
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
        checkFormValidity();
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
                "Pour valider le rendez-vous et avoir plus d'informations : ";
    }

    /**
     * Construction de l'intent pour le(s) destinataire(s) du rdv
     *
     * @return Intent intent à envoyer au(x) destinataire(s)
     */
    private Intent getValidationIntent() {

        Intent intent = new Intent();
        intent.setAction("com.rendez_vous_geo.VALIDATION");
        intent.putExtra("latitude", latLng.latitude);
        intent.putExtra("longitude", latLng.longitude);
        // TODO récupérer numéro + date

        return intent;
    }

    private void checkFormValidity() {
        Button location = findViewById(R.id.location);
        if (location.getText() == getResources().getString(R.string.default_location)) {
            findViewById(R.id.error_location).setVisibility(VISIBLE);
        }

        Button date = findViewById(R.id.date_picker);
        if (date.getText() == getResources().getString(R.string.default_date)) {
            findViewById(R.id.error_date).setVisibility(VISIBLE);
        }

        Button time = findViewById(R.id.time_picker);
        if (time.getText() == getResources().getString(R.string.default_time)) {
            findViewById(R.id.error_time).setVisibility(VISIBLE);
        }
    }

    public static class TimePickerFragment extends DialogFragment implements TimePickerDialog.OnTimeSetListener {

        @Override
        public Dialog onCreateDialog(Bundle savedInstanceState) {
            final Calendar c = Calendar.getInstance();
            int hour = c.get(Calendar.HOUR_OF_DAY);
            int minute = c.get(Calendar.MINUTE);

            return new TimePickerDialog(getActivity(), this, hour, minute,
                    DateFormat.is24HourFormat(getActivity()));
        }

        public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
            Button time = getActivity().findViewById(R.id.time_picker);

            String formattedTime = String.format(Locale.FRANCE, "%02d:%02d", hourOfDay, minute);
            time.setText(formattedTime);
        }
    }

    public void showTimePickerDialog(View view) {
        DialogFragment newFragment = new TimePickerFragment();
        newFragment.show(getSupportFragmentManager(), "timePicker");
    }

    public static class DatePickerFragment extends DialogFragment implements DatePickerDialog.OnDateSetListener {

        @Override
        public Dialog onCreateDialog(Bundle savedInstanceState) {
            final Calendar c = Calendar.getInstance();
            int year = c.get(Calendar.YEAR);
            int month = c.get(Calendar.MONTH);
            int day = c.get(Calendar.DAY_OF_MONTH);

            return new DatePickerDialog(getActivity(), this, year, month + 1, day);
        }

        public void onDateSet(DatePicker view, int year, int month, int day) {
            Button date = getActivity().findViewById(R.id.date_picker);

            String formattedDate = String.format(Locale.FRANCE, "%02d/%02d/%d", day, month, year);
            date.setText(formattedDate);
        }
    }

    public void showDatePickerDialog(View view) {
        DialogFragment newFragment = new DatePickerFragment();
        newFragment.show(getSupportFragmentManager(), "datePicker");
    }
}
