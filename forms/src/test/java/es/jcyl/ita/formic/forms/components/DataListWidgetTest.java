package es.jcyl.ita.formic.forms.components;
/*
 * Copyright 2020 Javier Ramos (javier.ramos@itacyl.es), ITACyL (http://www.itacyl.es).
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

import java.util.List;

import es.jcyl.ita.formic.forms.R;
import es.jcyl.ita.formic.forms.actions.ActionController;
import es.jcyl.ita.formic.forms.builders.FieldDataBuilder;
import es.jcyl.ita.formic.forms.components.datalist.DatalistItemWidget;
import es.jcyl.ita.formic.forms.components.datalist.DatalistWidget;
import es.jcyl.ita.formic.forms.components.datalist.UIDatalist;
import es.jcyl.ita.formic.forms.components.datalist.UIDatalistItem;
import es.jcyl.ita.formic.forms.components.inputfield.UIField;
import es.jcyl.ita.formic.forms.config.ConfigConverters;
import es.jcyl.ita.formic.forms.controllers.widget.WidgetController;
import es.jcyl.ita.formic.forms.utils.ContextTestUtils;
import es.jcyl.ita.formic.forms.view.render.renderer.RenderingEnv;
import es.jcyl.ita.formic.forms.view.render.renderer.RenderingEnvFactory;
import es.jcyl.ita.formic.forms.view.render.renderer.ViewRenderer;
import es.jcyl.ita.formic.forms.view.widget.WidgetContext;
import es.jcyl.ita.formic.repo.EditableRepository;
import es.jcyl.ita.formic.repo.Entity;
import es.jcyl.ita.formic.repo.builders.DevDbBuilder;
import es.jcyl.ita.formic.repo.db.SQLQueryFilter;
import es.jcyl.ita.formic.repo.meta.PropertyType;
import es.jcyl.ita.formic.repo.test.utils.RandomUtils;

import static org.mockito.Mockito.*;

/**
 * @author Gustavo Río (gustavo.rio@itacyl.es)
 * <p>
 */
@RunWith(RobolectricTestRunner.class)
public class DataListWidgetTest {

    Context ctx;
    ViewRenderer renderHelper = new ViewRenderer();
    FieldDataBuilder fieldBuilder = new FieldDataBuilder();

    @Before
    public void setup() {
        ctx = InstrumentationRegistry.getInstrumentation().getContext();
        ctx.setTheme(R.style.FormudruidLight);

        ConfigConverters confConverter = new ConfigConverters();
        confConverter.init();
    }

    @Test
    public void testControllerEntityAccess() {
        ActionController mcAC = mock(ActionController.class);
        RenderingEnv env = RenderingEnvFactory.getInstance().create(mcAC);
        env.setGlobalContext(ContextTestUtils.createGlobalContext());
        env.setAndroidContext(ctx);

        // create entities and repository
        EditableRepository repoMock = mock(EditableRepository.class);
        when(repoMock.getFilterClass()).thenReturn(SQLQueryFilter.class);
        int numEntities = 1;
        List<Entity> entities = DevDbBuilder.buildEntitiesRandomMeta(numEntities);

        // create an entity with 10 properties and gather then into a 2 rows x 5 columns
        UIDatalist datalist = new UIDatalist();
        // add datalist template item
        UIDatalistItem dlItem = new UIDatalistItem(); // template item
        PropertyType entityProperty = entities.get(0).getMetadata().getProperties()[0];
        UIField myField = fieldBuilder.withFieldType(UIField.TYPE.TEXT)
                .withId("myField")
                .withValueBindingExpression("${entity." + entityProperty.name + "}", entityProperty.getType())
                .build();
        dlItem.addChild(myField);
        datalist.addChild(dlItem);

        when(repoMock.find(any())).thenReturn(entities);
        datalist.setRepo(repoMock);
        DatalistWidget widget = (DatalistWidget) renderHelper.render(env, datalist);

        Assert.assertNotNull(widget.getController());
        WidgetController controller = widget.getController();
        Assert.assertEquals(repoMock, controller.getEditableRepo());

        // make sure the repoMock.save() method has been called for each entity
        controller.save();
        verify(repoMock, times(numEntities)).save(any());

        controller.delete();
        verify(repoMock, times(numEntities)).delete(any());

        /*
        Modify the view value through viewContext and check the entity is updated
         */
        DatalistItemWidget itemWidget = widget.getItems().get(0);
        WidgetContext widgetContext = itemWidget.getWidgetContext();
        Object value = RandomUtils.randomObject(entityProperty.type);
        widgetContext.put("view.myField", value);
        // call update from view method and check then entity has been modified (position = 0)
        controller.updateEntityFromView();
        Entity entity = entities.get(0);

        Assert.assertEquals(value, entity.get(entityProperty.name));

    }

}
