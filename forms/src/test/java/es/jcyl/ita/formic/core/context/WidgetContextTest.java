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

import androidx.test.platform.app.InstrumentationRegistry;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.RobolectricTestRunner;

import java.util.Date;
import java.util.Map;
import java.util.Set;

import es.jcyl.ita.formic.forms.R;
import es.jcyl.ita.formic.forms.actions.ActionController;
import es.jcyl.ita.formic.forms.builders.FormDataBuilder;
import es.jcyl.ita.formic.forms.components.form.UIForm;
import es.jcyl.ita.formic.forms.view.render.renderer.RenderingEnvFactory;
import es.jcyl.ita.formic.forms.view.widget.WidgetContextHolder;
import es.jcyl.ita.formic.forms.config.ConfigConverters;
import es.jcyl.ita.formic.forms.utils.ContextTestUtils;
import es.jcyl.ita.formic.forms.view.render.renderer.RenderingEnv;
import es.jcyl.ita.formic.forms.view.render.renderer.ViewRenderer;
import es.jcyl.ita.formic.forms.view.widget.WidgetContext;
import es.jcyl.ita.formic.repo.Entity;
import es.jcyl.ita.formic.repo.builders.DevDbBuilder;
import es.jcyl.ita.formic.repo.builders.EntityDataBuilder;
import es.jcyl.ita.formic.repo.builders.EntityMetaDataBuilder;
import es.jcyl.ita.formic.repo.meta.EntityMeta;
import es.jcyl.ita.formic.repo.meta.types.ByteArray;
import es.jcyl.ita.formic.repo.test.utils.AssertUtils;

import static org.mockito.Mockito.*;

/**
 * @author Gustavo Río (gustavo.rio@itacyl.es)
 * <p>
 * Test access to Entity and View through the FormContext.
 */
@RunWith(RobolectricTestRunner.class)
public class WidgetContextTest {

    FormDataBuilder formBuilder = new FormDataBuilder();
    EntityDataBuilder entityBuilder;
    EntityMetaDataBuilder metaBuilder = new EntityMetaDataBuilder();

    Context ctx;

    @Before
    public void setup() {
        ctx = InstrumentationRegistry.getInstrumentation().getContext();
        ctx.setTheme(R.style.FormudruidLight);

        ConfigConverters confConverter = new ConfigConverters();
        confConverter.init();
    }

    /**
     * Create FormContext from entity data and view and check access is correct.
     */
    @Test
    public void testEntityContext() {
        // create random entity meta and use databuilder to populate entity data
        EntityMeta meta = DevDbBuilder.buildRandomMeta();
        entityBuilder = new EntityDataBuilder(meta);
        Entity entity = entityBuilder.withRandomData().build();

        WidgetContext wContext = new WidgetContext(mock(WidgetContextHolder.class));

        wContext.setEntity(entity);

        // access entity elements throw context
        // check each entity property is correctly set in the form fields
        Map<String, Object> properties = entity.getProperties();
        Set<Map.Entry<String, Object>> entries = properties.entrySet();
        for (Map.Entry<String, Object> prop : entries) {
            // get the related component by id
            String propId = prop.getKey();
            Object expected = prop.getValue();
            Object actual = wContext.getValue("entity." + propId);
            Assert.assertEquals(expected, actual);
        }
    }

    /**
     * Create a UIForm randomly, load view and check each Android view element has the
     * same value as the referenced entity property.
     */
    @Test
    public void testViewContext() {
        // create random entity
        EntityMeta meta = DevDbBuilder.buildRandomMeta();
        meta = metaBuilder.addProperties(new Class[]{Double.class, Date.class, ByteArray.class, Boolean.class,
                String.class, Float.class, Integer.class, Long.class})
                .build();

        UIForm form = formBuilder.withMeta(meta).withRandomData().build();

        // create an entity using data builder
        entityBuilder = new EntityDataBuilder(meta);
        Entity entity = entityBuilder.withRandomData().build();
        form.setEntity(entity);

        RenderingEnvFactory.getInstance().setActionController(mock(ActionController.class));
        RenderingEnv env = RenderingEnvFactory.getInstance().create();
        env.setGlobalContext(ContextTestUtils.createGlobalContext());
        env.setAndroidContext(ctx);
        env.disableInterceptors();

        // render view to create android view components and viewContext
        ViewRenderer renderHelper = new ViewRenderer();
        renderHelper.render(env, form);
        WidgetContext wContext = env.getWidgetContext();

        // check each entity property is correctly set in the form fields
        Map<String, Object> properties = entity.getProperties();
        Set<Map.Entry<String, Object>> entries = properties.entrySet();
        for (Map.Entry<String, Object> prop : entries) {
            // get the related component by id
            String propId = prop.getKey();
            Object expected = prop.getValue();
            // get the view value using the FormViewContext
            System.out.println(String.format("Testing property [%s] of type [%s].", propId, expected.getClass()));
            Object actual = wContext.getValue("view." + propId);

            AssertUtils.assertEquals(String.format("Error trying to check property [%s] of type [%s].", propId, expected.getClass()),
                    expected, actual);
        }
    }


}
