package com.novoda.geofencing;

import android.app.Activity;
import android.app.AlertDialog;
import android.os.Bundle;
import android.view.View;

import com.google.android.gms.location.Geofence;

import java.util.ArrayList;
import java.util.List;

public class MyActivity extends Activity implements GeofenceManager.ConnectionStatusCallbacks {

    private static final long SECONDS_PER_HOUR = 60;
    private static final long MILLISECONDS_PER_SECOND = 1000;
    private static final long GEOFENCE_EXPIRATION_IN_HOURS = 12;
    private static final long GEOFENCE_EXPIRATION_TIME =
            GEOFENCE_EXPIRATION_IN_HOURS *
                    SECONDS_PER_HOUR *
                    MILLISECONDS_PER_SECOND;

    private List<Geofence> mGeofenceList;

    private GeofenceManager geofenceManager;
    private View addButton;
    private View removeButton;
    private PersistentLogger logger;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my);

        logger = PersistentLogger.newInstance(getApplicationContext());
        geofenceManager = new GeofenceManager(this, logger, this);
        mGeofenceList = createGeofences();

        setupButtons();
    }

    private void setupButtons() {
        addButton = findViewById(R.id.add);
        removeButton = findViewById(R.id.remove);
        addButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                geofenceManager.addGeofences(mGeofenceList);
            }
        });
        removeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                geofenceManager.removeGeofences(mGeofenceList);
            }
        });
        findViewById(R.id.logs).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new AlertDialog.Builder(MyActivity.this)
                        .setTitle("Logs")
                        .setMessage(logger.readLogs())
                        .show();
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        geofenceManager.connectToLocationClient();
    }

    @Override
    protected void onPause() {
        super.onPause();
        geofenceManager.disconnectFromLocationClient();
    }

    public List<Geofence> createGeofences() {
        List<Geofence> geofences = new ArrayList<Geofence>();
        geofences.add(createGeofence("Novoda", 51.5415382, -0.0956768, (float) 30));
        geofences.add(createGeofence("Home", 51.546683, -0.099088, (float) 30));
        geofences.add(createGeofence("Pub", 51.5465749, -0.0991935, (float) 50));
        geofences.add(createGeofence("Tesco", 51.543271, -0.091054, (float) 50));
        geofences.add(createGeofence("Sainsbury", 51.544040, -0.089969, (float) 50));
        return geofences;
    }

    private Geofence createGeofence(String geofenceId, Double latitude, Double longitude, Float radius) {
        return new Geofence.Builder()
                .setRequestId(geofenceId)
                .setTransitionTypes(Geofence.GEOFENCE_TRANSITION_ENTER | Geofence.GEOFENCE_TRANSITION_EXIT)
                .setCircularRegion(latitude, longitude, radius)
                .setExpirationDuration(GEOFENCE_EXPIRATION_TIME)
                .build();
    }

    @Override
    public void onConnected() {
        addButton.setEnabled(true);
        removeButton.setEnabled(true);
    }

    @Override
    public void onDisconnected() {
        addButton.setEnabled(false);
        removeButton.setEnabled(false);
    }
}
