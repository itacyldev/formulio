package es.jcyl.ita.formic.forms.components.table;
/*
 * Copyright 2020 Gustavo Río (gustavo.rio@itacyl.es), ITACyL (http://www.itacyl.es).
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

import es.jcyl.ita.formic.forms.components.UIGroupComponent;

/**
 * @author Gustavo Río (gustavo.rio@itacyl.es)
 */
public class UIRow extends UIGroupComponent {
    private String colspans;
    private String weights;

    public UIRow() {
        rendererType ="row";
        renderChildren = true;
    }

    public String getColspans() {
        return colspans;
    }

    public void setColspans(String colspans) {
        this.colspans = colspans;
    }

    public String getWeights() {
        return weights;
    }

    public void setWeights(String weights) {
        this.weights = weights;
    }

    @Override
    public String toString() {
        return "UIRow{" +
                "id='" + getId() + '\'' +
                ", label='" + label + '\'' +
                '}';
    }
}
