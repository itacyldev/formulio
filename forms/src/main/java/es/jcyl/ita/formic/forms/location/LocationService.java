package es.jcyl.ita.formic.forms.location;

/*
 * Copyright 2020 Javier Ramos (javier.ramos@itacyl.es), ITACyL (http://www.itacyl.es).
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

import android.Manifest;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.provider.Settings;

import androidx.core.app.ActivityCompat;

import es.jcyl.ita.formic.forms.R;

/**
 * @author Javier Ramos (javier.ramos@itacyl.es)
 */

public class LocationService implements LocationListener {

    private static int PERMISSION_REQUEST = 1234;

    private long REQUIRED_ACCURACY = 10;
    public static int MAX_ELAPSED_TIME = 60 * 1000;

    private LocationManager locationManager;
    Context ctx;

    public LocationService(Context ctx) {
        this.ctx = ctx;
        locationManager = (LocationManager) ctx.getSystemService(Context.LOCATION_SERVICE);
    }

    public void updateLocation() {
        if (ActivityCompat.checkSelfPermission(ctx, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED &&
                ActivityCompat.checkSelfPermission(ctx, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 5000, 1, this);
            return;
        }
    }

    public Location getLastLocation() {
        Location lastLocation = null;
        //TODO: mover al inicio
        if (ActivityCompat.checkSelfPermission(ctx, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED &&
                ActivityCompat.checkSelfPermission(ctx, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            Criteria criteria = new Criteria();
            String provider = locationManager.getBestProvider(criteria, true);
            lastLocation = locationManager.getLastKnownLocation(provider);
            //if last location is too old then return null
            if (lastLocation != null && (System.currentTimeMillis() - lastLocation.getTime() > MAX_ELAPSED_TIME)) {
                lastLocation = null;
            }

        } else {
            ActivityCompat.requestPermissions((Activity) ctx, new String[]{Manifest.permission.ACCESS_COARSE_LOCATION, Manifest.permission.ACCESS_FINE_LOCATION}, PERMISSION_REQUEST);
        }
        return lastLocation;
    }

    @Override
    public void onLocationChanged(Location location) {
        if (location.getAccuracy() < REQUIRED_ACCURACY) {
            locationManager.removeUpdates(this);
        }
    }

    @Override
    public void onStatusChanged(String provider, int status, Bundle extras) {
    }

    @Override
    public void onProviderEnabled(String provider) {
    }

    @Override
    public void onProviderDisabled(String provider) {
    }

    public String getAsString() {
        Location lastLocation = getLastLocation();
        return (lastLocation != null) ? lastLocation.toString() : "";
    }

    public String getAsWKT() {
        Location lastLocation = getLastLocation();
        return (lastLocation == null) ? null : String.format("POINT(%s %s)", lastLocation.getLongitude(),
                lastLocation.getLatitude());
    }

    public String getAsWKT3D() {
        Location lastLocation = getLastLocation();
        return (lastLocation == null) ? null : String.format("POINT(%s %s %s)", lastLocation.getLongitude(),
                lastLocation.getLatitude(), lastLocation.getAltitude());
    }

    public String getAsLatLong() {
        Location lastLocation = getLastLocation();
        return (lastLocation == null) ? null : String.format("%s , %s", lastLocation.getLatitude(),
                lastLocation.getLongitude());
    }


    public static void checkLocationProviderEnabled(final Context context) {
        LocationManager locationManager = (LocationManager) context.getSystemService(Context.LOCATION_SERVICE);
        boolean gps_enabled = false;
        boolean network_enabled = false;

        try {
            gps_enabled = locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER);
        } catch (Exception ex) {
        }

        try {
            network_enabled = locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER);
        } catch (Exception ex) {
        }

        if (!gps_enabled && !network_enabled) {
            // notify user
            new AlertDialog.Builder(context)
                    .setMessage(R.string.nolocationenabled)
                    .setPositiveButton(R.string.open_location_settings, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface paramDialogInterface, int paramInt) {
                            context.startActivity(new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS));
                        }
                    })
                    .setNegativeButton(R.string.Cancel, null)
                    .show();
        }
    }

    public LocationManager getLocationManager() {
        return locationManager;
    }

    public void setLocationManager(LocationManager locationManager) {
        this.locationManager = locationManager;
    }
}
