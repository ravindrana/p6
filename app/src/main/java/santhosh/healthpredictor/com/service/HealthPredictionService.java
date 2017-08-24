package santhosh.healthpredictor.com.service;

import android.Manifest;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Bundle;
import android.os.IBinder;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.util.Log;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.ActivityRecognition;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;

import santhosh.healthpredictor.com.data.Constants;

public class HealthPredictionService extends Service {
    public static boolean isRunning = false;
    private GoogleApiClient mApiClient;
    private Intent mLocationIntent;

    public HealthPredictionService() {
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        isRunning = true;
        Log.d(Constants.TAG, "HealthPredictionService:: onStartCommand");

        //init
        initGoogleApiClient();
        return START_STICKY;
    }


    private void initGoogleApiClient() {
        //start activity recognition
        //init activity recognition and location services
        mApiClient = new GoogleApiClient.Builder(this)
                .addConnectionCallbacks(mConnListener)
                .addOnConnectionFailedListener(mConnFailedListener)
                .addApi(LocationServices.API) //Location Service
                .addApi(ActivityRecognition.API) //ActivityRecognition Service
                .build();
        mApiClient.connect();

        //create location intent
        mLocationIntent = new Intent(Constants.ACTION_LOCATION);
    }

    private LocationListener mLocationListener = new LocationListener() {
        @Override
        public void onLocationChanged(Location location) {
            Log.d(Constants.TAG, "HealthPredictionService:: onLocationChanged! "+ location);
            if (mLocationIntent != null) {
                mLocationIntent.putExtra(Constants.LOC_LATITUDE, location.getLatitude());
                mLocationIntent.putExtra(Constants.LOC_LONGITUDE, location.getLongitude());

                //TODO: send info to health prediction data population server

                //send to UI
                sendBroadcast(mLocationIntent);
            }
        }
    };

    /**
     * Listener for GoogleApiClient ConnectionCallbacks
     */
    private GoogleApiClient.ConnectionCallbacks mConnListener = new GoogleApiClient.ConnectionCallbacks() {
        @Override
        public void onConnected(@Nullable Bundle bundle) {
            Log.d(Constants.TAG, "GoogleApiClient.onConnected!");

            //register activity recognition
            ActivityRecognition.ActivityRecognitionApi.requestActivityUpdates(mApiClient,
                    60000, getActivityDetectionPendingIntent());

            //create location request
            LocationRequest locationRequest = LocationRequest.create();
            locationRequest.setSmallestDisplacement(100);
            locationRequest.setInterval(60000);
            locationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);

            //get last known location
            if (ActivityCompat.checkSelfPermission(getBaseContext(), android.Manifest.permission.ACCESS_FINE_LOCATION)
                    != PackageManager.PERMISSION_GRANTED) {
                Toast.makeText(getBaseContext(), "ACCESS_FINE_LOCATION permission not granted.", Toast.LENGTH_SHORT).show();
                return;
            }

            //init location listener updates
            LocationServices.FusedLocationApi.requestLocationUpdates(mApiClient,
                    locationRequest, mLocationListener);
        }

        @Override
        public void onConnectionSuspended(int i) {
            Log.d(Constants.TAG, "GoogleApiClient.onConnectionSuspended: " + i);
        }
    };

    /**
     * Listener for GoogleApiClient OnConnectionFailedListener
     */
    private GoogleApiClient.OnConnectionFailedListener mConnFailedListener = new GoogleApiClient.OnConnectionFailedListener() {
        @Override
        public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {
            Log.d(Constants.TAG, "GoogleApiClient.onConnectionFailed: Reconnecting...");
            mApiClient.reconnect();
        }
    };

    @Override
    public void onDestroy() {
        super.onDestroy();
        isRunning = false;
        ActivityRecognition.ActivityRecognitionApi.removeActivityUpdates(mApiClient,
                getActivityDetectionPendingIntent());
        //stop location updates
        LocationServices.FusedLocationApi.removeLocationUpdates(mApiClient, mLocationListener);

        mApiClient.unregisterConnectionCallbacks(mConnListener);
        mApiClient.unregisterConnectionFailedListener(mConnFailedListener);
        mApiClient.disconnect();
        mApiClient = null;
        Log.d(Constants.TAG, "HealthPredictionService:: onDestroy");
    }

    private PendingIntent getActivityDetectionPendingIntent() {
        Intent intent = new Intent(getApplicationContext(), ActivityRecognitionService.class);

        // We use FLAG_UPDATE_CURRENT so that we get the same pending intent back when calling
        // requestActivityUpdates() and removeActivityUpdates().
        return PendingIntent.getService(getApplicationContext(), 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);
    }
    @Override
    public IBinder onBind(Intent intent) {
        // TODO: Return the communication channel to the service.
        throw new UnsupportedOperationException("Not yet implemented");
    }


}
