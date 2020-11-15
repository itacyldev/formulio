package es.jcyl.ita.formic.forms.controllers;
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

import es.jcyl.ita.formic.core.context.CompositeContext;
import es.jcyl.ita.formic.forms.components.UIInputComponent;
import es.jcyl.ita.formic.forms.components.form.UIForm;
import es.jcyl.ita.formic.forms.context.impl.EntityContext;
import es.jcyl.ita.formic.forms.context.impl.FormViewContext;
import es.jcyl.ita.formic.forms.controllers.operations.FormEntityPersister;
import es.jcyl.ita.formic.forms.validation.ValidatorException;
import es.jcyl.ita.formic.repo.Entity;

/**
 * @author Gustavo Río (gustavo.rio@itacyl.es)
 * <p>
 * Implements entity edition actions using a edit-form.
 */
public class FormEditController extends FormController {
    private UIForm mainForm;

    private FormEntityPersister entityPersister = new FormEntityPersister();

    public FormEditController(String id, String name) {
        super(id, name);
    }

    /****************************/
    /**  >>> EditView methods **/
    /****************************/

    /**
     * Save current entities
     */
    public void save(CompositeContext context) {
        save(context, this.mainForm);
    }

    public boolean save(CompositeContext context, UIForm form) {
        if (form.isReadOnly()) {
            return false;// no changes
        }
        // validate form date
        if (!validate(form)) {
            throw new ValidatorException(String.format("A error occurred during form validation " +
                    "on form [%s].", form.getId()));
        }
        // transfer changes from view fields to entity properties
        updateEntityFromView(form);

        // persist changes in current and related entities
        this.entityPersister.save(context, form);
        return true;
    }


    public void delete(CompositeContext context) {
        this.delete(context, this.mainForm);
    }

    public boolean delete(CompositeContext context, UIForm form) {
        if (form.isReadOnly()) {
            return false;
        }
        entityPersister.delete(context, form);
        return true;
    }

    /**
     * Access form fields looking for value bindings that can be setable
     */
    private void updateEntityFromView(UIForm form) {
        FormViewContext viewContext = form.getContext().getViewContext();
        EntityContext entityContext = form.getContext().getEntityContext();
        // go over all the form elements looking for bindings that are not readonly
        for (UIInputComponent field : form.getFields()) {
            updateEntityFromView(viewContext, entityContext, field);
        }
    }

    private void updateEntityFromView(FormViewContext viewContext, EntityContext entityContext,
                                      UIInputComponent field) {
        if (field.isBound() && !field.isReadOnly() && !field.isEntityMapping()) {
            // apply change from view context to entity context
            Object value = viewContext.get(field.getId());
            String entityProp = field.getValueExpression().getBindingProperty();
            // remove the "entity" prefix
            entityContext.put(entityProp.substring(entityProp.indexOf(".") + 1), value);
        }
    }

    private boolean isEntityRelation(UIInputComponent field) {
        return false;
    }

    /**
     * Runs validation on all the elements of the given form.
     *
     * @param form
     * @return
     */
    public boolean validate(UIForm form) {
        boolean valid = true;
        form.getContext().clearMessages();
        for (UIInputComponent field : form.getFields()) {
            // validate
            valid &= form.validate(field);
        }
        return valid;
    }

    /**
     * Runs validation on the given element
     *
     * @param field
     * @return
     */
    public boolean validate(UIInputComponent field) {
        UIForm form = field.getParentForm();
        return form.validate(field);
    }

    public Entity getCurrentEntity() {
        return this.mainForm.getEntity();
    }

    /****************************/
    /** GETTER/SETTERS **/
    /****************************/

    public UIForm getMainForm() {
        return mainForm;
    }

    public void setMainForm(UIForm mainForm) {
        this.mainForm = mainForm;
    }
}
