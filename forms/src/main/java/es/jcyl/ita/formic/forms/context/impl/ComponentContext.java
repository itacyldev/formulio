package es.jcyl.ita.formic.forms.context.impl;

import android.view.View;

import es.jcyl.ita.formic.core.context.impl.BasicContext;
import es.jcyl.ita.formic.core.context.impl.UnPrefixedCompositeContext;
import es.jcyl.ita.formic.forms.components.form.ContextHolder;
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

public class ComponentContext extends UnPrefixedCompositeContext {

    private final ContextHolder root;
    private View rootView;
    private Entity entity;

    private ViewContext viewContext;
    private EntityContext entityContext;

    public ComponentContext(ContextHolder component) {
        this.root = component;
        // component messages
        this.addContext(new BasicContext("messages"));
        // view state
        this.addContext(new BasicContext("state"));
    }
    public String getHolderId(){
        return this.root.getId();
    }

    @Override
    public String getPrefix() {
        return this.root.getId();
    }

    public ContextHolder getRoot() {
        return root;
    }

    public void setEntity(Entity entity) {
        this.entity = entity;
        this.entityContext = new EntityContext(entity);
        this.addContext(entityContext);
    }

    public void setView(View rootView) {
        this.rootView = rootView;
        this.viewContext = new ViewContext(this.root, this.rootView);
        this.addContext(viewContext);
    }

    public void clearMessages() {
        this.getContext("messages").clear();
    }

    public void clearMessages(String elementId) {
        this.getContext("messages").remove(elementId);
    }

    public Entity getEntity() {
        return entity;
    }

    public View getRootView() {
        return rootView;
    }

    public ViewContext getViewContext() {
        return viewContext;
    }

    public EntityContext getEntityContext() {
        return entityContext;
    }

    public void setEntityContext(EntityContext entityContext) {
        this.entityContext = entityContext;
    }

    public String toString() {
        return this.getRoot().getId();
    }

}


