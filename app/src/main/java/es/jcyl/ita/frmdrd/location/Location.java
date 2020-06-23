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


    private double x;
    private double y;
    private double accuracy;
    private double bearing;
    private Date time;

    public Location(double x, double y, double accuracy, double bearing) {
        this.x = x;
        this.y = y;
        this.accuracy = accuracy;
        this.bearing = bearing;
    }

    public double getX() {
        return x;
    }

    public double getY() {
        return y;
    }

    public double getAccuracy() {
        return accuracy;
    }

    public double getBearing() {
        return bearing;
    }

    public Date getTime() {
        return time;
    }
}
