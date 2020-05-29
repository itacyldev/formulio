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
import android.widget.TextView;

import androidx.test.platform.app.InstrumentationRegistry;

import org.apache.commons.lang.RandomStringUtils;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.RobolectricTestRunner;

import es.jcyl.ita.crtrepo.context.CompositeContext;
import es.jcyl.ita.frmdrd.R;
import es.jcyl.ita.frmdrd.builders.FormDataBuilder;
import es.jcyl.ita.frmdrd.context.impl.FormViewContext;
import es.jcyl.ita.frmdrd.ui.components.UIComponent;
import es.jcyl.ita.frmdrd.ui.components.UIInputComponent;
import es.jcyl.ita.frmdrd.ui.components.form.UIForm;
import es.jcyl.ita.frmdrd.utils.ContextTestUtils;
import es.jcyl.ita.frmdrd.view.InputFieldView;
import es.jcyl.ita.frmdrd.view.converters.ViewValueConverter;
import es.jcyl.ita.frmdrd.view.render.RenderingEnv;
import es.jcyl.ita.frmdrd.view.render.ViewRenderHelper;

/**
 * @author Gustavo Río (gustavo.rio@itacyl.es)
 * <p>
 * Test access (get/set) to Android view elements through FormViewContext to get user input and to
 * modify the view value.
 */
@RunWith(RobolectricTestRunner.class)
public class FormViewContextTest {

    FormDataBuilder formBuilder = new FormDataBuilder();

    Context ctx;

    @Before
    public void setup() {
        ctx = InstrumentationRegistry.getInstrumentation().getContext();
        ctx.setTheme(R.style.Theme_App_Light);
    }

    /**
     * Get ui android element value using FormViewContext
     */
    @Test
    public void testAccessViewValuesFromContext() {
        ViewRenderHelper renderHelper = new ViewRenderHelper();
        UIForm form = formBuilder.withNumFields(10).withRandomData().build();

        CompositeContext gCtx = ContextTestUtils.createGlobalContext();
        RenderingEnv env = new RenderingEnv(gCtx, null);
        env.setViewContext(ctx);

        // render view to create android view components
        View formView = renderHelper.render(env, form);

        // create view context to access view elements
        FormViewContext fvContext = new FormViewContext(form, formView);

        // check the context contains all the form elements
        for (UIComponent c : form.getChildren()) {
            // get value accessing to the View element value
            Object expected = c.getValue(gCtx);
            // get value directly from the component's expression
            Object actual = fvContext.get(c.getId());
            Assert.assertNotNull(actual);
            // do they match?
            Assert.assertEquals(expected, actual);
        }
    }

    /**
     * Modify an ui view value using the FormViewContext
     */
    @Test
    public void setSetViewValuesThroughContext() {
        ViewRenderHelper renderHelper = new ViewRenderHelper();
        UIForm form = formBuilder.withNumFields(10).withRandomData().build();

        CompositeContext gCtx = ContextTestUtils.createGlobalContext();
        RenderingEnv env = new RenderingEnv(gCtx, null);
        env.setViewContext(ctx);

        // render view to create android view components
        View formView = renderHelper.render(env, form);

        // create view context to access view elements
        FormViewContext fvContext = new FormViewContext(form, formView);

        // check the context contains all the form elements
        for (UIInputComponent c : form.getFields()) {
            // modify view value using the context
            String expected = RandomStringUtils.randomAlphanumeric(10);
            fvContext.put(c.getId(), expected);

            // access the value from the view element
            InputFieldView baseView = fvContext.findInputFieldViewById(c.getId());

            // use a viewConverter to get the value from android view element
            View v = baseView.getInputView();
            ViewValueConverter<TextView> converter = c.getConverter();
            String actual = converter.getValueFromView((TextView) v, String.class);

            // do they match?
            Assert.assertEquals(expected, actual);
        }
    }
}
