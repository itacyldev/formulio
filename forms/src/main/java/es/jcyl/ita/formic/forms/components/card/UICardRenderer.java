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
import android.view.ViewGroup;
import android.view.ViewParent;
import android.widget.ImageView;
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
import es.jcyl.ita.formic.forms.view.render.renderer.RenderingEnv;
import es.jcyl.ita.formic.forms.view.widget.Widget;

import static android.view.ViewGroup.LayoutParams.MATCH_PARENT;
import static android.view.ViewGroup.LayoutParams.WRAP_CONTENT;
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
        Widget<UICard> widget = ViewHelper.inflate(env.getAndroidContext(), getWidgetLayoutId(component), Widget.class);
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
        if (card.isShowHeader()) {
            setupHeader(widget, card, env);
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
                imageLayout.setMinimumWidth(MATCH_PARENT);
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
        String titleValue = null;
        if (title != null) {
            titleValue = (String) ConvertUtils.convert(title.getValue(env.getWidgetContext()), String.class);
        }
        if (StringUtils.isNotEmpty(titleValue)) {
            titleView.setText(titleValue);
        } else {
            titleView.setVisibility(View.GONE);
        }

        UIHeading subtitle = card.getSubtitle();
        TextView subtitleView = (TextView) ViewHelper.findViewByTagAndSetId(widget, "card_subtitle");
        String subtitleValue = null;
        if (subtitle != null) {
            subtitleValue = (String) ConvertUtils.convert(subtitle.getValue(env.getWidgetContext()), String.class);
        }
        if (StringUtils.isNotEmpty(subtitleValue)) {
            subtitleView.setText(subtitleValue);
        } else {
            subtitleView.setVisibility(View.GONE);
        }

        UIParagraph description = card.getDescription();
        TextView descriptionView = (TextView) ViewHelper.findViewByTagAndSetId(widget, "card_text");
        String descriptionValue = null;
        if (description != null) {
            descriptionValue = (String) ConvertUtils.convert(description.getValue(env.getWidgetContext()), String.class);
        }
        if (StringUtils.isNotEmpty(descriptionValue)) {
            descriptionView.setText(descriptionValue);
        } else {
            descriptionView.setVisibility(View.GONE);
        }
    }

    private void setupHeader(View cardView, UICard card, RenderingEnv env) {

        String headerLabel = card.getLabelValue(env.getWidgetContext());
        if (StringUtils.isNotEmpty(headerLabel)) {
            TextView headerLabelView = (TextView) ViewHelper.findViewAndSetId(cardView, R.id.card_header_label);
            headerLabelView.setText(headerLabel);
        }


        ImageView cardHeaderImage = ViewHelper.findViewAndSetId(cardView, R.id.card_header_image,
                ImageView.class);

        LinearLayout headerLayout = (LinearLayout) ViewHelper.findViewAndSetId(cardView, R.id.card_header);
        final View content = ViewHelper.findViewAndSetId(cardView, R.id.card_content);
        headerLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(content.getVisibility()==View.GONE){
                    cardHeaderImage.setImageResource(R.drawable.ic_action_collapse);
                    ViewHelper.expand(content);
                }else{
                    cardHeaderImage.setImageResource(R.drawable.ic_action_expand);
                    ViewHelper.collapse(content);
                }
            }
        });

        if (!card.isExpanded()) {
            content.setVisibility(View.GONE);
            cardHeaderImage.setImageResource(R.drawable.ic_action_expand);
        }else{
            content.setVisibility(View.VISIBLE);
            cardHeaderImage.setImageResource(R.drawable.ic_action_collapse);
        }

    }

    /**
     * @param widget
     * @param imageView
     */
    private void setImageView(Widget<UICard> widget, View imageView) {
        UICard card = widget.getComponent();
        String imagePosition = card.getImagePosition();
        LinearLayout.LayoutParams params = (LinearLayout.LayoutParams) imageView.getLayoutParams();
        if (imagePosition.equals(TOP.getPosition()) || imagePosition.equals(BOTTOM.getPosition())) {
            params = new LinearLayout.LayoutParams(MATCH_PARENT, MATCH_PARENT);
        }

        if (imagePosition.equals(LEFT.getPosition())) {
            params = new LinearLayout.LayoutParams(WRAP_CONTENT, WRAP_CONTENT);
            params.gravity = Gravity.LEFT;
        }

        LinearLayout imageContainer = (LinearLayout) ViewHelper.findViewByTagAndSetId(widget, "card_image_container");
        imageContainer.removeAllViews();
        imageContainer.setLayoutParams(params);
        imageContainer.addView(imageView);
        imageView.setLayoutParams(params);
    }

    @Override
    public void addViews(RenderingEnv env, Widget<UICard> root, Widget[] views) {
        for (View view : views) {
            if (view instanceof ImageWidget) {
                View imageView = ((ImageWidget) view).getInputView();
                if (imageView != null) {
                    ViewParent parent = imageView.getParent();
                    ((ViewGroup) parent).removeView(imageView);
                }
                setImageView(root, imageView);
            }
        }
    }
}
