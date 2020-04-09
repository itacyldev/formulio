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

import es.jcyl.ita.crtrepo.EditableRepository;
import es.jcyl.ita.crtrepo.Entity;
import es.jcyl.ita.crtrepo.builders.EntityMetaDataBuilder;
import es.jcyl.ita.crtrepo.context.impl.BasicContext;
import es.jcyl.ita.frmdrd.MainController;
import es.jcyl.ita.frmdrd.builders.FieldDataBuilder;
import es.jcyl.ita.frmdrd.builders.FormDataBuilder;
import es.jcyl.ita.frmdrd.context.impl.FormContext;
import es.jcyl.ita.frmdrd.forms.FormController;
import es.jcyl.ita.frmdrd.forms.FormEditController;
import es.jcyl.ita.frmdrd.ui.components.UIComponent;
import es.jcyl.ita.frmdrd.ui.components.UIField;
import es.jcyl.ita.frmdrd.ui.components.form.UIForm;
import es.jcyl.ita.frmdrd.ui.components.view.UIView;
import es.jcyl.ita.frmdrd.view.render.RenderingEnv;

/**
 * @author Gustavo Río (gustavo.rio@itacyl.es)
 * <p>
 * Facade class with helper methods to automatically create data and fixtures for tests
 */
public class DevFormBuilder {

    EntityMetaDataBuilder metaBuilder = new EntityMetaDataBuilder();
    static FieldDataBuilder fBuilder = new FieldDataBuilder();
    static FormDataBuilder formBuilder = new FormDataBuilder();

    public static FormEditController createFormEditController(Context viewContext, UIForm mainForm, UIForm... forms) {
        UIView view = new UIView("v1");
        view.addChild(mainForm);
        if(forms != null){
            for (UIForm form : forms) {
                view.addChild(form);
            }
        }
        FormEditController fc = new FormEditController("c", "");
        fc.setView(view);
        fc.setViewContext(viewContext);
        fc.setMainForm(mainForm);
        return fc;
    }

    public static UIForm createOneFieldForm() {
        UIForm form = formBuilder.withNumFields(0).withRandomData().build();
        UIField field = fBuilder.withRandomData().withFieldType(UIField.TYPE.TEXT).build();
        // setup field-form relation
        field.setParent(form);
        form.setChildren(new UIComponent[]{field});
        return form;
    }

    public static FormContext createFormContextForEntity(UIForm form, Entity entity) {
        return null;
    }

    /***************
     * Data preparation recipes as Object methods
     **********************/

    public static class CreateOneFieldForm {
        public Context ctx;
        public UIForm form;
        public UIField field;
        public RenderingEnv env;
        public EditableRepository repo;
        public MainController mc;

        public CreateOneFieldForm invoke(android.content.Context ctx) {
            // disable triggers by default
            return invoke(ctx, true);
        }

        public CreateOneFieldForm invoke(android.content.Context ctx, boolean disableTriggers) {
            this.ctx = ctx;
            mc = MainController.getInstance();

            // configure the context as the MainController would do
            env = mc.getRenderingEnv();
            // disable user action handlers during the tests
            env.disableInterceptors();
            // create a one-field form
            form = createOneFieldForm();

            withForm(form);
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

        public CreateOneFieldForm withForm(UIForm uiForm) {
            checkInvokeHasBeenCalled();
            this.form = uiForm;
            this.field = form.getFields().get(0); // link the first field
            this.field.setParentForm(this.form); // make sure field and form are linked

            FormController fc = DevFormBuilder.createFormEditController(ctx, form);
            withFormController(fc);
            return this;
        }

        public CreateOneFieldForm withFormController(FormController formController) {
            checkInvokeHasBeenCalled();
            mc.setFormController(formController, formController.getView());
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
            mc.getGlobalContext().removeContext("params");
            BasicContext bc = new BasicContext("params");
            bc.put(param, value);
            mc.getGlobalContext().addContext(bc);
            return this;
        }

        public CreateOneFieldForm withRepo(EditableRepository repository) {
            checkInvokeHasBeenCalled();
            this.repo = repository;
            this.mc.getFormController().setMainRepo(repository);
            this.form.setRepo(repo);
            return this;
        }

        public CreateOneFieldForm load() {
            checkInvokeHasBeenCalled();
            if (repo == null) {
                throw new IllegalStateException("Call withRepo(repo) before you call the load method.");
            }
            // load entity using form controller
            mc.getFormController().load(mc.getGlobalContext());
            return this;
        }

        public CreateOneFieldForm render() {
            checkInvokeHasBeenCalled();
            // render the form to setup the viewContext
            mc.renderView(ctx);
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
