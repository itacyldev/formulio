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

import org.mini2Dx.beanutils.ConvertUtils;

import java.util.Map;

import es.jcyl.ita.formic.core.context.CompositeContext;
import es.jcyl.ita.formic.forms.components.UIInputComponent;
import es.jcyl.ita.formic.forms.components.form.UIForm;
import es.jcyl.ita.formic.forms.context.FormContextHelper;
import es.jcyl.ita.formic.forms.context.impl.EntityContext;
import es.jcyl.ita.formic.forms.context.impl.ComponentContext;
import es.jcyl.ita.formic.forms.context.impl.ViewContext;
import es.jcyl.ita.formic.forms.controllers.operations.FormEntityPersister;
import es.jcyl.ita.formic.forms.scripts.ScriptEngine;
import es.jcyl.ita.formic.forms.validation.Validator;
import es.jcyl.ita.formic.forms.validation.ValidatorException;
import es.jcyl.ita.formic.forms.view.widget.InputWidget;
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
        if ((Boolean) ConvertUtils.convert(form.isReadOnly(context), Boolean.class)) {
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
        this.entityPersister.save(form);
        return true;
    }


    public void delete(CompositeContext context) {
        this.delete(context, this.mainForm);
    }

    public boolean delete(CompositeContext context, UIForm form) {
        if ((Boolean) ConvertUtils.convert(form.isReadOnly(context), Boolean.class)) {
            return false;
        }
        entityPersister.delete(form);
        return true;
    }

    /**
     * Access form fields looking for value bindings that can be setable
     */
    private void updateEntityFromView(UIForm form) {
        ViewContext viewContext = form.getContext().getViewContext();
        EntityContext entityContext = form.getContext().getEntityContext();
        // go over all the form elements looking for bindings that are not readonly
        for (UIInputComponent field : form.getFields()) {
            updateEntityFromView(viewContext, entityContext, field);
        }
    }

    private void updateEntityFromView(ViewContext viewContext, EntityContext entityContext,
                                      UIInputComponent field) {
        if (field.isBound() && !field.isNestedProperty()) {
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
            valid &= validate(field);
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
        ComponentContext context = form.getContext();
        ViewContext viewContext = context.getViewContext();

        // get user input using view context and check all validators.
        String value = viewContext.getString(field.getId());
        boolean valid = true;
        for (Validator validator : field.getValidators()) {
            try {
                if (isVisible(context, field)) {
                    validator.validate(context, field, value);
                }
            } catch (ValidatorException e) {
                // get the error and put it in form context
                FormContextHelper.setMessage(context, field.getId(), e.getMessage());
                valid = false;
            }
        }
        // call validation function
        if (form.getOnValidate() != null) {
            ScriptEngine srcEngine = mc.getScriptEngine();
            // TODO: we have to pass a combination of globalContext + formContext
            Map result = (Map) srcEngine.callFunction(this.id, form.getOnValidate());
            if (result.containsKey("error")) {
                FormContextHelper.setMessage(context, form.getId(), (String) result.get("message"));
                valid = false;
            }
        }
        return valid;
    }

    public boolean isVisible(ComponentContext context, UIInputComponent field) {
        ViewContext viewContext = context.getViewContext();

        InputWidget fieldView = viewContext.findInputFieldViewById(field.getId());
        return fieldView.isVisible();
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
