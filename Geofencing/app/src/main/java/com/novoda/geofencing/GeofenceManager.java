package com.novoda.geofencing;

import android.app.Activity;
import android.app.Dialog;
import android.app.PendingIntent;
import android.content.Intent;
import android.content.IntentSender;
import android.os.Bundle;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.ErrorDialogFragment;
import com.google.android.gms.common.GooglePlayServicesClient;
import com.google.android.gms.common.GooglePlayServicesUtil;
import com.google.android.gms.location.Geofence;
import com.google.android.gms.location.LocationClient;
import com.google.android.gms.location.LocationStatusCodes;

import java.util.ArrayList;
import java.util.List;

public class GeofenceManager implements
        GooglePlayServicesClient.ConnectionCallbacks,
        GooglePlayServicesClient.OnConnectionFailedListener,
        LocationClient.OnAddGeofencesResultListener,
        LocationClient.OnRemoveGeofencesResultListener {

    private final static int CONNECTION_FAILURE_RESOLUTION_REQUEST = 9000;

    private final Activity activity;
    private final ConnectionStatusCallbacks callbacks;
    private final PersistentLogger logger;

    private LocationClient mLocationClient;

    // Flag that indicates if a request is underway.
    private boolean mInProgress;

    public interface ConnectionStatusCallbacks {
        void onConnected();

        void onDisconnected();
    }

    public GeofenceManager(Activity activity, PersistentLogger logger, ConnectionStatusCallbacks callbacks) {
        this.activity = activity;
        this.logger = logger;
        this.callbacks = callbacks;
    }

    private boolean servicesConnected() {
        int resultCode = GooglePlayServicesUtil.isGooglePlayServicesAvailable(activity);
        if (ConnectionResult.SUCCESS == resultCode) {
            logger.logDebug(GeofenceUtils.APPTAG, "Google Play services is available.");
            return true;
        } else {
            Dialog errorDialog = GooglePlayServicesUtil.getErrorDialog(resultCode, activity, CONNECTION_FAILURE_RESOLUTION_REQUEST);
            if (errorDialog != null) {
                ErrorDialogFragment errorFragment = ErrorDialogFragment.newInstance(errorDialog);
                errorFragment.show(activity.getFragmentManager(), "Geofence Detection");
            }
        }
        return false;
    }

    public void connectToLocationClient() {

        if (!servicesConnected()) {
            logger.logError(GeofenceUtils.APPTAG, "Services not connected");
            return;
        }

        /*
         * Create a new location client object. Since the current
         * activity class implements ConnectionCallbacks and
         * OnConnectionFailedListener, pass the current activity object
         * as the listener for both parameters
         */
        mLocationClient = new LocationClient(activity, this, this);
        mLocationClient.connect();

    }

    public void disconnectFromLocationClient() {

        if (mLocationClient == null || !mLocationClient.isConnected()) {
            logger.logError(GeofenceUtils.APPTAG, "Services not connected");
            return;
        }

        mLocationClient.disconnect();

    }

    @Override
    public void onConnected(Bundle bundle) {
        logger.logDebug(GeofenceUtils.APPTAG, "Connected to the Location services");
        callbacks.onConnected();
    }

    @Override
    public void onDisconnected() {
        logger.logDebug(GeofenceUtils.APPTAG, "Disconnected from the Location services");
        callbacks.onDisconnected();
        // Turn off the request flag
        mInProgress = false;
        // Destroy the current location client
        mLocationClient = null;
    }

    public void addGeofences(List<Geofence> geofences) { // If a request is not already underway
        if (mInProgress) {
            /*
             * A request is already underway. You can handle
             * this situation by disconnecting the client,
             * re-setting the flag, and then re-trying the
             * request.
             */
            logger.logError(GeofenceUtils.APPTAG, "Request underway try disconnecting and then retry");
            return;
        }
        mInProgress = true;
        mLocationClient.addGeofences(geofences, getTransitionPendingIntent(), this);
    }

    public void removeGeofences(List<Geofence> geofences) {
        if (mInProgress) {
            /*
             * A request is already underway. You can handle
             * this situation by disconnecting the client,
             * re-setting the flag, and then re-trying the
             * request.
             */
            logger.logError(GeofenceUtils.APPTAG, "Request underway try disconnecting and then retry");
            return;
        }
        mInProgress = true;
        List<String> ids = new ArrayList<String>();
        for (Geofence geofence : geofences) {
            ids.add(geofence.getRequestId());
        }
        mLocationClient.removeGeofences(ids, this);
    }

    @Override
    public void onAddGeofencesResult(int statusCode, String[] strings) {
        // If adding the geofences was successful
        if (LocationStatusCodes.SUCCESS == statusCode) {
            /*
             * Handle successful addition of geofences here.
             * You can send out a broadcast intent or update the UI.
             * geofences into the Intent's extended data.
             */
            logger.logDebug(GeofenceUtils.APPTAG, "Geofence add successful");
        } else {
            // If adding the geofences failed
            /*
             * Report errors here.
             * You can log the error using Log.e() or update
             * the UI.
             */
            logger.logError(GeofenceUtils.APPTAG, "Geofence add failed with status code " + statusCode);
        }
        // Turn off the in progress flag
        mInProgress = false;
    }

    @Override
    public void onConnectionFailed(ConnectionResult connectionResult) {
        // Turn off the request flag
        mInProgress = false;
        /*
         * If the error has a resolution, start a Google Play services
         * activity to resolve it.
         */
        if (connectionResult.hasResolution()) {
            try {
                connectionResult.startResolutionForResult(
                        activity,
                        CONNECTION_FAILURE_RESOLUTION_REQUEST);
            } catch (IntentSender.SendIntentException e) {
                logger.logError(GeofenceUtils.APPTAG, "Could not send intent", e);
            }
            // If no resolution is available, display an error dialog
        } else {
            // Get the error code
            int errorCode = connectionResult.getErrorCode();
            // Get the error dialog from Google Play services
            Dialog errorDialog = GooglePlayServicesUtil.getErrorDialog(errorCode, activity, CONNECTION_FAILURE_RESOLUTION_REQUEST);
            // If Google Play services can provide an error dialog
            if (errorDialog != null) {
                // Create a new DialogFragment for the error dialog
                ErrorDialogFragment errorFragment = ErrorDialogFragment.newInstance(errorDialog);
                errorFragment.show(activity.getFragmentManager(), "Geofence Detection");
            }
        }
    }

    @Override
    public void onRemoveGeofencesByRequestIdsResult(int statusCode, String[] strings) {  // If removing the geofences was successful
        onRemoveGeofences(statusCode);
    }

    @Override
    public void onRemoveGeofencesByPendingIntentResult(int statusCode, PendingIntent pendingIntent) {
        onRemoveGeofences(statusCode);
    }

    private void onRemoveGeofences(int statusCode) {
        if (statusCode == LocationStatusCodes.SUCCESS) {
            /*
             * Handle successful removal of geofences here.
             * You can send out a broadcast intent or update the UI.
             * geofences into the Intent's extended data.
             */
            logger.logDebug(GeofenceUtils.APPTAG, "Geofence remove successful");
        } else {
            // If adding the geocodes failed
            /*
             * Report errors here.
             * You can log the error using Log.e() or update
             * the UI.
             */
            logger.logDebug(GeofenceUtils.APPTAG, "Geofence remove failed with status code " + statusCode);
        }
        /*
         * Indicate that a request is no
         * longer in progress
         */
        mInProgress = false;
    }

    private PendingIntent getTransitionPendingIntent() {
        // Create an explicit Intent
        Intent intent = new Intent(activity,
                ReceiveTransitionsIntentService.class);
        /*
         * Return the PendingIntent
         */
        return PendingIntent.getService(
                activity,
                42,
                intent,
                PendingIntent.FLAG_UPDATE_CURRENT);
    }
}
