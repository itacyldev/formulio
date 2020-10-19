package es.jcyl.ita.formic.forms.location;


import android.content.Context;
import android.location.Location;
import android.location.LocationManager;

public class MockLocationProvider {
    String providerName;
    Context ctx;

    public MockLocationProvider(String name, Context ctx) {
        this.providerName = name;
        this.ctx = ctx;

        LocationManager locationManager = (LocationManager) ctx.getSystemService(
                Context.LOCATION_SERVICE);
        locationManager.addTestProvider(providerName, false, false, false, false, false,
                true, true, 0, 5);
        locationManager.setTestProviderEnabled(providerName, true);
    }

    public void pushLocation(Location mockLocation) {
        LocationManager locationManager = (LocationManager) ctx.getSystemService(
                Context.LOCATION_SERVICE);

        locationManager.setTestProviderLocation(providerName, mockLocation);
    }

    public void shutdown() {
        LocationManager locationManager = (LocationManager) ctx.getSystemService(
                Context.LOCATION_SERVICE);
        locationManager.removeTestProvider(providerName);
    }
}
