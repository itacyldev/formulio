package es.jcyl.ita.frmdrd.context;
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

import androidx.test.platform.app.InstrumentationRegistry;

import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.RobolectricTestRunner;

import es.jcyl.ita.crtrepo.builders.EntityDataBuilder;
import es.jcyl.ita.crtrepo.builders.EntityMetaDataBuilder;
import es.jcyl.ita.crtrepo.context.CompositeContext;
import es.jcyl.ita.frmdrd.actions.ActionController;
import es.jcyl.ita.frmdrd.builders.FieldBuilder;
import es.jcyl.ita.frmdrd.builders.FormBuilder;
import es.jcyl.ita.frmdrd.configuration.ConfigConverters;
import es.jcyl.ita.frmdrd.context.impl.EntityContext;
import es.jcyl.ita.frmdrd.context.impl.FormViewContext;
import es.jcyl.ita.frmdrd.forms.FormController;
import es.jcyl.ita.frmdrd.ui.components.UIComponent;
import es.jcyl.ita.frmdrd.ui.components.form.UIForm;
import es.jcyl.ita.frmdrd.ui.components.inputfield.UIField;
import es.jcyl.ita.frmdrd.utils.ContextUtils;
import es.jcyl.ita.frmdrd.utils.DevFormBuilder;
import es.jcyl.ita.frmdrd.view.ViewRenderHelper;
import es.jcyl.ita.frmdrd.view.render.ExecEnvironment;

/**
 * @author Gustavo Río (gustavo.rio@itacyl.es)
 * <p>
 * Test Context creation during rendering process
 */
@RunWith(RobolectricTestRunner.class)
public class EnvExecutionContextTest {

    FormBuilder formBuilder = new FormBuilder();
    EntityDataBuilder entityBuilder;
    EntityMetaDataBuilder metaBuilder = new EntityMetaDataBuilder();

    @BeforeClass
    public static void setUp() {
        ConfigConverters confConverter = new ConfigConverters();
        confConverter.init();
    }

    /**
     * Run rendering process on a view with two forms and check the global context contains all the
     * forms referenced by id, and each form context contains and entity and view context.
     */
    @Test
    public void testResultingCompositeContext() {
        Context ctx = InstrumentationRegistry.getInstrumentation().getContext();

        // create a one-field form
        UIForm f1  = DevFormBuilder.createOneFieldForm();
        UIForm f2  = DevFormBuilder.createOneFieldForm();
        // add the forms to the formController view
        FormController fc = DevFormBuilder.createFormController(ctx, f1, f2);

        // render the view and check de resulting context
        CompositeContext globalContext = ContextUtils.createGlobalContext();
        ExecEnvironment env = new ExecEnvironment(globalContext, new ActionController());

        // render the view
        ViewRenderHelper viewRenderer = new ViewRenderHelper();

        View view = viewRenderer.render(ctx, env, fc.getEditView());

        FormViewContext viewContext = env.getFormContext().getViewContext();
        viewContext.entrySet();

        Assert.assertNotNull(view);

        // lets check global context contains a "form1","form2" context


    }


}
