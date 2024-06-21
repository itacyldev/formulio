package es.jcyl.ita.formic.forms.view.converters;
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

import android.view.View;

/**
 * Provides an extensible way to get/set values from android View elements.

 * <p>
 * @author Gustavo Río (gustavo.rio@itacyl.es)
 */
public interface ViewValueConverter<T extends View> {


    /**
     * Retrieves the view value conveted as the given expectecType. This method is used during
     * the applyData phase to set the entity values from the view.
     *
     * @param view
     * @return
     */
    Object getValueFromView(T view);

    /**
     * Sets value in the view using directly the entity property value, letting the component
     * to convert the value as it needs.
     *
     * @param view
     * @param value
     */
    void setViewValue(T view, Object value);

    void setPattern(String pattern);

    void setType(String type);
}
