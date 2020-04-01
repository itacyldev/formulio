package es.jcyl.ita.frmdrd.forms;
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

import android.widget.Toast;

import es.jcyl.ita.crtrepo.EditableRepository;
import es.jcyl.ita.crtrepo.Entity;
import es.jcyl.ita.crtrepo.context.Context;
import es.jcyl.ita.frmdrd.context.impl.EntityContext;
import es.jcyl.ita.frmdrd.context.impl.FormViewContext;
import es.jcyl.ita.frmdrd.ui.components.form.UIForm;
import es.jcyl.ita.frmdrd.ui.components.inputfield.UIField;
import es.jcyl.ita.frmdrd.ui.components.view.UIView;
import es.jcyl.ita.frmdrd.validation.ValidatorException;

/**
 * @author Gustavo Río (gustavo.rio@itacyl.es)
 * <p>
 * Stores form configuration, view, permissions, etc. and provides operations to perform CRUD over and entity
 */
public class FormController {
    private String id;
    private String name;
    private UIView editView;
    private UIView listView;
    private android.content.Context viewContext; // current view context

    public FormController(String id, String name) {
        this.id = id;
        this.name = name;
    }

    /**************************************************/
    /** CRUD operations on Entity */
    /**************************************************/

    /****************************/
    /**  >>> EditView methods **/
    /****************************/
    /**
     * Loads related entity and sets it in the form contexts
     */
    public void load(Context globalCtx) {
        // load all forms included in the view
        for (UIForm form : this.editView.getForms()) {
            form.load(globalCtx);
        }
    }


    private Object getEntityIdFromContext(UIForm uiform, Context globalCtx) {
        String entityIdProp = uiform.getEntityId();
        Object entityId;
        try {
            entityId = globalCtx.get(entityIdProp);
        } catch (Exception e) {
            throw new FormException(String.format("An error occurred while trying to obtain the " +
                    "entity id from params context for form [%s]." +
                    " It seems the property 'entityId' is not properly set: [%s].", uiform.getId(), entityIdProp));
        }
        return entityId;
    }

    /**
     * Save current entities
     */
    public void save() {
        // load all forms included in the view
        for (UIForm form : this.editView.getForms()) {
            // validate form date
            if (!validate(form)) {
                throw new ValidatorException(String.format("A error occurred during form validation on form [%s].",
                        form.getId()));
            }
            // set changes from view fields to entity properties
            updateEntity(form);
            // persist changes
            Entity entity = form.getContext().getEntity();
            form.getRepo().save(entity);
        }

        Toast.makeText(this.viewContext, "Entity successfully saved.", Toast.LENGTH_SHORT).show();
    }

    /**
     * Access form fields looking for value bindings that can be setable
     */
    private void updateEntity(UIForm form) {
        // go over all the form elements looking for bindings that are not readonly
        for (UIField field : form.getFields()) {
            updateEntity(form.getContext().getViewContext(), form.getContext().getEntityContext(), field);
        }
    }

    private void updateEntity(UIField field) {
        UIForm form = field.getParentForm();
        FormViewContext viewContext = form.getContext().getViewContext();
        EntityContext entityContext = form.getContext().getEntityContext();
        updateEntity(viewContext, entityContext, field);
    }

    private void updateEntity(FormViewContext viewContext, EntityContext entityContext, UIField field) {
        if (!field.getValueExpression().isReadOnly()) {
            // apply change from view context to entity context
            Object value = viewContext.get(field.getId());
            String entityProp = field.getValueExpression().getBindingProperty();
            // remove the "entity" prefix
            entityContext.put(entityProp.substring(entityProp.indexOf(".") + 1), value);
        }
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
        for (UIField field : form.getFields()) {
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
    public boolean validate(UIField field) {
        UIForm form = field.getParentForm();
        return form.validate(field);
    }


    /****************************/
    /**  >>> ListView methods */
    /****************************/
    public void list(Object id) {

    }

    public void delete(Context globalCtx) {
        // load all forms included in the view
        for (UIForm form : this.editView.getForms()) {
            doDelete(form, globalCtx);
        }
    }

    protected void doDelete(UIForm uiform, Context globalCtx) {
        EditableRepository repo = uiform.getRepo();
        String entityIdProp = uiform.getEntityId();
        Object entityId = globalCtx.get(entityIdProp);
        repo.deleteById(entityId);
    }

    /****************************/
    /** Save/Restore state **/
    /****************************/

    public void saveViewState() {
        for (UIForm form : this.editView.getForms()) {
            form.saveViewState();
        }
    }

    public void restoreViewState() {
        for (UIForm form : this.editView.getForms()) {
            form.restoreViewState();
        }
    }

    /****************************/
    /** GETTER/SETTERS **/
    /****************************/
    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public UIView getEditView() {
        return editView;
    }

    public UIView getListView() {
        return listView;
    }

    public void setEditView(UIView editView) {
        this.editView = editView;
    }

    public void setListView(UIView listView) {
        this.listView = listView;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public android.content.Context getViewContext() {
        return viewContext;
    }

    public void setViewContext(android.content.Context viewContext) {
        this.viewContext = viewContext;
    }

}
