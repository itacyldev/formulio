package es.jcyl.ita.frmdrd.view.render;
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

import androidx.test.platform.app.InstrumentationRegistry;

import org.junit.Assert;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.Robolectric;
import org.robolectric.RobolectricTestRunner;

import es.jcyl.ita.crtrepo.Entity;
import es.jcyl.ita.crtrepo.builders.EntityDataBuilder;
import es.jcyl.ita.crtrepo.builders.EntityMetaDataBuilder;
import es.jcyl.ita.crtrepo.meta.EntityMeta;
import es.jcyl.ita.frmdrd.MainActivity;
import es.jcyl.ita.frmdrd.actions.ActionController;
import es.jcyl.ita.frmdrd.builders.FormDataBuilder;
import es.jcyl.ita.frmdrd.configuration.ConfigConverters;
import es.jcyl.ita.frmdrd.el.ValueExpressionFactory;
import es.jcyl.ita.frmdrd.ui.components.UIInputComponent;
import es.jcyl.ita.frmdrd.ui.components.form.UIForm;
import es.jcyl.ita.frmdrd.utils.ContextTestUtils;
import es.jcyl.ita.frmdrd.utils.DevFormBuilder;
import es.jcyl.ita.frmdrd.view.ViewHelper;

import static org.mockito.Mockito.mock;

/**
 * @author Gustavo Río (gustavo.rio@itacyl.es)
 * <p>
 */
@RunWith(RobolectricTestRunner.class)
public class ConditionalRenderingTest {

    FormDataBuilder formBuilder = new FormDataBuilder();
    EntityDataBuilder entityBuilder;
    EntityMetaDataBuilder metaBuilder = new EntityMetaDataBuilder();
    ValueExpressionFactory exprFactory = ValueExpressionFactory.getInstance();
    ViewRenderHelper renderHelper = new ViewRenderHelper();

    Context ctx;

    @Before
    public void setUp() {
        ctx = Robolectric.buildActivity(MainActivity.class).create().get();

        ConfigConverters confConverter = new ConfigConverters();
        confConverter.init();
    }

    /**
     * Creates a view tree and applies rendering conditions based on entity values and checks
     * the view is not visible
     */
    @Test
    public void testSimpleViewWithStringCondsRender() {
        // create one field form
        DevFormBuilder.CreateOneFieldForm recipe = new DevFormBuilder.CreateOneFieldForm().invoke(ctx);
        UIForm form = recipe.form;
        UIInputComponent field = recipe.field;

        // create meta with one field for pk and additional string to test different conditions
        EntityMeta meta = metaBuilder.withNumProps(1).addProperties(new Class[]{String.class}).build();
        entityBuilder = new EntityDataBuilder(meta);
        Entity entity = entityBuilder.withRandomData().build();
        // set entity in forms context
        form.getContext().setEntity(entity);

        // modify form field to set expression depending on entity value
        String strPropName = meta.getProperties()[1].name;
        String renderExpression = String.format("${entity.%s >= 'B'}", strPropName);
        String[] values = new String[]{"A", "B", "C", null, ""};
        Boolean[] expectedVisibility = new Boolean[]{false, true, true, false, false};
        // set field render expression
        field.setRenderExpression(exprFactory.create(renderExpression));
        ActionController mcAC = mock(ActionController.class);
        RenderingEnv env = new RenderingEnv(ContextTestUtils.createGlobalContext(), mcAC);
        env.setViewContext(ctx);
        // per each value, render the view to calculate expressions and check the field has the
        // expected visibility
        int i = 0;
        for (String value : values) {
            entity.set(strPropName, value);
            // render view
            View baseFormView = renderHelper.render(env, form);
            // find the field and check visibility
            View fieldView = ViewHelper.findInputFieldViewById(baseFormView, field);
            Assert.assertEquals("Unexpected visibility value for: " + value,
                    expectedVisibility[i], ViewHelper.isVisible(fieldView));
            i++;
        }
    }

    @Test
    public void testSimpleViewWithNumberCondsRender() {
        // create one field form
        DevFormBuilder.CreateOneFieldForm recipe = new DevFormBuilder.CreateOneFieldForm().invoke(ctx);
        UIForm form = recipe.form;
        UIInputComponent field = recipe.field;

        // create meta with one field for pk and additional Floats to test different conditions
        EntityMeta meta = metaBuilder.withNumProps(1).addProperties(new Class[]{Float.class}).build();
        entityBuilder = new EntityDataBuilder(meta);
        Entity entity = entityBuilder.withRandomData().build();
        // set entity in forms context
        form.getContext().setEntity(entity);

        // modify form field to set expression depending on entity value
        String longPropName = meta.getProperties()[1].name;
        String renderExpression = String.format("${entity.%s >= 25.3}", longPropName);
        Float[] values = new Float[]{-1f, 25f, 25.3f, 26f, null, 0f};

        Boolean[] expectedVisibility = new Boolean[]{false, false, true, true, false, false};
        // set field render expression
        field.setRenderExpression(exprFactory.create(renderExpression));

        ActionController mcAC = mock(ActionController.class);
        RenderingEnv env = new RenderingEnv(ContextTestUtils.createGlobalContext(), mcAC);
        env.setViewContext(ctx);

        // per each value, render the view to calculate expressions and check the field has the
        // expected visibility
        int i = 0;
        for (Float value : values) {
            entity.set(longPropName, value);
            // render view
            View baseFormView = renderHelper.render(env, form);
            // find the field and check visibility
            View fieldView = ViewHelper.findInputFieldViewById(baseFormView, field);
            Assert.assertEquals("Unexpected visibility value for: " + value,
                    expectedVisibility[i], ViewHelper.isVisible(fieldView));
            i++;
        }
    }

    @Test
    public void testMultipleCondsSimpleViewRender() {
        // create one field form
        DevFormBuilder.CreateOneFieldForm recipe = new DevFormBuilder.CreateOneFieldForm().invoke(ctx);
        UIForm form = recipe.form;
        UIInputComponent field = recipe.field;

        // create meta with one field for pk and additional Floats to test different conditions
        EntityMeta meta = metaBuilder.withNumProps(1).addProperties(new Class[]{Float.class, String.class}).build();
        entityBuilder = new EntityDataBuilder(meta);
        Entity entity = entityBuilder.withRandomData().build();
        // set entity in forms context
        form.getContext().setEntity(entity);

        // modify form field to set expression depending on entity value
        String longPropName = meta.getProperties()[1].name;
        String strPropName = meta.getProperties()[2].name;

        String renderExpression = String.format("${entity.%s >= 25.3 && entity.%s >= 'B'}", longPropName, strPropName);
        Float[] fValues = new Float[]{-1f, 25f, 25.3f, 26f, null, null};
        String[] strValues = new String[]{"A", "B", "C", null, "", "C"};
        Boolean[] expectedVisibility = new Boolean[]{false, false, true, false, false, false};
        // set field render expression
        field.setRenderExpression(exprFactory.create(renderExpression));

        ActionController mcAC = mock(ActionController.class);
        RenderingEnv env = new RenderingEnv(ContextTestUtils.createGlobalContext(), mcAC);
        env.setViewContext(ctx);

        // per each value, render the view to calculate expressions and check the field has the
        // expected visibility
        int i = 0;
        for (Float value : fValues) {
            entity.set(longPropName, value);
            entity.set(strPropName, strValues[i]);
            // render view
            View baseFormView = renderHelper.render(env, form);
            // find the field and check visibility
            View fieldView = ViewHelper.findInputFieldViewById(baseFormView, field);
            Assert.assertEquals("Unexpected visibility value for: " + value,
                    expectedVisibility[i], ViewHelper.isVisible(fieldView));
            i++;
        }
    }

    /**
     * Apply render condition on form and check if children are rendered just if the parent form has
     * a positive render value
     */
    @Test
    public void testRenderConditionOnForm() {
        // create one field form
        DevFormBuilder.CreateOneFieldForm recipe = new DevFormBuilder.CreateOneFieldForm().invoke(ctx);
        UIForm form = recipe.form;
        UIInputComponent field = recipe.field;

        // create meta with one field for pk and additional Floats to test different conditions
        EntityMeta meta = metaBuilder.withNumProps(1).addProperties(new Class[]{Float.class}).build();
        entityBuilder = new EntityDataBuilder(meta);
        Entity entity = entityBuilder.withRandomData().build();
        // set entity in forms context
        form.getContext().setEntity(entity);

        // set render condition to form
        // modify form field to set expression depending on entity value
        String longPropName = meta.getProperties()[1].name;
        String renderExpression = String.format("${entity.%s >= 25.3}", longPropName);
        Float[] values = new Float[]{-1f, 25f, 25.3f, 26f, null, 0f};

        Boolean[] expectedVisibility = new Boolean[]{false, false, true, true, false, false};
        // set field render expression
        form.setRenderExpression(exprFactory.create(renderExpression));

        ActionController mcAC = mock(ActionController.class);
        RenderingEnv env = new RenderingEnv(ContextTestUtils.createGlobalContext(), mcAC);
        env.setViewContext(ctx);

        // per each value, render the view to calculate expressions and check the field has the
        // expected visibility
        int i = 0;
        for (Float value : values) {
            entity.set(longPropName, value);
            // render view
            View baseFormView = renderHelper.render(env, form);
            boolean isFormVisible = ViewHelper.isVisible(baseFormView);
            // check form render visibility
            Assert.assertEquals("Unexpected visibility value for Form in value: " + value,
                    expectedVisibility[i], isFormVisible);
            if (!isFormVisible) {
                // check the children haven't been rendered
                View fieldView = ViewHelper.findInputFieldViewById(baseFormView, field);
                Assert.assertNull(fieldView);
            }
            i++;
        }
    }
}
