package es.jcyl.ita.formic.forms.controllers;
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

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.robolectric.RobolectricTestRunner;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import androidx.test.platform.app.InstrumentationRegistry;
import es.jcyl.ita.formic.repo.EditableRepository;
import es.jcyl.ita.formic.repo.Entity;
import es.jcyl.ita.formic.repo.builders.DevDbBuilder;
import es.jcyl.ita.formic.repo.builders.EntityDataBuilder;
import es.jcyl.ita.formic.repo.builders.EntityMetaDataBuilder;
import es.jcyl.ita.formic.repo.meta.EntityMeta;
import es.jcyl.ita.formic.repo.meta.PropertyType;
import es.jcyl.ita.formic.repo.test.utils.AssertUtils;
import es.jcyl.ita.formic.repo.test.utils.RandomUtils;
import es.jcyl.ita.formic.forms.R;
import es.jcyl.ita.formic.forms.builders.FormDataBuilder;
import es.jcyl.ita.formic.forms.config.ConfigConverters;
import es.jcyl.ita.formic.forms.context.impl.FormViewContext;
import es.jcyl.ita.formic.forms.el.ValueBindingExpression;
import es.jcyl.ita.formic.forms.el.ValueExpressionFactory;
import es.jcyl.ita.formic.forms.repo.EntityRelation;
import es.jcyl.ita.formic.forms.components.form.UIForm;
import es.jcyl.ita.formic.forms.utils.DevFormBuilder;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

/**
 * @author Gustavo Río (gustavo.rio@itacyl.es)
 * <p>
 */
@RunWith(RobolectricTestRunner.class)
public class FormControllerTest {

    FormDataBuilder formBuilder = new FormDataBuilder();
    EntityDataBuilder entityBuilder;
    EntityMetaDataBuilder metaBuilder = new EntityMetaDataBuilder();

    Context ctx;

    @Before
    public void setUp() {
        ctx = InstrumentationRegistry.getInstrumentation().getContext();
        ctx.setTheme(R.style.FormudruidLight);

        ConfigConverters confConverter = new ConfigConverters();
        confConverter.init();
    }

    @Test
    public void testLoadEntity() {
        // create random meta for entity and create entity
        EntityMeta meta = DevDbBuilder.createRandomMeta();
        entityBuilder = new EntityDataBuilder(meta);
        Entity entity = entityBuilder.withRandomData().build();
        // create repository mock
        EditableRepository mockRepo = mock(EditableRepository.class);
        when(mockRepo.getMeta()).thenReturn(meta);
        when(mockRepo.findById(entity.getId())).thenReturn(entity);

        // prepare data/state
        DevFormBuilder.CreateOneFieldForm recipe = new DevFormBuilder.CreateOneFieldForm()
                .invoke(ctx)
                .withParam("entityId", entity.getId())
                .withRepo(mockRepo)
                .load();

        Assert.assertNotNull(recipe.form.getEntity());
        Assert.assertEquals(entity.getId(), recipe.form.getEntity().getId());
    }

    @Test
    public void testLoadEntityWithRelations() {
        String REL_PROPERTY = "image1";
        // create random meta for entity and create entity
        EntityMeta meta = DevDbBuilder.createRandomMeta();
        Entity entity = DevDbBuilder.buildEntity(meta);
        // create repository mock
        EditableRepository mockRepo = mock(EditableRepository.class);
        when(mockRepo.getMeta()).thenReturn(meta);
        when(mockRepo.findById(entity.getId())).thenReturn(entity);

        // define an entity relation with an Entity
        EditableRepository relRepo = mock(EditableRepository.class);
        EntityMeta relMeta = DevDbBuilder.createRandomMeta();
        when(relRepo.getMeta()).thenReturn(relMeta);
        Entity relEntity = DevDbBuilder.buildEntity(relMeta);
        when(relRepo.findById(relEntity.getId())).thenReturn(relEntity);

        // prepare data/state
        DevFormBuilder.CreateOneFieldForm recipe = new DevFormBuilder.CreateOneFieldForm()
                .invoke(ctx)
                .withParam("entityId", entity.getId())
                .withRepo(mockRepo);

        // set relation
        entity.set(REL_PROPERTY, relEntity.getId(), true);

        ValueBindingExpression bindingExpression = ValueExpressionFactory.getInstance().create("$" +
                "{entity." + REL_PROPERTY + "}");
        ArrayList<EntityRelation> lst = new ArrayList<>();
        lst.add(new EntityRelation(relRepo, "relation1", bindingExpression));
        recipe.form.setEntityRelations(lst);
        recipe.load();

        Assert.assertNotNull(recipe.form.getEntity());
        Assert.assertNotNull(recipe.form.getEntity().get("image1")); // property used to
        // get the related entity Id
        Assert.assertNotNull(recipe.form.getEntity().get("relation1")); // related entity
    }

    /**
     * Load an entity with a FormController, modify it using view components and save.
     * Check the entity has been correctly modified.
     */
    @Test
    public void testSaveEntity() {
        // create random entity meta and use databuilder to populate entity data
        EntityMeta meta = DevDbBuilder.createRandomMeta();
        entityBuilder = new EntityDataBuilder(meta);
        Entity entity = entityBuilder.withRandomData().build();

        // create form using entity meta to define UIFields
        UIForm form = formBuilder.withMeta(meta).withRandomData().build();
        // create a mock repository, set to form and load the entity
        EditableRepository mockRepo = mock(EditableRepository.class);
        when(mockRepo.findById(entity.getId())).thenReturn(entity);
        when(mockRepo.getMeta()).thenReturn(meta);

        // prepare data/state
        DevFormBuilder.CreateOneFieldForm recipe = new DevFormBuilder.CreateOneFieldForm()
                .invoke(ctx)
                .withForm(form)
                .withParam("entityId", entity.getId())
                .withRepo(mockRepo)
                .load()
                .render();

        // save expected values to check later
        Map<String, Object> expectedValues = new HashMap<>();
        FormViewContext viewContext = (FormViewContext) form.getContext().getContext("view");
        // modify view using viewContext to mimic user interaction
        Map<String, Object> properties = entity.getProperties();
        Set<Map.Entry<String, Object>> entries = properties.entrySet();
        for (Map.Entry<String, Object> prop : entries) {
            // get the related component by id and set a random value
            String propId = prop.getKey();
            PropertyType propertyType = entity.getMetadata().getPropertyByName(propId);
            Object expected = RandomUtils.randomObject(propertyType.getType());

            expectedValues.put(propId, expected);

            viewContext.put(propId, expected);
        }
        // execute save and check the repo has been hit with the new values
        ((FormEditController) recipe.mc.getFormController()).save(recipe.mc.getGlobalContext());

        ArgumentCaptor<Entity> argument = ArgumentCaptor.forClass(Entity.class);
        verify(mockRepo).save(argument.capture());

        Entity actualEntity = argument.getValue();

        properties = entity.getProperties();
        entries = properties.entrySet();
        for (Map.Entry<String, Object> prop : entries) {
            // get the related component by id
            String propId = prop.getKey();
            Object expected = expectedValues.get(propId);
            AssertUtils.assertEquals(expected, prop.getValue());
        }
    }
}
