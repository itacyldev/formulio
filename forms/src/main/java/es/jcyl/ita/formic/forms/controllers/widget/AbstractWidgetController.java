package es.jcyl.ita.formic.forms.controllers.widget;
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

import es.jcyl.ita.formic.forms.components.UIInputComponent;
import es.jcyl.ita.formic.forms.context.impl.EntityContext;
import es.jcyl.ita.formic.forms.context.impl.ViewContext;
import es.jcyl.ita.formic.forms.controllers.operations.WidgetValidator;
import es.jcyl.ita.formic.forms.view.render.renderer.WidgetContext;
import es.jcyl.ita.formic.forms.view.widget.StatefulWidget;
import es.jcyl.ita.formic.repo.EditableRepository;
import es.jcyl.ita.formic.repo.Entity;


/**
 * Provides load/persistence operations over the entity related to the referenced WidgetContext
 *
 * @author Gustavo Río (gustavo.rio@itacyl.es)
 */
public abstract class AbstractWidgetController implements WidgetController {
    protected WidgetValidator validator;

    /**
     * Persist changes in current entity gathering data from Widgets
     *
     * @return
     */
    protected boolean doSave(WidgetContext widgetContext, EditableRepository repo) {
        // transfer changes from view widgets to entity properties
        Entity entity = widgetContext.getEntity();
        boolean valid = true;
        if (validator != null) {
            valid &= validator.validate(widgetContext.getHolder());
        }
        if (valid) {
            doUpdateEntityFromView(widgetContext);
            repo.save(entity);
        }
        return valid;
    }

    protected void doDelete(WidgetContext widgetContext, EditableRepository repo) {
        repo.delete(widgetContext.getEntity());
    }

    public abstract void updateEntityFromView();

    /**
     * Updates current ViewContext entity using the state of the view
     */
    protected void doUpdateEntityFromView(WidgetContext widgetContext) {
        ViewContext viewContext = widgetContext.getViewContext();
        EntityContext entityContext = widgetContext.getEntityContext();
        // go over all the input components looking for bindings that are not readonly
        for (StatefulWidget widget : widgetContext.getStatefulWidgets()) {
            if (widget.getComponent() instanceof UIInputComponent) {
                updateEntity(viewContext, entityContext, (UIInputComponent) widget.getComponent());
            }
        }
    }

    /**
     * Get values from the view widgets and apply them to the entity properties
     *
     * @param viewContext
     * @param entityContext
     * @param field
     */
    private void updateEntity(ViewContext viewContext, EntityContext entityContext,
                              UIInputComponent field) {
        if (field.isBound() && !field.isNestedProperty()) {
            // Update the entity using the widgets contained in the view, no visible
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

    public WidgetValidator getValidator() {
        return validator;
    }

    public void setValidator(WidgetValidator validator) {
        this.validator = validator;
    }
}