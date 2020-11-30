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

import android.view.Gravity;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.lang3.RandomUtils;
import org.apache.commons.lang3.StringUtils;
import org.mini2Dx.beanutils.ConvertUtils;

import es.jcyl.ita.formic.forms.R;
import es.jcyl.ita.formic.forms.components.UIComponent;
import es.jcyl.ita.formic.forms.components.image.ImageWidget;
import es.jcyl.ita.formic.forms.components.image.UIImage;
import es.jcyl.ita.formic.forms.components.placeholders.UIHeading;
import es.jcyl.ita.formic.forms.components.placeholders.UIParagraph;
import es.jcyl.ita.formic.forms.view.helpers.ViewHelper;
import es.jcyl.ita.formic.forms.view.render.AbstractGroupRenderer;
import es.jcyl.ita.formic.forms.view.render.RenderingEnv;
import es.jcyl.ita.formic.forms.view.widget.Widget;

import static es.jcyl.ita.formic.forms.components.card.UICard.ImagePosition.BOTTOM;
import static es.jcyl.ita.formic.forms.components.card.UICard.ImagePosition.LEFT;
import static es.jcyl.ita.formic.forms.components.card.UICard.ImagePosition.TOP;

/**
 * @author Javier Ramos (javier.ramos@itacyl.es)
 */

public class UICardRenderer extends AbstractGroupRenderer<UICard, Widget<UICard>> {

    public static final String TEMPLATE1 = "card_template_1";
    public static final String TEMPLATE2 = "card_template_2";
    public static final String CUSTOM_TEMPLATE = "card_custom_template";

    @Override
    protected int getWidgetLayoutId(UICard card) {
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

        // Set up header if card has a label
        if (StringUtils.isNotEmpty(card.getLabel())) {
            setupHeader(widget, card);
        } else {
            View headerLayout = widget.findViewById(R.id.card_header);
            headerLayout.setVisibility(View.GONE);
        }


        LinearLayout imageLayout = (LinearLayout) ViewHelper.findViewByTagAndSetId(widget, "card_image_container");
        LinearLayout textLayout = (LinearLayout) ViewHelper.findViewByTagAndSetId(widget, "card_text_container");

        // Check if card has an image configured
        UIImage image = null;
        if (ArrayUtils.isNotEmpty(card.getChildren())) {
            for (UIComponent child : card.getChildren()) {
                if (child instanceof UIImage) {
                    image = (UIImage) child;
                }
            }
        }


        // If card hasn't image don't render the ImageView container
        if (image == null) {
            imageLayout.setVisibility(View.GONE);
        } else {

            LinearLayout contentLayout = (LinearLayout) ViewHelper.findViewByTagAndSetId(widget, "card_content_layout");
            String imagePosition = card.getImagePosition();
            if (imagePosition.equals(TOP.getPosition()) || imagePosition.equals(BOTTOM.getPosition())) {
                contentLayout.setOrientation(LinearLayout.VERTICAL);
            } else {
                contentLayout.setOrientation(LinearLayout.HORIZONTAL);
            }

            if (imagePosition.equals(BOTTOM.getPosition())) {
                contentLayout.removeAllViews();
                textLayout.setHorizontalGravity(Gravity.CENTER_HORIZONTAL);
                contentLayout.addView(textLayout, 0);
                imageLayout.setHorizontalGravity(Gravity.CENTER_HORIZONTAL);
                imageLayout.setVerticalGravity(Gravity.BOTTOM);
                contentLayout.addView(imageLayout, 1);
            }

            if (imagePosition.equals(LEFT.getPosition())) {
                contentLayout.removeAllViews();
                textLayout.setVerticalGravity(Gravity.CENTER_VERTICAL);
                contentLayout.addView(textLayout, 0);
                imageLayout.setVerticalGravity(Gravity.CENTER_VERTICAL);
                imageLayout.setHorizontalGravity(Gravity.LEFT);
                contentLayout.addView(imageLayout, 1);
            }
        }

        UIHeading title = card.getTitle();
        TextView titleView = (TextView) ViewHelper.findViewByTagAndSetId(widget, "card_title");
        if (title != null) {
            Object titleValue = title.getValue(env.getFormContext());
            String value = (String) ConvertUtils.convert(titleValue, String.class);
            titleView.setText(value);
        } else {
            titleView.setVisibility(View.GONE);
        }

        UIHeading subtitle = card.getSubtitle();
        TextView subtitleView = (TextView) ViewHelper.findViewByTagAndSetId(widget, "card_subtitle");
        if (subtitle != null) {
            String value = (String) ConvertUtils.convert(subtitle.getValue(env.getFormContext()), String.class);
            subtitleView.setText(value);
        } else {
            subtitleView.setVisibility(View.GONE);
        }

        UIParagraph description = card.getDescription();
        TextView descriptionView = (TextView) ViewHelper.findViewByTagAndSetId(widget, "card_text");
        if (description != null) {
            String value = (String) ConvertUtils.convert(description.getValue(env.getFormContext()), String.class);
            descriptionView.setText(value);
        } else {
            descriptionView.setVisibility(View.GONE);
        }


    }

    private void setupHeader(View cardView, UICard card) {

        TextView headerLabel = (TextView) ViewHelper.findViewAndSetId(cardView, R.id.card_header_label);
        headerLabel.setText(card.getLabel());

        LinearLayout headerLayout = (LinearLayout) ViewHelper.findViewAndSetId(cardView, R.id.card_header);
        final View content = ViewHelper.findViewAndSetId(cardView, R.id.card_content);
        headerLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //View content = cardView.findViewWithTag("card_content_layout");
                if (card.isExpanded()) {
                    card.setExpanded(false);
                    ViewHelper.collapse(content);
                } else {
                    card.setExpanded(true);
                    ViewHelper.expand(content);
                }
            }
        });


        if (!card.isExpanded()) {
            ViewHelper.collapse(content);
        }
    }

    /**
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

    @Override
    public void endGroup(RenderingEnv env, Widget<UICard> root) {
        try {
//            RelativeLayout contentLayout = root.findViewWithTag("card_content_layout");
//
//            LinearLayout imageContainer = contentLayout.findViewWithTag("card_image_container");
//            RelativeLayout.LayoutParams lpImageContainer = (RelativeLayout.LayoutParams) imageContainer.getLayoutParams();
//
//
//            LinearLayout titleLayout = contentLayout.findViewWithTag("card_title_layout");
//            RelativeLayout.LayoutParams lpTitleLayout = new RelativeLayout.LayoutParams(titleLayout.getLayoutParams());
//            lpTitleLayout.addRule(BELOW, imageContainer.getId());
//
//            LinearLayout subtitleLayout = contentLayout.findViewWithTag("card_subtitle_layout");
//            RelativeLayout.LayoutParams lpSubtitleLayout = new RelativeLayout.LayoutParams(subtitleLayout.getLayoutParams());
//            lpSubtitleLayout.addRule(BELOW, titleLayout.getId());
//
//            contentLayout.removeAllViews();
//
//            contentLayout.addView(imageContainer, lpImageContainer);
//            contentLayout.addView(titleLayout, lpTitleLayout);
//            contentLayout.addView(subtitleLayout, lpSubtitleLayout);

        } catch (ClassCastException ex) {

        }
    }


}
