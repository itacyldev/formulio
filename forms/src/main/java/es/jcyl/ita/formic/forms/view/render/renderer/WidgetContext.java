package es.jcyl.ita.formic.forms.view.render.renderer;

import es.jcyl.ita.formic.core.context.impl.BasicContext;
import es.jcyl.ita.formic.core.context.impl.UnPrefixedCompositeContext;
import es.jcyl.ita.formic.forms.components.form.WidgetContextHolder;
import es.jcyl.ita.formic.forms.context.impl.EntityContext;
import es.jcyl.ita.formic.forms.context.impl.ViewContext;
import es.jcyl.ita.formic.forms.view.widget.StatefulWidget;
import es.jcyl.ita.formic.forms.view.widget.Widget;
import es.jcyl.ita.formic.repo.Entity;

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

/**
 * @author Gustavo Rio (gustavo.rio@itacyl.es)
 */

public class WidgetContext extends UnPrefixedCompositeContext {

    private final WidgetContextHolder holder;

    private ViewContext viewContext;
    private EntityContext entityContext;

    public WidgetContext(WidgetContextHolder holder) {
        this.holder = holder;
        this.holder.setWidgetContext(this);
        // view values
        this.viewContext = new ViewContext(holder.getWidget());
        this.addContext(viewContext);
    }

    public String getHolderId() {
        return this.holder.getWidget().getWidgetId();
    }

    public WidgetContextHolder getHolder() {
        return holder;
    }

    @Override
    public String getPrefix() {
        return this.holder.getWidget().getWidgetId();
    }

    public Widget getWidget() {
        return holder.getWidget();
    }

    public ViewContext getViewContext() {
        return viewContext;
    }

    public void setEntity(Entity entity) {
        this.entityContext = new EntityContext(entity);
        this.addContext(entityContext);
    }

    public Entity getEntity() {
        return this.entityContext.getEntity();
    }

    public EntityContext getEntityContext() {
        return entityContext;
    }

    public void registerWidget(Widget widget) {
        if (widget instanceof StatefulWidget) {
            this.viewContext.registerWidget((StatefulWidget) widget);
        }
    }

}


