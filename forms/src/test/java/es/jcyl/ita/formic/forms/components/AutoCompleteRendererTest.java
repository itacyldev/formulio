package es.jcyl.ita.formic.forms.components;
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
import android.widget.ArrayAdapter;
import android.widget.TextView;

import androidx.test.platform.app.InstrumentationRegistry;

import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mini2Dx.beanutils.ConvertUtils;
import org.robolectric.RobolectricTestRunner;

import java.util.List;

import es.jcyl.ita.formic.forms.R;
import es.jcyl.ita.formic.forms.actions.ActionController;
import es.jcyl.ita.formic.forms.components.autocomplete.AutoCompleteView;
import es.jcyl.ita.formic.forms.components.autocomplete.AutoCompleteWidget;
import es.jcyl.ita.formic.forms.components.autocomplete.UIAutoComplete;
import es.jcyl.ita.formic.forms.components.option.UIOption;
import es.jcyl.ita.formic.forms.config.ConfigConverters;
import es.jcyl.ita.formic.forms.el.ValueExpressionFactory;
import es.jcyl.ita.formic.forms.utils.ContextTestUtils;
import es.jcyl.ita.formic.forms.view.render.renderer.RenderingEnv;
import es.jcyl.ita.formic.forms.view.render.renderer.ViewRenderer;
import es.jcyl.ita.formic.forms.view.widget.InputWidget;
import es.jcyl.ita.formic.repo.EditableRepository;
import es.jcyl.ita.formic.repo.Entity;
import es.jcyl.ita.formic.repo.builders.DevDbBuilder;
import es.jcyl.ita.formic.repo.db.SQLQueryFilter;
import es.jcyl.ita.formic.repo.meta.EntityMeta;
import es.jcyl.ita.formic.repo.test.utils.RandomUtils;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

/**
 * @author Gustavo Río (gustavo.rio@itacyl.es)
 * <p>
 */
@RunWith(RobolectricTestRunner.class)
public class AutoCompleteRendererTest {

    ValueExpressionFactory exprFactory = ValueExpressionFactory.getInstance();
    ViewRenderer renderHelper = new ViewRenderer();

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
        ctx.setTheme(R.style.FormudruidLight);

        ActionController mcAC = mock(ActionController.class);
        RenderingEnv env = new RenderingEnv(mcAC);
        env.setGlobalContext(ContextTestUtils.createGlobalContext());
        env.setAndroidContext(ctx);

        UIAutoComplete select = new UIAutoComplete();
        select.setId(RandomUtils.randomString(4));

        int expectedOptions = RandomUtils.randomInt(0, 5);
        UIOption[] options = new UIOption[expectedOptions];
        for (int i = 0; i < options.length; i++) {
            String name = RandomUtils.randomString(3);
            options[i] = new UIOption(name, name);
        }
        select.setOptions(options);
        InputWidget<UIAutoComplete, AutoCompleteView> widget =
                (InputWidget<UIAutoComplete, AutoCompleteView>) renderHelper.render(env, select);
        Assert.assertNotNull(widget);

        // check elements in the view
        Adapter adapter = widget.getInputView().getAdapter();
        Assert.assertNotNull(adapter);
        Assert.assertEquals(expectedOptions, adapter.getCount() - 1);// empty option was added by renderer

    }

    @Test
    public void testNotVisibleSelect() {
        Context ctx = InstrumentationRegistry.getInstrumentation().getContext();
        ctx.setTheme(R.style.FormudruidLight);

        ActionController mcAC = mock(ActionController.class);
        RenderingEnv env = new RenderingEnv(mcAC);
        env.setGlobalContext(ContextTestUtils.createGlobalContext());
        env.setAndroidContext(ctx);

        UIAutoComplete select = new UIAutoComplete();
        select.setId(RandomUtils.randomString(4));
        select.setRenderExpression(exprFactory.create("false"));
        View view = renderHelper.render(env, select);

        Assert.assertNotNull(view);
        Assert.assertTrue(view.getVisibility() == View.GONE);
    }

    /**
     * Fill the autocomplete values using a repository. Create an EL expressino combining two
     * entity properties and make sure the views in the autocomplete list have the right values.
     */
    @Test
    public void testGetOptionsFromRepo() {
        Context ctx = InstrumentationRegistry.getInstrumentation().getContext();
        ctx.setTheme(R.style.FormudruidLight);

        ActionController mcAC = mock(ActionController.class);
        RenderingEnv env = new RenderingEnv(mcAC);
        env.setGlobalContext(ContextTestUtils.createGlobalContext());
        env.setAndroidContext(ctx);

        env.initialize();

        EntityMeta meta = DevDbBuilder.buildRandomMeta();
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
        autoSel.setOptionValueProperty("id");
        String secondPropertyName = meta.getPropertyNames()[1];
        String thirdPropertyName = meta.getPropertyNames()[2];
        // create and expression combining two entity properties
        autoSel.setOptionLabelExpression(exprFactory.create(String.format("${entity.%s}-${entity.%s}", secondPropertyName, thirdPropertyName)));

        AutoCompleteWidget widget = (AutoCompleteWidget) renderHelper.render(env, autoSel);
        widget.load(env);
        Assert.assertNotNull(widget);

        // check number of elements in the adapter
        ArrayAdapter<Entity> adapter = (ArrayAdapter<Entity>) widget.getInputView().getAdapter();
        Assert.assertNotNull(adapter);
        Assert.assertEquals(expectedOptions, adapter.getCount());

        // check options value
        for (int i = 0; i < adapter.getCount(); i++) {
            View itemView = adapter.getView(i, null, widget);
            TextView textView = (TextView) itemView.findViewById(R.id.autocomplete_item);
            Entity entity = entities.get(i);
            String expected = String.format("%s-%s",
                    ConvertUtils.convert(entity.get(secondPropertyName), String.class),
                    ConvertUtils.convert(entity.get(thirdPropertyName), String.class));
            Assert.assertEquals(expected, textView.getText());
        }
    }

}
