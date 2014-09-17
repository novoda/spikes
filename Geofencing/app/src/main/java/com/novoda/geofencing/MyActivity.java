package com.novoda.geofencing;

import android.app.Activity;
import android.app.AlertDialog;
import android.os.Bundle;
import android.view.View;

import com.google.android.gms.location.Geofence;

import java.util.ArrayList;
import java.util.List;

public class MyActivity extends Activity implements GeofenceManager.ConnectionStatusCallbacks {

    private static final int DWELLING_DELAY_IN_MILLIS = 1000 * 60 * 15;

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
        geofences.add(createGeofence("Novoda", 51.5415382, -0.0956768, (float) 100));
        geofences.add(createGeofence("Home", 51.546683, -0.099088, (float) 100));
        geofences.add(createGeofence("Alwyn", 51.5465749, -0.0991935, (float) 100));
        geofences.add(createGeofence("Tesco", 51.543271, -0.091054, (float) 100));
        geofences.add(createGeofence("Sainsbury", 51.544040, -0.089969, (float) 100));
        geofences.add(createGeofence("Marquess", 51.543139, -0.095534, (float) 100));
        geofences.add(createGeofence("Old queen", 51.537309, -0.100578, (float) 100));
        geofences.add(createGeofence("Wenlock", 51.536724, -0.101341, (float) 100));
        geofences.add(createGeofence("Flashback", 51.537824, -0.099964, (float) 100));
        geofences.add(createGeofence("Costa", 51.539137, -0.098368, (float) 100));
        geofences.add(createGeofence("Stuffed", 51.537306, -0.100583, (float) 100));
        geofences.add(createGeofence("North Pole", 51.537635, -0.091399, (float) 300));
        geofences.add(createGeofence("Cortado", 51.535776, -0.089387, (float) 500));
        geofences.add(createGeofence("Hummingbird", 51.536137, -0.086218, (float) 300));
        geofences.add(createGeofence("Embassy East", 51.534777, -0.080914, (float) 200));
        geofences.add(createGeofence("Hackney Brewery", 51.534679, -0.075800, (float) 500));
        geofences.add(createGeofence("London Event Photography", 51.535016, -0.069189, (float) 300));
        geofences.add(createGeofence("Lock 7", 51.535337, -0.062668, (float) 300));
        geofences.add(createGeofence("Home2", 51.537344, -0.064213, (float) 100));
        return geofences;
    }

    private Geofence createGeofence(String geofenceId, Double latitude, Double longitude, Float radius) {
        return new Geofence.Builder()
                .setRequestId(geofenceId)
                .setTransitionTypes(Geofence.GEOFENCE_TRANSITION_ENTER | Geofence.GEOFENCE_TRANSITION_EXIT | Geofence.GEOFENCE_TRANSITION_DWELL)
                .setLoiteringDelay(DWELLING_DELAY_IN_MILLIS)
                .setCircularRegion(latitude, longitude, radius)
                .setExpirationDuration(Geofence.NEVER_EXPIRE)
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
