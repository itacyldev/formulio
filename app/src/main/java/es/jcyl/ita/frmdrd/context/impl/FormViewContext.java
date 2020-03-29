package es.jcyl.ita.frmdrd.context.impl;

import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import java.util.AbstractMap;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import es.jcyl.ita.crtrepo.context.AbstractBaseContext;
import es.jcyl.ita.frmdrd.ui.components.UIComponent;
import es.jcyl.ita.frmdrd.ui.components.form.UIForm;
import es.jcyl.ita.frmdrd.view.ViewConfigException;
import es.jcyl.ita.frmdrd.view.ViewHelper;
import es.jcyl.ita.frmdrd.view.converters.ViewValueConverter;
import es.jcyl.ita.frmdrd.view.converters.ViewValueConverterFactory;

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
    View view;
    UIForm form;

    ViewValueConverterFactory convFactory = ViewValueConverterFactory.getInstance();
    private Set<String> keyset;

    public FormViewContext(UIForm form, View formView) {
        this.setPrefix("view");
        this.form = form;
        this.view = formView;
    }

    /**
     * Locates a form element by its id
     *
     * @param componentId
     * @return
     */
    public View findComponentView(String componentId) {
        String formId = this.form.getId();
        if(!componentId.startsWith(formId)){
            componentId = formId + ":" + componentId;
        }
        return this.view.findViewWithTag(componentId);
    }

    @Override
    public String getString(String key) {
        return null;
    }

    @Override
    public Object getValue(String elementId) {
        View view = findComponentView(elementId);
        if (view == null) {
            throw new IllegalArgumentException(String.format("No view element id [%s] " +
                    "doesn't exists in the form [%s].", elementId, form.getId()));
        }

        // get related component to get expected Class type
        UIComponent component = form.getElement(elementId);
        if (component == null) {
            throw new IllegalArgumentException(String.format("The component id provided [%s] " +
                    "doesn't exists in the form [%s].", elementId, form.getId()));
        }
        ViewValueConverter converter = this.convFactory.get(component);
        if (converter == null) {
            throw new ViewConfigException("No converter found for the component " + component.getClass());
        }
        Class expType = component.getValueExpression().getExpectedType();
        if (expType == null) {
            return converter.getValueFromView(view, component);
        } else {
            return converter.getValueFromView(view, component, expType);
        }
    }

    @Override
    public int size() {
        return 0;
    }

    @Override
    public boolean isEmpty() {
        return false;
    }

    @Override
    public boolean containsKey(@Nullable Object o) {
        return findComponentView(o.toString()) != null;
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
        View view = findComponentView(elementId);
        UIComponent component = form.getElement(elementId);
        ViewValueConverter converter = this.convFactory.get(component);
        converter.setViewValue(view, component, value);
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

    @NonNull
    @Override
    public Set<String> keySet() {
        if (this.keyset == null) {
            this.keyset = ViewHelper.getViewsWithTag((ViewGroup) this.view);
        }
        return keyset;
    }

    @NonNull
    @Override
    public Collection<Object> values() {
        List<Object> values = new ArrayList<>();
        for(String key: keySet()){
            values.add(this.getValue(key));
        }
        return values;
    }

    @NonNull
    @Override
    public Set<Entry<String, Object>> entrySet() {
        Set<Entry<String, Object>> entries = new HashSet<>();
        for(String key: keySet()){
            if(key.contains(":")){
                // keep the field id
                key = key.substring(key.indexOf(":")+1);
            }
            entries.add(new AbstractMap.SimpleEntry<String, Object>(key,this.getValue(key)));
        }
        return entries;
    }
}

