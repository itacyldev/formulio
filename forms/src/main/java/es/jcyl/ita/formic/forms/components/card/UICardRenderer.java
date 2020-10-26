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

import org.mini2Dx.beanutils.ConvertUtils;

import es.jcyl.ita.formic.forms.R;
import es.jcyl.ita.formic.forms.components.image.ImageResourceView;
import es.jcyl.ita.formic.forms.components.image.UIImage;
import es.jcyl.ita.formic.forms.view.ViewHelper;
import es.jcyl.ita.formic.forms.view.render.AbstractGroupRenderer;
import es.jcyl.ita.formic.forms.view.render.RenderingEnv;
import es.jcyl.ita.formic.forms.view.widget.Widget;

/**
 * @author Javier Ramos (javier.ramos@itacyl.es)
 */

public class UICardRenderer extends AbstractGroupRenderer<UICard, Widget<UICard>> {
    @Override
    protected int getWidgetLayoutId() {
        return R.layout.card_template_1;
    }

    @Override
    protected void composeWidget(RenderingEnv env, Widget<UICard> widget) {
        UICard card = widget.getComponent();

        UIHeading1 title = card.getTitle();
        if (title != null) {
            TextView titleView = (TextView) ViewHelper.findViewByTagAndSetId(widget, "card_title");
            String value = (String) ConvertUtils.convert(title.getValue(env.getContext()), String.class);
            titleView.setText(value);
        }

        UIHeading2 subtitle = card.getSubtitle();
        if (subtitle != null) {
            TextView subtitleView = (TextView) ViewHelper.findViewByTagAndSetId(widget, "card_subtitle");
            String value = (String) ConvertUtils.convert(subtitle.getValue(env.getContext()), String.class);
            subtitleView.setText(value);
        }

        UIImage image = card.getImage();
        if (image != null) {
            ImageResourceView imageView = (ImageResourceView) ViewHelper.findViewByTagAndSetId(widget, "card_image");
        }
    }


    private void setImageView(Widget<UICard> widget, ImageResourceView imageView) {
        LinearLayout imageContainer = (LinearLayout) ViewHelper.findViewByTagAndSetId(widget, "card_image_container");
        imageContainer.addView(imageView);
    }

    @Override
    public void addViews(RenderingEnv env, Widget<UICard> root, View[] views) {
        for (View view : views) {
            if (view instanceof ImageResourceView) {
                setImageView(root, (ImageResourceView) view);
            }
        }
    }


    @Override
    protected void setupWidget(RenderingEnv env, Widget<UICard> widget) {
        super.setupWidget(env, widget);

    }
}
