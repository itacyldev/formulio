package es.jcyl.ita.formic.forms.components.datalist;

import es.jcyl.ita.formic.forms.components.UIGroupComponent;
import es.jcyl.ita.formic.forms.components.form.ContextHolder;
import es.jcyl.ita.formic.forms.context.impl.ComponentContext;
import es.jcyl.ita.formic.forms.context.impl.EntityContext;
import es.jcyl.ita.formic.forms.context.impl.ViewContext;

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
 * @author Javier Ramos (javier.ramos@itacyl.es)
 */

public class UIDatalistItem extends UIGroupComponent implements ContextHolder {

    public UIDatalistItem() {
        this.setRendererType("datalistitem");
        this.setRenderChildren(true);
        this.context = new ComponentContext(this);
    }

    private ComponentContext context;

    @Override
    public ComponentContext getContext() {
        return context;
    }

    @Override
    public ViewContext getViewContext() {
        return context.getViewContext();
    }

    @Override
    public EntityContext getEntityContext() {
        return context.getEntityContext();
    }
}
