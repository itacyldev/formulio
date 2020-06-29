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

import java.util.Date;

/**
 * @author Javier Ramos (javier.ramos@itacyl.es)
 */

public class Location {


    private float x;
    private float y;
    private float accuracy;
    private float bearing;
    private Date time;

    public Location() {
    }

    public Location(float x, float y, float accuracy, float bearing) {
        this.x = x;
        this.y = y;
        this.accuracy = accuracy;
        this.bearing = bearing;
    }

    public float getX() {
        return x;
    }

    public float getY() {
        return y;
    }

    public float getAccuracy() {
        return accuracy;
    }

    public float getBearing() {
        return bearing;
    }

    public Date getTime() {
        return time;
    }

    public void setX(float x) {
        this.x = x;
    }

    public void setY(float y) {
        this.y = y;
    }

    public void setAccuracy(float accuracy) {
        this.accuracy = accuracy;
    }

    public void setBearing(float bearing) {
        this.bearing = bearing;
    }

    public void setTime(Date time) {
        this.time = time;
    }

}
