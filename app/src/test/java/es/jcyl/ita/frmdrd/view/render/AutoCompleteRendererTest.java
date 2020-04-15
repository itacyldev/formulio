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

import android.content.Context;
import android.view.View;
import android.widget.Adapter;

import androidx.test.platform.app.InstrumentationRegistry;

import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.RobolectricTestRunner;

import java.util.List;

import es.jcyl.ita.crtrepo.EditableRepository;
import es.jcyl.ita.crtrepo.Entity;
import es.jcyl.ita.crtrepo.builders.DevDbBuilder;
import es.jcyl.ita.crtrepo.db.SQLQueryFilter;
import es.jcyl.ita.crtrepo.meta.EntityMeta;
import es.jcyl.ita.crtrepo.test.utils.RandomUtils;
import es.jcyl.ita.frmdrd.actions.ActionController;
import es.jcyl.ita.frmdrd.configuration.ConfigConverters;
import es.jcyl.ita.frmdrd.el.ValueExpressionFactory;
import es.jcyl.ita.frmdrd.ui.components.autocomplete.AutoCompleteView;
import es.jcyl.ita.frmdrd.ui.components.autocomplete.UIAutoComplete;
import es.jcyl.ita.frmdrd.ui.components.select.UIOption;
import es.jcyl.ita.frmdrd.utils.ContextTestUtils;
import es.jcyl.ita.frmdrd.view.InputFieldView;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

/**
 * @author Gustavo Río (gustavo.rio@itacyl.es)
 * <p>
 */
@RunWith(RobolectricTestRunner.class)
public class AutoCompleteRendererTest {

    ValueExpressionFactory exprFactory = ValueExpressionFactory.getInstance();
    ViewRenderHelper renderHelper = new ViewRenderHelper();

    @BeforeClass
    public static void setUp() {
        ConfigConverters confConverter = new ConfigConverters();
        confConverter.init();
    }

    /**
     * Creates a view tree and applies rendering conditions based on entity values and checks
     * the view is not visible
     */
    @Test
    public void testSimpleAutoComplete() {
        Context ctx = InstrumentationRegistry.getInstrumentation().getContext();
        ActionController mockAC = mock(ActionController.class);
        RenderingEnv env = new RenderingEnv(ContextTestUtils.createGlobalContext(), mockAC);
        env.setViewContext(ctx);

        UIAutoComplete select = new UIAutoComplete();
        select.setId(RandomUtils.randomString(4));

        int expectedOptions = RandomUtils.randomInt(0, 5);
        UIOption[] options = new UIOption[expectedOptions];
        for (int i = 0; i < options.length; i++) {
            String name = RandomUtils.randomString(3);
            options[i] = new UIOption(name, name);
        }
        select.setOptions(options);
        InputFieldView<AutoCompleteView> view = (InputFieldView<AutoCompleteView>) renderHelper.render(env, select);
        Assert.assertNotNull(view);

        // check elements in the view
        Adapter adapter = view.getInputView().getAdapter();
        Assert.assertNotNull(adapter);
        Assert.assertEquals(expectedOptions, adapter.getCount() - 1);// empty option was added by renderer

    }

    @Test
    public void testNotVisibleSelect() {
        Context ctx = InstrumentationRegistry.getInstrumentation().getContext();

        RenderingEnv env = new RenderingEnv(ContextTestUtils.createGlobalContext(), new ActionController(null, null));
        env.setViewContext(ctx);

        UIAutoComplete select = new UIAutoComplete();
        select.setId(RandomUtils.randomString(4));
        select.setRenderExpression(exprFactory.create("false"));
        View view = renderHelper.render(env, select);

        Assert.assertNotNull(view);
        Assert.assertTrue(view.getVisibility() == View.GONE);
    }

    /**
     * Fill the autocomplete values using a repository.
     */
    @Test
    public void testGetOptionsFromRepo() {
        Context ctx = InstrumentationRegistry.getInstrumentation().getContext();
        ActionController mockAC = mock(ActionController.class);
        RenderingEnv env = new RenderingEnv(ContextTestUtils.createGlobalContext(), mockAC);
        env.setViewContext(ctx);

        EntityMeta meta = DevDbBuilder.createRandomMeta();
        int expectedOptions = RandomUtils.randomInt(0, 13);
        List<Entity> entities = DevDbBuilder.buildEntities(meta, expectedOptions);
        // mock the repository to return random entities

        EditableRepository repoMock = mock(EditableRepository.class);
        when(repoMock.find(any())).thenReturn(entities);
        when(repoMock.getMeta()).thenReturn(meta);
        when(repoMock.getFilterClass()).thenReturn(SQLQueryFilter.class);

        UIAutoComplete autoSel = new UIAutoComplete();
        autoSel.setId("111");
        autoSel.setRepo(repoMock);

        InputFieldView<AutoCompleteView> view = (InputFieldView<AutoCompleteView>) renderHelper.render(env, autoSel);
        Assert.assertNotNull(view);

        // check elements in the view
        Adapter adapter = view.getInputView().getAdapter();
        Assert.assertNotNull(adapter);
        Assert.assertEquals(expectedOptions, adapter.getCount() - 1);// empty option was added by renderer

    }

}
