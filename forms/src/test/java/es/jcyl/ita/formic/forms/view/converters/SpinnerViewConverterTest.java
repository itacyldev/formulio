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
import android.widget.Spinner;

import androidx.test.platform.app.InstrumentationRegistry;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mini2Dx.beanutils.ConvertUtils;
import org.robolectric.RobolectricTestRunner;

import java.util.ArrayList;
import java.util.List;

import es.jcyl.ita.formic.repo.Entity;
import es.jcyl.ita.formic.repo.builders.EntityDataBuilder;
import es.jcyl.ita.formic.repo.builders.EntityMetaDataBuilder;
import es.jcyl.ita.formic.repo.db.meta.DBPropertyType;
import es.jcyl.ita.formic.repo.meta.EntityMeta;
import es.jcyl.ita.formic.repo.test.utils.RandomUtils;
import es.jcyl.ita.formic.forms.R;
import es.jcyl.ita.formic.forms.builders.FormDataBuilder;
import es.jcyl.ita.formic.forms.builders.MultiOptionComponentBuilder;
import es.jcyl.ita.formic.forms.config.ConfigConverters;
import es.jcyl.ita.formic.forms.components.select.UISelect;
import es.jcyl.ita.formic.forms.utils.DevFormBuilder;
import es.jcyl.ita.formic.forms.view.widget.InputWidget;
import es.jcyl.ita.formic.forms.view.helpers.ViewHelper;
import es.jcyl.ita.formic.forms.view.render.RenderingEnv;
import es.jcyl.ita.formic.forms.view.render.ViewRenderHelper;

/**
 * @author Gustavo Río (gustavo.rio@itacyl.es)
 * <p>
 */
@RunWith(RobolectricTestRunner.class)
public class SpinnerViewConverterTest {

    FormDataBuilder formBuilder = new FormDataBuilder();
    MultiOptionComponentBuilder selectBuilder = new MultiOptionComponentBuilder(UISelect.class);
    EntityMetaDataBuilder metaBuilder = new EntityMetaDataBuilder();
    ViewRenderHelper renderHelper = new ViewRenderHelper();

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
     * using a SpinnerValueConverter
     */
    @Test
    public void testStringValues() {
        String[] values = RandomUtils.randomObjectArray(10, String.class);
        UISelect field = (UISelect) selectBuilder.withRandomData().withOptionValues(values, values).build();
        // create view, form and render
        DevFormBuilder.CreateOneFieldForm recipe = new DevFormBuilder.CreateOneFieldForm()
                .invoke(ctx, true)
                .withField(field)
                .render();

        RenderingEnv env = recipe.env;
        // Test all different kinds of types

        SpinnerValueConverter conv = new SpinnerValueConverter();
        for (int i = 0; i < values.length; i++) {
            InputWidget<UISelect, Spinner> widget =
                    ViewHelper.findInputFieldViewById(env.getViewRoot(),
                    field);
            Spinner inputView = widget.getInputView();

            // transform the value using the converter and check the result against the original value
            conv.setViewValue(inputView, values[i]);
            Object actual = conv.getValueFromView(inputView);

            Assert.assertEquals(values[i], actual);
        }
    }

    /**
     * Create random form with a select with some options, try to set Integer values and check
     * the conversion works as expected
     */
    @Test
    public void testIntegerValues() {
        String[] options = new String[]{"0", "1", "2", "3", "4"};
        UISelect field = (UISelect) selectBuilder.withRandomData().withOptionValues(options, options).build();
        // create view, form and render
        DevFormBuilder.CreateOneFieldForm recipe = new DevFormBuilder.CreateOneFieldForm()
                .invoke(ctx, true)
                .withField(field)
                .render();
        RenderingEnv env = recipe.env;
        env.disableInterceptors();

        Integer[] values = new Integer[]{null, 0, 1, 2, 4, 9};
        Integer[] expected = new Integer[]{null, 0, 1, 2, 4, null};
        SpinnerValueConverter conv = new SpinnerValueConverter();
        for (int i = 0; i < values.length; i++) {
            InputWidget<UISelect, Spinner> widget =
                    ViewHelper.findInputFieldViewById(env.getViewRoot(), field);
            Spinner inputView = widget.getInputView();

            // transform the value using the converter and check the result against the original value
            conv.setViewValue(inputView, values[i]);
            Object o = conv.getValueFromView(inputView);
            Integer actual = (Integer) ConvertUtils.convert(o, Integer.class);

            Assert.assertEquals(expected[i], actual);
        }
    }

    /**
     * Create different true/false option values and check the conversion from view is done properly
     * when trying to set/get value from a Boolean object.
     */
    @Test
    public void testBooleanValues() {
        List<String[]> stringPairs = new ArrayList<>();
        stringPairs.add(new String[]{"true", "false"});
        stringPairs.add(new String[]{"0", "1"});
        stringPairs.add(new String[]{"si", "no"});
        stringPairs.add(new String[]{"y", "n"});
        stringPairs.add(new String[]{"T", "F"});


//        String[] options = new String[]{"true", "false"};
        for (String[] options : stringPairs) {

            System.out.println(String.format("Checking with pair: %s-%s", options[0], options[1]));

            UISelect field = (UISelect) selectBuilder.withRandomData().withOptionValues(options, options).build();
            // create view, form and render
            DevFormBuilder.CreateOneFieldForm recipe = new DevFormBuilder.CreateOneFieldForm()
                    .invoke(ctx, true)
                    .withField(field)
                    .render();
            RenderingEnv env = recipe.env;
            env.disableInterceptors();

            Boolean[] values = new Boolean[]{null, true, false};
            Boolean[] expected = new Boolean[]{null, true, false};
            SpinnerValueConverter conv = new SpinnerValueConverter();
            for (int i = 0; i < values.length; i++) {
                InputWidget<UISelect,Spinner> widget =
                        ViewHelper.findInputFieldViewById(env.getViewRoot(), field);
                Spinner inputView = widget.getInputView();

                // transform the value using the converter and check the result against the original value
                conv.setViewValue(inputView, values[i]);
                Object o = conv.getValueFromView(inputView);
                Boolean actual = (Boolean) ConvertUtils.convert(o, Boolean.class);
                Assert.assertEquals(expected[i], actual);
            }
        }
    }

    /**
     * Create a select with random options and use an entity property to bind the selected value
     */
    @Test
    public void testBindValueFromEntity() {
        // create an entity
        String bindProperty = "myProperty";
        EntityMeta<DBPropertyType> meta = this.metaBuilder.withNumProps(1)
                .addProperty(bindProperty, String.class).build();
        EntityDataBuilder entityBuilder = new EntityDataBuilder(meta);
        Entity entity = entityBuilder.withRandomData().build();

        // get one of the values of the option and set to the entity
        // create view, form and render
        UISelect select = (UISelect) selectBuilder.withRandomData()
                .withNumOptions(5)
                .withValueBindingExpression("${entity.myProperty}")
                .build();
        String[] values = new String[]{null,
                select.getOptions()[2].getValue(),
                select.getOptions()[4].getValue(),

                "CCCCC" // not existing value in options, has to be return as null
        };
        String expected[] = {null, select.getOptions()[2].getValue(),
                select.getOptions()[4].getValue(), null};

        for (int i = 0; i < values.length; i++) {
            entity.set(bindProperty, values[i]);

            System.out.println("Checking with value: " + values[i]);

            DevFormBuilder.CreateOneFieldForm recipe = new DevFormBuilder.CreateOneFieldForm()
                    .invoke(ctx, true)
                    .withField(select)
                    .loadEntity(entity)
                    .render();

            RenderingEnv env = recipe.env;
            env.disableInterceptors();

            InputWidget<UISelect, Spinner> widget =
                    ViewHelper.findInputFieldViewById(env.getViewRoot(), select);
            Spinner inputView = widget.getInputView();
            SpinnerValueConverter conv = new SpinnerValueConverter();
            // transform the value using the converter and check the result against the original value

            Object actual = conv.getValueFromView(inputView);
            Assert.assertEquals(expected[i], actual);
        }
    }

}
