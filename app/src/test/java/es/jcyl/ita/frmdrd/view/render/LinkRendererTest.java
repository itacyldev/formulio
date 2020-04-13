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

import androidx.test.platform.app.InstrumentationRegistry;

import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.RobolectricTestRunner;

import es.jcyl.ita.crtrepo.builders.EntityDataBuilder;
import es.jcyl.ita.crtrepo.builders.EntityMetaDataBuilder;
import es.jcyl.ita.crtrepo.test.utils.RandomUtils;
import es.jcyl.ita.frmdrd.actions.ActionController;
import es.jcyl.ita.frmdrd.builders.FormDataBuilder;
import es.jcyl.ita.frmdrd.configuration.ConfigConverters;
import es.jcyl.ita.frmdrd.el.ValueExpressionFactory;
import es.jcyl.ita.frmdrd.ui.components.link.UILink;
import es.jcyl.ita.frmdrd.utils.ContextUtils;
import es.jcyl.ita.frmdrd.view.converters.ViewValueConverterFactory;

/**
 * @author Gustavo Río (gustavo.rio@itacyl.es)
 * <p>
 */
@RunWith(RobolectricTestRunner.class)
public class LinkRendererTest {

    FormDataBuilder formBuilder = new FormDataBuilder();
    EntityDataBuilder entityBuilder;
    EntityMetaDataBuilder metaBuilder = new EntityMetaDataBuilder();
    ViewValueConverterFactory convFactory = ViewValueConverterFactory.getInstance();
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
    public void testSimpleLink() {
        Context ctx = InstrumentationRegistry.getInstrumentation().getContext();

        RenderingEnv env = new RenderingEnv(ContextUtils.createGlobalContext(), new ActionController(null, null));
        env.setViewContext(ctx);

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

        RenderingEnv env = new RenderingEnv(ContextUtils.createGlobalContext(), new ActionController(null, null));
        env.setViewContext(ctx);

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
