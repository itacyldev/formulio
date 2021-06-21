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

import org.mini2Dx.collections.CollectionUtils;

import java.util.List;

import es.jcyl.ita.formic.core.context.CompositeContext;
import es.jcyl.ita.formic.forms.MainController;
import es.jcyl.ita.formic.forms.components.form.UIForm;
import es.jcyl.ita.formic.forms.components.view.UIView;
import es.jcyl.ita.formic.forms.components.view.ViewWidget;
import es.jcyl.ita.formic.forms.controllers.operations.FormEntityLoader;
import es.jcyl.ita.formic.forms.view.ViewStateHolder;
import es.jcyl.ita.formic.repo.Entity;


/**
 * @author Gustavo Río (gustavo.rio@itacyl.es)
 * <p>
 * Stores form configuration, view, permissions, etc. and provides operations to perform CRUD over and entity
 */
public abstract class ViewController {
    protected MainController mc;
    protected String id;
    protected String name;
    protected String description;
    protected UIView view;
    private ViewWidget rootWidget;
    protected ViewGroup contentView; // Android view element where the UIView is rendered
    protected ViewStateHolder stateHolder = new ViewStateHolder();

    //    protected Repository repo;
    private FormEntityLoader entityLoader = new FormEntityLoader();

    public ViewController(String id, String name) {
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
            form.setEntity(entity);
        }
    }

    public UIForm getMainForm() {
        // TODO: improve this
        List<UIForm> forms = this.getView().getForms();
        return (CollectionUtils.isEmpty(forms)) ? null : forms.get(0);
    }

    /****************************/
    /** Save/Restore state **/
    /****************************/

    public void saveViewState() {
        ViewWidget rootWidget = this.getRootWidget();
        stateHolder.saveState(rootWidget);
    }

    public void restoreViewState() {
        stateHolder.restoreState(rootWidget);
    }

    public void restoreViewPartialState() {
        stateHolder.restorePartialState(rootWidget);
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

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public UIView getView() {
        return view;
    }

    public void setView(UIView view) {
        this.view = view;
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

    public MainController getMc() {
        return mc;
    }

    public void setMc(MainController mc) {
        this.mc = mc;
    }

    public void setRootWidget(ViewWidget widget) {
        this.rootWidget = widget;
    }

    public ViewWidget getRootWidget() {
        return rootWidget;
    }
}
