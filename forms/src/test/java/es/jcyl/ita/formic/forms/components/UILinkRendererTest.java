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

import androidx.test.platform.app.InstrumentationRegistry;

import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.RobolectricTestRunner;

import es.jcyl.ita.formic.forms.actions.ActionController;
import es.jcyl.ita.formic.forms.components.link.UILink;
import es.jcyl.ita.formic.forms.config.ConfigConverters;
import es.jcyl.ita.formic.forms.el.ValueExpressionFactory;
import es.jcyl.ita.formic.forms.utils.ContextTestUtils;
import es.jcyl.ita.formic.forms.view.render.renderer.RenderingEnv;
import es.jcyl.ita.formic.forms.view.render.renderer.RenderingEnvFactory;
import es.jcyl.ita.formic.forms.view.render.renderer.ViewRenderer;
import es.jcyl.ita.formic.repo.builders.EntityMetaDataBuilder;
import es.jcyl.ita.formic.repo.test.utils.RandomUtils;

import static org.mockito.Mockito.mock;

/**
 * @author Gustavo Río (gustavo.rio@itacyl.es)
 * <p>
 */
@RunWith(RobolectricTestRunner.class)
public class UILinkRendererTest {

    EntityMetaDataBuilder metaBuilder = new EntityMetaDataBuilder();
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
    public void testSimpleLink() {
        Context ctx = InstrumentationRegistry.getInstrumentation().getContext();
        ActionController mcAC = mock(ActionController.class);
        RenderingEnvFactory.getInstance().setActionController(mcAC);
        RenderingEnv env = RenderingEnvFactory.getInstance().create();
        env.setGlobalContext(ContextTestUtils.createGlobalContext());
        env.setAndroidContext(ctx);

        // link component
        UILink link = new UILink();
        link.setId(RandomUtils.randomString(4));
        link.setRoute("form1");

        View linkView = renderHelper.render(env, link);

        // check there's a TextView element
        Assert.assertNotNull(linkView);

    }

    @Test
    public void testNotVisibleLink() {
        Context ctx = InstrumentationRegistry.getInstrumentation().getContext();
        ActionController mcAC = mock(ActionController.class);
        RenderingEnvFactory.getInstance().setActionController(mcAC);
        RenderingEnv env = RenderingEnvFactory.getInstance().create();
        env.setGlobalContext(ContextTestUtils.createGlobalContext());
        env.setAndroidContext(ctx);

        // link component
        UILink link = new UILink();
        link.setId(RandomUtils.randomString(4));
        link.setRenderExpression(exprFactory.create("false"));
        link.setRoute("form1");

        View linkView = renderHelper.render(env, link);

        // check there's a TextView element
        Assert.assertNotNull(linkView);
        Assert.assertTrue(linkView.getVisibility() == View.GONE);

    }
}
