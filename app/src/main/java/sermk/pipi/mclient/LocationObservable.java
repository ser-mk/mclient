package sermk.pipi.mclient;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.util.Log;


/**
 * Created by ser on 01.04.18.
 */

class LocationObservable implements LocationListener {

    private static final int REQUEST_FINE_LOCATION = 1234;
    private final boolean enable;
    private LocationManager locationManager;
    private final String TAG = this.getClass().getName();
    private final long MIN_TIME_BW_UPDATES = 1000 * 0;
    private final long MIN_DISTANCE_CHANGE_FOR_UPDATES = 0;

    private Location mLastLocation;


    public LocationObservable(Context context) {
        enable = initNetworkLocation(context);
    }

    public String getStringLocation(){
        if(!enable){
            return "not permission for location";
        }

        if(mLastLocation == null){
            return "not find locations";
        }

        StringBuilder sb = new StringBuilder();

        sb.append("lat: ");
        sb.append(mLastLocation.getLatitude());
        sb.append(" long: ");
        sb.append(mLastLocation.getLongitude());
        sb.append(" acc: ");
        sb.append(mLastLocation.getAccuracy());
        return sb.toString();
    }

    private boolean initNetworkLocation(Context context){

        if(context.checkSelfPermission(Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED){
            Log.w(TAG, "no permission for location provider");
            //ActivityCompat.requestPermissions(this, new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION}, REQUEST_FINE_LOCATION);
            return false;
        }

        locationManager = (LocationManager) context
                .getSystemService(Context.LOCATION_SERVICE);
        if (locationManager == null) {
            Log.w(TAG, "error init locationManager");
            return false;
        }

        final boolean isNetworkEnabled = locationManager
                .isProviderEnabled(LocationManager.NETWORK_PROVIDER);

        if(!isNetworkEnabled){
            Log.w(TAG, "Network locate provider disable");
            return false;
        }

        try {
            locationManager.requestLocationUpdates(
                    LocationManager.NETWORK_PROVIDER,
                    MIN_TIME_BW_UPDATES,
                    MIN_DISTANCE_CHANGE_FOR_UPDATES, this);
        } catch (Exception e){
            e.printStackTrace();
            return false;
        }

        mLastLocation = locationManager.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);

        Log.d(TAG, "Location Enabled! last location " + mLastLocation);

        return true;
    }


    public void release(){
        locationManager.removeUpdates(this);
    }

    @Override
    public void onLocationChanged(Location location) {
        Log.v(TAG, "onLocationChanged: " + location);
        System.out.println("=========== " + location.getLatitude() + " " + location.getLongitude());
        mLastLocation.set(location);
    }

    @Override
    public void onStatusChanged(String provider, int status, Bundle extras) {
        Log.w(TAG, "onStatusChanged: " + provider);
    }

    @Override
    public void onProviderEnabled(String provider) {
        Log.w(TAG, "onProviderEnabled: " + provider);
    }

    @Override
    public void onProviderDisabled(String provider) {
        Log.w(TAG, "onProviderDisabled: " + provider);
    }
}
