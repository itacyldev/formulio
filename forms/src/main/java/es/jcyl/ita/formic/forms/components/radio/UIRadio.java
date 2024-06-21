package es.jcyl.ita.formic.forms.components.radio;

/*
 * Copyright 2020 Gustavo Río Briones (gustavo.rio@itacyl.es), ITACyL (http://www.itacyl.es).
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

import android.widget.LinearLayout;

import es.jcyl.ita.formic.forms.components.select.UISelect;

/**
 * @author Gustavo Río Briones (gustavo.rio@itacyl.es)
 */

public class UIRadio extends UISelect {
    private final static String RADIO_TYPE = "radio";
    private String orientation = "vertical";
    private String weights;

    @Override
    public String getRendererType() {
        return RADIO_TYPE;
    }

    @Override
    public String getValueConverter() {
        return RADIO_TYPE;
    }

    public String getOrientation() {
        return orientation;
    }

    public void setOrientation(String orientation) {
        this.orientation = orientation;
    }

    public int getOrientationType() {
        return (this.orientation.equalsIgnoreCase("horizontal")
                ? LinearLayout.HORIZONTAL : LinearLayout.VERTICAL);
    }

    public String getWeights() {
        return weights;
    }

    public void setWeights(String weights) {
        this.weights = weights;
    }
}
