package es.jcyl.ita.formic.forms.components.datalist;
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

import android.annotation.SuppressLint;
import android.widget.LinearLayout;

import org.mini2Dx.collections.CollectionUtils;

import java.util.ArrayList;
import java.util.List;

import es.jcyl.ita.formic.forms.R;
import es.jcyl.ita.formic.forms.controllers.operations.BaseWidgetValidator;
import es.jcyl.ita.formic.forms.controllers.widget.GroupWidgetController;
import es.jcyl.ita.formic.forms.view.render.AbstractGroupRenderer;
import es.jcyl.ita.formic.forms.view.render.DeferredView;
import es.jcyl.ita.formic.forms.view.render.renderer.RenderingEnv;
import es.jcyl.ita.formic.forms.view.widget.Widget;
import es.jcyl.ita.formic.forms.view.widget.WidgetContextHolder;


/**
 * @author Javier Ramos (javier.ramos@itacyl.es)
 */

public class UIDatalistRenderer extends AbstractGroupRenderer<UIDatalist, DatalistWidget> {

    public BaseWidgetValidator validator;

    @Override
    protected void composeWidget(RenderingEnv env, DatalistWidget widget) {
        validator =  new BaseWidgetValidator();
    }

    @Override
    protected int getWidgetLayoutId(UIDatalist component) {
        return R.layout.widget_datalist;
    }

    @Override
    protected DatalistWidget createWidget(RenderingEnv env, UIDatalist component) {
        DatalistWidget widget = super.createWidget(env, component);
        widget.setTag(getWidgetViewTag(component));
        return widget;
    }

    @Override
    protected void setupWidget(RenderingEnv env, DatalistWidget widget) {
        super.setupWidget(env, widget);

        LinearLayout contentView = widget.findViewById(R.id.datalist_content_layout);

        widget.setContentView(contentView);
        widget.load(env);
    }

    @SuppressLint("ResourceType")
    @Override
    public void addViews(RenderingEnv env, Widget<UIDatalist> root, Widget[] widgets) {
        DatalistWidget dataListWidget = (DatalistWidget) root;
        List<DatalistItemWidget> itemWidgets = new ArrayList<>();
        for (Widget widget : widgets) {
            if (root instanceof DeferredView) {
                root.addView(widget);
            } else {
                ((DatalistWidget) root).getContentView().addView(widget);
                itemWidgets.add((DatalistItemWidget) widget);
            }
        }
        dataListWidget.setItems(itemWidgets);
    }

    @Override
    public void endGroup(RenderingEnv env, Widget<UIDatalist> root) {
        super.endGroup(env, root);

        DatalistWidget dataListWidget = (DatalistWidget) root;
        List<DatalistItemWidget> items = dataListWidget.getItems();

        WidgetContextHolder[] holdersArr = (CollectionUtils.isEmpty(items)) ? new WidgetContextHolder[0] :
                items.toArray(new WidgetContextHolder[items.size()]);

        GroupWidgetController controller = new GroupWidgetController((DatalistWidget) root, holdersArr);
        controller.setValidator(validator);
        ((DatalistWidget) root).setController(controller);
    }
}
