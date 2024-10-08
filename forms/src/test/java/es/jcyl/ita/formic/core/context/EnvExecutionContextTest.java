package es.jcyl.ita.formic.core.context;
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
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.RobolectricTestRunner;

import es.jcyl.ita.formic.forms.R;
import es.jcyl.ita.formic.forms.actions.ActionController;
import es.jcyl.ita.formic.forms.builders.FormDataBuilder;
import es.jcyl.ita.formic.forms.components.form.UIForm;
import es.jcyl.ita.formic.forms.config.ConfigConverters;
import es.jcyl.ita.formic.forms.controllers.FormEditController;
import es.jcyl.ita.formic.forms.controllers.ViewController;
import es.jcyl.ita.formic.forms.el.JexlFormUtils;
import es.jcyl.ita.formic.forms.utils.ContextTestUtils;
import es.jcyl.ita.formic.forms.utils.DevFormBuilder;
import es.jcyl.ita.formic.forms.view.render.renderer.RenderingEnv;
import es.jcyl.ita.formic.forms.view.render.renderer.RenderingEnvFactory;
import es.jcyl.ita.formic.forms.view.render.renderer.ViewRenderer;
import es.jcyl.ita.formic.repo.Entity;
import es.jcyl.ita.formic.repo.builders.DevDbBuilder;
import es.jcyl.ita.formic.repo.builders.EntityDataBuilder;
import es.jcyl.ita.formic.repo.meta.EntityMeta;

import static org.mockito.Mockito.*;

/**
 * @author Gustavo Río (gustavo.rio@itacyl.es)
 * <p>
 * Test Context creation during rendering process
 */
@RunWith(RobolectricTestRunner.class)
public class EnvExecutionContextTest {

    FormDataBuilder formBuilder = new FormDataBuilder();

    Context ctx;

    @Before
    public void setup() {
        ctx = InstrumentationRegistry.getInstrumentation().getContext();
        ctx.setTheme(R.style.FormudruidLight);

        ConfigConverters confConverter = new ConfigConverters();
        confConverter.init();
    }

    /**
     * Run rendering process on a view with two forms and check the global context contains all the
     * forms referenced by id, and each form context contains and entity and view context.
     */
    @Test
    public void testResultingCompositeContext() {
        // create random entity meta and use databuilder to populate entity data
        UIForm f1 = createForm();
        UIForm f2 = createForm();

        ViewController fc = DevFormBuilder.createFormEditController(f1, f2);

        // render the view and check de resulting context
        CompositeContext globalContext = ContextTestUtils.createGlobalContext();
        ActionController mcAC = mock(ActionController.class);
        RenderingEnvFactory.getInstance().setActionController(mcAC);
        RenderingEnv env = RenderingEnvFactory.getInstance().create();
        env.setGlobalContext(globalContext);
        env.setAndroidContext(ctx);

        // render the view
        ViewRenderer viewRenderer = new ViewRenderer();

        View view = viewRenderer.render(env, fc.getView());
        Assert.assertNotNull(env.getWidgetContext().getViewContext());
        Assert.assertNotNull(env.getWidgetContext().getEntityContext());

//        UIForm lastForm = (UIForm) env.getWidgetContext().getRoot();
//        lastForm.getContext().setView(view);

        // access the context of the last evaluated form using relative paths like view.field or entity.field
        String id1 = "entity." + f2.getChildren()[0].getId();
        String id2 = "view." + f2.getChildren()[0].getId();

        // check the values can be accessed using JEXL expressions
        Assert.assertNotNull(JexlFormUtils.eval(env.getWidgetContext(), id1));
        Assert.assertNotNull(JexlFormUtils.eval(env.getWidgetContext(), id2));

        // Access each form context using absolute paths
        id1 = f1.getId() + ".entity." + f1.getChildren()[0].getId();
        id2 = f2.getId() + ".view." + f2.getChildren()[0].getId();

        // check the values can be accessed using JEXL expressions
        Assert.assertNotNull(JexlFormUtils.eval(env.getWidgetContext(), id1));
        Assert.assertNotNull(JexlFormUtils.eval(env.getWidgetContext(), id2));

        // lets check global context contains a "form1","form2" context
        Assert.assertNotNull(JexlFormUtils.eval(globalContext, id1));
        Assert.assertNotNull(JexlFormUtils.eval(globalContext, id2));

    }


    private UIForm createForm() {
        EntityMeta meta1 = DevDbBuilder.buildRandomMeta();
        EntityDataBuilder entityBuilder1 = new EntityDataBuilder(meta1);
        Entity entity1 = entityBuilder1.withRandomData().build();
        UIForm f1 = formBuilder.withMeta(meta1).withRandomData().build();

        // set an entity to simulate the loading before rendering
        f1.setEntity(entity1);
        return f1;
    }
}
