package es.jcyl.ita.formic.forms.integration;
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

import org.junit.AfterClass;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.Robolectric;
import org.robolectric.RobolectricTestRunner;

import java.util.List;

import es.jcyl.ita.formic.core.context.CompositeContext;
import es.jcyl.ita.formic.core.context.impl.BasicContext;
import es.jcyl.ita.formic.forms.App;
import es.jcyl.ita.formic.forms.MainControllerMock;
import es.jcyl.ita.formic.forms.R;
import es.jcyl.ita.formic.forms.components.datalist.DatalistItemWidget;
import es.jcyl.ita.formic.forms.components.datalist.DatalistWidget;
import es.jcyl.ita.formic.forms.config.ConfigConverters;
import es.jcyl.ita.formic.forms.config.elements.FormConfig;
import es.jcyl.ita.formic.forms.controllers.ViewController;
import es.jcyl.ita.formic.forms.utils.DagTestUtils;
import es.jcyl.ita.formic.forms.utils.DevFormNav;
import es.jcyl.ita.formic.forms.utils.RepositoryUtils;
import es.jcyl.ita.formic.forms.utils.XmlConfigUtils;
import es.jcyl.ita.formic.forms.view.activities.FormEditViewHandlerActivity;
import es.jcyl.ita.formic.repo.Entity;
import es.jcyl.ita.formic.repo.Repository;
import es.jcyl.ita.formic.repo.builders.DevDbBuilder;
import es.jcyl.ita.formic.repo.builders.EntityMetaDataBuilder;
import es.jcyl.ita.formic.repo.meta.EntityMeta;

import static org.mockito.Mockito.*;


/**
 * Tests UIView createion from xml config files
 * <p>
 *
 * @author Gustavo Río Briones (gustavo.rio@itacyl.es)
 */
@RunWith(RobolectricTestRunner.class)
public class InputValidationIntegrationTest {

    private static Repository contacts;
    private static EntityMeta meta;

    @BeforeClass
    public static void setUp() {
        App.init("");
        ConfigConverters confConverter = new ConfigConverters();
        confConverter.init();
        // Create entity Meta with a property called "email"
        EntityMetaDataBuilder metaBuilder = new EntityMetaDataBuilder();
        metaBuilder.withRandomData().addProperty("email", String.class);
        meta = metaBuilder.build();
        contacts = RepositoryUtils.registerMock("contacts", meta);
    }

    private static final String XML_SNIPPET =
            " <datalist>\n" +
                    "            <datalistitem repo=\"contacts\">\n" +
                    "                <text id=\"email\" value=\"${entity.email}\" validator=\"required\" />\n" +
                    "            </datalistitem>\n" +
                    "</datalist>";

    @Test
    public void testFormConfig() throws Exception {
        CompositeContext globalContext = App.getInstance().getGlobalContext();

        // read XML config
        String xml = XmlConfigUtils.createEditForm(XML_SNIPPET);
        FormConfig formConfig = XmlConfigUtils.readFormConfig(xml);
        ViewController viewController = formConfig.getEdits().get(0);
        DagTestUtils.registerDags(formConfig);

        // mock main controller
        MainControllerMock mc = new MainControllerMock();
        mc.setContext(globalContext);
        mc.setViewController(viewController); // set test viewController

        // add entities to the repo mock
        List<Entity> entities = DevDbBuilder.buildEntities(meta, 2);
        when(contacts.find(any())).thenReturn(entities);

        // Create Android context and navigate to form
        Context ctx = Robolectric.setupActivity(FormEditViewHandlerActivity.class);
        ctx.setTheme(R.style.FormudruidLight);
        DevFormNav formNav = new DevFormNav(ctx, mc);

        // navigate to test viewController
        formNav.nav("");

        /************/
        /** CASE 1: set all the datatlistItems-email components to null and check all of them have
         * a message error after saving */
        /************/
        // Get view widgets
        DatalistWidget widget = formNav.getWidget("datalist1", DatalistWidget.class);
        Assert.assertNotNull(widget);
        List<DatalistItemWidget> items = widget.getItems();

        // set all the email inputs to null to trigger validators
        for (DatalistItemWidget item : widget.getItems()) {
            item.getWidgetContext().put("view.email", null);
        }
        // execute save() to run validation
        formNav.clickSave("datalist1");

        // Get the widgets from view and check all item widgetContext have an error message
        widget = formNav.getWidget("datalist1", DatalistWidget.class);
        for (DatalistItemWidget item : widget.getItems()) {
            BasicContext msgCtx = item.getWidgetContext().getMessageContext();
            Assert.assertNotNull(msgCtx.get("email"));
        }
        items = widget.getItems();

        // check each widgetContext has a different message Context
        Assert.assertFalse("Each widgetContext must have a different messageContext",
                items.get(0).getWidgetContext().getMessageContext() ==
                        items.get(1).getWidgetContext().getMessageContext());

        /************/
        /** CASE 2:  modify first item-email to set a valid value and keep null in the second
         * component. After validating, the first mustn't have an error message in the widget context.
         /************/

        // put to null just second field
        // EVERYTIME YOU EXECUTE AN ACTION YOU HAVE TO GET THE WIDGET TO OBTAIN THE CURRENT RENDERED VIEWS!!!
        widget = formNav.getWidget("datalist1", DatalistWidget.class);
        items = widget.getItems();

        items.get(0).getWidgetContext().put("view.email", "thisisavalid@email.com");
        items.get(1).getWidgetContext().put("view.email", null);

        formNav.clickSave("datalist1");
        widget = formNav.getWidget("datalist1", DatalistWidget.class);
        items = widget.getItems();

        Assert.assertNull(items.get(0).getWidgetContext().getMessageContext().get("email"));
        Assert.assertNotNull(items.get(1).getWidgetContext().getMessageContext().get("email"));

        // fix the error on second view and check there are no validation errors
        items.get(0).getWidgetContext().put("view.email", "thisisavalid@email.com");
        items.get(1).getWidgetContext().put("view.email", "anothervalidemail@email.com");

        formNav.clickSave("datalist1");
        widget = formNav.getWidget("datalist1", DatalistWidget.class);
        items = widget.getItems();

        Assert.assertNull(items.get(0).getWidgetContext().getMessageContext().get("email"));
        Assert.assertNull(items.get(1).getWidgetContext().getMessageContext().get("email"));
    }

    @AfterClass
    public static void tearDown() {
        RepositoryUtils.unregisterMock("contacts");
    }

}
