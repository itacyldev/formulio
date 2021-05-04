package es.jcyl.ita.formic.forms.config.builders.ui;
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

import org.junit.AfterClass;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.RobolectricTestRunner;

import java.util.List;

import es.jcyl.ita.formic.forms.components.UIComponentHelper;
import es.jcyl.ita.formic.forms.components.UIComponent;
import es.jcyl.ita.formic.forms.components.datalist.UIDatalist;
import es.jcyl.ita.formic.forms.components.datalist.UIDatalistItem;
import es.jcyl.ita.formic.forms.config.Config;
import es.jcyl.ita.formic.forms.config.ConfigConverters;
import es.jcyl.ita.formic.forms.config.elements.FormConfig;
import es.jcyl.ita.formic.forms.utils.RepositoryUtils;
import es.jcyl.ita.formic.forms.utils.XmlConfigUtils;


/**
 * @author Javier Ramos (javier.ramos@itacyl.es)
 * <p>
 * Tests to check commons-converters functionallity
 */
@RunWith(RobolectricTestRunner.class)
public class UIDatalistBuilderTest {

    @BeforeClass
    public static void setUp() {
        Config.init("");
        ConfigConverters confConverter = new ConfigConverters();
        confConverter.init();
        // register repos
        RepositoryUtils.registerMock("contacts");
    }


    private static final String XML_TEST_BASIC = "<datalist/>";

    @Test
    public void testBasicDatalist() throws Exception {
        String xml = XmlConfigUtils.createMainList(XML_TEST_BASIC);

        FormConfig formConfig = XmlConfigUtils.readFormConfig(xml);
        List<UIDatalist> datalists = UIComponentHelper.findByClass(formConfig.getList().getView(), UIDatalist.class);
        Assert.assertNotNull(datalists);

        // repo must be set with parent value "contacts"
        UIDatalist datalist = datalists.get(0);
        Assert.assertNotNull(datalist.getId());
        Assert.assertEquals(formConfig.getRepo(), datalist.getRepo());
    }

    private static final String XML_TEST_REPO = "<datalist id=\"mydatalist\" repo=\"contacts2\"/>";

    @Test
    public void tesDatalistAttributes() throws Exception {
        RepositoryUtils.registerMock("contacts2");

        String xml = XmlConfigUtils.createMainList(XML_TEST_REPO);

        FormConfig formConfig = XmlConfigUtils.readFormConfig(xml);
        List<UIDatalist> datalists = UIComponentHelper.findByClass(formConfig.getList().getView(), UIDatalist.class);
        Assert.assertNotNull(datalists);

        // repo must be set with parent value "contacts"
        UIDatalist datalist = (UIDatalist) datalists.get(0);

        Assert.assertEquals("mydatalist", datalist.getId());
        Assert.assertEquals(RepositoryUtils.getRepo("contacts2"), datalist.getRepo());
    }

    private static final String XML_TEST_WITH_DATALISTITEM = "<datalist  id=\"mydatalist\" >"
            + "  <datalistitem id=\"my_datalistitem\">"
            + "  </datalistitem>"
            + "</datalist>";

    @Test
    public void testDatalistitem() throws Exception {
        String xml = XmlConfigUtils.createMainList(XML_TEST_WITH_DATALISTITEM);

        FormConfig formConfig = XmlConfigUtils.readFormConfig(xml);
        List<UIDatalist> datalists = UIComponentHelper.findByClass(formConfig.getList().getView(), UIDatalist.class);
        Assert.assertNotNull(datalists);

        UIDatalist datalist = datalists.get(0);
        Assert.assertNotNull(datalist.getId());

        UIComponent[] children = datalist.getChildren();
        Assert.assertEquals(1, children.length);
        UIDatalistItem item = (UIDatalistItem) children[0];
        Assert.assertEquals("my_datalistitem", item.getId());
    }


    private static final String XML_TEST_WITHOUT_DATALISTITEM = "<datalist  id=\"mydatalist\" >"
            + "  <input id=\"myId\" />"
            + "  <input id=\"myId2\" />"
            + "</datalist>";

    @Test
    public void testDatalistitemCreated() throws Exception {
        String xml = XmlConfigUtils.createMainList(XML_TEST_WITHOUT_DATALISTITEM);

        FormConfig formConfig = XmlConfigUtils.readFormConfig(xml);
        List<UIDatalist> datalists = UIComponentHelper.findByClass(formConfig.getList().getView(), UIDatalist.class);
        Assert.assertNotNull(datalists);

        UIDatalist datalist = datalists.get(0);
        Assert.assertNotNull(datalist.getId());

        UIComponent[] children = datalist.getChildren();
        Assert.assertEquals(1, children.length);
        UIDatalistItem item = (UIDatalistItem) children[0];
        Assert.assertNotNull(item.getId());
    }

    @AfterClass
    public static void tearDown() {
        RepositoryUtils.unregisterMock("contacts");
        RepositoryUtils.unregisterMock("contacts2");
    }

}
