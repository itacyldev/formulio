package es.jcyl.ita.formic.forms.context.impl;

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
import es.jcyl.ita.formic.forms.view.widget.InputWidget;
import es.jcyl.ita.formic.forms.view.widget.StatefulWidget;
import es.jcyl.ita.formic.forms.view.widget.Widget;

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

public class ViewContext extends AbstractBaseContext {
    private final Widget widget; // Form's Android view root
    private Map<String, StatefulWidget> statefulViews = new HashMap<String, StatefulWidget>();

    public ViewContext(Widget widget) {
        this.setPrefix("view");
        this.widget = widget;
    }

    /**
     * Locates a form element widget by its component
     *
     * @param field
     * @return
     */
    public Widget findWidget(UIComponent field) {
        return (Widget) this.statefulViews.get(field.getId());
    }

    public Widget findWidget(String componentId) {
        return (Widget) this.statefulViews.get(componentId);
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
        Object oValue = getValue(elementId);
        return (String) ConvertUtils.convert(oValue, String.class);
    }

    @Override
    public Object getValue(String elementId) {
        Widget widget = findWidget(elementId);
        if (widget == null) {
            warn(String.format("No view element id [%s] .", elementId));
            return null;
        }
        if (!(widget instanceof InputWidget)) {
            return null;
        }
        InputWidget fieldView = (InputWidget) widget;
        UIComponent component = fieldView.getComponent();
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
        return findWidget((String) o) != null;
    }

    @Override
    public boolean containsValue(@Nullable Object o) {
        return findWidget((String) o) != null;
    }

    @Nullable
    @Override
    public Object get(@Nullable Object o) {
        return getValue((String) o);
    }

    @Nullable
    @Override
    public Object put(String elementId, Object value) {
        InputWidget viewField = (InputWidget) findWidget(elementId);
        if (viewField != null) {
            viewField.setValue(value);
        }
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

    public List<StatefulWidget> getStatefulViews() {
        return new ArrayList(statefulViews.values());
    }

    @NonNull
    @Override
    public Set<String> keySet() {
        // get input ids
        Set<String> keys = new HashSet<>();
        statefulViews.keySet();
        if (statefulViews != null) {
            keys.addAll(statefulViews.keySet());
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

    /**
     * Registers componentes in the view contexto to store/retrieve their state in case of re-rendering (postback)
     *
     * @param widget
     * @param widget
     */
    public void registerWidget(StatefulWidget widget) {
        this.statefulViews.put(widget.getComponent().getId(), widget);
    }

}

