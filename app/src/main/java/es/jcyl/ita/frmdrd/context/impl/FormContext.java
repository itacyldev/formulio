package es.jcyl.ita.frmdrd.context.impl;

import android.view.View;

import es.jcyl.ita.crtrepo.Entity;
import es.jcyl.ita.crtrepo.context.impl.BasicContext;
import es.jcyl.ita.frmdrd.ui.components.form.UIForm;

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

public class FormContext extends UnPrefixedCompositeContext {

    private final UIForm form;
    private View rootView;
    private Entity entity;

    private FormViewContext viewContext;
    private EntityContext entityContext;

    public FormContext(UIForm form) {
        this.form = form;
        // component messages
        this.addContext(new BasicContext("messages"));
        // view state
        this.addContext(new BasicContext("state"));
    }

    @Override
    public String getPrefix() {
        return this.form.getId();
    }

    public UIForm getForm() {
        return form;
    }

    public void setEntity(Entity entity) {
        this.entity = entity;
        this.entityContext = new EntityContext(entity);
        this.addContext(entityContext);
    }

    public void setView(View rootView) {
        this.rootView = rootView;
        this.viewContext = new FormViewContext(this.form, this.rootView);
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

    public FormViewContext getViewContext() {
        return viewContext;
    }

    public EntityContext getEntityContext() {
        return entityContext;
    }

    public String toString(){return this.getForm().getId();}

}
