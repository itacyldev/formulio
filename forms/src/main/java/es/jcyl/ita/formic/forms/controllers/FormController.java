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

import android.view.ViewGroup;

import es.jcyl.ita.formic.core.context.CompositeContext;
import es.jcyl.ita.formic.forms.components.FilterableComponent;
import es.jcyl.ita.formic.forms.components.form.UIForm;
import es.jcyl.ita.formic.forms.components.view.UIView;
import es.jcyl.ita.formic.forms.controllers.operations.FormEntityLoader;
import es.jcyl.ita.formic.forms.repo.meta.Identificable;
import es.jcyl.ita.formic.repo.EditableRepository;
import es.jcyl.ita.formic.repo.Entity;
import es.jcyl.ita.formic.repo.Repository;
import es.jcyl.ita.formic.repo.query.Filter;


/**
 * @author Gustavo Río (gustavo.rio@itacyl.es)
 * <p>
 * Stores form configuration, view, permissions, etc. and provides operations to perform CRUD over and entity
 */
public abstract class FormController implements Identificable, FilterableComponent {
    protected String id;
    protected String name;
    protected UIView view;
    protected Repository repo;
    protected Filter filter;
    protected ViewGroup contentView; // Android view element where the UIView is rendered
    private FCAction[] actions; // form actions ids
    private FormEntityLoader entityLoader = new FormEntityLoader();
    private String[] mandatoryFilters;

    public FormController(String id, String name) {
        this.id = id;
        this.name = name;
    }

    /**
     * Loads related entity and sets it in the form contexts
     */
    public void load(CompositeContext globalCtx) {
        Entity entity;
        // load all forms included in the view
        for (UIForm form : this.view.getForms()) {
            entity = entityLoader.load(globalCtx, form);
        }
    }
    public EditableRepository getEditableRepo() {
        return getEditableRepo(this.repo);

    }

    public EditableRepository getEditableRepo(Repository repo) {
        try {
            return (EditableRepository) repo;
        } catch (ClassCastException e) {
            throw new FormException(String.format("You can't use a readonly repository to modify " +
                    "entity data repoId:[%s].", repo.getId()));
        }
    }

    /****************************/
    /** Save/Restore state **/
    /****************************/

    public void saveViewState() {
        for (UIForm form : this.view.getForms()) {
            form.saveViewState();
        }
    }

    public void restoreViewState() {
        for (UIForm form : this.view.getForms()) {
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

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }


    public UIView getView() {
        return view;
    }

    public void setView(UIView view) {
        this.view = view;
    }

    public FCAction[] getActions() {
        return actions;
    }

    public void setActions(FCAction[] actions) {
        this.actions = actions;
    }

    public FCAction getAction(String name) {
        if (this.actions == null) {
            return null;
        } else {
            for (FCAction action : actions) {
                if (name.equalsIgnoreCase(action.getType())) {
                    return action;
                }
            }
            return null;
        }
    }

    public Repository getRepo() {
        return repo;
    }

    public void setRepo(Repository repo) {
        this.repo = repo;
    }

    public Filter getFilter() {
        return filter;
    }

    public void setFilter(Filter filter) {
        this.filter = filter;
    }

    @Override
    public String[] getMandatoryFilters() {
        return mandatoryFilters;
    }

    @Override
    public void setMandatoryFilters(String[] mandatoryFilters) {
        this.mandatoryFilters = mandatoryFilters;
    }

    public boolean hasAction(String name) {
        return getAction(name) != null;
    }

    public ViewGroup getContentView() {
        return contentView;
    }

    public void setContentView(ViewGroup contentView) {
        if (contentView == null) {
            throw new FormException("The content View cannot be null!. " + this.getId());
        }
        this.contentView = contentView;
    }
}
