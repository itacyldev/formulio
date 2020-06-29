package es.jcyl.ita.frmdrd.location;

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
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.provider.Settings;

import androidx.core.app.ActivityCompat;

import java.util.Observable;
import java.util.Observer;

import es.jcyl.ita.frmdrd.R;

/**
 * @author Javier Ramos (javier.ramos@itacyl.es)
 */

public class LocationService extends Observable implements LocationListener {

    private static int PERMISSION_REQUEST = 1234;

    private long REQUIRED_ACCURACY = 10;
    private int MAX_ELAPSED_TIME = 5 * 1000; // 5 seconds

    private LocationManager locationManager;
    private static LocationService _instance;
    Context _ctx;


    private Location location;

    public static LocationService getInstance() {
        if (_instance == null) {
            _instance = new LocationService();
        }
        return _instance;
    }

    public LocationService() {
        locationManager = (LocationManager) _ctx.getSystemService(Context.LOCATION_SERVICE);
    }

    public void setContext(Context ctx) {
        _ctx = ctx;
    }


    public void updateLocation(Observer observer) {
        this.addObserver(observer);
        if (_ctx == null) {
            //throw
        } else {
            if (ActivityCompat.checkSelfPermission(_ctx, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED &&
                    ActivityCompat.checkSelfPermission(_ctx, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                // TODO: Consider calling
                //    ActivityCompat#requestPermissions
                // here to request the missing permissions, and then overriding
                //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
                //                                          int[] grantResults)
                // to handle the case where the user grants the permission. See the documentation
                // for ActivityCompat#requestPermissions for more details.
                return;
            }

            locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 5000, 1, this);
        }
    }

    public Location getLastLocation() {
        Location lastLocation = null;
        if (ActivityCompat.checkSelfPermission(_ctx, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED &&
                ActivityCompat.checkSelfPermission(_ctx, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            lastLocation = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
            if (System.currentTimeMillis() - lastLocation.getTime() < MAX_ELAPSED_TIME) {
                lastLocation = null;
            }

        } else {
            ActivityCompat.requestPermissions((Activity) _ctx, new String[]{Manifest.permission.ACCESS_COARSE_LOCATION, Manifest.permission.ACCESS_FINE_LOCATION}, PERMISSION_REQUEST);
        }

        return lastLocation;
    }

    @Override
    public void onLocationChanged(Location location) {
        if (location.getAccuracy() < REQUIRED_ACCURACY) {
            locationManager.removeUpdates(this);
            this.notifyObservers(location);
            this.deleteObservers();
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


    public static void checkLocationProviderEnabled(final Context context) {
        LocationManager lm = (LocationManager) context.getSystemService(Context.LOCATION_SERVICE);
        boolean gps_enabled = false;
        boolean network_enabled = false;

        try {
            gps_enabled = lm.isProviderEnabled(LocationManager.GPS_PROVIDER);
        } catch (Exception ex) {
        }

        try {
            network_enabled = lm.isProviderEnabled(LocationManager.NETWORK_PROVIDER);
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

}
