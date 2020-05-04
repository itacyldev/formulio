package es.jcyl.ita.frmdrd.context;
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
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.Robolectric;
import org.robolectric.RobolectricTestRunner;

import java.util.Date;
import java.util.Map;

import es.jcyl.ita.crtrepo.EditableRepository;
import es.jcyl.ita.crtrepo.Entity;
import es.jcyl.ita.crtrepo.builders.DevDbBuilder;
import es.jcyl.ita.crtrepo.builders.EntityDataBuilder;
import es.jcyl.ita.crtrepo.builders.EntityMetaDataBuilder;
import es.jcyl.ita.crtrepo.context.CompositeContext;
import es.jcyl.ita.crtrepo.meta.EntityMeta;
import es.jcyl.ita.crtrepo.test.utils.AssertUtils;
import es.jcyl.ita.crtrepo.types.ByteArray;
import es.jcyl.ita.frmdrd.MainActivity;
import es.jcyl.ita.frmdrd.builders.FormDataBuilder;
import es.jcyl.ita.frmdrd.config.ConfigConverters;
import es.jcyl.ita.frmdrd.context.impl.FormContext;
import es.jcyl.ita.frmdrd.forms.FormEditController;
import es.jcyl.ita.frmdrd.ui.components.form.UIForm;
import es.jcyl.ita.frmdrd.utils.ContextTestUtils;
import es.jcyl.ita.frmdrd.utils.DevFormBuilder;
import es.jcyl.ita.frmdrd.view.render.RenderingEnv;
import es.jcyl.ita.frmdrd.view.render.ViewRenderHelper;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

/**
 * @author Gustavo Río (gustavo.rio@itacyl.es)
 * <p>
 * Test access to Entity and View through the FormContext.
 */
@RunWith(RobolectricTestRunner.class)
public class FormContextTest {

    FormDataBuilder formBuilder = new FormDataBuilder();
    EntityDataBuilder entityBuilder;
    EntityMetaDataBuilder metaBuilder = new EntityMetaDataBuilder();

    Context ctx;

    @Before
    public void setup(){
        ctx = Robolectric.buildActivity(MainActivity.class).create().get();

        ConfigConverters confConverter = new ConfigConverters();
        confConverter.init();
    }

    /**
     * Create FormContext from entity data and view and check access is correct.
     */
    @Test
    public void testEntityContext() {
        // create random entity meta and use databuilder to populate entity data
        EntityMeta meta = DevDbBuilder.createRandomMeta();
        entityBuilder = new EntityDataBuilder(meta);
        Entity entity = entityBuilder.withRandomData().build();

        // configure the context as the MainController would do during navigation
        CompositeContext gCtx = ContextTestUtils.createGlobalContextWithParam("entityId", entity.getId());
        RenderingEnv env = new RenderingEnv(gCtx, null);

        // create form using entity meta to define UIFields
        UIForm form = formBuilder.withMeta(meta).withRandomData().build();
        // create a mock repository, set to form and load the entity
        EditableRepository mockRepo = mock(EditableRepository.class);
        when(mockRepo.findById(entity.getId())).thenReturn(entity);
        when(mockRepo.getMeta()).thenReturn(meta);
        form.setRepo(mockRepo);

        // load entity and check the entity context is fulfill
        FormEditController fc = DevFormBuilder.createFormEditController(ctx, form);
        fc.load(gCtx);

        FormContext fCtx = form.getContext();
        Assert.assertNotNull(fCtx.getEntity());

        // access entity elements throw context
        // check each entity property is correctly set in the form fields
        for (Map.Entry<String, Object> prop : entity.getProperties().entrySet()) {
            // get the related component by id
            String propId = prop.getKey();
            Object expected = prop.getValue();
            Object actual = fCtx.getValue("entity." + propId);
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
        EntityMeta meta = DevDbBuilder.createRandomMeta();
        meta = metaBuilder.addProperties(new Class[]{Double.class, Date.class, ByteArray.class, Boolean.class,
                String.class, Float.class, Integer.class, Long.class})
                .build();

        UIForm form = formBuilder.withMeta(meta).withRandomData().build();

        // create an entity using data builder
        entityBuilder = new EntityDataBuilder(meta);
        Entity entity = entityBuilder.withRandomData().build();

        // configure the context as the MainController would do
        CompositeContext gCtx = ContextTestUtils.createGlobalContextWithParam("entityId", entity.getId());
        RenderingEnv env = new RenderingEnv(gCtx, null);
        env.setViewContext(ctx);

        // create a mock repository, set to form and load the entity
        EditableRepository mockRepo = mock(EditableRepository.class);
        when(mockRepo.findById(entity.getId())).thenReturn(entity);
        when(mockRepo.getMeta()).thenReturn(meta);
        form.setRepo(mockRepo);
        // use FormController to load form
        FormEditController fc = DevFormBuilder.createFormEditController(ctx, form);
        fc.load(gCtx);
        FormContext fCtx = form.getContext();

        // render view to create android view components and viewContext
        ViewRenderHelper renderHelper = new ViewRenderHelper();
        renderHelper.render(env, form);

        // check each entity property is correctly set in the form fields
        for (Map.Entry<String, Object> prop : entity.getProperties().entrySet()) {
            // get the related component by id
            String propId = prop.getKey();
            Object expected = prop.getValue();
            // get the view value using the FormViewContext
            System.out.println(String.format("Testing property [%s] of type [%s].", propId, expected.getClass()));
            Object actual = fCtx.getValue("view." + propId);

            AssertUtils.assertEquals(String.format("Error trying to check property [%s] of type [%s].", propId, expected.getClass()),
                    expected, actual);
        }
    }



}
