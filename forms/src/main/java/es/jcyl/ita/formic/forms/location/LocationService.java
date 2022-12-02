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

import static androidx.core.content.ContextCompat.checkSelfPermission;

import android.Manifest;
import android.annotation.SuppressLint;
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

import java.util.List;

import es.jcyl.ita.formic.forms.R;
import es.jcyl.ita.formic.forms.config.DevConsole;

/**
 * @author Javier Ramos (javier.ramos@itacyl.es)
 */

public class LocationService implements LocationListener {

    private static final long MAX_ELAPSED_TIME_DEFAULT = 60 * 60 * 1000; // 60 min
    private static int PERMISSION_REQUEST = 1234;

    private long REQUIRED_ACCURACY = 10;
    public static int MAX_ELAPSED_TIME_GPS = 60 * 1000; // 1 min

    private LocationManager locationManager;
    Context ctx;

    public LocationService(Context ctx) {
        this.ctx = ctx;
        locationManager = (LocationManager) ctx.getSystemService(Context.LOCATION_SERVICE);
    }

    public void updateLocation() {
        if (checkSelfPermission(ctx, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED &&
                checkSelfPermission(ctx, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 5000, 1, this);
            return;
        }
    }

    protected void checkPermission(String provider) {
        if (provider.equals(LocationManager.GPS_PROVIDER) || provider.equals(LocationManager.PASSIVE_PROVIDER)) {
            if (checkSelfPermission(ctx, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions((Activity) ctx,
                        new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, PERMISSION_REQUEST);
            }
        }
        if (provider.equals(LocationManager.NETWORK_PROVIDER)) {
            if (checkSelfPermission(ctx, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED
                    || checkSelfPermission(ctx, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions((Activity) ctx,
                        new String[]{Manifest.permission.ACCESS_COARSE_LOCATION}, PERMISSION_REQUEST);
            }
        }
    }

    @SuppressLint("MissingPermission")
    public Location getLastLocation() {
        Location lastLocation = null;
        //TODO: mover al inicio
        Criteria criteria = new Criteria();
        String bestProvider = locationManager.getBestProvider(criteria, true);
        try {
            checkPermission(bestProvider);
            lastLocation = locationManager.getLastKnownLocation(bestProvider);
            lastLocation = validateLocation(lastLocation, bestProvider);
        } catch (Exception e) {
            DevConsole.error("Error while trying to get location from BEST provider " + bestProvider, e);
        }
        if (lastLocation == null) {
            // try with other providers
            List<String> providers = locationManager.getProviders(true);
            for (String prv : providers) {
                if (!prv.equals(bestProvider)) {
                    try {
                        checkPermission(prv);
                        lastLocation = locationManager.getLastKnownLocation(prv);
                        lastLocation = validateLocation(lastLocation, prv);
                        if (lastLocation != null) {
                            break;
                        }
                    } catch (Exception e) {
                        DevConsole.error("Error while trying to get location from provider " + prv, e);
                    }
                }
            }
        }
        return lastLocation;
    }

    private Location validateLocation(Location location, String provider) {
        long elapsedTime = provider.equals(LocationManager.GPS_PROVIDER) ? MAX_ELAPSED_TIME_GPS :
                MAX_ELAPSED_TIME_DEFAULT;
        if (location == null || (System.currentTimeMillis() - location.getTime() > elapsedTime)) {
            return null;
        } else {
            return location;
        }
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
