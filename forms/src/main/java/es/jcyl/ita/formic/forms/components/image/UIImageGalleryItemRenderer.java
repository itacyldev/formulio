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

import es.jcyl.ita.formic.forms.R;
import es.jcyl.ita.formic.forms.view.render.AbstractRenderer;
import es.jcyl.ita.formic.forms.view.render.renderer.RenderingEnv;

/**
 * @author Gustavo Río (gustavo.rio@itacyl.es)
 * <p>
 * Renders uiImage componentes using Android ImageView
 */
public class UIImageGalleryItemRenderer extends AbstractRenderer<UIImageGalleryItem, ImageGalleryItemWidget> {

    @Override
    protected int getWidgetLayoutId(UIImageGalleryItem component) {
        return R.layout.widget_imagegalleryitem;
    }

    @Override
    protected void composeWidget(RenderingEnv env, ImageGalleryItemWidget widget) {
        setValueInView(env, widget);
    }


    private void setValueInView(RenderingEnv env, ImageGalleryItemWidget widget) {
        ImageResourceView imageView = widget.findViewById(R.id.galleryitem_image);
        UIImageGalleryItem component = widget.getComponent();
        Object value = getComponentValue(env, component, null);
        component.getConverter().setViewValue(imageView, value);
    }
}
