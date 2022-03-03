package es.jcyl.ita.formic.forms.config.builders.ui;
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

import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.Robolectric;
import org.robolectric.RobolectricTestRunner;

import java.util.List;
import java.util.Set;

import es.jcyl.ita.formic.core.context.CompositeContext;
import es.jcyl.ita.formic.forms.App;
import es.jcyl.ita.formic.forms.MainControllerMock;
import es.jcyl.ita.formic.forms.R;
import es.jcyl.ita.formic.forms.components.UIComponentHelper;
import es.jcyl.ita.formic.forms.components.inputfield.UIField;
import es.jcyl.ita.formic.forms.config.ConfigConverters;
import es.jcyl.ita.formic.forms.config.elements.FormConfig;
import es.jcyl.ita.formic.forms.controllers.ViewController;
import es.jcyl.ita.formic.forms.el.ValueBindingExpression;
import es.jcyl.ita.formic.forms.utils.DagTestUtils;
import es.jcyl.ita.formic.forms.utils.DevFormNav;
import es.jcyl.ita.formic.forms.utils.RepositoryUtils;
import es.jcyl.ita.formic.forms.utils.XmlConfigUtils;
import es.jcyl.ita.formic.forms.view.activities.FormEditViewHandlerActivity;
import es.jcyl.ita.formic.repo.Entity;
import es.jcyl.ita.formic.repo.Repository;
import es.jcyl.ita.formic.repo.builders.DevDbBuilder;

import static org.mockito.Mockito.*;

/**
 * Checks UIField creation from xml with expressions that will generate a proxified version
 * of the uicomponents.
 *
 * @author Gustavo Río (gustavo.rio@itacyl.es)
 */
@RunWith(RobolectricTestRunner.class)
public class ComponentProxyBuilderIntegrationTest {

    private static Context ctx;
    private static Repository contacts;

    @BeforeClass
    public static void beforeClass() {
        App.init("");
        ConfigConverters confConverter = new ConfigConverters();
        confConverter.init();
        // register repos
        contacts = RepositoryUtils.registerMock("contacts");
    }


    private static final String XML_TEST_BASIC = "<textarea id=\"myId\" " +
            "value =\"${entity.property1}\"" +
            "lines=\"${5}\"" +
            "pattern=\"${device.localTime}\"" +
            "label=\"${entity.property}\"" +
            "hint=\"${entity.property==1}\"/>";

    @Test
    public void testExpressionGeneration() throws Exception {

        String xml = XmlConfigUtils.createMainList(XML_TEST_BASIC);

        FormConfig formConfig = XmlConfigUtils.readFormConfig(xml);
        UIField field = UIComponentHelper.getChildrenByClass(formConfig.getList().getView(), UIField.class).get(0);
        Set<ValueBindingExpression> expressions = field.getValueBindingExpressions();

        Assert.assertNotNull(expressions);
        Assert.assertEquals(5, expressions.size());
    }


    private static final String XML_INPUT = "<textarea id=\"myId\" " +
            "value =\"${entity.last_name}\"" +
            "lines=\"${entity.it_profile}\"" +
            "label=\"Type your name Mr: ${entity.first_name}\"" +
            "hint=\"Is youR name: ${entity.first_name}?\"/>";

    @Test
    public void testRenderProxyComponent() throws Exception {
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

        // add entities to the repo mock
        List<Entity> entities = DevDbBuilder.buildEntities(contacts.getMeta(), 5);
        when(contacts.find(any())).thenReturn(entities);

        // Create Android context and navigate to form
        Context ctx = Robolectric.setupActivity(FormEditViewHandlerActivity.class);
        ctx.setTheme(R.style.FormudruidLight);
        DevFormNav formNav = new DevFormNav(ctx, mc);

        // navigate to test viewController
        formNav.nav("");


        // assert something about the widget....
    }

}
