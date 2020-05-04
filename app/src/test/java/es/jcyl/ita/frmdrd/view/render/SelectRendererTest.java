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

import android.app.Activity;
import android.content.Context;
import android.test.ActivityTestCase;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Spinner;
import android.widget.SpinnerAdapter;

import androidx.test.platform.app.InstrumentationRegistry;

import org.junit.Assert;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.Robolectric;
import org.robolectric.RobolectricTestRunner;

import es.jcyl.ita.crtrepo.test.utils.RandomUtils;
import es.jcyl.ita.frmdrd.MainActivity;
import es.jcyl.ita.frmdrd.R;
import es.jcyl.ita.frmdrd.actions.ActionController;
import es.jcyl.ita.frmdrd.config.ConfigConverters;
import es.jcyl.ita.frmdrd.el.ValueExpressionFactory;
import es.jcyl.ita.frmdrd.ui.components.option.UIOption;
import es.jcyl.ita.frmdrd.ui.components.select.UISelect;
import es.jcyl.ita.frmdrd.utils.ContextTestUtils;
import es.jcyl.ita.frmdrd.view.InputFieldView;

import static org.mockito.Mockito.mock;

/**
 * @author Gustavo Río (gustavo.rio@itacyl.es)
 * <p>
 */
@RunWith(RobolectricTestRunner.class)
public class SelectRendererTest {

    ValueExpressionFactory exprFactory = ValueExpressionFactory.getInstance();
    ViewRenderHelper renderHelper = new ViewRenderHelper();

    Context ctx;

    @Before
    public void setup(){
        ctx = Robolectric.buildActivity(MainActivity.class).create().get();
    }

    /**
     * Creates a view tree and applies rendering conditions based on entity values and checks
     * the view is not visible
     */
    @Test
    public void testSimpleSelect() {
        ConfigConverters confConverter = new ConfigConverters();
        confConverter.init();

        ActionController mockAC = mock(ActionController.class);
        RenderingEnv env = new RenderingEnv(ContextTestUtils.createGlobalContext(), mockAC);
        env.setViewContext(ctx);

        UISelect select = new UISelect();
        int expectedOptions = RandomUtils.randomInt(0, 5);

        select.setId(RandomUtils.randomString(4));

        UIOption[] options = new UIOption[expectedOptions];
        for (int i = 0; i < options.length; i++) {
            String name = RandomUtils.randomString(3);
            options[i] = new UIOption(name, name);
        }
        select.setOptions(options);
        InputFieldView<Spinner> view = (InputFieldView<Spinner>) renderHelper.render(env, select);
        Assert.assertNotNull(view);

        // check elements in the view
        SpinnerAdapter adapter = view.getInputView().getAdapter();
        Assert.assertNotNull(adapter);
        Assert.assertEquals(expectedOptions, adapter.getCount()-1);// empty option was added by renderer
    }

    @Test
    public void testNotVisibleSelect() {
        ActionController mockAC = mock(ActionController.class);
        RenderingEnv env = new RenderingEnv(ContextTestUtils.createGlobalContext(), mockAC);
        env.setViewContext(ctx);

        UISelect select = new UISelect();
        select.setId(RandomUtils.randomString(4));
        select.setRenderExpression(exprFactory.create("false"));
        View view = renderHelper.render(env, select);

        Assert.assertNotNull(view);
        Assert.assertTrue(view.getVisibility() == View.GONE);
    }


}
