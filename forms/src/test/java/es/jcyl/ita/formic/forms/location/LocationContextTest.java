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

import android.content.Context;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationManager;

import androidx.test.platform.app.InstrumentationRegistry;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.util.ReflectionHelpers;

import es.jcyl.ita.formic.forms.R;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

/**
 * @author Javier Ramos (javier.ramos@itacyl.es)
 * <p>
 * Tests to check Location Context
 */
@RunWith(RobolectricTestRunner.class)
@Ignore("Revisar tests")
public class LocationContextTest {


    Context ctx;
    LocationManager mockLocationManager;

    TestLocationProvider testProvider;

    @Before
    public void setup() {
        ctx = InstrumentationRegistry.getInstrumentation().getContext();
        ctx.setTheme(R.style.FormudruidLight);

        mockLocationManager = mock(LocationManager.class);
        when(mockLocationManager.getBestProvider(any(Criteria.class), any(Boolean.class))).thenReturn(LocationManager.GPS_PROVIDER);
    }

    @Test
    public void testGetLocation() {

        LocationBuilder builder = new LocationBuilder();
        Location mockLocation = builder.withRandomData().build();
        when(mockLocationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER)).thenReturn(mockLocation);

        LocationService locationService = new LocationService(ctx);
        locationService.setLocationManager(mockLocationManager);
    }


    @Test
    public void testGetOldPosition() {
        LocationBuilder builder = new LocationBuilder();
        Location mockLocation = builder.withRandomData().withTime(System.currentTimeMillis() - 100000).build();
        when(mockLocationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER)).thenReturn(mockLocation);

        //
        LocationService locationService = new LocationService(ctx);

        ReflectionHelpers.setField(locationService, "locationManager", mockLocationManager);

    }

}

