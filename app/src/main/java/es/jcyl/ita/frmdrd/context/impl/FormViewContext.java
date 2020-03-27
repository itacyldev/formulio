package es.jcyl.ita.frmdrd.context.impl;
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

import java.util.Collection;
import java.util.Map;
import java.util.Set;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import es.jcyl.ita.crtrepo.context.AbstractBaseContext;
import es.jcyl.ita.frmdrd.ui.components.form.UIForm;

/**
 * @author Gustavo Río (gustavo.rio@itacyl.es)
 */

/**
 * Gives access to view component values using a context interface.
 */
class FormViewContext extends AbstractBaseContext {
    View view;
    UIForm form;

    public FormViewContext(UIForm form, View formView) {
        this.setPrefix("view");
        this.form = form;
        this.view = formView;
    }

    /**
     * Locates a form element by its id
     * @param componentId
     * @return
     */
    public View findComponentView(String componentId) {
        String tag = String.format("%s:%s", this.form.getId(), componentId);
        return this.view.findViewWithTag(tag);
    }

    @Override
    public String getString(String key) {
        return null;
    }

    @Override
    public Object getValue(String key) {
        return null;
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
        return false;
    }

    @Override
    public boolean containsValue(@Nullable Object o) {
        return false;
    }

    @Nullable
    @Override
    public Object get(@Nullable Object o) {
        return null;
    }

    @Nullable
    @Override
    public Object put(String s, Object o) {
        return null;
    }

    @Nullable
    @Override
    public Object remove(@Nullable Object o) {
        return null;
    }

    @Override
    public void putAll(@NonNull Map<? extends String, ?> map) {

    }

    @Override
    public void clear() {

    }

    @NonNull
    @Override
    public Set<String> keySet() {
        return null;
    }

    @NonNull
    @Override
    public Collection<Object> values() {
        return null;
    }

    @NonNull
    @Override
    public Set<Entry<String, Object>> entrySet() {
        return null;
    }
}
