package es.jcyl.ita.frmdrd.view;
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
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.RobolectricTestRunner;

import es.jcyl.ita.crtrepo.builders.EntityDataBuilder;
import es.jcyl.ita.crtrepo.context.CompositeContext;
import es.jcyl.ita.frmdrd.builders.FormBuilder;
import es.jcyl.ita.frmdrd.context.impl.FormViewContext;
import es.jcyl.ita.frmdrd.render.ExecEnvironment;
import es.jcyl.ita.frmdrd.ui.components.UIComponent;
import es.jcyl.ita.frmdrd.ui.components.form.UIForm;
import es.jcyl.ita.frmdrd.utils.ContextUtils;
import es.jcyl.ita.frmdrd.view.converters.ViewValueConverterFactory;

/**
 * @author Gustavo Río (gustavo.rio@itacyl.es)
 * <p>
 * Test the render lifecycle
 */
@RunWith(RobolectricTestRunner.class)
public class ViewRenderHelperTest {

    FormBuilder formBuilder = new FormBuilder();
    ViewValueConverterFactory convFactory = ViewValueConverterFactory.getInstance();

    /**
     * Set and entity Id in the param context and execute view render and check if the
     * android view componects have the entity values
     */
    @Test
    public void testRenderEntityValues() {
        Context ctx = InstrumentationRegistry.getInstrumentation().getContext();

        ViewRenderHelper renderHelper = new ViewRenderHelper();
        UIForm form = formBuilder.withNumFields(10).withRandomData().build();

        CompositeContext gCtx = ContextUtils.createGlobalContext();
        ExecEnvironment env = new ExecEnvironment(gCtx);

        // render view to create android view components
        View formView = renderHelper.render(ctx, env, form);

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


}
