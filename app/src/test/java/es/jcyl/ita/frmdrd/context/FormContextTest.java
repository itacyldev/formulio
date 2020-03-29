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
import android.view.View;

import androidx.test.platform.app.InstrumentationRegistry;

import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;
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
import es.jcyl.ita.frmdrd.builders.FormBuilder;
import es.jcyl.ita.frmdrd.configuration.ConfigConverters;
import es.jcyl.ita.frmdrd.context.impl.FormContext;
import es.jcyl.ita.frmdrd.forms.FormController;
import es.jcyl.ita.frmdrd.render.ExecEnvironment;
import es.jcyl.ita.frmdrd.ui.components.form.UIForm;
import es.jcyl.ita.frmdrd.ui.components.view.UIView;
import es.jcyl.ita.frmdrd.utils.ContextUtils;
import es.jcyl.ita.frmdrd.view.ViewRenderHelper;
import es.jcyl.ita.frmdrd.view.converters.ViewValueConverterFactory;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

/**
 * @author Gustavo Río (gustavo.rio@itacyl.es)
 * <p>
 * Test access to Entity and View through the FormContext.
 */
@RunWith(RobolectricTestRunner.class)
public class FormContextTest {

    FormBuilder formBuilder = new FormBuilder();
    EntityDataBuilder entityBuilder;
    EntityMetaDataBuilder metaBuilder = new EntityMetaDataBuilder();
    ViewValueConverterFactory convFactory = ViewValueConverterFactory.getInstance();

    @BeforeClass
    public static void setUp() {
        ConfigConverters confConverter = new ConfigConverters();
        confConverter.init();
    }

    /**
     * Create FormContext from entity data and view and check access is correct.
     */
    @Test
    public void testEntityContext() {
        Context ctx = InstrumentationRegistry.getInstrumentation().getContext();

        // create random entity meta and use databuilder to populate entity data
        EntityMeta meta = DevDbBuilder.createRandomMeta();
        entityBuilder = new EntityDataBuilder(meta);
        Entity entity = entityBuilder.withRandomData().build();

        // configure the context as the MainController would do during navigation
        CompositeContext gCtx = ContextUtils.createGlobalContextWithParam("entityId", entity.getId());
        ExecEnvironment env = new ExecEnvironment(gCtx);

        // create form using entity meta to define UIFields
        UIForm form = formBuilder.withMeta(meta).withRandomData().build();
        // create a mock repository, set to form and load the entity
        EditableRepository mockRepo = mock(EditableRepository.class);
        when(mockRepo.findById(entity.getId())).thenReturn(entity);
        form.setRepo(mockRepo);

        // load entity and check the entity context is fulfill
        loadForm(form, gCtx);
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
        Context ctx = InstrumentationRegistry.getInstrumentation().getContext();

        // create random entity
        EntityMeta meta = DevDbBuilder.createRandomMeta();
        meta = metaBuilder.addProperties(new Class[]{Date.class, ByteArray.class, Boolean.class,
                String.class, Double.class, Float.class, Integer.class, Long.class})
                .build();

        UIForm form = formBuilder.withMeta(meta).withRandomData().build();

        // create an entity using data builder
        entityBuilder = new EntityDataBuilder(meta);
        Entity entity = entityBuilder.withRandomData().build();

        // configure the context as the MainController would do
        CompositeContext gCtx = ContextUtils.createGlobalContextWithParam("entityId", entity.getId());
        ExecEnvironment env = new ExecEnvironment(gCtx);

        // create a mock repository, set to form and load the entity
        EditableRepository mockRepo = mock(EditableRepository.class);
        when(mockRepo.findById(entity.getId())).thenReturn(entity);
        form.setRepo(mockRepo);
        // use FormController to load form
        loadForm(form, gCtx);
        FormContext fCtx = form.getContext();

        // render view to create android view components and viewContext
        ViewRenderHelper renderHelper = new ViewRenderHelper();
        renderHelper.render(ctx, env, form);

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


    private void loadForm(UIForm form, es.jcyl.ita.crtrepo.context.Context context){
        UIView view = new UIView("v1");
        view.addChild(form);
        FormController fc = new FormController("c","");
        fc.setEditView(view);
        fc.load(context);
    }
}
