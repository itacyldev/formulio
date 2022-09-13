package es.jcyl.ita.formic.forms.components.image;

import android.widget.GridView;

import java.util.List;

import es.jcyl.ita.formic.forms.R;
import es.jcyl.ita.formic.forms.view.render.AbstractRenderer;
import es.jcyl.ita.formic.forms.view.render.renderer.RenderingEnv;
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
public class UIImageGalleryRenderer extends AbstractRenderer<UIImageGallery, ImageGalleryWidget> {

    @Override
    protected int getWidgetLayoutId(UIImageGallery component) {
        return R.layout.widget_imagegallery;
    }

    @Override
    protected void composeWidget(RenderingEnv env, ImageGalleryWidget widget) {
        UIImageGallery component = widget.getComponent();

        GridView gridView = widget.findViewById(R.id.imagegrid_view);


        widget.setGridView(gridView);

        widget.setConverter(component.getConverter());

        //setValueInView();

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

        ImageListAdapter adapter = new ImageListAdapter(widget.getComponent(), env);
        adapter.addItems(entityList);
        widget.setAdapter(adapter);
    }


    private void setValueInView(RenderingEnv env, ImageGalleryWidget widget) {
        //ImageResourceView inputView = widget.getInputView();
        UIImageGallery component = widget.getComponent();
        Object value = getComponentValue(env, component, null);
        //widget.getConverter().setViewValue(inputView, value);
    }


//    @Override
//    protected void setNestedMessage(RenderingEnv env, Widget<UITab> widget) {
//        TabLayout tabLayout = widget.findViewById(R.id.tab_layout);
//        // find which tabs has error messages
//        UIComponent[] kids = widget.getComponent().getChildren();
//        if (kids == null) {
//            return;
//        }
//        int pos = 0;
//        for (UIComponent tabItem : kids) {
//            String message = MessageHelper.getMessage(env, tabItem);
//            if (!StringUtils.isBlank(message)) {
//                tabLayout.getTabAt(pos).setIcon(R.drawable.ic_input_error);
//            }
//            pos++;
//        }
//    }

}
