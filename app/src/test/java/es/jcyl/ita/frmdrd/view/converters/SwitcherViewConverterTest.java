package es.jcyl.ita.frmdrd.view.converters;
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

import org.apache.commons.lang.RandomStringUtils;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mini2Dx.beanutils.ConversionException;
import org.robolectric.RobolectricTestRunner;

import java.util.Date;

import es.jcyl.ita.crtrepo.context.CompositeContext;
import es.jcyl.ita.crtrepo.test.utils.AssertUtils;
import es.jcyl.ita.crtrepo.test.utils.RandomUtils;
import es.jcyl.ita.crtrepo.types.ByteArray;
import es.jcyl.ita.crtrepo.types.Geometry;
import es.jcyl.ita.frmdrd.builders.FieldBuilder;
import es.jcyl.ita.frmdrd.builders.FormBuilder;
import es.jcyl.ita.frmdrd.configuration.ConfigConverters;
import es.jcyl.ita.frmdrd.render.ExecEnvironment;
import es.jcyl.ita.frmdrd.ui.components.form.UIForm;
import es.jcyl.ita.frmdrd.ui.components.inputfield.UIField;
import es.jcyl.ita.frmdrd.utils.ContextUtils;
import es.jcyl.ita.frmdrd.utils.DevFormBuilder;
import es.jcyl.ita.frmdrd.view.ViewRenderHelper;

/**
 * @author Gustavo Río (gustavo.rio@itacyl.es)
 * <p>
 */
@RunWith(RobolectricTestRunner.class)
public class SwitcherViewConverterTest {

    FieldBuilder fBuilder = new FieldBuilder();
    FormBuilder formBuilder = new FormBuilder();

    @BeforeClass
    public static void setUp() {
        ConfigConverters confConverter = new ConfigConverters();
        confConverter.init();
    }

    /**
     * Set and entity Id in the param context and execute view render and check if the
     * android view components have the entity values.
     */
    @Test
    public void testSupportedStrings() {
        Context ctx = InstrumentationRegistry.getInstrumentation().getContext();
        UIField field = fBuilder.withRandomData().withFieldType(UIField.TYPE.BOOLEAN)
                .withValueBindingExpression("true", Boolean.class) // literal expression
                .build();
        // prepare data/state
        DevFormBuilder.CreateOneFieldForm recipe = new DevFormBuilder.CreateOneFieldForm()
                .invoke(ctx,true)
                .withField(field)
                .render();

        // Test all different kinds of types
        Object[] values = {"1", "0", "true", "false", "Y", "y", "N", "n", "S", "s", "T",
                "F", "True",  "FALSE", "SI", "No"};
        Object[] expected = {true, false, true, false, true, true, false, false, true, true, true,
                false, true, false, true, false};

        SwitcherFieldViewConverter conv = new SwitcherFieldViewConverter();
        for (int i = 0; i < values.length; i++) {
            View inputView = recipe.view.findViewWithTag(field.getViewId());
            conv.setViewValue(inputView, field, values[i]);

            Object actual = conv.getValueFromView(inputView, field, expected.getClass());
            AssertUtils.assertEquals(expected[i], actual);
        }
    }

    @Test
    public void testSupportedNumeric() {
        Context ctx = InstrumentationRegistry.getInstrumentation().getContext();
        UIField field = fBuilder.withRandomData().withFieldType(UIField.TYPE.BOOLEAN)
                .withValueBindingExpression("true", Boolean.class) // literal expression
                .build();
        // prepare data/state
        DevFormBuilder.CreateOneFieldForm recipe = new DevFormBuilder.CreateOneFieldForm()
                .invoke(ctx,true)
                .withField(field)
                .render();

        // Test all different kinds of types
        Object[] values = {1, 1.1, 2, -1.1, 0, 0.5, -0.5};
        Object[] expected = {true, true, true, false, false, true, false};

        SwitcherFieldViewConverter conv = new SwitcherFieldViewConverter();
        for (int i = 0; i < values.length; i++) {
            View inputView = recipe.view.findViewWithTag(field.getViewId());
            conv.setViewValue(inputView, field, values[i]);

            Object actual = conv.getValueFromView(inputView, field, expected.getClass());
            Assert.assertEquals("Error checking value " + values[i], expected[i], actual);
        }
    }

    @Test
    public void testUnSupportedStrings() {
        Context ctx = InstrumentationRegistry.getInstrumentation().getContext();
        UIField field = fBuilder.withRandomData().withFieldType(UIField.TYPE.BOOLEAN)
                .withValueBindingExpression("true", Boolean.class) // literal expression
                .build();
        // prepare data/state
        DevFormBuilder.CreateOneFieldForm recipe = new DevFormBuilder.CreateOneFieldForm()
                .invoke(ctx,true)
                .withField(field)
                .render();

        String value = RandomStringUtils.randomAlphanumeric(10);

        SwitcherFieldViewConverter conv = new SwitcherFieldViewConverter();

        View inputView = recipe.view.findViewWithTag(field.getViewId());

        boolean hasFailed = false;
        try {
            conv.setViewValue(inputView, field, value);
        } catch (ConversionException e) {
            hasFailed = true;
        }
        Assert.assertTrue("The converter should've faild during the conversion", hasFailed);
    }

    @Test
    public void testUnsupportedTypes() {
        Context ctx = InstrumentationRegistry.getInstrumentation().getContext();
        UIField field = fBuilder.withRandomData().withFieldType(UIField.TYPE.BOOLEAN)
                .withValueBindingExpression("true", Boolean.class) // literal expression
                .build();
        // prepare data/state
        DevFormBuilder.CreateOneFieldForm recipe = new DevFormBuilder.CreateOneFieldForm()
                .invoke(ctx,true)
                .withField(field)
                .render();

        View view = recipe.view;

        // Test all different kinds of types
        Class[] clazzez = new Class[]{ByteArray.class, Date.class, Geometry.class};

        SwitcherFieldViewConverter conv = new SwitcherFieldViewConverter();
        for (Class clazz : clazzez) {
            Object expected = RandomUtils.randomObject(clazz);
            View inputView = view.findViewWithTag(field.getViewId());
            boolean hasFailed = false;
            try {
                conv.setViewValue(inputView, field, expected);
            } catch (ConversionException e) {
                hasFailed = true;
            }
            Assert.assertTrue("The converter should've faild during the conversion", hasFailed);
        }
    }

    private class PrepareContext {
        private UIField field;
        private View view;

        public UIField getField() {
            return field;
        }

        public View getView() {
            return view;
        }

        public PrepareContext invoke() {

            Context ctx = InstrumentationRegistry.getInstrumentation().getContext();
            UIForm form = formBuilder.withNumFields(0).withRandomData().build();
            field = fBuilder.withRandomData().withFieldType(UIField.TYPE.BOOLEAN)
                    .withValueBindingExpression("true", Boolean.class) // literal expression
                    .build();
            field.setParent(form);


            // configure the context as the MainController would do
            CompositeContext gCtx = ContextUtils.createGlobalContext();
            ExecEnvironment env = new ExecEnvironment(gCtx);

            // get testField renderer
            ViewRenderHelper renderer = new ViewRenderHelper();
            view = renderer.render(ctx, env, field);
            return this;
        }
    }
}
