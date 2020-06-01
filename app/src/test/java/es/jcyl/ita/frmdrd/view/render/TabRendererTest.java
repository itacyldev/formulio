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
import android.widget.LinearLayout;

import androidx.viewpager2.widget.ViewPager2;

import com.google.android.material.tabs.TabLayout;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.Robolectric;
import org.robolectric.RobolectricTestRunner;

import java.util.ArrayList;
import java.util.List;

import es.jcyl.ita.crtrepo.test.utils.RandomUtils;
import es.jcyl.ita.frmdrd.MainController;
import es.jcyl.ita.frmdrd.R;
import es.jcyl.ita.frmdrd.forms.FormEditController;
import es.jcyl.ita.frmdrd.ui.components.UIComponent;
import es.jcyl.ita.frmdrd.ui.components.inputfield.UIField;
import es.jcyl.ita.frmdrd.ui.components.tab.UITab;
import es.jcyl.ita.frmdrd.ui.components.tab.UITabItem;
import es.jcyl.ita.frmdrd.ui.components.tab.ViewPagerAdapter;
import es.jcyl.ita.frmdrd.utils.ContextTestUtils;
import es.jcyl.ita.frmdrd.view.activities.FormEditViewHandlerActivity;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

/**
 * @author Javier Ramos (javier.ramos@itacyl.es)
 * <p>
 */
@RunWith(RobolectricTestRunner.class)
public class TabRendererTest {

    Context ctx;

    ViewRenderHelper renderHelper = new ViewRenderHelper();

    @Before
    public void setup() {
        MainController mainController = MainController.getInstance();
        FormEditController mockFC = mock(FormEditController.class);
        mainController.setFormController(mockFC, null);

        ctx = Robolectric.setupActivity(FormEditViewHandlerActivity.class);
        ctx.setTheme(R.style.AppTheme_Dark);
    }

    /**
     * Creates a view tree and applies rendering conditions based on entity values and checks
     * the view is not visible
     */
    @Test
    public void test2Tabs() {
        RenderingEnv env = mock(RenderingEnv.class);
        when(env.getViewContext()).thenReturn(ctx);
        when(env.getContext()).thenReturn(ContextTestUtils.createGlobalContext());

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

        LinearLayout tabView = (LinearLayout) renderHelper.render(env, tab);
        // check there's a TextView element
        Assert.assertNotNull(tabView);

        TabLayout tabLayout = (TabLayout) tabView.getChildAt(0);

        Assert.assertEquals(2, tabLayout.getTabCount());
        Assert.assertEquals(tabItem1.getLabel(), tabLayout.getTabAt(0).getText());
        Assert.assertEquals(tabItem2.getLabel(), tabLayout.getTabAt(1).getText());

        ViewPager2 viewPager = (ViewPager2) tabView.getChildAt(1);

        ViewPagerAdapter adapter = (ViewPagerAdapter) viewPager.getAdapter();
        Assert.assertEquals(2, adapter.getItemCount());
    }
}
