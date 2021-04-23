package es.jcyl.ita.formic.core.context;
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

import org.apache.commons.lang3.RandomStringUtils;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.RobolectricTestRunner;

import es.jcyl.ita.formic.forms.MainController;
import es.jcyl.ita.formic.forms.R;
import es.jcyl.ita.formic.forms.components.UIComponent;
import es.jcyl.ita.formic.forms.components.UIInputComponent;
import es.jcyl.ita.formic.forms.components.form.UIForm;
import es.jcyl.ita.formic.forms.context.impl.ViewContext;
import es.jcyl.ita.formic.forms.builders.FormDataBuilder;
import es.jcyl.ita.formic.forms.utils.ContextTestUtils;
import es.jcyl.ita.formic.forms.utils.MockingUtils;
import es.jcyl.ita.formic.forms.view.converters.ViewValueConverter;
import es.jcyl.ita.formic.forms.view.render.RenderingEnv;
import es.jcyl.ita.formic.forms.view.render.ViewRenderer;
import es.jcyl.ita.formic.forms.view.widget.InputWidget;

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
        ctx.setTheme(R.style.FormudruidLight);
    }

    /**
     * Get ui android element value using FormViewContext
     */
    @Test
    public void testAccessViewValuesFromContext() {
        ViewRenderer renderHelper = new ViewRenderer();
        UIForm form = formBuilder.withNumFields(10).withRandomData().build();

        CompositeContext gCtx = ContextTestUtils.createGlobalContext();
        MainController mc = MockingUtils.mockMainController(ctx);
        RenderingEnv env = new RenderingEnv(mc.getActionController());
        env.setGlobalContext(gCtx);
        env.setViewContext(ctx);

        // render view to create android view components
        View formView = renderHelper.render(env, form);

        // create view context to access view elements
        ViewContext fvContext = new ViewContext(form, formView);

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
        ViewRenderer renderHelper = new ViewRenderer();
        UIForm form = formBuilder.withNumFields(10).withRandomData().build();

        CompositeContext gCtx = ContextTestUtils.createGlobalContext();
        MainController mc = MockingUtils.mockMainController(ctx);
        RenderingEnv env = new RenderingEnv(mc.getActionController());
        env.setGlobalContext(gCtx);
        env.setViewContext(ctx);

        // render view to create android view components
        View formView = renderHelper.render(env, form);

        // create view context to access view elements
        ViewContext fvContext = new ViewContext(form, formView);

        // check the context contains all the form elements
        for (UIInputComponent c : form.getFields()) {
            // modify view value using the context
            String expected = RandomStringUtils.randomAlphanumeric(10);
            fvContext.put(c.getId(), expected);

            // access the value from the view element
            InputWidget widget = fvContext.findInputFieldViewById(c);

            // use a viewConverter to get the value from android view element
            ViewValueConverter<TextView> converter = c.getConverter();
            Object actual = converter.getValueFromView((TextView) widget.getInputView());

            // do they match?
            Assert.assertEquals(expected, actual);
        }
    }
}
