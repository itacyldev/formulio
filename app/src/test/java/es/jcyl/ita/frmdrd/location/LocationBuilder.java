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

import android.location.Location;
import android.location.LocationManager;
import android.os.SystemClock;

import java.util.Date;

import es.jcyl.ita.crtrepo.test.utils.RandomUtils;
import es.jcyl.ita.frmdrd.builders.AbstractDataBuilder;
import es.jcyl.ita.frmdrd.builders.DataBuilder;

/**
 * @author Javier Ramos (javier.ramos@itacyl.es)
 */
public class LocationBuilder extends AbstractDataBuilder<Location> {

    @Override
    protected Location getModelInstance() {
        return new Location(LocationManager.GPS_PROVIDER);
    }

    @Override
    public LocationBuilder withRandomData() {
        float randomLatitude = RandomUtils.randomFloat(-90, 90);
        baseModel.setLatitude(randomLatitude);
        float randomLongitude = RandomUtils.randomFloat(-180, 180);
        baseModel.setLongitude(randomLongitude);
        float randomAccuracy = RandomUtils.randomFloat(1, 100);
        baseModel.setAccuracy(randomAccuracy);
        float randomBearing = RandomUtils.randomFloat(-180, 180);
        baseModel.setBearing(randomBearing);

        baseModel.setAltitude(0);

        baseModel.setSpeed(0);

        baseModel.setTime(System.currentTimeMillis());
        baseModel.setElapsedRealtimeNanos(SystemClock.elapsedRealtimeNanos());

        return this;
    }

    public LocationBuilder withLatitude(float latitude) {
        this.baseModel.setLatitude(latitude);
        return this;
    }

    public LocationBuilder withLongitude(float longitude) {
        this.baseModel.setLongitude(longitude);
        return this;
    }

    public LocationBuilder withAccuracy(float accuracy) {
        this.baseModel.setAccuracy(accuracy);
        return this;
    }

    public LocationBuilder withBearing(float bearing) {
        this.baseModel.setBearing(bearing);
        return this;
    }

    public LocationBuilder withTime(long time) {
        this.baseModel.setTime(time);
        return this;
    }

}
