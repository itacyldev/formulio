package es.jcyl.ita.frmdrd.context.impl;

import android.view.View;

import es.jcyl.ita.crtrepo.Entity;
import es.jcyl.ita.crtrepo.context.impl.OrderedCompositeContext;
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

public class FormContext extends OrderedCompositeContext {

    private final UIForm form;
    private View rootView;
    private Entity entity;

    public FormContext(UIForm form) {
        this.form = form;
    }

    public UIForm getForm() {
        return form;
    }

    public void setEntity(Entity entity) {
        this.entity = entity;
        this.addContext(new EntityContext(entity));
    }

    public void setView(View rootView) {
        this.rootView = rootView;
        this.addContext(new FormViewContext(this.form, this.rootView));
    }

    public Entity getEntity() {
        return entity;
    }
}
