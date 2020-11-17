package es.jcyl.ita.formic.forms.view.helpers;
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

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.viewpager2.widget.ViewPager2;

import com.google.android.material.textfield.TextInputLayout;

import org.apache.commons.lang3.RandomUtils;
import org.apache.commons.lang3.StringUtils;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import es.jcyl.ita.formic.forms.components.UIComponent;
import es.jcyl.ita.formic.forms.components.UIInputComponent;
import es.jcyl.ita.formic.forms.components.tab.TabFragment;
import es.jcyl.ita.formic.forms.components.tab.ViewPagerAdapter;
import es.jcyl.ita.formic.forms.view.widget.InputWidget;

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

    /**
     * Find views containing a text
     *
     * @param rootView
     * @param text
     * @return
     */
    public static Set<View> findViewsContainingText(@NonNull View rootView, @NonNull String text) {
        Set<View> viewSet = new HashSet<>();
        ArrayList<View> viewList = new ArrayList<>();
        rootView.findViewsWithText(viewList, text, View.FIND_VIEWS_WITH_CONTENT_DESCRIPTION);
        viewSet.addAll(viewList);
        viewList.clear();
        rootView.findViewsWithText(viewList, text, View.FIND_VIEWS_WITH_TEXT);
        viewSet.addAll(viewList);
        return viewSet;
    }

    public static <T> Set<T> findViewsContainingText(@NonNull View rootView, @NonNull String text,
                                                     @NonNull Class<T> clazz) {
        Set<T> viewSet = new HashSet<>();
        for (View view : findViewsContainingText(rootView, text)) {
            if (view.getClass().getName().equals(clazz.getName())) {
                viewSet.add((T) view);
            }
        }
        return viewSet;
    }

    /**
     * Find the set of tags that are under a root view.
     *
     * @param rootView
     * @return
     */
    public static Set<String> getTags(@NonNull View rootView) {
        Set<String> tags = new HashSet<>();

        for (View view : getViewList(rootView)) {
            if (view.getTag() instanceof String) {
                tags.add((String) view.getTag());
            }
        }

        return tags;
    }

    /**
     * Recursively loops through the view tree, and returns a listing.
     *
     * @param view Root view.
     * @return All views of tree.
     */
    private static List<View> getViewList(@NonNull View view) {
        List<View> viewList = new ArrayList<>();
        if (view instanceof ViewGroup) {
            int numViews = ((ViewGroup) view).getChildCount();
            if (numViews <= 0) {
                return viewList;
            }
            for (int index = 0; index < numViews; index++) {
                viewList.addAll(getViewList(((ViewGroup) view).getChildAt(index)));
            }
            return viewList;
        } else {
            viewList.add(view);
            return viewList;
        }
    }

    public static View findLabelView(View rootView, String componentId) {
        return rootView.findViewWithTag("label_" + componentId);
    }

    public static View findLabelView(View rootView, UIComponent component) {
        return rootView.findViewWithTag("label_" + component.getId());
    }

    public static String getLabelValue(View rootView, UIComponent component) {
        String label = null;

        View labelView = findLabelView(rootView, component);
        if (labelView instanceof TextView) {
            label = (String) ((TextView) labelView).getText();
        } else if (labelView instanceof TextInputLayout) {
            label = (String) ((TextInputLayout) labelView).getHint();
        }

        return label;
    }

    public static View findComponentView(View rootView, String formId, String componentId) {
        // same name rule followed by FileRenderer to tag the BaseView of
        // the InputFileView: formId:elementId
        View view = rootView.findViewWithTag(formId + ":" + componentId);

        if (view == null) {
            //if rootView is an object of type ViewPager2,
            // must search in all fragments of the ViewPager2's adapter
            if (rootView instanceof ViewPager2) {
                ViewPagerAdapter adapter = (ViewPagerAdapter) ((ViewPager2) rootView).getAdapter();
                for (TabFragment fragment : adapter.getTabFragments()) {
                    view = findComponentView(fragment.getTabView(), formId, componentId);
                    if (view != null) {
                        break;
                    }
                }
            } else if (rootView instanceof ViewGroup) {
                ViewGroup group = (ViewGroup) rootView;
                for (int i = 0; i < group.getChildCount(); i++) {
                    View child = group.getChildAt(i);
                    view = findComponentView(child, formId, componentId);
                    if (view != null) {
                        break;
                    }
                }
            }
        }
        return view;
    }

    public static View findComponentView(View rootView, UIComponent component) {
        // same name rule followed by FileRenderer to tag the BaseView of
        // the InputFileView: formId:elementId
        String formId = (component.getParentForm() != null) ? component.getParentForm().getId() : "root";
        return rootView.findViewWithTag(formId + ":" + component.getId());
    }
//
//    public static List<InputWidget> findInputFieldViews(ViewGroup root) {
//        List<InputWidget> views = new ArrayList<>();
//        final int childCount = root.getChildCount();
//
//        for (int i = 0; i < childCount; i++) {
//            final View child = root.getChildAt(i);
//            if (child instanceof ViewGroup) {
//                views.addAll(findInputFieldViews((ViewGroup) child));
//            }
//            if (child instanceof InputWidget) {
//                views.add((InputWidget) child);
//            }
//        }
//        return views;
//    }

    public static InputWidget findInputFieldViewById(View rootView, UIInputComponent field) {
        return findInputFieldViewById(rootView, field.getParentForm().getId(), field.getId());
    }

    public static InputWidget findInputFieldViewById(View rootView, String formId, String fieldId) {
        View view = ViewHelper.findComponentView(rootView, formId, fieldId);
        if (view == null) {
            return null;
        }
        if (!(view instanceof InputWidget)) {
            throw new IllegalArgumentException(String.format("The view element referenced by [%s]" +
                    " in the form [%s] is not and InputFieldView", formId, fieldId));
        }
        return (InputWidget) view;
    }

    public static <T> T inflate(Context context, int id, Class<T> clazz) {
        return inflate(context, id, null, clazz);
    }

    public static <T> T inflate(Context context, int id, ViewGroup viewGroup, Class<T> clazz) {
        // ramdomize id
        View view = View.inflate(context, id, viewGroup);
        view.setId(RandomUtils.nextInt());
        return (T) view;
    }

    public static <T> T findViewAndSetId(View baseView, int id, Class<T> clazz) {
        // ramdomize id
        View view = baseView.findViewById(id);
        if (view != null) {
            view.setId(RandomUtils.nextInt());
        }
        return (T) view;
    }

    public static View findViewAndSetId(View baseView, int id) {
        // ramdomize id
        View view = baseView.findViewById(id);
        if (view != null) {
            view.setId(RandomUtils.nextInt());
        }
        return view;
    }

    public static View findViewByTagAndSetId(View baseView, String tag) {
        View view = baseView.findViewWithTag(tag);
        // ramdomize id
        if (view != null) {
            view.setId(RandomUtils.nextInt());
        }
        return view;
    }

    public static boolean isVisible(View rootView) {
        return rootView.getVisibility() == View.VISIBLE;
    }
}
