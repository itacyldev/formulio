package es.jcyl.ita.formic.forms.view.widget;

import java.util.List;

import es.jcyl.ita.formic.core.context.impl.BasicContext;
import es.jcyl.ita.formic.core.context.impl.UnPrefixedCompositeContext;
import es.jcyl.ita.formic.forms.view.widget.WidgetContextHolder;
import es.jcyl.ita.formic.forms.context.impl.EntityContext;
import es.jcyl.ita.formic.forms.context.impl.ViewContext;
import es.jcyl.ita.formic.forms.view.widget.StatefulWidget;
import es.jcyl.ita.formic.forms.view.widget.Widget;
import es.jcyl.ita.formic.repo.Entity;

/*
 *
 *  * Copyright 2020 Javier Ramos (javier.ramos@itacyl.es), ITACyL (http://www.itacyl.es).
 *  *
 *  * Licensed under the Apache License, Version 2.0 (the "License");
 *  * you may not use this file except in compliance with the License.
 *  * You may obtain a copy of the License at
 *  *
 *  *      https://www.apache.org/licenses/LICENSE-2.0
 *  *
 *  * Unless required by applicable law or agreed to in writing, software
 *  * distributed under the License is distributed on an "AS IS" BASIS,
 *  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  * See the License for the specific language governing permissions and
 *  * limitations under the License.
 *
 */

/**
 * @author Gustavo Rio (gustavo.rio@itacyl.es)
 */

public class WidgetContext extends UnPrefixedCompositeContext {

    private WidgetContextHolder holder;

    private boolean disposable = false;
    private ViewContext viewContext;
    private EntityContext entityContext;
    private BasicContext messageContext;

    public WidgetContext() {
    }

    public WidgetContext(WidgetContextHolder holder) {
        this.holder = holder;
        this.holder.setWidgetContext(this);
        // view values
        this.viewContext = new ViewContext(holder.getWidget());
        this.addContext(viewContext);
    }

    @Override
    public Object getValue(String key) {
        // shortcut to access entity object instead of EntityContext
        if ("entity".equals(key) && this.entityContext != null) {
            return this.entityContext.getEntity();
        } else {
            return super.getValue(key);
        }
    }

    public void clear() {
        disposable = true;
        this.contexts.clear();
        super.clear();
        if (this.viewContext != null) {
            this.viewContext.clear();
            this.viewContext = null;
        }
        if (this.entityContext != null) {
            this.entityContext.clear();
            this.entityContext = null;
        }
        this.holder = null;
    }

    public String getHolderId() {
        return this.holder.getWidget().getComponentId();
    }

    public WidgetContextHolder getHolder() {
        return holder;
    }

    @Override
    public String getPrefix() {
        return this.holder.getWidget().getComponentId();
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

    public List<StatefulWidget> getStatefulWidgets() {
        return (viewContext == null) ? null : this.viewContext.getStatefulWidgets();
    }

    public BasicContext getMessageContext() {
        return messageContext;
    }

    public void setMessageContext(BasicContext messageContext) {
        this.messageContext = messageContext;
    }
}


