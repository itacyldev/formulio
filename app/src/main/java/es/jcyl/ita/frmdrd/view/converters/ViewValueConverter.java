package es.jcyl.ita.frmdrd.view.converters;
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

import es.jcyl.ita.frmdrd.ui.components.UIComponent;

/**
 * @author Gustavo Río (gustavo.rio@itacyl.es)
 * <p>
 * Funcionality to get and set value to view components
 */
public interface ViewValueConverter<T extends View> {

    /**
     * Used to get value as string to use commons-validation validators and to
     * save current view state
     *
     * @param view
     * @return
     */
    String getValueFromViewAsString(T view);

    /**
     * Retrieves the view value conveted as the given expectecType. This method is used during
     * the applyData phase to set the entity values from the view.
     *
     * @param view
     * @param expectedType
     * @param <C> returning Type
     * @return
     */
    <C> C getValueFromView(T view,  Class<C> expectedType);

    /**
     * Sets value in the view using directly the entity property value, letting the component
     * to convert the value as it needs.
     *
     * @param view
     * @param value
     */
    void setViewValue(T view, Object value);

    /**
     * Method used to restore view from previous state
     *
     * @param view
     * @param value
     */
    void setViewValueAsString(T view, String value);

}
