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
import android.view.View;
import android.widget.TextView;

import androidx.test.platform.app.InstrumentationRegistry;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mini2Dx.beanutils.ConvertUtils;
import org.robolectric.RobolectricTestRunner;

import java.util.Date;

import es.jcyl.ita.formic.forms.R;
import es.jcyl.ita.formic.forms.builders.FieldDataBuilder;
import es.jcyl.ita.formic.forms.builders.FormDataBuilder;
import es.jcyl.ita.formic.forms.config.ConfigConverters;
import es.jcyl.ita.formic.forms.utils.DevFormBuilder;
import es.jcyl.ita.formic.forms.view.helpers.ViewHelper;
import es.jcyl.ita.formic.forms.view.render.renderer.RenderingEnv;
import es.jcyl.ita.formic.forms.view.widget.InputWidget;
import es.jcyl.ita.formic.repo.meta.types.ByteArray;
import es.jcyl.ita.formic.repo.meta.types.Geometry;
import es.jcyl.ita.formic.repo.test.utils.AssertUtils;
import es.jcyl.ita.formic.repo.test.utils.RandomUtils;

/**
 * @author Gustavo Río (gustavo.rio@itacyl.es)
 * <p>
 */
@RunWith(RobolectricTestRunner.class)
public class TextFieldViewConverterTest {

    FieldDataBuilder fBuilder = new FieldDataBuilder();
    FormDataBuilder formBuilder = new FormDataBuilder();

    Context ctx;

    @Before
    public void setUp() {
        ctx = InstrumentationRegistry.getInstrumentation().getContext();
        ctx.setTheme(R.style.FormudruidLight);

        ConfigConverters confConverter = new ConfigConverters();
        confConverter.init();
    }

    /**
     * Set and entity Id in the param context and execute view render and check if the
     * android view components have the entity values.
     */
    @Test
    public void testRenderString() {
        DevFormBuilder.CreateOneFieldForm recipe = new DevFormBuilder.CreateOneFieldForm()
                .invoke(ctx, true).render();
        RenderingEnv env = recipe.env;
        View view = env.getRootWidget();

        // Test all different kinds of types
        Class[] clazzez = new Class[]{ByteArray.class, Date.class, String.class, Long.class, Integer.class,
                Float.class, Double.class, Boolean.class,
                Geometry.class};

        TextViewConverter conv = new TextViewConverter();
        for (Class clazz : clazzez) {
            Object expected = RandomUtils.randomObject(clazz);
            // retrieve the android view element and set value using the converter
            InputWidget widget = ViewHelper.findInputWidget(view, recipe.field);
            TextView inputView = (TextView) widget.getInputView();
            conv.setViewValue(inputView, expected);

            // get the view and check de value is set
            Object actual = conv.getValueFromView(inputView);
            actual = ConvertUtils.convert(actual, expected.getClass());
            AssertUtils.assertEquals(expected, actual);
        }

    }

    /**
     * Set a null value in the InputText (clear view component) and make sure the converter returs a null
     */
    @Test
    public void testGetNullValue() {
        DevFormBuilder.CreateOneFieldForm recipe = new DevFormBuilder.CreateOneFieldForm()
                .invoke(ctx, true).render();
        RenderingEnv env = recipe.env;
        View view = env.getRootWidget();

        // Test all different kinds of types
        Class[] clazzez = new Class[]{ByteArray.class, Date.class, String.class, Long.class, Integer.class,
                Float.class, Double.class, Boolean.class,
                Geometry.class};

        TextViewConverter conv = new TextViewConverter();
        for (Class clazz : clazzez) {
//            Object expected = RandomUtils.randomObject(clazz);
            // retrieve the android view element and set value using the converter
            InputWidget widget = ViewHelper.findInputWidget(view, recipe.field);
            TextView inputView = (TextView) widget.getInputView();
            conv.setViewValue(inputView, null);

            // get the view and check de value is set
            Object actual = conv.getValueFromView(inputView);
            actual = ConvertUtils.convert(actual, clazz);
            Assert.assertNull("Assert not fulfill for class: " + clazz.getName(), actual);
        }

    }
}
