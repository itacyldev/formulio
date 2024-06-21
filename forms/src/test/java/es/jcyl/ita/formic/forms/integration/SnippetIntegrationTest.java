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
import org.junit.BeforeClass;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.Robolectric;
import org.robolectric.RobolectricTestRunner;

import java.util.List;

import es.jcyl.ita.formic.core.context.CompositeContext;
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
public class SnippetIntegrationTest {

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

    @Ignore("Template test case to check errors on view")
    @Test
    public void testRunViewXML() throws Exception {
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

        // get a widget a put some data on the view
        DatalistWidget widget = formNav.getWidget("datalist1", DatalistWidget.class);

        // set all the email inputs to null to trigger validators
        for (DatalistItemWidget item : widget.getItems()) {
            item.getWidgetContext().put("view.email", null);
        }

        // execute save() on a controller
        formNav.clickSave("datalist1");
        // or click save on the form with:
        // formNav.clickSave();

        // Get the widgets from view and check all item widgetContext have an error message
        widget = formNav.getWidget("datalist1", DatalistWidget.class);

        // assert something about the widget....

    }


    @AfterClass
    public static void tearDown() {
        RepositoryUtils.unregisterMock("contacts");
    }

}
