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
import android.view.ViewGroup;

import org.junit.AfterClass;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.Robolectric;
import org.robolectric.RobolectricTestRunner;

import java.util.List;

import es.jcyl.ita.formic.core.context.CompositeContext;
import es.jcyl.ita.formic.forms.MainControllerMock;
import es.jcyl.ita.formic.forms.R;
import es.jcyl.ita.formic.forms.components.datalist.DatalistWidget;
import es.jcyl.ita.formic.forms.components.view.UIView;
import es.jcyl.ita.formic.forms.config.Config;
import es.jcyl.ita.formic.forms.config.ConfigConverters;
import es.jcyl.ita.formic.forms.config.elements.FormConfig;
import es.jcyl.ita.formic.forms.controllers.ViewController;
import es.jcyl.ita.formic.forms.utils.DagTestUtils;
import es.jcyl.ita.formic.forms.utils.DevFormNav;
import es.jcyl.ita.formic.forms.utils.RepositoryUtils;
import es.jcyl.ita.formic.forms.utils.WidgetTestUtils;
import es.jcyl.ita.formic.forms.utils.XmlConfigUtils;
import es.jcyl.ita.formic.forms.view.activities.FormEditViewHandlerActivity;
import es.jcyl.ita.formic.forms.view.helpers.ViewHelper;
import es.jcyl.ita.formic.forms.view.render.DeferredView;
import es.jcyl.ita.formic.repo.Entity;
import es.jcyl.ita.formic.repo.Repository;
import es.jcyl.ita.formic.repo.builders.DevDbBuilder;

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

    @BeforeClass
    public static void setUp() {
        Config.init("");
        ConfigConverters confConverter = new ConfigConverters();
        confConverter.init();
        // register repos
        contacts = RepositoryUtils.registerMock("contacts");
    }

    //    private static final String XML_SNIPPET = "<text id=\"input1\"/>";
    private static final String XML_SNIPPET =
            " <datalist>\n" +
                    "            <datalistitem repo=\"contacts\">\n" +
                    "                <text id=\"mitext\" hint=\"${entity.last_name}\" label=\"input\" value=\"${entity.last_name}\" />\n" +
                    "                <textarea label=\"textarea\" value=\"${entity.last_name}\" render=\"${not(empty(view.mitext))}\" />\n" +
                    "            </datalistitem>\n" +
                    "</datalist>";

    @Test
    public void testFormConfig() throws Exception {
        CompositeContext globalContext = Config.getInstance().getGlobalContext();

        // read XML config
        String xml = XmlConfigUtils.createEditForm(XML_SNIPPET);
        FormConfig formConfig = XmlConfigUtils.readFormConfig(xml);
        ViewController viewController = formConfig.getEdits().get(0);
        DagTestUtils.registerDags(formConfig);

        // mock main controller
        MainControllerMock mc = new MainControllerMock();
        mc.setContext(globalContext);
        mc.setViewController(viewController);

        // add entities to the repo mock
        List<Entity> entities = DevDbBuilder.buildEntities(contacts.getMeta(), 2);
        when(contacts.find(any())).thenReturn(entities);

        // Create Android context and navigate to form
        Context ctx = Robolectric.setupActivity(FormEditViewHandlerActivity.class);
        ctx.setTheme(R.style.FormudruidLight);
        DevFormNav formNav = new DevFormNav(ctx, mc);
        formNav.nav("");

        // Get content view and check against Android Views
        UIView uiView = viewController.getView();
        ViewGroup contentView = viewController.getContentView();
//        Widget widget = ViewHelper.findComponentWidget(contentView, "input1");
        DatalistWidget widget = (DatalistWidget) ViewHelper.findComponentWidget(contentView, "datalist1");
        Assert.assertNotNull(widget);

        // traverse the view tree looking for deferred views
        WidgetTestUtils.LayoutTraverser traverser = WidgetTestUtils.buildViewTraverser(view -> {
            Assert.assertFalse("Deferred view found with id: " + view.getTag(),
                    (view instanceof DeferredView));
            return null;
        });
        traverser.traverse(widget);
    }

    @AfterClass
    public static void tearDown() {
        RepositoryUtils.unregisterMock("contacts");
    }

}
