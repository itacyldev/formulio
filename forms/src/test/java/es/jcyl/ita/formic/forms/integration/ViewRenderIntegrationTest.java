package es.jcyl.ita.formic.forms.integration;
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

import androidx.test.platform.app.InstrumentationRegistry;

import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.Robolectric;
import org.robolectric.RobolectricTestRunner;

import java.util.List;

import es.jcyl.ita.formic.core.context.CompositeContext;
import es.jcyl.ita.formic.forms.App;
import es.jcyl.ita.formic.forms.MainControllerMock;
import es.jcyl.ita.formic.forms.R;
import es.jcyl.ita.formic.forms.actions.ActionController;
import es.jcyl.ita.formic.forms.components.UIComponentHelper;
import es.jcyl.ita.formic.forms.components.datalist.DatalistWidget;
import es.jcyl.ita.formic.forms.components.datalist.UIDatalist;
import es.jcyl.ita.formic.forms.components.view.UIView;
import es.jcyl.ita.formic.forms.config.ConfigConverters;
import es.jcyl.ita.formic.forms.config.elements.FormConfig;
import es.jcyl.ita.formic.forms.controllers.ViewController;
import es.jcyl.ita.formic.forms.utils.ContextTestUtils;
import es.jcyl.ita.formic.forms.utils.DagTestUtils;
import es.jcyl.ita.formic.forms.utils.DevFormNav;
import es.jcyl.ita.formic.forms.utils.RepositoryUtils;
import es.jcyl.ita.formic.forms.utils.XmlConfigUtils;
import es.jcyl.ita.formic.forms.view.ViewConfigException;
import es.jcyl.ita.formic.forms.view.activities.FormEditViewHandlerActivity;
import es.jcyl.ita.formic.forms.view.dag.DAGManager;
import es.jcyl.ita.formic.forms.view.dag.ViewDAG;
import es.jcyl.ita.formic.forms.view.render.renderer.RenderingEnv;
import es.jcyl.ita.formic.forms.view.render.renderer.RenderingEnvFactory;
import es.jcyl.ita.formic.forms.view.render.renderer.ViewRenderer;
import es.jcyl.ita.formic.forms.view.widget.Widget;

import static org.mockito.Mockito.*;

/**
 * @author Gustavo Río (gustavo.rio@itacyl.es)
 * <p>
 */
@RunWith(RobolectricTestRunner.class)
public class ViewRenderIntegrationTest {

    private static Context ctx;
    ViewRenderer renderHelper = new ViewRenderer();

    @BeforeClass
    public static void beforeClass() {
        App.init("");
        ConfigConverters confConverter = new ConfigConverters();
        confConverter.init();
        // register repos
        RepositoryUtils.registerMock("contacts");
    }


    private static final String XML_DATALIST_WITH_FILTER = "<form>  " +
            "<input id=\"contactName\" label=\"Name:\" />\n" +
            "          <datalist repo=\"contacts\">\n" +
            "              <repofilter>\n" +
            "                  <contains property=\"first_name\" value=\"${view.contactName}\" />\n" +
            "              </repofilter>\n" +
            "              <input value=\"${entity.first_name}\" />\n" +
            "          </datalist>" +
            "</form>\n";

    @Test
    public void testDatalistitem() throws Exception {
        ctx = InstrumentationRegistry.getInstrumentation().getContext();
        ctx.setTheme(R.style.FormudruidLight);

        String xml = XmlConfigUtils.createMainList(XML_DATALIST_WITH_FILTER);

        FormConfig formConfig = XmlConfigUtils.readFormConfig(xml);

        UIView mainView = formConfig.getList().getView();

        List<UIDatalist> datalists = UIComponentHelper.getChildrenByClass(mainView, UIDatalist.class);
        Assert.assertNotNull(datalists);

        DAGManager dagManager = DAGManager.getInstance();
        dagManager.flush();
        dagManager.generateDags(mainView);
        ViewDAG viewDAG = dagManager.getViewDAG(mainView.getId());

        UIDatalist datalist = datalists.get(0);
        Assert.assertNotNull(datalist.getId());

        ActionController mcAC = mock(ActionController.class);
        RenderingEnv env = RenderingEnvFactory.getInstance().create(mcAC);
        env.setGlobalContext(ContextTestUtils.createGlobalContext());
        env.setAndroidContext(ctx);
        env.setViewDAG(viewDAG);

        // walk the tree executing expressions
        Widget viewWidget = renderHelper.render(env, mainView);
        Assert.assertNotNull(viewWidget);
    }

    private static final String XML_INPUT = "<text  id=\"mitext1\" label=\"input\"  value=\"${entity.first_name}\" render=\"${view.noneExistingElement}\"/>";

    /**
     * Define a render expression that depends on a none existing element and check the renderer
     * doesn't keep looking for the missing.
     *
     * @throws Exception
     */
    @Test(expected = ViewConfigException.class)
    public void testRenderLoop() throws Exception {
        CompositeContext globalContext = App.getInstance().getGlobalContext();

        // read XML config
        String xml = XmlConfigUtils.createEditForm(XML_INPUT);
        FormConfig formConfig = XmlConfigUtils.readFormConfig(xml);
        ViewController viewController = formConfig.getEdits().get(0);
        DagTestUtils.registerDags(formConfig);

        // mock main controller
        MainControllerMock mc = new MainControllerMock();
        mc.setContext(globalContext);
        mc.setViewController(viewController); // set test viewController

        // Create Android context and navigate to form
        Context ctx = Robolectric.setupActivity(FormEditViewHandlerActivity.class);
        ctx.setTheme(R.style.FormudruidLight);
        DevFormNav formNav = new DevFormNav(ctx, mc);

        // navigate to test viewController

        formNav.nav("");

        // get a widget a put some data on the view
        DatalistWidget widget = formNav.getWidget("mitext1", DatalistWidget.class);

    }
}
