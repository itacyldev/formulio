package es.jcyl.ita.formic.forms.components.image;
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

import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.view.View;
import android.widget.ImageView;

import androidx.core.content.ContextCompat;

import java.io.ByteArrayOutputStream;
import java.util.List;

import es.jcyl.ita.formic.forms.R;
import es.jcyl.ita.formic.forms.components.form.UIForm;
import es.jcyl.ita.formic.forms.view.render.InputRenderer;
import es.jcyl.ita.formic.forms.view.render.renderer.RenderingEnv;
import es.jcyl.ita.formic.forms.view.widget.InputWidget;
import es.jcyl.ita.formic.repo.EntityMapping;

/**
 * @author Gustavo Río (gustavo.rio@itacyl.es)
 * <p>
 * Renders uiImage componentes using Android ImageView
 */
public class UIImageRenderer extends InputRenderer<UIImage, ImageResourceView> {

    @Override
    protected int getWidgetLayoutId(UIImage component) {
        return R.layout.widget_image;
    }

    @Override
    protected InputWidget<UIImage, ImageResourceView> createWidget(RenderingEnv env, UIImage component) {
        ImageWidget imageWidget = (ImageWidget) super.createWidget(env, component);
        env.getFormActivity().registerCallBackForActivity(imageWidget);
        env.getFormActivity().registerCallBackForActivity(imageWidget.getGallerySelector());
        return imageWidget;
    }


    @Override
    protected void composeInputView(RenderingEnv env, InputWidget<UIImage, ImageResourceView> widget) {
        ImageResourceView inputView = widget.getInputView();
        UIImage component = widget.getComponent();
        inputView.requestLayout();

        Integer height = component.getHeight();
        if (height != null) {
            inputView.getLayoutParams().height = height;
        }
        Integer width = component.getWidth();
        if (width != null) {
            inputView.getLayoutParams().width = width;
            //inputView.setAdjustViewBounds(true);
        }

        setOnClickListenerResetButton(env, (ImageWidget) widget);
    }

    private void setOnClickListenerResetButton(RenderingEnv env, ImageWidget widget) {
        UIImage component = widget.getComponent();
        ImageResourceView inputView = widget.getInputView();
        ImageView resetButton = component.getResetButton();
        resetButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(final View arg0) {
                Drawable noImage = ContextCompat
                        .getDrawable(env.getAndroidContext(), R.drawable.
                                no_image);
                inputView.setImageDrawable(noImage);

                UIForm form = (UIForm) component.getParentForm();
                String entityProp = null;
                if (component.getRepo() != null) {
                    entityProp = getEntityProp(component, form);
                    form.getEntity().set(entityProp.substring(entityProp.indexOf(".") + 1, entityProp.length() - 1), null);
                } else if (component.getEmbedded()) { // the image is stored as an entity property
                    Bitmap bitmap = ((BitmapDrawable) noImage).getBitmap();
                    ByteArrayOutputStream stream = new ByteArrayOutputStream();
                    bitmap.compress(Bitmap.CompressFormat.PNG, 90, stream);
                    ((ImageWidget) widget).updateRelatedEntity(stream.toByteArray());
                }
            }
        });
    }

    @Override
    protected void setValueInView(RenderingEnv env, InputWidget<UIImage, ImageResourceView> widget) {
        ImageResourceView inputView = widget.getInputView();
        UIImage component = widget.getComponent();
        Object value = getComponentValue(env, component, null);
        widget.getConverter().setViewValue(inputView, value);
    }

    @Override
    protected void setMessages(RenderingEnv env, InputWidget<UIImage, ImageResourceView> widget) {

    }

    private String getEntityProp(UIImage component, UIForm form) {
        String entityProp = null;

        List<EntityMapping> mappings = form.getRepo().getMappings();
        for (EntityMapping mapping : mappings) {
            if (mapping.getProperty().equals(component.getId())) {
                entityProp = mapping.getFk();
            }
        }
        return entityProp;
    }

}
