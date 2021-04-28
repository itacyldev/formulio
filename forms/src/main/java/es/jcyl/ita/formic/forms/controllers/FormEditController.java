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

import es.jcyl.ita.formic.core.context.CompositeContext;
import es.jcyl.ita.formic.forms.MainController;
import es.jcyl.ita.formic.forms.components.UIInputComponent;
import es.jcyl.ita.formic.forms.components.form.FormWidget;
import es.jcyl.ita.formic.forms.components.form.UIForm;
import es.jcyl.ita.formic.forms.context.impl.EntityContext;
import es.jcyl.ita.formic.forms.context.impl.ViewContext;
import es.jcyl.ita.formic.forms.controllers.operations.FormEntityPersister;
import es.jcyl.ita.formic.forms.controllers.operations.FormValidator;
import es.jcyl.ita.formic.forms.validation.ValidatorException;
import es.jcyl.ita.formic.forms.view.helpers.ViewHelper;

/**
 * @author Gustavo Río (gustavo.rio@itacyl.es)
 * <p>
 * Implements entity edition actions using a edit-form.
 */
public class FormEditController extends FormController {
    private UIForm mainForm;

    private FormEntityPersister entityPersister = new FormEntityPersister();
    private FormValidator formValidator;

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
        FormWidget widget = (FormWidget) ViewHelper.findComponentWidget(this.getRootWidget(), this.mainForm);
        save(context, widget);
    }

    public boolean save(CompositeContext context, FormWidget formWidget) {
        UIForm form = formWidget.getComponent();
        if ((Boolean) ConvertUtils.convert(form.isReadOnly(context), Boolean.class)) {
            return false;// no changes
        }
        // validate form date
        if (!formValidator.validate(formWidget)) {
            throw new ValidatorException(String.format("A error occurred during form validation " +
                    "on form [%s].", form.getId()));
        }
        // transfer changes from view fields to entity properties
        updateEntityFromView(formWidget);

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
    private void updateEntityFromView(FormWidget widget) {
        ViewContext viewContext = widget.getWidgetContext().getViewContext();
        EntityContext entityContext = widget.getWidgetContext().getEntityContext();
        // go over all the form elements looking for bindings that are not readonly
        UIForm form = widget.getComponent();
        for (UIInputComponent field : form.getFields()) {
            updateEntityFromView(viewContext, entityContext, field);
        }
    }

    /**
     * Get values from the view widgets and apply them to the entity properties
     *
     * @param viewContext
     * @param entityContext
     * @param field
     */
    // apply change from view context to entity context
    private void updateEntityFromView(ViewContext viewContext, EntityContext entityContext,
                                      UIInputComponent field) {
        if (field.isBound() && !field.isNestedProperty()) {
            // Update the entity usint just the widgets contained in the view, no visible
            // widgets might not have been rendered
            if (viewContext.containsKey(field.getId())) {
                Object value = viewContext.get(field.getId());
                String entityProp = field.getValueExpression().getBindingProperty();
                // remove the "entity" prefix
                entityProp = entityProp.substring(entityProp.indexOf(".") + 1);
                entityContext.put(entityProp, value);
            }
        }
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

    @Override
    public void setMc(MainController mc) {
        super.setMc(mc);
        this.formValidator = new FormValidator(mc);
    }
}
