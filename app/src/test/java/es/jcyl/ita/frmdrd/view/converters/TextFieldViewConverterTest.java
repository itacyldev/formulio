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

import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;
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
import es.jcyl.ita.frmdrd.ui.validation.RequiredValidator;
import es.jcyl.ita.frmdrd.utils.ContextUtils;
import es.jcyl.ita.frmdrd.utils.DevFormBuilder;
import es.jcyl.ita.frmdrd.view.ViewRenderHelper;

/**
 * @author Gustavo Río (gustavo.rio@itacyl.es)
 * <p>
 */
@RunWith(RobolectricTestRunner.class)
public class TextFieldViewConverterTest {

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
    public void testRenderString() {
        Context ctx = InstrumentationRegistry.getInstrumentation().getContext();

        DevFormBuilder.CreateOneFieldForm recipe = new DevFormBuilder.CreateOneFieldForm()
                .invoke(ctx, true).render();
        View view = recipe.view;

        // Test all different kinds of types
        Class[] clazzez = new Class[]{ByteArray.class, Date.class, String.class, Long.class, Integer.class,
                Float.class, Double.class, Boolean.class,
                Geometry.class};

        TextFieldViewConverter conv = new TextFieldViewConverter();
        for (Class clazz : clazzez) {
            Object expected = RandomUtils.randomObject(clazz);
            View inputView = view.findViewWithTag(recipe.field.getViewId());
            conv.setViewValue(inputView, recipe.field, expected);

            // get the view and check de value is set
            Object actual = conv.getValueFromView(inputView, recipe.field, expected.getClass());
            AssertUtils.assertEquals(expected, actual);
        }
    }
}
