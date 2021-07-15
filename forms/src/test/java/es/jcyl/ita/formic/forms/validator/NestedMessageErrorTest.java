package es.jcyl.ita.formic.forms.validator;
/*
 *
 *  * Copyright 2020 Javier Ramos (javier.ramos@itacyl.es), ITACyL (http://www.itacyl.es).
 *  *
 *  * Licensed under the Apache License, Version 2.0 (the "License");
 *  * you may not use this file except in compliance with the License.
 *  * You may obtain a copy of the License at
 *  *
 *  *      https://www.apache.org/licenses/LICENSE-2.0
 *  *
 *  * Unless required by applicable law or agreed to in writing, software
 *  * distributed under the License is distributed on an "AS IS" BASIS,
 *  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  * See the License for the specific language governing permissions and
 *  * limitations under the License.
 *
 */

import android.content.Context;

import androidx.fragment.app.FragmentActivity;
import androidx.test.platform.app.InstrumentationRegistry;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.Robolectric;
import org.robolectric.RobolectricTestRunner;

import java.util.List;

import es.jcyl.ita.formic.core.context.impl.BasicContext;
import es.jcyl.ita.formic.forms.R;
import es.jcyl.ita.formic.forms.actions.ActionController;
import es.jcyl.ita.formic.forms.components.datalist.UIDatalist;
import es.jcyl.ita.formic.forms.components.datalist.UIDatalistItem;
import es.jcyl.ita.formic.forms.components.form.UIForm;
import es.jcyl.ita.formic.forms.components.inputfield.UIField;
import es.jcyl.ita.formic.forms.components.tab.UITab;
import es.jcyl.ita.formic.forms.components.tab.UITabItem;
import es.jcyl.ita.formic.forms.components.view.UIView;
import es.jcyl.ita.formic.forms.components.view.ViewWidget;
import es.jcyl.ita.formic.forms.config.ConfigConverters;
import es.jcyl.ita.formic.forms.utils.ContextTestUtils;
import es.jcyl.ita.formic.forms.utils.DevFormBuilder;
import es.jcyl.ita.formic.forms.validation.RequiredValidator;
import es.jcyl.ita.formic.forms.view.helpers.ViewHelper;
import es.jcyl.ita.formic.forms.view.render.renderer.MessageHelper;
import es.jcyl.ita.formic.forms.view.render.renderer.RenderingEnv;
import es.jcyl.ita.formic.forms.view.render.renderer.ViewRenderer;
import es.jcyl.ita.formic.forms.view.render.renderer.WidgetContext;
import es.jcyl.ita.formic.forms.view.widget.ControllableWidget;
import es.jcyl.ita.formic.forms.view.widget.StatefulWidget;
import es.jcyl.ita.formic.forms.view.widget.Widget;
import es.jcyl.ita.formic.forms.view.widget.WidgetContextHolder;
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
public class NestedMessageErrorTest {
    Context ctx;
    ViewRenderer renderHelper = new ViewRenderer();

    @Before
    public void setup() {
        ctx = InstrumentationRegistry.getInstrumentation().getContext();
        ctx.setTheme(R.style.FormudruidLight);

        ConfigConverters confConverter = new ConfigConverters();
        confConverter.init();
    }

    /**
     * Creates a view with two tabs, with a form in first tab and a datalist in second tab.
     * In the form and in each datalist item, there's a field. During the test, all field are
     * set to null. Then, using WidgetControllers, save operations are perform to execution validation.
     * Afther this, it is expected that all the MessageContext contain error messages and that
     * tabitems has to return true as parameter of the method hasNestedMessage().
     */
    @Test
    public void testNestedMessages() {
        ctx = Robolectric.setupActivity(FragmentActivity.class);
        ctx.setTheme(R.style.FormudruidDark);

        ActionController mcAC = mock(ActionController.class);
        RenderingEnv env = new RenderingEnv(mcAC);
        env.setGlobalContext(ContextTestUtils.createGlobalContext());
        env.setAndroidContext(ctx);

        // create view with two tabs and a form in each tab
        UIView view = new UIView();
        UITab tab = new UITab();
        tab.setId("tab");
        view.addChild(tab);
        UITabItem tabItem1 = createTab1WithForm();
        tab.addChild(tabItem1);
        UITabItem tabItem2 = createTab2WithDataList();
        tab.addChild(tabItem2);

        // Render view
        ViewWidget viewWidget = (ViewWidget) renderHelper.render(env, view);

        // Access widgetContexts and set all the fields to null;
        List<WidgetContextHolder> contextHolders = viewWidget.getContextHolders();
        for (WidgetContextHolder holder : contextHolders) {
            if (holder instanceof ViewWidget) {
                continue;
            }
            WidgetContext wCtx = holder.getWidgetContext();
            List<StatefulWidget> statefulWidgets = wCtx.getStatefulWidgets();
            // there must be just one
            String componentId = statefulWidgets.get(0).getWidget().getComponentId();
            wCtx.getViewContext().put(componentId, null);
        }
        // Execute save using  controllers
        for (ControllableWidget ctrlWidget : viewWidget.getControllableWidgets()) {
            boolean valid = ctrlWidget.getController().save();
            Widget widget = ctrlWidget.getWidget();
            if (widget instanceof ViewWidget) {
                continue;
            }
            Assert.assertFalse(valid);
        }
        // check all MessageContext has an error registered
        for (WidgetContextHolder holder : contextHolders) {
            if (holder instanceof ViewWidget) {
                continue;
            }
            BasicContext messageContext = holder.getWidgetContext().getMessageContext();
            Assert.assertTrue(messageContext.size() >= 1);
        }
        // find tab1 and tab2 widgets and check nested messages
        Widget tabitem1Widget = ViewHelper.findNestedWidgetsById(viewWidget, "tabitem1");
        Widget tabitem2Widget = ViewHelper.findNestedWidgetsById(viewWidget, "tabitem2");


        Assert.assertTrue(MessageHelper.hasNestedMessages(env, tabitem1Widget));
        boolean hasNested = MessageHelper.hasNestedMessages(env, tabitem2Widget);
        Assert.assertTrue(hasNested);
    }

    public UITabItem createTab1WithForm() {
        UITabItem tabitem = new UITabItem();
        tabitem.setId("tabitem1");

        UIForm form = DevFormBuilder.createOneFieldForm();
        tabitem.addChild(form);
        // add required validator to field
        UIField field = (UIField) form.getFields().get(0);
        field.addValidator(new RequiredValidator());
        return tabitem;
    }

    public UITabItem createTab2WithDataList() {
        UITabItem tabitem = new UITabItem();
        tabitem.setId("tabitem2");

        // Use formbuilder to create a field and set a required validator
        UIForm form = DevFormBuilder.createOneFieldForm();
        UIField field = (UIField) form.getFields().get(0);
        field.addValidator(new RequiredValidator());

        UIDatalist datalist = new UIDatalist();
        UIDatalistItem dlItem = new UIDatalistItem();
        datalist.addChild(dlItem);
        dlItem.addChild(field);

        // mock repository to return 5 entities
        EditableRepository repo = mock(EditableRepository.class);
        when(repo.getFilterClass()).thenReturn(SQLQueryFilter.class);
        int numEntities = 5;
        List<Entity> entities = DevDbBuilder.buildEntitiesRandomMeta(numEntities);
        when(repo.find(any())).thenReturn(entities);
        datalist.setRepo(repo);

        tabitem.addChild(datalist);
        return tabitem;
    }


}

