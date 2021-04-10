package es.jcyl.ita.formic.forms.view.converters;
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
import android.widget.RadioGroup;

import androidx.test.platform.app.InstrumentationRegistry;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.RobolectricTestRunner;

import es.jcyl.ita.formic.forms.R;
import es.jcyl.ita.formic.forms.components.radio.UIRadio;
import es.jcyl.ita.formic.forms.config.ConfigConverters;
import es.jcyl.ita.formic.forms.builders.FormDataBuilder;
import es.jcyl.ita.formic.forms.builders.MultiOptionComponentBuilder;
import es.jcyl.ita.formic.forms.utils.DevFormBuilder;
import es.jcyl.ita.formic.forms.view.helpers.ViewHelper;
import es.jcyl.ita.formic.forms.view.render.RenderingEnv;
import es.jcyl.ita.formic.forms.view.render.ViewRenderer;
import es.jcyl.ita.formic.forms.view.widget.InputWidget;
import es.jcyl.ita.formic.repo.builders.EntityMetaDataBuilder;
import es.jcyl.ita.formic.repo.test.utils.RandomUtils;

/**
 * @author Gustavo Río (gustavo.rio@itacyl.es)
 * <p>
 */
@RunWith(RobolectricTestRunner.class)
public class RadioViewConverterTest {

    FormDataBuilder formBuilder = new FormDataBuilder();
    MultiOptionComponentBuilder builder = new MultiOptionComponentBuilder(UIRadio.class);
    EntityMetaDataBuilder metaBuilder = new EntityMetaDataBuilder();
    ViewRenderer renderHelper = new ViewRenderer();

    Context ctx;

    @Before
    public void setUp() {
        ctx = InstrumentationRegistry.getInstrumentation().getContext();
        ctx.setTheme( R.style.FormudruidLight);

        ConfigConverters confConverter = new ConfigConverters();
        confConverter.init();
    }



    /**
     * Generates a select with random String values, and sets/gets the values
     * using a RadioValueConverter
     */
    @Test
    public void testStringValues() {
        String[] values = RandomUtils.randomObjectArray(10, String.class);
        // use select builder
        UIRadio field = (UIRadio) builder.withRandomData().withOptionValues(values, values).build();


        // create view, form and render
        DevFormBuilder.CreateOneFieldForm recipe = new DevFormBuilder.CreateOneFieldForm()
                .invoke(ctx, true)
                .withField(field)
                .render();

        RenderingEnv env = recipe.env;
        // Test all different kinds of types

        RadioValueConverter conv = new RadioValueConverter();
        for (int i = 0; i < values.length; i++) {
            InputWidget<UIRadio, RadioGroup> widget =
                    ViewHelper.findInputFieldViewById(env.getViewRoot(),
                    field);
            RadioGroup inputView = widget.getInputView();

            // transform the value using the converter and check the result against the original value
            conv.setViewValue(inputView, values[i]);
            Object actual = conv.getValueFromView(inputView);

            Assert.assertEquals(values[i], actual);
        }
    }

}
