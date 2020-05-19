package es.jcyl.ita.frmdrd.view.render;
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

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.Robolectric;
import org.robolectric.RobolectricTestRunner;

import java.util.ArrayList;
import java.util.List;

import es.jcyl.ita.crtrepo.builders.EntityDataBuilder;
import es.jcyl.ita.crtrepo.builders.EntityMetaDataBuilder;
import es.jcyl.ita.crtrepo.test.utils.RandomUtils;
import es.jcyl.ita.frmdrd.MainActivity;
import es.jcyl.ita.frmdrd.actions.ActionController;
import es.jcyl.ita.frmdrd.builders.FormDataBuilder;
import es.jcyl.ita.frmdrd.el.ValueExpressionFactory;
import es.jcyl.ita.frmdrd.ui.components.UIComponent;
import es.jcyl.ita.frmdrd.ui.components.inputfield.UIField;
import es.jcyl.ita.frmdrd.ui.components.tab.UITab;
import es.jcyl.ita.frmdrd.ui.components.tab.UITabItem;
import es.jcyl.ita.frmdrd.utils.ContextTestUtils;

import static org.mockito.Mockito.mock;

/**
 * @author Javier Ramos (javier.ramos@itacyl.es)
 * <p>
 */
@RunWith(RobolectricTestRunner.class)
public class TabRendererTest {

    Context ctx;

    FormDataBuilder formBuilder = new FormDataBuilder();
    EntityDataBuilder entityBuilder;
    EntityMetaDataBuilder metaBuilder = new EntityMetaDataBuilder();
    ValueExpressionFactory exprFactory = ValueExpressionFactory.getInstance();
    ViewRenderHelper renderHelper = new ViewRenderHelper();

    @Before
    public void setup() {
        ctx = Robolectric.buildActivity(MainActivity.class).create().get();
    }

    /**
     * Creates a view tree and applies rendering conditions based on entity values and checks
     * the view is not visible
     */
    @Test
    public void test2Tabs() {
        ActionController mockAC = mock(ActionController.class);
        RenderingEnv env = new RenderingEnv(ContextTestUtils.createGlobalContext(), mockAC);
        env.setViewContext(ctx);


        UITabItem tabItem1 = new UITabItem();
        tabItem1.setLabel("tab 1");
        UIField field1 = new UIField();
        field1.setLabel("texto 1");
        field1.setId(RandomUtils.randomString(4));
        tabItem1.addChild(field1);


        UITabItem tabItem2 = new UITabItem();
        tabItem2.setLabel("tab 2");
        UIField field2 = new UIField();
        field2.setLabel("texto 2");
        field2.setId(RandomUtils.randomString(4));
        tabItem2.addChild(field2);

        List<UIComponent> lstTabItem = new ArrayList<>();
        lstTabItem.add(tabItem1);
        lstTabItem.add(tabItem2);

        UITab tab = new UITab();
        tab.setId(RandomUtils.randomString(4));
        tab.setChildren(lstTabItem);

        View tabView = renderHelper.render(env, tab);



        // check there's a TextView element
        Assert.assertNotNull(tabView);

    }

//    @Test
//    public void testNotVisibleLink() {
//        Context ctx = InstrumentationRegistry.getInstrumentation().getContext();
//        ActionController mockAC = mock(ActionController.class);
//        RenderingEnv env = new RenderingEnv(ContextTestUtils.createGlobalContext(), mockAC);
//        env.setViewContext(ctx);
//
//        // link component
//        UILink link = new UILink();
//        link.setId(RandomUtils.randomString(4));
//        link.setRenderExpression(exprFactory.create("false"));
//        link.setRoute("form1");
//
//        View linkView = renderHelper.render(env, link);
//
//        // check there's a TextView element
//        Assert.assertNotNull(linkView);
//        Assert.assertTrue(linkView.getVisibility() == View.GONE);
//
//    }
}
