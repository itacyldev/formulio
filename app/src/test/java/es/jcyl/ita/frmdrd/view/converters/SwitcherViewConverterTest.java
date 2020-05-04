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

import android.app.Activity;
import android.content.Context;
import android.view.View;
import android.widget.Switch;

import androidx.test.platform.app.InstrumentationRegistry;

import org.junit.Assert;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mini2Dx.beanutils.ConversionException;
import org.robolectric.Robolectric;
import org.robolectric.RobolectricTestRunner;

import java.util.Date;

import es.jcyl.ita.crtrepo.test.utils.AssertUtils;
import es.jcyl.ita.crtrepo.test.utils.RandomUtils;
import es.jcyl.ita.crtrepo.types.ByteArray;
import es.jcyl.ita.crtrepo.types.Geometry;
import es.jcyl.ita.frmdrd.MainActivity;
import es.jcyl.ita.frmdrd.builders.FieldDataBuilder;
import es.jcyl.ita.frmdrd.builders.FormDataBuilder;
import es.jcyl.ita.frmdrd.configuration.ConfigConverters;
import es.jcyl.ita.frmdrd.ui.components.inputfield.UIField;
import es.jcyl.ita.frmdrd.utils.DevFormBuilder;
import es.jcyl.ita.frmdrd.view.InputFieldView;
import es.jcyl.ita.frmdrd.view.ViewHelper;
import es.jcyl.ita.frmdrd.view.render.RenderingEnv;

/**
 * @author Gustavo Río (gustavo.rio@itacyl.es)
 * <p>
 */
@RunWith(RobolectricTestRunner.class)
public class SwitcherViewConverterTest {

    FieldDataBuilder fBuilder = new FieldDataBuilder();
    FormDataBuilder formBuilder = new FormDataBuilder();

    Context ctx;

    @Before
    public void setUp() {
        ctx = Robolectric.buildActivity(MainActivity.class).create().get();

        ConfigConverters confConverter = new ConfigConverters();
        confConverter.init();
    }

    /**
     * Set and entity Id in the param context and execute view render and check if the
     * android view components have the entity values.
     */
    @Test
    public void testSupportedStrings() {
        UIField field = fBuilder.withRandomData().withFieldType(UIField.TYPE.BOOLEAN)
                .withValueBindingExpression("true", Boolean.class) // literal expression
                .build();
        // prepare data/state
        DevFormBuilder.CreateOneFieldForm recipe = new DevFormBuilder.CreateOneFieldForm()
                .invoke(ctx, true)
                .withField(field)
                .render();


        RenderingEnv env = recipe.env;
        // Test all different kinds of types
        Object[] values = {"1", "0", "true", "false", "Y", "y", "N", "n", "S", "s", "T",
                "F", "True", "FALSE", "SI", "No"};
        Object[] expected = {true, false, true, false, true, true, false, false, true, true, true,
                false, true, false, true, false};

        SwitcherFieldViewConverter conv = new SwitcherFieldViewConverter();
        for (int i = 0; i < values.length; i++) {
            InputFieldView baseView = ViewHelper.findInputFieldViewById(env.getViewRoot(), field);
            Switch inputView = (Switch) baseView.getInputView();

            // transform the value using the converter and check the result against the original value
            conv.setViewValue(inputView, values[i]);
            Object actual = conv.getValueFromView(inputView, expected.getClass());
            AssertUtils.assertEquals(expected[i], actual);
        }
    }

    @Test
    public void testSupportedNumeric() {
        UIField field = fBuilder.withRandomData().withFieldType(UIField.TYPE.BOOLEAN)
                .withValueBindingExpression("true", Boolean.class) // literal expression
                .build();
        // prepare data/state
        DevFormBuilder.CreateOneFieldForm recipe = new DevFormBuilder.CreateOneFieldForm()
                .invoke(ctx, true)
                .withField(field)
                .render();

        // Test all different kinds of types
        RenderingEnv env = recipe.env;
        Object[] values = {1, 1.1, 2, -1.1, 0, 0.5, -0.5};
        Object[] expected = {true, true, true, false, false, true, false};

        SwitcherFieldViewConverter conv = new SwitcherFieldViewConverter();
        for (int i = 0; i < values.length; i++) {
            InputFieldView baseView = ViewHelper.findInputFieldViewById(env.getViewRoot(), field);
            Switch inputView = (Switch) baseView.getInputView();

            // transform the value using the converter and check the result against the original value
            conv.setViewValue(inputView, values[i]);
            Object actual = conv.getValueFromView(inputView, expected.getClass());
            AssertUtils.assertEquals(expected[i], actual);
        }
    }
//
//    @Test
//    public void testUnSupportedStrings() {
//        Context ctx = InstrumentationRegistry.getInstrumentation().getContext();
//        UIField field = fBuilder.withRandomData().withFieldType(UIField.TYPE.BOOLEAN)
//                .withValueBindingExpression("true", Boolean.class) // literal expression
//                .build();
//        // prepare data/state
//        DevFormBuilder.CreateOneFieldForm recipe = new DevFormBuilder.CreateOneFieldForm()
//                .invoke(ctx, true)
//                .withField(field)
//                .render();
//
//        String value = RandomStringUtils.randomAlphanumeric(10);
//
//        SwitcherFieldViewConverter conv = new SwitcherFieldViewConverter();
//
//        Switch inputView = (Switch) recipe.view.findViewWithTag(field.getViewId());
//
//        boolean hasFailed = false;
//        try {
//            conv.setViewValue(inputView, field, value);
//        } catch (ConversionException e) {
//            hasFailed = true;
//        }
//        Assert.assertTrue("The converter should've faild during the conversion", hasFailed);
//    }
//
    @Test
    public void testUnsupportedTypes() {
        UIField field = fBuilder.withRandomData().withFieldType(UIField.TYPE.BOOLEAN)
                .withValueBindingExpression("true", Boolean.class) // literal expression
                .build();
        // prepare data/state
        DevFormBuilder.CreateOneFieldForm recipe = new DevFormBuilder.CreateOneFieldForm()
                .invoke(ctx, true)
                .withField(field)
                .render();

        // Test all different kinds of types
        RenderingEnv env = recipe.env;
        Class[] clazzez = new Class[]{ByteArray.class, Date.class, Geometry.class};

        SwitcherFieldViewConverter conv = new SwitcherFieldViewConverter();
        for (Class clazz : clazzez) {
            Object expected = RandomUtils.randomObject(clazz);
            InputFieldView baseView = ViewHelper.findInputFieldViewById(env.getViewRoot(), field);
            Switch inputView = (Switch) baseView.getInputView();
            boolean hasFailed = false;
            try {
                conv.setViewValue(inputView, expected);
            } catch (ConversionException e) {
                hasFailed = true;
            }
            Assert.assertTrue("The converter should've failed during the conversion of type "+clazz, hasFailed);
        }
    }

}
