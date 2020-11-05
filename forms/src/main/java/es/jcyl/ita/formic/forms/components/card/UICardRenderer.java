package es.jcyl.ita.formic.forms.components.card;
/*
 * Copyright 2020 Javier Ramos (javier.ramos@itacyl.es), ITACyL (http://www.itacyl.es).
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
import android.widget.LinearLayout;
import android.widget.TextView;

import org.apache.commons.lang3.RandomUtils;
import org.mini2Dx.beanutils.ConvertUtils;

import es.jcyl.ita.formic.forms.R;
import es.jcyl.ita.formic.forms.components.image.ImageResourceView;
import es.jcyl.ita.formic.forms.components.image.ImageWidget;
import es.jcyl.ita.formic.forms.components.image.UIImage;
import es.jcyl.ita.formic.forms.components.placeholders.UIHeading;
import es.jcyl.ita.formic.forms.view.helpers.ViewHelper;
import es.jcyl.ita.formic.forms.view.render.AbstractGroupRenderer;
import es.jcyl.ita.formic.forms.view.render.RenderingEnv;
import es.jcyl.ita.formic.forms.view.widget.Widget;

/**
 * @author Javier Ramos (javier.ramos@itacyl.es)
 */

public class UICardRenderer extends AbstractGroupRenderer<UICard, Widget<UICard>> {

    public static final String TEMPLATE1 = "card_template_1";
    public static final String TEMPLATE2 = "card_template_2";
    public static final String CUSTOM_TEMPLATE = "card_custom_template";

    @Override
    protected int getWidgetLayoutId() {
        return R.layout.card_template_1;
    }

    private int getWidgetLayoutId(UICard card) {
        int id = R.layout.card_template_1;
        String template = card.getTemplate();
        if (template == null) {
            return id;
        }

        // select the template
        switch (template) {
            case TEMPLATE1: {
                id = R.layout.card_template_1;
                break;
            }
            case TEMPLATE2: {
                id = R.layout.card_template_2;
                break;
            }
        }
        return id;
    }

    /**
     * Create a base view from context and component information to view used as placeholder in the form view
     *
     * @param env
     * @param component
     * @return
     */
    @Override
    protected Widget<UICard> createWidget(RenderingEnv env, UICard component) {
        Widget<UICard> widget = ViewHelper.inflate(env.getViewContext(), getWidgetLayoutId(component), Widget.class);
        // set unique id and tag
        widget.setId(RandomUtils.nextInt());
        widget.setTag(getWidgetViewTag(component));
        widget.setComponent(component);
        return widget;
    }

    @Override
    protected void composeWidget(RenderingEnv env, Widget<UICard> widget) {
        UICard card = widget.getComponent();

        UIHeading title = card.getTitle();
        if (title != null) {
            TextView titleView = (TextView) ViewHelper.findViewByTagAndSetId(widget, "card_title");
            Object titleValue = title.getValue(env.getFormContext());
            String value = (String) ConvertUtils.convert(titleValue, String.class);
            titleView.setText(value);
        }

        UIHeading subtitle = card.getSubtitle();
        if (subtitle != null) {
            TextView subtitleView = (TextView) ViewHelper.findViewByTagAndSetId(widget, "card_subtitle");
            String value = (String) ConvertUtils.convert(subtitle.getValue(env.getFormContext()), String.class);
            subtitleView.setText(value);
        }

        ImageResourceView imageView = (ImageResourceView) ViewHelper.findViewByTagAndSetId(widget, "card_image");
        UIImage image = card.getImage();
        if (image != null) {

        } else {
            //imageView.setVisibility(View.GONE);
        }
    }

    /**
     *
     * @param widget
     * @param imageView
     */
    private void setImageView(Widget<UICard> widget, View imageView) {
        LinearLayout imageContainer = (LinearLayout) ViewHelper.findViewByTagAndSetId(widget, "card_image_container");
        imageContainer.removeAllViews();
        imageContainer.addView(imageView);
    }

    @Override
    public void addViews(RenderingEnv env, Widget<UICard> root, View[] views) {
        for (View view : views) {
            if (view instanceof ImageWidget) {
                View imageView = ((ImageWidget) view).getInputView();
                if (imageView != null)
                    ((ImageWidget) view).removeView(imageView);
                setImageView(root, imageView);
            }
        }
    }
}
