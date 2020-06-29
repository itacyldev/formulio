package es.jcyl.ita.frmdrd.location;

import android.content.Context;
import android.location.Location;
import android.location.LocationManager;

import androidx.test.platform.app.InstrumentationRegistry;

import com.google.android.gms.location.LocationCallback;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.RobolectricTestRunner;

import es.jcyl.ita.frmdrd.R;
import es.jcyl.ita.frmdrd.context.impl.LocationContext;

/**
 * @author Javier Ramos (javier.ramos@itacyl.es)
 * <p>
 * Tests to check Location Context
 */
@RunWith(RobolectricTestRunner.class)
public class LocationContextTest {


    Context ctx;

    @Before
    public void setup() {
        ctx = InstrumentationRegistry.getInstrumentation().getContext();
        ctx.setTheme(R.style.FormudruidLight);
    }

    @Test
    public void testGetPosition() throws Exception {


        LocationTestUtils utils = new LocationTestUtils();
        utils.addTestProvider(ctx, LocationManager.GPS_PROVIDER,
                false, false, false, false,
                false, false, false, 0,
                android.location.Criteria.ACCURACY_FINE);
        LocationService service = LocationService.getInstance();
        service.setContext(ctx);
        LocationObserver observer = new LocationObserver();
        service.updateLocation(observer);

        LocationContext locCtx = new LocationContext("location");
        Location lastLocation = (Location) locCtx.get("lastLocation");


        LocationBuilder builder = new LocationBuilder();
        Location location = builder.withRandomData().build();
        utils.addNewLocation(location);
    }

}

