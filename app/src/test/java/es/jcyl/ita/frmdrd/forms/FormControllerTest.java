package es.jcyl.ita.frmdrd.forms;
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

import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.robolectric.RobolectricTestRunner;

import java.util.HashMap;
import java.util.Map;

import es.jcyl.ita.crtrepo.EditableRepository;
import es.jcyl.ita.crtrepo.Entity;
import es.jcyl.ita.crtrepo.builders.DevDbBuilder;
import es.jcyl.ita.crtrepo.builders.EntityDataBuilder;
import es.jcyl.ita.crtrepo.builders.EntityMetaDataBuilder;
import es.jcyl.ita.crtrepo.context.CompositeContext;
import es.jcyl.ita.crtrepo.meta.EntityMeta;
import es.jcyl.ita.crtrepo.meta.PropertyType;
import es.jcyl.ita.crtrepo.test.utils.AssertUtils;
import es.jcyl.ita.crtrepo.test.utils.RandomUtils;
import es.jcyl.ita.frmdrd.builders.FormBuilder;
import es.jcyl.ita.frmdrd.configuration.ConfigConverters;
import es.jcyl.ita.frmdrd.context.impl.FormViewContext;
import es.jcyl.ita.frmdrd.render.ExecEnvironment;
import es.jcyl.ita.frmdrd.ui.components.form.UIForm;
import es.jcyl.ita.frmdrd.utils.ContextUtils;
import es.jcyl.ita.frmdrd.utils.DevFormBuilder;
import es.jcyl.ita.frmdrd.view.ViewRenderHelper;
import es.jcyl.ita.frmdrd.view.converters.ViewValueConverterFactory;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

/**
 * @author Gustavo Río (gustavo.rio@itacyl.es)
 * <p>
 */
@RunWith(RobolectricTestRunner.class)
public class FormControllerTest {

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
     * Load an entity with a FormController, modify it using view components and save.
     * Check the entity has been correctly modified.
     */
    @Test
    public void testSaveEntity() {
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

        // load entity using form controller
        FormController fc = DevFormBuilder.createFormController(form, ctx);
        fc.load(gCtx);

        // render view to create android view components and viewContext
        ViewRenderHelper renderHelper = new ViewRenderHelper();
        renderHelper.render(ctx, env, form);

        // save expected values to check later
        Map<String, Object> expectedValues = new HashMap<>();
        FormViewContext viewContext = (FormViewContext) form.getContext().getContext("view");
        // modify view using viewContext to mimic user interaction
        for (Map.Entry<String, Object> prop : entity.getProperties().entrySet()) {
            // get the related component by id and set a random value
            String propId = prop.getKey();
            PropertyType propertyType = entity.getMetadata().getPropertyByName(propId);
            Object expected = RandomUtils.randomObject(propertyType.getType());
            expectedValues.put(propId, expected);
            viewContext.put(propId, expected);
        }
        // execute save and check the repo has been hit with the new values
        fc.save();
        ArgumentCaptor<Entity> argument = ArgumentCaptor.forClass(Entity.class);
        verify(mockRepo).save(argument.capture());

        Entity actualEntity = argument.getValue();

        for (Map.Entry<String, Object> prop : actualEntity.getProperties().entrySet()) {
            // get the related component by id
            String propId = prop.getKey();
            Object expected = expectedValues.get(propId);
            AssertUtils.assertEquals(expected, prop.getValue());
        }
    }
}
