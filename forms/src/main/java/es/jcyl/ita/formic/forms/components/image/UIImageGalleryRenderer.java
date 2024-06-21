package es.jcyl.ita.formic.forms.components.image;

import android.widget.GridView;

import org.mini2Dx.collections.CollectionUtils;

import java.util.List;

import es.jcyl.ita.formic.forms.R;
import es.jcyl.ita.formic.forms.controllers.operations.BaseWidgetValidator;
import es.jcyl.ita.formic.forms.controllers.widget.GroupWidgetController;
import es.jcyl.ita.formic.forms.view.render.AbstractGroupRenderer;
import es.jcyl.ita.formic.forms.view.render.renderer.RenderingEnv;
import es.jcyl.ita.formic.forms.view.widget.Widget;
import es.jcyl.ita.formic.forms.view.widget.WidgetContextHolder;
import es.jcyl.ita.formic.repo.Entity;

/*
 * Copyright 2022 Javier Ramos (javier.ramos@itacyl.es), ITACyL (http://www.itacyl.es).
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

/**
 * @author Javier Ramos (javier.ramos@itacyl.es)
 */
public class UIImageGalleryRenderer extends AbstractGroupRenderer<UIImageGallery, ImageGalleryWidget> {

    public BaseWidgetValidator validator;

    @Override
    protected int getWidgetLayoutId(UIImageGallery component) {
        return R.layout.widget_imagegallery;
    }

    @Override
    protected void composeWidget(RenderingEnv env, ImageGalleryWidget widget) {
        validator = new BaseWidgetValidator();

        UIImageGallery component = widget.getComponent();
        GridView gridView = widget.findViewById(R.id.imagegrid_view);

        widget.setGridView(gridView);
        widget.setConverter(component.getConverter());

        ImageListAdapter adapter = new ImageListAdapter(env);
        ((ImageGalleryWidget) widget).setAdapter(adapter);
    }


    /**
     * Configure widget after creation.
     *
     * @param env
     * @param widget
     */
    protected void setupWidget(RenderingEnv env, ImageGalleryWidget widget) {
        widget.setup(env);
        List<Entity> entityList = widget.getEntities();
    }

    @Override
    public void addViews(RenderingEnv env, Widget<UIImageGallery> root, Widget[] widgets) {
        ImageGalleryWidget imageGalleryWidget = (ImageGalleryWidget) root;
        ImageListAdapter adapter = imageGalleryWidget.getAdapter();
        for (Widget widget : widgets) {
            adapter.addView((ImageGalleryItemWidget) widget);
        }

    }

    @Override
    public void endGroup(RenderingEnv env, Widget<UIImageGallery> root) {
        super.endGroup(env, root);

        ImageGalleryWidget widget = (ImageGalleryWidget) root;
        List<ImageGalleryItemWidget> items = widget.getItems();

        WidgetContextHolder[] holdersArr = (CollectionUtils.isEmpty(items)) ? new WidgetContextHolder[0] :
                items.toArray(new WidgetContextHolder[items.size()]);

        GroupWidgetController controller = new GroupWidgetController((ImageGalleryWidget) root, holdersArr);
        controller.setValidator(validator);
        ((ImageGalleryWidget) root).setController(controller);
    }
}
