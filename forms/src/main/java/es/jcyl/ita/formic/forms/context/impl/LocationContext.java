package es.jcyl.ita.formic.forms.context.impl;

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

import android.location.Location;

import es.jcyl.ita.formic.core.context.AbstractMapContext;
import es.jcyl.ita.formic.core.context.Context;
import es.jcyl.ita.formic.forms.location.LocationService;

/**
 * @author Javier Ramos (javier.ramos@itacyl.es)
 */

public class LocationContext extends AbstractMapContext implements Context {

    LocationService locationService;

    public LocationContext(String prefix) {
        super(prefix);
        locationService = LocationService.getInstance();
    }

    @Override
    public Object get(Object key) {
        return get((String) key);
    }


    @Override
    public Object get(String key) {
        Location lastValidLocation = null;
        if ("lastLocation".equalsIgnoreCase(key)) {
            lastValidLocation = locationService.getLastLocation();
            if (lastValidLocation == null) {
                //locationUpdated = false;
                //locationService.updateLocation(LocationManager.GPS_PROVIDER);

                //waitUpdateLocation(System.currentTimeMillis());
            }
        }

        return lastValidLocation;
    }

    public String getStringLocation() {
        return (locationService.getLastLocation() == null) ? null : locationService.getLastLocation().toString();
    }

    public void setLocationService(LocationService locationService) {
        this.locationService = locationService;
    }

}