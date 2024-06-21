package es.jcyl.ita.formic.forms.location;

import android.content.Context;
import android.location.Location;
import android.location.LocationManager;
import android.location.LocationProvider;

public class LocationTestUtils {

    Context ctx;
    LocationManager locationManager;
    private String providerName;

    public boolean addTestProvider(
            Context ctx,
            String providerName, boolean requiresSatellite, boolean requiresNetwork,
            boolean requiresCell, boolean hasMonetaryCost, boolean supportsAltitude,
            boolean supportsSpeed, boolean supportsBearing, int powerRequirement, int accuracy) {

        this.providerName = providerName;
        this.ctx = ctx;

        locationManager = (LocationManager) ctx.getSystemService(Context.LOCATION_SERVICE);

        locationManager.addTestProvider(
                providerName,
                requiresNetwork,
                requiresSatellite,
                requiresCell,
                hasMonetaryCost,
                supportsAltitude,
                supportsSpeed,
                supportsBearing,
                powerRequirement,
                accuracy
        );

        return true;
    }

    /**
     * Adds a new mock location
     */
    public void addNewLocation(Location location) {
        locationManager.setTestProviderEnabled(providerName, true);
        locationManager.setTestProviderStatus(providerName, LocationProvider.AVAILABLE, null, System.currentTimeMillis());
        locationManager.setTestProviderLocation(providerName, location);
    }
}
