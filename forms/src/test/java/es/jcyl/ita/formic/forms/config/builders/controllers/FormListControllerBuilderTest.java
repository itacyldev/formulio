package es.jcyl.ita.formic.forms.config.builders.controllers;
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

import org.junit.AfterClass;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.RobolectricTestRunner;

import es.jcyl.ita.formic.forms.components.UIComponent;
import es.jcyl.ita.formic.forms.components.UIComponentHelper;
import es.jcyl.ita.formic.forms.components.buttonbar.UIButtonBar;
import es.jcyl.ita.formic.forms.components.datatable.UIDatatable;
import es.jcyl.ita.formic.forms.components.link.UIButton;
import es.jcyl.ita.formic.forms.components.view.UIView;
import es.jcyl.ita.formic.forms.config.Config;
import es.jcyl.ita.formic.forms.config.ConfigConverters;
import es.jcyl.ita.formic.forms.config.elements.FormConfig;
import es.jcyl.ita.formic.forms.controllers.FormListController;
import es.jcyl.ita.formic.forms.controllers.UIAction;
import es.jcyl.ita.formic.forms.utils.RepositoryUtils;
import es.jcyl.ita.formic.forms.utils.XmlConfigUtils;


/**
 * @author Javier Ramos (javier.ramos@itacyl.es)
 * <p>
 * Tests to check commons-converters functionallity
 */
@RunWith(RobolectricTestRunner.class)
public class FormListControllerBuilderTest {

    @BeforeClass
    public static void setUp() {
        Config.init("");
        ConfigConverters confConverter = new ConfigConverters();
        confConverter.init();
        // register repos
        RepositoryUtils.registerMock("contacts");
    }

    @Test
    public void testDefaultListTag() throws Exception {
        String xml = XmlConfigUtils.createMainList("");
        FormConfig formConfig = XmlConfigUtils.readFormConfig(xml);
        FormListController listController = formConfig.getList();
        Assert.assertNotNull(listController);
        UIAction[] actions = listController.getActions();
        Assert.assertNull(actions);
        // check default fabButton
        UIView view = listController.getView();
        UIButtonBar fabBar = view.getFabBar();
        Assert.assertNotNull(fabBar);

        UIComponent[] children = fabBar.getChildren();
        Assert.assertNotNull(children);
        Assert.assertEquals(1, children.length);
        UIButton navButton = (UIButton) children[0];
        Assert.assertEquals("nav", navButton.getAction().getType());

        // find datatable and check route att
        UIDatatable datatable = UIComponentHelper.findFirstByClass(view, UIDatatable.class);
        Assert.assertNotNull(datatable.getRoute());
        Assert.assertNotNull(datatable.getAction().getRoute());
    }

    private static final String XML_FAB = "<buttonbar type=\"fab\">" +
            "  <button action=\"nav\" route=\"route1\"/>" +
            "</buttonbar>";

    @Test
    public void testCustomActionInFabButtonBar() throws Exception {
        String xml = XmlConfigUtils.createMainList(XML_FAB);
        FormConfig formConfig = XmlConfigUtils.readFormConfig(xml);
        FormListController listController = formConfig.getList();
        Assert.assertNotNull(listController);
        UIAction[] actions = listController.getActions();
        Assert.assertNull(actions);
        // check default fabButton
        UIView view = listController.getView();
        UIButtonBar fabBar = view.getFabBar();
        Assert.assertNotNull(fabBar);

        UIComponent[] children = fabBar.getChildren();
        Assert.assertNotNull(children);
        Assert.assertEquals(1, children.length);
        UIButton navButton = (UIButton) children[0];
        Assert.assertEquals("nav", navButton.getAction().getType());
        // find datatable and check route att
        UIDatatable datatable = UIComponentHelper.findFirstByClass(view, UIDatatable.class);
        Assert.assertEquals("route1", datatable.getRoute());
        Assert.assertEquals("route1", datatable.getAction().getRoute());
    }

    @AfterClass
    public static void tearDown() {
        RepositoryUtils.unregisterMock("contacts");
    }

}
