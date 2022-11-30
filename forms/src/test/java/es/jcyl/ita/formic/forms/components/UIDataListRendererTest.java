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
import android.view.View;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;

import androidx.test.platform.app.InstrumentationRegistry;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.internal.matchers.Any;
import org.robolectric.RobolectricTestRunner;

import java.util.List;

import es.jcyl.ita.formic.forms.R;
import es.jcyl.ita.formic.forms.actions.ActionController;
import es.jcyl.ita.formic.forms.builders.FieldDataBuilder;
import es.jcyl.ita.formic.forms.components.datalist.DatalistWidget;
import es.jcyl.ita.formic.forms.components.datalist.UIDatalist;
import es.jcyl.ita.formic.forms.components.datalist.UIDatalistItem;
import es.jcyl.ita.formic.forms.components.table.UIRow;
import es.jcyl.ita.formic.forms.components.table.UITable;
import es.jcyl.ita.formic.forms.config.ConfigConverters;
import es.jcyl.ita.formic.forms.utils.ContextTestUtils;
import es.jcyl.ita.formic.forms.view.render.renderer.RenderingEnv;
import es.jcyl.ita.formic.forms.view.render.renderer.RenderingEnvFactory;
import es.jcyl.ita.formic.forms.view.render.renderer.ViewRenderer;
import es.jcyl.ita.formic.forms.view.widget.Widget;
import es.jcyl.ita.formic.repo.EditableRepository;
import es.jcyl.ita.formic.repo.Entity;
import es.jcyl.ita.formic.repo.builders.DevDbBuilder;
import es.jcyl.ita.formic.repo.db.SQLQueryFilter;

import static org.mockito.Mockito.*;

/**
 * @author Gustavo Río (gustavo.rio@itacyl.es)
 * <p>
 */
@RunWith(RobolectricTestRunner.class)
public class UIDataListRendererTest {

    Context ctx;

    ViewRenderer renderHelper = new ViewRenderer();

    @Before
    public void setup() {
        ctx = InstrumentationRegistry.getInstrumentation().getContext();
        ctx.setTheme(R.style.FormudruidLight);

        ConfigConverters confConverter = new ConfigConverters();
        confConverter.init();
    }

    @Test
    public void testEmptyDataList() {
        ActionController mcAC = mock(ActionController.class);
        RenderingEnv env = RenderingEnvFactory.getInstance().create(mcAC);
        env.setGlobalContext(ContextTestUtils.createGlobalContext());
        env.setAndroidContext(ctx);

        // create an entity with 10 properties and gather then into a 2 rows x 5 columns
        UIDatalist datalist = new UIDatalist();
        datalist.addChild(new UIDatalistItem());

        EditableRepository repo = mock(EditableRepository.class);
        when(repo.getFilterClass()).thenReturn(SQLQueryFilter.class);
        datalist.setRepo(repo);
        DatalistWidget widget = (DatalistWidget) renderHelper.render(env, datalist);

        Assert.assertNotNull(widget);
        Assert.assertNotNull(widget.getController());
        int childCount = widget.getItems().size();
        Assert.assertEquals(0, childCount);
    }

    @Test
    public void testNotEmptyDataList() {
        ActionController mcAC = mock(ActionController.class);
        RenderingEnv env = RenderingEnvFactory.getInstance().create(mcAC);
        env.setGlobalContext(ContextTestUtils.createGlobalContext());
        env.setAndroidContext(ctx);

        // create an entity with 10 properties and gather then into a 2 rows x 5 columns
        UIDatalist datalist = new UIDatalist();
        // add template datalist
        datalist.addChild(new UIDatalistItem());
        EditableRepository repo = mock(EditableRepository.class);
        when(repo.getFilterClass()).thenReturn(SQLQueryFilter.class);
        int numEntities = 10;
        List<Entity> entities = DevDbBuilder.buildEntitiesRandomMeta(numEntities);
        when(repo.find(any())).thenReturn(entities);
        datalist.setRepo(repo);
        DatalistWidget widget = (DatalistWidget) renderHelper.render(env, datalist);

        // check children
        int childCount = widget.getItems().size();
        Assert.assertNotNull(widget.getController());
        Assert.assertEquals(numEntities, childCount);
    }

}
