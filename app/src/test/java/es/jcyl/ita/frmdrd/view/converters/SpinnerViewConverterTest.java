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
import android.widget.Spinner;

import androidx.test.platform.app.InstrumentationRegistry;

import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.RobolectricTestRunner;

import es.jcyl.ita.crtrepo.test.utils.AssertUtils;
import es.jcyl.ita.crtrepo.test.utils.RandomUtils;
import es.jcyl.ita.frmdrd.builders.FormDataBuilder;
import es.jcyl.ita.frmdrd.builders.SelectDataBuilder;
import es.jcyl.ita.frmdrd.configuration.ConfigConverters;
import es.jcyl.ita.frmdrd.ui.components.select.UISelect;
import es.jcyl.ita.frmdrd.utils.DevFormBuilder;
import es.jcyl.ita.frmdrd.view.InputFieldView;
import es.jcyl.ita.frmdrd.view.ViewHelper;
import es.jcyl.ita.frmdrd.view.render.RenderingEnv;

/**
 * @author Gustavo Río (gustavo.rio@itacyl.es)
 * <p>
 */
@RunWith(RobolectricTestRunner.class)
public class SpinnerViewConverterTest {

    FormDataBuilder formBuilder = new FormDataBuilder();
    SelectDataBuilder selectBuilder = new SelectDataBuilder();

    @BeforeClass
    public static void setUp() {
        ConfigConverters confConverter = new ConfigConverters();
        confConverter.init();
    }

    /**
     * Generates a select with random String values, and sets/gets the values
     * using a SpinnerValueConverter
     */
    @Test
    public void testStringValues() {
        Context ctx = InstrumentationRegistry.getInstrumentation().getContext();

        String[] values = RandomUtils.randomObjectArray(10, String.class);
        UISelect field = selectBuilder.withRandomData().withOptionValues(values, values).build();
        // create view, form and render
        DevFormBuilder.CreateOneFieldForm recipe = new DevFormBuilder.CreateOneFieldForm()
                .invoke(ctx, true)
                .withField(field)
                .render();

        RenderingEnv env = recipe.env;
        // Test all different kinds of types

        SpinnerValueConverter conv = new SpinnerValueConverter();
        for (int i = 0; i < values.length; i++) {
            InputFieldView<Spinner> baseView = ViewHelper.findInputFieldViewById(env.getViewRoot(), field);
            Spinner inputView = baseView.getInputView();

            // transform the value using the converter and check the result against the original value
            conv.setViewValue(inputView, values[i]);
            String actual = conv.getValueFromViewAsString(inputView);

            AssertUtils.assertEquals(values[i], actual);
        }
    }


}
