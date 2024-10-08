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
import android.view.animation.Animation;
import android.view.animation.Transformation;
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
import es.jcyl.ita.formic.forms.view.widget.Widget;

import static es.jcyl.ita.formic.forms.config.DevConsole.warn;

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

    public static <T> List<T> findNestedWidgetsByClass(View rootView, Class<? extends T> clazz) {
        List<T> list = new ArrayList<>();
        _findWidgetsByClass(rootView, clazz, list);
        return list;
    }

    private static <T> void _findWidgetsByClass(View rootView, Class<? extends T> clazz, List<T> lst) {
        // same name rule followed by FileRenderer to tag the BaseView of
        // the InputFileView: formId:elementId

        //if rootView is an object of type ViewPager2,
        // must search in all fragments of the ViewPager2's adapter
        if (clazz.isInstance(rootView)) {
            lst.add((T) rootView);
        }
        if (rootView instanceof ViewPager2) {
            ViewPagerAdapter adapter = (ViewPagerAdapter) ((ViewPager2) rootView).getAdapter();
            for (TabFragment fragment : adapter.getTabFragments()) {
                _findWidgetsByClass(fragment.getTabView(), clazz, lst);
            }
        } else if (rootView instanceof ViewGroup) {
            ViewGroup group = (ViewGroup) rootView;
            for (int i = 0; i < group.getChildCount(); i++) {
                View child = group.getChildAt(i);
                _findWidgetsByClass(child, clazz, lst);
            }
        }
    }

    public static Widget findNestedWidgetsById(View rootView, String id) {
        Widget view = findComponentWidget(rootView, id);
        if (view == null) {
            //if rootView is an object of type ViewPager2,
            // must search in all fragments of the ViewPager2's adapter
            if (rootView instanceof ViewPager2) {
                ViewPagerAdapter adapter = (ViewPagerAdapter) ((ViewPager2) rootView).getAdapter();
                for (TabFragment fragment : adapter.getTabFragments()) {
                    view = findNestedWidgetsById(fragment.getTabView(), id);
                    if (view != null) {
                        break;
                    }
                }
            } else if (rootView instanceof ViewGroup) {
                ViewGroup group = (ViewGroup) rootView;
                for (int i = 0; i < group.getChildCount(); i++) {
                    View child = group.getChildAt(i);
                    view = findNestedWidgetsById(child, id);
                    if (view != null) {
                        break;
                    }
                }
            }
        }
        return view;
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

    public static Widget findComponentWidget(View rootView, String componentId) {
        return (Widget) rootView.findViewWithTag(componentId);
    }

    public static Widget findComponentWidget(View rootView, UIComponent component) {
        return rootView.findViewWithTag(component.getId());
    }

    public static InputWidget findInputWidget(View rootView, UIInputComponent field) {
        return findInputWidget(rootView, field.getId());
    }

    public static InputWidget findInputWidget(View rootView, String fieldId) {
        View view = ViewHelper.findComponentWidget(rootView, fieldId);
        if (view == null) {
            return null;
        }
        if (!(view instanceof InputWidget)) {
            warn(String.format("The view element referenced by [%s]" +
                    " is not and InputFieldView", fieldId));
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

    public static void expand(final View v) {
        int matchParentMeasureSpec = View.MeasureSpec.makeMeasureSpec(((View) v.getParent()).getWidth(), View.MeasureSpec.EXACTLY);
        int wrapContentMeasureSpec = View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED);
        v.measure(matchParentMeasureSpec, wrapContentMeasureSpec);
        final int targetHeight = v.getMeasuredHeight();

        // Older versions of android (pre API 21) cancel animations for views with a height of 0.
        v.getLayoutParams().height = 1;
        v.setVisibility(View.VISIBLE);
        Animation a = new Animation() {
            @Override
            protected void applyTransformation(float interpolatedTime, Transformation t) {
                v.getLayoutParams().height = interpolatedTime == 1
                        ? ViewGroup.LayoutParams.WRAP_CONTENT
                        : (int) (targetHeight * interpolatedTime);
                v.requestLayout();
            }

            @Override
            public boolean willChangeBounds() {
                return true;
            }
        };

        // Expansion speed of 1dp/ms

        a.setDuration((int) (targetHeight / v.getContext().getResources().getDisplayMetrics().density));
        v.startAnimation(a);
    }

    public static void collapse(final View v) {
        final int initialHeight = v.getMeasuredHeight();

        Animation a = new Animation() {
            @Override
            protected void applyTransformation(float interpolatedTime, Transformation t) {
                if (interpolatedTime == 1) {
                    v.setVisibility(View.GONE);
                } else {
                    v.getLayoutParams().height = initialHeight - (int) (initialHeight * interpolatedTime);
                    v.requestLayout();
                }
            }

            @Override
            public boolean willChangeBounds() {
                return true;
            }
        };

        // Collapse speed of 1dp/ms
        a.setDuration((int) (initialHeight / v.getContext().getResources().getDisplayMetrics().density));
        v.startAnimation(a);
    }

}
