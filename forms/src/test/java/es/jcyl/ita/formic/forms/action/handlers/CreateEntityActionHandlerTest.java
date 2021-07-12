package es.jcyl.ita.formic.forms.action.handlers;
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
import org.mockito.ArgumentCaptor;
import org.robolectric.RobolectricTestRunner;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

import es.jcyl.ita.formic.forms.MainController;
import es.jcyl.ita.formic.forms.actions.ActionContext;
import es.jcyl.ita.formic.forms.actions.ActionType;
import es.jcyl.ita.formic.forms.actions.UserAction;
import es.jcyl.ita.formic.forms.actions.handlers.CreateEntityActionHandler;
import es.jcyl.ita.formic.forms.config.Config;
import es.jcyl.ita.formic.forms.utils.MockingUtils;
import es.jcyl.ita.formic.repo.EditableRepository;
import es.jcyl.ita.formic.repo.Entity;
import es.jcyl.ita.formic.repo.RepositoryFactory;
import es.jcyl.ita.formic.repo.builders.DevDbBuilder;
import es.jcyl.ita.formic.repo.builders.EntityDataBuilder;
import es.jcyl.ita.formic.repo.meta.EntityMeta;
import es.jcyl.ita.formic.repo.meta.PropertyType;
import es.jcyl.ita.formic.repo.test.utils.RandomUtils;

import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.*;

/**
 * @author Gustavo Río (gustavo.rio@itacyl.es)
 */
@RunWith(RobolectricTestRunner.class)
public class CreateEntityActionHandlerTest {
    static EntityDataBuilder entityBuilder;
    static RepositoryFactory repoFactory = RepositoryFactory.getInstance();
    static Context ctx;

    @Before
    public void setUp() {
        EntityMeta meta = DevDbBuilder.buildRandomMeta();
        entityBuilder = new EntityDataBuilder(meta);
        if (ctx != null) {
            ctx = InstrumentationRegistry.getInstrumentation().getContext();
        }
        Config.init(ctx, "");
    }


    @Test
    public void testCreateEntity() throws Exception {

        // mock main controller and prepare action controller
        MainController mc = MockingUtils.mockMainController(ctx);

        // prepare user Action
        UserAction userAction = new UserAction(ActionType.CREATE.name(), null, mc.getViewController());
        Map<String, Object> params = new HashMap<>();
        String entityType = "testRandomEntityType";
        params.put("repo", entityType);
        userAction.setParams(params);

        // create an entity using data builders
        Entity entity = entityBuilder.withRandomData().build();
        entity.clear();
        // set random data in userAction params.
        fillWithRandomValues(entity.getMetadata(), params);

        // mock repository and register for entity type, and register
        EditableRepository mockRepo = mock(EditableRepository.class);
        when(mockRepo.newEntity()).thenReturn(entity);
        try {
            repoFactory.register(entityType, mockRepo);

            // act - execute action
            CreateEntityActionHandler handler = new CreateEntityActionHandler(mc, mc.getRouter());
            handler.handle(new ActionContext(mc.getViewController(), ctx), userAction);

            ArgumentCaptor<Entity> argument = ArgumentCaptor.forClass(Entity.class);
            verify(mockRepo).save(argument.capture());
            Entity actualEntity = argument.getValue();

            // check actionEntity data
            Assert.assertNotNull(actualEntity);
            params.remove("repo");
            for (Map.Entry<String, Object> param : params.entrySet()) {
                // check parameters passed are in the entity, except for Id values that
                // should be set by the repo
                if (!actualEntity.getMetadata().isIdProperty(param.getKey())) {
                    assertTrue(actualEntity.get(param.getKey()).equals(param.getValue()));
                }
            }

        } finally {
            repoFactory.unregister(entityType);
        }

    }

    private void fillWithRandomValues(EntityMeta meta, Map<String, Object> params) {
        for (PropertyType type : meta.getProperties()) {
            Class clz = type.getType();
            Object value = RandomUtils.randomObject(clz);
            params.put(type.name, (Serializable) value);
        }
    }
}
