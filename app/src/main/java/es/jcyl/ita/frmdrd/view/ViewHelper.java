package es.jcyl.ita.frmdrd.view;
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
import android.view.ViewGroup;

import org.apache.commons.lang3.StringUtils;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import es.jcyl.ita.frmdrd.ui.components.inputfield.UIField;

/**
 * @author Gustavo Río (gustavo.rio@itacyl.es)
 */
public class ViewHelper {

    /**
     * Goes over a view group collecting all components with a tag set
     *
     * @param root
     * @return
     */
    public static Set<String> getViewsWithTag(ViewGroup root) {
        Set<String> views = new HashSet<>();
        final int childCount = root.getChildCount();

        for (int i = 0; i < childCount; i++) {
            final View child = root.getChildAt(i);
            if (child instanceof ViewGroup) {
                views.addAll(getViewsWithTag((ViewGroup) child));
            }

            final Object tagObj = child.getTag();
            if (tagObj != null) {
                String tagString = tagObj.toString();
                if (StringUtils.isNotBlank(tagString) && tagString.contains(":")) {
                    views.add(tagString);
                }
            }
        }
        return views;
    }

    public static View findComponentView(View rootView, String formId, String componentId) {
        // same name rule followed by FileRenderer to tag the BaseView of
        // the InputFileView: formId:elementId
        return rootView.findViewWithTag(formId + ":" + componentId);
    }

    public static List<InputFieldView> findInputFieldViews(ViewGroup root) {
        List<InputFieldView> views = new ArrayList<>();
        final int childCount = root.getChildCount();

        for (int i = 0; i < childCount; i++) {
            final View child = root.getChildAt(i);
            if (child instanceof ViewGroup) {
                views.addAll(findInputFieldViews((ViewGroup) child));
            }
            if (child instanceof InputFieldView) {
                views.add((InputFieldView) child);
            }
        }
        return views;
    }

    public static InputFieldView findInputFieldViewById(View rootView, UIField field) {
        return findInputFieldViewById(rootView, field.getParentForm().getId(), field.getId());
    }

    public static InputFieldView findInputFieldViewById(View rootView, String formId, String fieldId) {
        View view = ViewHelper.findComponentView(rootView, formId, fieldId);
        if (view == null) {
            return null;
        }
        if (!(view instanceof InputFieldView)) {
            throw new IllegalArgumentException(String.format("The view element referenced by [%s]" +
                    " in the form [%s] is not and InputFieldView", formId, fieldId));
        }
        return (InputFieldView) view;
    }
}