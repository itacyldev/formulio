package es.jcyl.ita.frmdrd.location;

import android.content.Context;
import android.location.Location;
import android.location.LocationManager;
import android.location.LocationProvider;
import android.provider.Settings;

public class LocationTestUtils {

    Context ctx;
    LocationManager _locationManager;
    private String _providerName;

    public boolean addTestProvider(
            Context ctx,
            String providerName, boolean requiresSatellite, boolean requiresNetwork,
            boolean requiresCell, boolean hasMonetaryCost, boolean supportsAltitude,
            boolean supportsSpeed, boolean supportsBearing, int powerRequirement, int accuracy) {

        _providerName = providerName;
        this.ctx = ctx;

        _locationManager = (LocationManager) ctx.getSystemService(Context.LOCATION_SERVICE);

        _locationManager.addTestProvider(
                _providerName,
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
        _locationManager.setTestProviderEnabled(_providerName, true);
        _locationManager.setTestProviderStatus(_providerName, LocationProvider.AVAILABLE, null, System.currentTimeMillis());
        _locationManager.setTestProviderLocation(_providerName, location);
    }

    public LocationManager getLocationManager() {
        return _locationManager;
    }

    public boolean isLocationProviderEnabled(Context ctx, String provider) {
        return Settings.Secure.isLocationProviderEnabled(ctx.getContentResolver(), provider);
    }
}
