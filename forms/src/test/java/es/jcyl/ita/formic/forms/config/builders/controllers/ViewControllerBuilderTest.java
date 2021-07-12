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
import es.jcyl.ita.formic.forms.controllers.ViewController;
import es.jcyl.ita.formic.forms.utils.RepositoryUtils;
import es.jcyl.ita.formic.forms.utils.XmlConfigUtils;


/**
 * Tests creation of ViewController from <view/> tag in separate file.
 * <p>
 * @author Gustavo Río (gustavo.rio@itacyl.es)
 */
@RunWith(RobolectricTestRunner.class)
public class ViewControllerBuilderTest {


    @BeforeClass
    public static void setUp() {
        Config.init("");
        ConfigConverters confConverter = new ConfigConverters();
        confConverter.init();
        // register repos
        RepositoryUtils.registerMock("contacts");
    }

    private static final String EMPTY_VIEW = "<view/>";
    @Test
    public void testEmptyView() throws Exception {
        String xml = XmlConfigUtils.createMain(EMPTY_VIEW);
        FormConfig formConfig = XmlConfigUtils.readFormConfig(xml);
        Assert.assertNotNull(formConfig.getEdits());
        ViewController viewController = formConfig.getEdits().get(0);
        Assert.assertNotNull(viewController.getView());
    }

    private static final String MULTIPLE_VIEW = "<view/><view/><view/>";
    @Test
    public void testMultipleViewsInFile() throws Exception {
        String xml = XmlConfigUtils.createMain(MULTIPLE_VIEW);
        FormConfig formConfig = XmlConfigUtils.readFormConfig(xml);
        Assert.assertNotNull(formConfig.getEdits());
        Assert.assertEquals(3, formConfig.getEdits().size());
        for(ViewController viewController: formConfig.getEdits()){
            Assert.assertNotNull(viewController.getView());
            Assert.assertNotNull(viewController.getView().getId());
            Assert.assertNotNull(viewController.getId());
        }
    }

    @AfterClass
    public static void tearDown() {
        RepositoryUtils.unregisterMock("contacts");
    }

}
