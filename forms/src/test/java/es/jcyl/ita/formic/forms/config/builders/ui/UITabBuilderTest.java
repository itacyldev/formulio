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

import org.junit.AfterClass;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.RobolectricTestRunner;

import java.util.List;

import es.jcyl.ita.formic.forms.controllers.ViewController;
import es.jcyl.ita.formic.repo.Repository;
import es.jcyl.ita.formic.repo.builders.EntityMetaDataBuilder;
import es.jcyl.ita.formic.repo.db.meta.DBPropertyType;
import es.jcyl.ita.formic.repo.meta.EntityMeta;
import es.jcyl.ita.formic.forms.App;
import es.jcyl.ita.formic.forms.config.ConfigConverters;
import es.jcyl.ita.formic.forms.config.elements.FormConfig;
import es.jcyl.ita.formic.forms.components.UIComponentHelper;
import es.jcyl.ita.formic.forms.components.tab.UITab;
import es.jcyl.ita.formic.forms.components.tab.UITabItem;
import es.jcyl.ita.formic.forms.utils.RepositoryUtils;
import es.jcyl.ita.formic.forms.utils.XmlConfigUtils;

/**
 * @author Javier Ramos (javier.ramos@itacyl.es)
 * <p>
 * Tests to check commons-converters functionallity
 */
@RunWith(RobolectricTestRunner.class)
public class UITabBuilderTest {

    @BeforeClass
    public static void setUp() {
        App.init("");
        ConfigConverters confConverter = new ConfigConverters();
        confConverter.init();
        // register repos
        RepositoryUtils.registerMock("contacts");
    }


    /**
     * Tests the creation of tab and tabitems.
     * Creates a tab with two tabitems, each one containing one defined inputfield
     *
     * @throws Exception
     */
    private static final String XML_TEST_2_TABS = "<form repo=\"otherRepo\">" +
            "  <tab>" +
            "    <tabitem label=\"tabitem 1\">" +
            "      <text id=\"prop1\" label=\"mycustomlabel\"/>" +
            "    </tabitem>" +
            "    <tabitem label=\"tabitem 2\">" +
            "      <text id=\"prop2\" label=\"mycustomlabel2\"/>" +
            "    </tabitem>" +
            "  </tab>" +
            "</form>";

    @Test
    public void test2Tabs() throws Exception {
        EntityMetaDataBuilder metaBuilder = new EntityMetaDataBuilder();
        EntityMeta<DBPropertyType> meta = metaBuilder.withNumProps(0)
                .addProperties(new String[]{"prop1", "prop2"},
                        new Class[]{String.class, String.class})
                .build();
        Repository otherRepo = RepositoryUtils.registerMock("otherRepo", meta);

        String xml = XmlConfigUtils.createMainEdit(XML_TEST_2_TABS);
        FormConfig formConfig = XmlConfigUtils.readFormConfig(xml);

        ViewController editCtl = formConfig.getEdits().get(0);
        List<UITab> tabs = UIComponentHelper.getChildrenByClass(editCtl.getView(), UITab.class);
        Assert.assertNotNull(tabs);

        UITab tab = tabs.get(0);
        Assert.assertNotNull(tab.getId());

        // tab must have two tabItems
        Assert.assertEquals(2, tab.getChildren().length);

        // tabitem 1 must have one field
        UITabItem tabItem1 = (UITabItem) tab.getChildren()[0];
        Assert.assertNotNull(tabItem1);
        Assert.assertEquals(1, tabItem1.getChildren().length);

        // tabitem 2 must have one field
        UITabItem tabItem2 = (UITabItem) tab.getChildren()[1];
        Assert.assertNotNull(tabItem2);
        Assert.assertEquals(1, tabItem2.getChildren().length);


    }

    /**
     * Tests the automatic creation of inputFields using properties attribute and the merge with
     * already defined properties.
     * Creates a tab with two tabitems:
     * - First one with two fields, one explicitly declared and the
     * other one through the properties attribute.
     * - Second tabitem with one field because the same field has been declared twice
     *
     * @throws Exception
     */
    private static final String XML_TEST_TABS_PROPERTIES = "<form repo=\"otherRepo\">" +
            "  <tab>" +
            "    <tabitem label=\"tabitem 1\" properties=\"prop1\">" +
            "      <text id=\"prop3\" label=\"mycustomlabel\"/>" +
            "    </tabitem>" +
            "    <tabitem label=\"tabitem 2\" properties=\"prop2\">" +
            "      <text id=\"prop2\" label=\"mycustomlabel2\"/>" +
            "    </tabitem>" +
            "  </tab>" +
            "</form>";

    @Test
    public void testTabsWithProperties() throws Exception {
        EntityMetaDataBuilder metaBuilder = new EntityMetaDataBuilder();
        EntityMeta<DBPropertyType> meta = metaBuilder.withNumProps(0)
                .addProperties(new String[]{"prop1", "prop2", "prop3"},
                        new Class[]{String.class, String.class, String.class, String.class, String.class})
                .build();
        Repository otherRepo = RepositoryUtils.registerMock("otherRepo", meta);

        String xml = XmlConfigUtils.createMainEdit(XML_TEST_TABS_PROPERTIES);
        FormConfig formConfig = XmlConfigUtils.readFormConfig(xml);

        ViewController editCtl = formConfig.getEdits().get(0);
        List<UITab> tabs = UIComponentHelper.getChildrenByClass(editCtl.getView(), UITab.class);
        Assert.assertNotNull(tabs);

        UITab tab = tabs.get(0);
        Assert.assertNotNull(tab.getId());

        // tabitem 1 must have two children, one explicitly declared and the other through the properties attribute
        UITabItem tabItem1 = (UITabItem) tab.getChildren()[0];
        Assert.assertNotNull(tabItem1);
        Assert.assertEquals(2, tabItem1.getChildren().length);

        // tabitem 2 must have one child
        UITabItem tabItem2 = (UITabItem) tab.getChildren()[1];
        Assert.assertNotNull(tabItem2);
        Assert.assertEquals(1, tabItem2.getChildren().length);
        // the child must be the one explicity declared
        Assert.assertEquals("mycustomlabel2", tabItem2.getChildren()[0].getLabel());
    }

    /**
     * Tests the automatic creation of all fields when properties = "all"
     *
     * @throws Exception
     */
    private static final String XML_TEST_TAB_ALL_PROPERTIES = "<form repo=\"otherRepo\">" +
            "  <tab>" +
            "    <tabitem label=\"tabitem 1\" properties=\"all\"/>" +
            "  </tab>" +
            "</form>";

    @Test
    public void testTabWithAllProperties() throws Exception {
        EntityMetaDataBuilder metaBuilder = new EntityMetaDataBuilder();
        EntityMeta<DBPropertyType> meta = metaBuilder.withNumProps(0)
                .addProperties(new String[]{"prop1", "prop2", "prop3", "prop4", "prop5"},
                        new Class[]{String.class, String.class, String.class, String.class, String.class})
                .build();
        Repository otherRepo = RepositoryUtils.registerMock("otherRepo", meta);

        String xml = XmlConfigUtils.createMainEdit(XML_TEST_TAB_ALL_PROPERTIES);
        FormConfig formConfig = XmlConfigUtils.readFormConfig(xml);

        ViewController editCtl = formConfig.getEdits().get(0);
        List<UITab> tabs = UIComponentHelper.getChildrenByClass(editCtl.getView(), UITab.class);
        Assert.assertNotNull(tabs);

        UITab tab = tabs.get(0);
        Assert.assertNotNull(tab.getId());

        // tabitem 1 must have all properties
        UITabItem tabItem1 = (UITabItem) tab.getChildren()[0];
        Assert.assertNotNull(tabItem1);
        Assert.assertEquals(5, tabItem1.getChildren().length);
    }

    /**
     * Tests the automatic creation of all fields when no properties are defined
     *
     * @throws Exception
     */
    private static final String XML_TEST_TAB_NO_PROPERTIES = "<form repo=\"otherRepo\">" +
            "  <tab>" +
            "    <tabitem label=\"tabitem 1\"/>" +
            "  </tab>" +
            "</form>";

    @Test
    public void testTabWithNoProperties() throws Exception {
        EntityMetaDataBuilder metaBuilder = new EntityMetaDataBuilder();
        EntityMeta<DBPropertyType> meta = metaBuilder.withNumProps(0)
                .addProperties(new String[]{"prop1", "prop2", "prop3", "prop4", "prop5"},
                        new Class[]{String.class, String.class, String.class, String.class, String.class})
                .build();
        Repository otherRepo = RepositoryUtils.registerMock("otherRepo", meta);

        String xml = XmlConfigUtils.createMainEdit(XML_TEST_TAB_NO_PROPERTIES);
        FormConfig formConfig = XmlConfigUtils.readFormConfig(xml);

        ViewController editCtl = formConfig.getEdits().get(0);
        List<UITab> tabs = UIComponentHelper.getChildrenByClass(editCtl.getView(), UITab.class);
        Assert.assertNotNull(tabs);

        UITab tab = tabs.get(0);
        Assert.assertNotNull(tab.getId());

        // tabitem 1 must have all properties
        UITabItem tabItem1 = (UITabItem) tab.getChildren()[0];
        Assert.assertNotNull(tabItem1);
        Assert.assertEquals(5, tabItem1.getChildren().length);
    }


    @AfterClass
    public static void tearDown() {
        RepositoryUtils.unregisterMock("contacts");
    }

}
