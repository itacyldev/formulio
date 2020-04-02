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
import android.widget.Switch;

import androidx.test.platform.app.InstrumentationRegistry;

import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.RobolectricTestRunner;

import es.jcyl.ita.crtrepo.test.utils.AssertUtils;
import es.jcyl.ita.frmdrd.builders.FieldBuilder;
import es.jcyl.ita.frmdrd.builders.FormBuilder;
import es.jcyl.ita.frmdrd.configuration.ConfigConverters;
import es.jcyl.ita.frmdrd.ui.components.UIField;
import es.jcyl.ita.frmdrd.utils.DevFormBuilder;
import es.jcyl.ita.frmdrd.view.InputFieldView;
import es.jcyl.ita.frmdrd.view.ViewHelper;

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
                .invoke(ctx, true)
                .withField(field)
                .render();

        // Test all different kinds of types
        Object[] values = {"1", "0", "true", "false", "Y", "y", "N", "n", "S", "s", "T",
                "F", "True", "FALSE", "SI", "No"};
        Object[] expected = {true, false, true, false, true, true, false, false, true, true, true,
                false, true, false, true, false};

        SwitcherFieldViewConverter conv = new SwitcherFieldViewConverter();
        for (int i = 0; i < values.length; i++) {
            InputFieldView baseView = ViewHelper.findInputFieldViewById(recipe.mc.getViewRoot(), field);
            Switch inputView = (Switch) baseView.getInputView();

            // transform the value using the converter and check the result against the original value
            conv.setViewValue(inputView, values[i]);
            Object actual = conv.getValueFromView(inputView, expected.getClass());
            AssertUtils.assertEquals(expected[i], actual);
        }
    }
//
//    @Test
//    public void testSupportedNumeric() {
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
//        // Test all different kinds of types
//        Object[] values = {1, 1.1, 2, -1.1, 0, 0.5, -0.5};
//        Object[] expected = {true, true, true, false, false, true, false};
//
//        SwitcherFieldViewConverter conv = new SwitcherFieldViewConverter();
//        for (int i = 0; i < values.length; i++) {
//            InputFieldView baseView = ViewHelper.findInputFieldViewById(recipe.view, field);
//            Switch inputView = (Switch) baseView.getInputView();
//
//            // transform the value using the converter and check the result against the original value
//            conv.setViewValue(inputView, values[i]);
//            Object actual = conv.getValueFromView(inputView, expected.getClass());
//            AssertUtils.assertEquals(expected[i], actual);
//        }
//    }
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
//    @Test
//    public void testUnsupportedTypes() {
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
//        View view = recipe.view;
//
//        // Test all different kinds of types
//        Class[] clazzez = new Class[]{ByteArray.class, Date.class, Geometry.class};
//
//        SwitcherFieldViewConverter conv = new SwitcherFieldViewConverter();
//        for (Class clazz : clazzez) {
//            Object expected = RandomUtils.randomObject(clazz);
//            Switch inputView = (Switch) view.findViewWithTag(field.getViewId());
//            boolean hasFailed = false;
//            try {
//                conv.setViewValue(inputView, field, expected);
//            } catch (ConversionException e) {
//                hasFailed = true;
//            }
//            Assert.assertTrue("The converter should've faild during the conversion", hasFailed);
//        }
//    }

}
