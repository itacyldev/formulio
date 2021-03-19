package es.jcyl.ita.formic.forms.context.impl;

import android.view.View;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import org.mini2Dx.beanutils.ConvertUtils;

import java.util.AbstractMap;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import es.jcyl.ita.formic.core.context.AbstractBaseContext;
import es.jcyl.ita.formic.forms.components.UIComponent;
import es.jcyl.ita.formic.forms.components.UIInputComponent;
import es.jcyl.ita.formic.forms.components.form.UIForm;
import es.jcyl.ita.formic.forms.view.helpers.ViewHelper;
import es.jcyl.ita.formic.forms.view.widget.InputWidget;

import static es.jcyl.ita.formic.forms.config.DevConsole.error;
import static es.jcyl.ita.formic.forms.config.DevConsole.warn;

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

/**
 * @author Gustavo Río (gustavo.rio@itacyl.es)
 * <p>
 * Gives access to view component values using a context interface.
 */

public class FormViewContext extends AbstractBaseContext {
    private final View view; // Form's Android view root
    private final UIForm form;
    private Map<String, View> inputViews = new HashMap<String, View>();

    public FormViewContext(UIForm form, View formView) {
        this.setPrefix("view");
        this.form = form;
        this.view = formView;
    }

    /**
     * Locates a form element by its id
     *
     * @param fieldId
     * @return
     */
    public InputWidget findInputFieldViewById(String fieldId) {
        return ViewHelper.findInputFieldViewById(this.view, this.form.getId(), fieldId);
    }

    /**
     * Access the component value as string, without applying the conversion using the
     * component binding expression
     *
     * @param elementId
     * @return
     */
    @Override
    public String getString(String elementId) {
        InputWidget fieldView = findInputFieldViewById(elementId);
        if (fieldView == null) {
            warn(String.format("No view element id [%s] " +
                    "doesn't exists in the form [%s].", elementId, form.getId()));
            return null;
        }
        Object value = fieldView.getValue();
        String strValue = (String) ConvertUtils.convert(value, String.class);
        return strValue;
    }

    @Override
    public Object getValue(String elementId) {
        InputWidget fieldView = findInputFieldViewById(elementId);
        if (fieldView == null) {
            warn(String.format("No view element id [%s] " +
                    "doesn't exists in the form [%s].", elementId, form.getId()));
            return null;
        }
        UIComponent component = form.getElement(elementId);
        if (component == null) {
            warn(String.format("The component id provided [%s] " +
                    "doesn't exists in the form [%s].", elementId, form.getId()));
            return null;
        }
        Object value = fieldView.getValue();
        if (component.getValueExpression() == null) {
            return value;
        } else {
            // get the expected type from the EntityMeta through the binding expression
            Class expType = component.getValueExpression().getExpectedType();
            return (expType == null) ? value : ConvertUtils.convert(value, expType);
        }
    }

    @Override
    public int size() {
        return keySet().size();
    }

    @Override
    public boolean isEmpty() {
        return false;
    }

    @Override
    public boolean containsKey(@Nullable Object o) {
        return ViewHelper.findComponentView(this.view, this.form.getId(), o.toString()) != null;
    }

    @Override
    public boolean containsValue(@Nullable Object o) {
        return form.getElement(o.toString()) != null;
    }

    @Nullable
    @Override
    public Object get(@Nullable Object o) {
        return getValue(o.toString());
    }

    @Nullable
    @Override
    public Object put(String elementId, Object value) {
        InputWidget viewField = findInputFieldViewById(elementId);
        viewField.setValue(value);
        return null; // don't return previous value
    }

    @Nullable
    @Override
    public Object remove(@Nullable Object o) {
        throw new UnsupportedOperationException("You can't remove one component from the view using the context!.");
    }

    @Override
    public void putAll(@NonNull Map<? extends String, ?> map) {
        throw new UnsupportedOperationException("Not supported yet!.");
    }

    @Override
    public void clear() {
        throw new UnsupportedOperationException("You can't remove one component from the view using the context!.");
    }

    public List<InputWidget> getInputViews() {
        return new ArrayList(inputViews.values());
    }

    @NonNull
    @Override
    public Set<String> keySet() {
        // get input ids
        Set<String> keys = new HashSet<>();
        inputViews.keySet();
        if (inputViews != null) {
            keys.addAll(inputViews.keySet());
        }
        return keys;
    }

    @NonNull
    @Override
    public Collection<Object> values() {
        List<Object> values = new ArrayList<>();
        for (String key : keySet()) {
            try {
                Object value = getValue(key);
                values.add(value);
            } catch (Exception e) {
                // TODO: this shouldn't happen
            }
        }
        return values;
    }

    @NonNull
    @Override
    public Set<Entry<String, Object>> entrySet() {
        Set<Entry<String, Object>> entries = new HashSet<>();
        for (String key : keySet()) {
            entries.add(new AbstractMap.SimpleEntry<String, Object>(key, this.getValue(key)));
        }
        return entries;
    }

    public void registerComponentView(UIComponent component, View componentView) {
        if (component instanceof UIInputComponent) {
            this.inputViews.put(component.getId(), componentView);
        }
    }
}

