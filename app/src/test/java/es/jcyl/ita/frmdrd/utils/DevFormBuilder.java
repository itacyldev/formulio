package es.jcyl.ita.frmdrd.utils;
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

import android.content.Context;
import android.view.View;

import es.jcyl.ita.crtrepo.EditableRepository;
import es.jcyl.ita.crtrepo.context.CompositeContext;
import es.jcyl.ita.crtrepo.context.impl.BasicContext;
import es.jcyl.ita.frmdrd.MainController;
import es.jcyl.ita.frmdrd.builders.FieldBuilder;
import es.jcyl.ita.frmdrd.builders.FormBuilder;
import es.jcyl.ita.frmdrd.forms.FormController;
import es.jcyl.ita.frmdrd.render.ExecEnvironment;
import es.jcyl.ita.frmdrd.ui.components.UIComponent;
import es.jcyl.ita.frmdrd.ui.components.form.UIForm;
import es.jcyl.ita.frmdrd.ui.components.inputfield.UIField;
import es.jcyl.ita.frmdrd.ui.components.view.UIView;
import es.jcyl.ita.frmdrd.view.ViewRenderHelper;

/**
 * @author Gustavo Río (gustavo.rio@itacyl.es)
 * <p>
 * Facade class with helper methods to automatically create data and fixtures for tests
 */
public class DevFormBuilder {


    static FieldBuilder fBuilder = new FieldBuilder();
    static FormBuilder formBuilder = new FormBuilder();

    public static FormController createFormController(UIForm form, Context viewContext) {
        UIView view = new UIView("v1");
        view.addChild(form);
        FormController fc = new FormController("c", "");
        fc.setEditView(view);
        fc.setViewContext(viewContext);
        return fc;
    }

    /***************
     * Data preparation recipes as Object methods
     **********************/


    public static class CreateOneFieldForm {
        public Context ctx;
        public UIForm form;
        public UIField field;
        public ExecEnvironment env;
        public FormController fc;
        public View view;
        public CompositeContext gCtx;
        public EditableRepository repo;

        public CreateOneFieldForm invoke(android.content.Context ctx) {
            // disable triggers by default
            return invoke(ctx, true);
        }

        public CreateOneFieldForm invoke(android.content.Context ctx, boolean disableTriggers) {
            this.ctx = ctx;
            MainController mainController = MainController.getInstance();

            // configure the context as the MainController would do
            gCtx = mainController.getGlobalContext();
            env = mainController.getExecEnvironment();
            if (disableTriggers) {
                // disable user action handlers during the tests
                env.setChangeInterceptor(null);
                env.setSaveFormInterceptor(null);
            }

            // create a one-field form
            form = formBuilder.withNumFields(0).withRandomData().build();
            field = fBuilder.withRandomData().withFieldType(UIField.TYPE.TEXT).build();
            // setup field-form relation
            field.setParent(form);
            form.setChildren(new UIComponent[]{field});
            fc = DevFormBuilder.createFormController(form, ctx);

            mainController.setFormController(fc);

            return this;
        }

        public CreateOneFieldForm withForm(UIForm uiForm) {
            checkInvokeHasBeenCalled();
            this.form = uiForm;
            this.field = form.getFields().get(0); // link the first field
            this.field.setParentForm(this.form); // make sure field and form are linked
            fc = DevFormBuilder.createFormController(form, ctx);

            MainController mainController = MainController.getInstance();
            mainController.setFormController(fc);
            return this;
        }

        public CreateOneFieldForm withField(UIField field) {
            checkInvokeHasBeenCalled();
            // setup field-form relation
            field.setParent(form);
            form.setChildren(new UIComponent[]{field});
            this.field = field;
            return this;
        }

        /**
         * Removes the "params" context from global context and sets a new one with the given values.
         *
         * @param param
         * @param value
         * @return
         */
        public CreateOneFieldForm withParam(String param, Object value) {
            gCtx.removeContext("params");
            BasicContext bc = new BasicContext("params");
            bc.put(param, value);
            gCtx.addContext(bc);
            return this;
        }

        public CreateOneFieldForm withRepo(EditableRepository repository) {
            checkInvokeHasBeenCalled();
            this.repo = repository;
            this.form.setRepo(repo);
            return this;
        }

        public CreateOneFieldForm load() {
            checkInvokeHasBeenCalled();
            if (repo == null) {
                throw new IllegalStateException("Call withRepo(repo) before you call the load method.");
            }
            // load entity using form controller
            fc.load(gCtx);
            return this;
        }

        public CreateOneFieldForm render() {
            checkInvokeHasBeenCalled();
            // render the form to setup the viewContext
            ViewRenderHelper renderer = new ViewRenderHelper();
            view = renderer.render(ctx, env, form);
            return this;
        }

        private void checkInvokeHasBeenCalled() {
            if (ctx == null) {
                throw new IllegalStateException("Call invoke() and then withxxx to setup the " +
                        "objects you need in the test first!!");
            }
        }


    }
}
