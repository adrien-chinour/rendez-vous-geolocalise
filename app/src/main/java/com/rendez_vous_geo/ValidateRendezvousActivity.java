package com.rendez_vous_geo;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.provider.CalendarContract;
import android.telephony.SmsManager;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

import org.jetbrains.annotations.NotNull;
import org.joda.time.DateTime;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;

import java.util.ArrayList;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import static android.Manifest.permission.SEND_SMS;

/**
 * Activité permettant de d'accepter ou non un rdv
 */
public class ValidateRendezvousActivity extends AppCompatActivity implements OnMapReadyCallback {

    private static final int MY_PERMISSIONS_REQUEST_SEND_SMS = 0;

    private static final String MAP_VIEW_BUNDLE_KEY = "MapViewBundle";

    private MapView mapView;

    private boolean accepted;

    // Attributs récupérés du SMS
    private LatLng latLng;
    private String phoneNumber = null;
    private String address = null;
    private String date = null;
    private String time = null;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_validate_rendezvous);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        Bundle mapViewBundle = null;
        if (savedInstanceState != null) {
            mapViewBundle = savedInstanceState.getBundle(MAP_VIEW_BUNDLE_KEY);
        }

        addViewData();

        mapView = findViewById(R.id.map_view);
        mapView.onCreate(mapViewBundle);
        mapView.getMapAsync(this);
    }

    private void addViewData() {
        Uri dataFromLink = getIntent().getData();
        this.latLng = new LatLng(
                Double.parseDouble(dataFromLink.getQueryParameter("latitude")),
                Double.parseDouble(dataFromLink.getQueryParameter("longitude")));
        this.phoneNumber = dataFromLink.getQueryParameter("phone");
        this.address = dataFromLink.getQueryParameter("adress");
        this.date = dataFromLink.getQueryParameter("date");
        this.time = dataFromLink.getQueryParameter("time");

        TextView title = findViewById(R.id.display_title);
        title.setText("Nouveau rendez-vous reçu du " + this.phoneNumber);

        TextView date = findViewById(R.id.display_date);
        date.setText("Rendez-vous le " + this.date + " à " + this.time);
    }

    @Override
    protected void onResume() {
        super.onResume();
        mapView.onResume();
    }

    @Override
    protected void onStart() {
        super.onStart();
        mapView.onStart();
    }

    @Override
    protected void onStop() {
        super.onStop();
        mapView.onStop();
    }

    @Override
    protected void onPause() {
        mapView.onPause();
        super.onPause();
    }

    @Override
    protected void onDestroy() {
        mapView.onDestroy();
        super.onDestroy();
    }

    @Override
    public void onLowMemory() {
        super.onLowMemory();
        mapView.onLowMemory();
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);

        Bundle mapViewBundle = outState.getBundle(MAP_VIEW_BUNDLE_KEY);
        if (mapViewBundle == null) {
            mapViewBundle = new Bundle();
            outState.putBundle(MAP_VIEW_BUNDLE_KEY, mapViewBundle);
        }

        mapView.onSaveInstanceState(mapViewBundle);
    }

    public void createEventAction(View view) {
        DateTime dateTime;

        try {
            DateTimeFormatter formatter = DateTimeFormat.forPattern("dd/MM/yyyy");
            dateTime = formatter.parseDateTime(date);
        } catch (Exception e) {
            Toast.makeText(this, "Impossible de créer un évenement.", Toast.LENGTH_SHORT).show();
            return;
        }

        Intent intent = new Intent(Intent.ACTION_INSERT)
                .setData(CalendarContract.Events.CONTENT_URI)
                .putExtra(CalendarContract.EXTRA_EVENT_BEGIN_TIME, dateTime.millisOfSecond())
                .putExtra(CalendarContract.Events.EVENT_LOCATION, address);

        startActivity(intent);
    }

    /**
     * Méthode de validation du rdv
     *
     * @param view : bouton accepter
     */
    public void validateAction(View view) {
        accepted = true;
        sendSMS();
    }

    /**
     * Méthode de refus du rdv
     *
     * @param view : bouton refuser
     */
    public void refuseAction(View view) {
        accepted = false;
        sendSMS();
    }

    /**
     * Fonction permettant d'envoyer un SMS
     */
    @SuppressLint("StringFormatMatches")
    public void sendSMS() {

        // vérification des permissions pour l'envoi du sms
        if (ContextCompat.checkSelfPermission(this, SEND_SMS) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{SEND_SMS}, MY_PERMISSIONS_REQUEST_SEND_SMS);
        } else {
            SmsManager sm = SmsManager.getDefault();
            ArrayList<String> parts = sm.divideMessage(getMessage());
            sm.sendMultipartTextMessage(this.phoneNumber, null, parts, null, null);
        }

        // retour a l'activité main
        Toast.makeText(getApplicationContext(), "Votre message a bien été envoyé.", Toast.LENGTH_LONG).show();
        Intent i = new Intent(this, MainActivity.class);
        startActivity(i);
        finish();
    }

    private String getMessage() {
        String message = "Le rendez-vous au " + address + " le " + date + " à " + time;
        if (accepted) {
            return message + " a été accepté.";
        }

        return message + " a été refusé.";
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NotNull String[] permissions, @NotNull int[] grantResults) {
        if (requestCode == MY_PERMISSIONS_REQUEST_SEND_SMS) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                sendSMS();
            } else {
                Toast.makeText(this, getString(R.string.error_permission_sms), Toast.LENGTH_LONG).show();
            }
        }
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        googleMap.setMinZoomPreference(15);
        if (googleMap != null) {
            googleMap.addMarker(new MarkerOptions()
                    .position(latLng).title(address)
                    .icon(BitmapDescriptorFactory.defaultMarker())
                    .draggable(false).visible(true));
            googleMap.moveCamera(CameraUpdateFactory.newLatLng(latLng));
        }
    }
}
