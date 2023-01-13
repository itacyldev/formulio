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
import android.widget.RadioGroup;

import androidx.test.platform.app.InstrumentationRegistry;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.RobolectricTestRunner;

import es.jcyl.ita.formic.forms.R;
import es.jcyl.ita.formic.forms.actions.ActionController;
import es.jcyl.ita.formic.forms.components.option.UIOption;
import es.jcyl.ita.formic.forms.components.radio.UIRadio;
import es.jcyl.ita.formic.forms.config.ConfigConverters;
import es.jcyl.ita.formic.forms.el.ValueExpressionFactory;
import es.jcyl.ita.formic.forms.utils.ContextTestUtils;
import es.jcyl.ita.formic.forms.view.render.renderer.RenderingEnv;
import es.jcyl.ita.formic.forms.view.render.renderer.RenderingEnvFactory;
import es.jcyl.ita.formic.forms.view.render.renderer.ViewRenderer;
import es.jcyl.ita.formic.forms.view.widget.InputWidget;
import es.jcyl.ita.formic.repo.test.utils.RandomUtils;

import static org.mockito.Mockito.*;

/**
 * @author Gustavo Río (gustavo.rio@itacyl.es)
 * <p>
 */
@RunWith(RobolectricTestRunner.class)
public class UIRadioRendererTest {

    ValueExpressionFactory exprFactory = ValueExpressionFactory.getInstance();
    ViewRenderer renderHelper = new ViewRenderer();

    Context ctx;

    @Before
    public void setup() {
        ctx = InstrumentationRegistry.getInstrumentation().getContext();
        ctx.setTheme(R.style.FormudruidLight);
    }

    /**
     * Creates a view tree and applies rendering conditions based on entity values and checks
     * the view is not visible
     */
    @Test
    public void testSimpleRadio() {
        ConfigConverters confConverter = new ConfigConverters();
        confConverter.init();

        ActionController mcAC = mock(ActionController.class);
        RenderingEnvFactory.getInstance().setActionController(mcAC);
        RenderingEnv env = RenderingEnvFactory.getInstance().create();
        env.setGlobalContext(ContextTestUtils.createGlobalContext());
        env.setAndroidContext(ctx);
        env.disableInterceptors();

        UIRadio radio = new UIRadio();
        int expectedOptions = RandomUtils.randomInt(0, 5);

        radio.setId(RandomUtils.randomString(4));

        UIOption[] options = new UIOption[expectedOptions];
        for (int i = 0; i < options.length; i++) {
            String name = RandomUtils.randomString(3);
            options[i] = new UIOption(name, name);
        }
        radio.setOptions(options);
        InputWidget<UIRadio, RadioGroup> view =
                (InputWidget<UIRadio, RadioGroup>) renderHelper.render(env,
                        radio);
        Assert.assertNotNull(view);

        // check elements in the view
        RadioGroup inputView = view.getInputView();

        Assert.assertNotNull(inputView);
        Assert.assertEquals(expectedOptions, inputView.getChildCount());// empty option was added by renderer
    }

    @Test
    public void testNotVisibleRadio() {
        ActionController mcAC = mock(ActionController.class);
        RenderingEnvFactory.getInstance().setActionController(mcAC);
        RenderingEnv env = RenderingEnvFactory.getInstance().create();
        env.setGlobalContext(ContextTestUtils.createGlobalContext());
        env.setAndroidContext(ctx);

        UIRadio select = new UIRadio();
        select.setId(RandomUtils.randomString(4));
        select.setRenderExpression(exprFactory.create("false"));
        View view = renderHelper.render(env, select);

        Assert.assertNotNull(view);
        Assert.assertTrue(view.getVisibility() == View.GONE);
    }

    @Test
    public void testNotVisibleRadio2() {
        ActionController mcAC = mock(ActionController.class);
        RenderingEnvFactory.getInstance().setActionController(mcAC);
        RenderingEnv env = RenderingEnvFactory.getInstance().create();
        env.setGlobalContext(ContextTestUtils.createGlobalContext());
        env.setAndroidContext(ctx);

        UIRadio select = new UIRadio();
        select.setId(RandomUtils.randomString(4));
        select.setRenderExpression(exprFactory.create("false"));
        View view = renderHelper.render(env, select);

        Assert.assertNotNull(view);
        Assert.assertTrue(view.getVisibility() == View.GONE);
    }

}
