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

import es.jcyl.ita.formic.forms.App;
import es.jcyl.ita.formic.forms.controllers.ViewController;
import es.jcyl.ita.formic.repo.Repository;
import es.jcyl.ita.formic.repo.builders.EntityMetaDataBuilder;
import es.jcyl.ita.formic.repo.db.meta.DBPropertyType;
import es.jcyl.ita.formic.repo.meta.EntityMeta;
import es.jcyl.ita.formic.forms.config.ConfigConverters;
import es.jcyl.ita.formic.forms.config.elements.FormConfig;
import es.jcyl.ita.formic.forms.components.UIComponentHelper;
import es.jcyl.ita.formic.forms.components.UIInputComponent;
import es.jcyl.ita.formic.forms.components.form.UIForm;
import es.jcyl.ita.formic.forms.components.tab.UITab;
import es.jcyl.ita.formic.forms.utils.RepositoryUtils;
import es.jcyl.ita.formic.forms.utils.XmlConfigUtils;

/**
 * @author Gustavo Río (gustavo.rio@itacyl.es)
 * <p>
 * Tests to check commons-converters functionallity
 */
@RunWith(RobolectricTestRunner.class)
public class UIFormBuilderTest {

    @BeforeClass
    public static void setUp() {
        App.init("");
        ConfigConverters confConverter = new ConfigConverters();
        confConverter.init();
        // register repos
        RepositoryUtils.registerMock("contacts");
    }


    /**
     * Create empty form and check its created and repo attribute is set
     *
     * @throws Exception
     */
    private static final String XML_TEST_BASIC = "<form/>";

    @Test
    public void testBasicForm() throws Exception {
        String xml = XmlConfigUtils.createMainEdit(XML_TEST_BASIC);
        FormConfig formConfig = XmlConfigUtils.readFormConfig(xml);

        ViewController editCtl = formConfig.getEdits().get(0);
        List<UIForm> forms = UIComponentHelper.getChildrenByClass(editCtl.getView(), UIForm.class);
        Assert.assertNotNull(forms);
        Assert.assertTrue("One form is expected, found: " + forms.size(), forms.size() == 1);

        // repo must be set with parent value "contacts"
        UIForm form = forms.get(0);
        Assert.assertNotNull(form.getId());
        Assert.assertNotNull(form.getRepo());
        Assert.assertEquals(formConfig.getRepo(), form.getRepo());

        // the form must have as many fields as the repo meta
        int numProperties = form.getRepo().getMeta().getProperties().length;
        Assert.assertNotNull(form.getFields());
        Assert.assertEquals("Unexpected number of fields in form.", numProperties, form.getFields().size());
        // check all forms has rendering expressions
        for (UIInputComponent c : form.getFields()) {
            Assert.assertNotNull("Null renderExpression found in component: " + c.getId(), c.getValueExpression());
        }
    }

    private static final String XML_TEST_ATTS = "<form id=\"myFormId\" repo=\"otherRepo\" />";

    @Test
    public void testFormAttributes() throws Exception {
        Repository otherRepo = RepositoryUtils.registerMock("otherRepo");

        String xml = XmlConfigUtils.createMainEdit(XML_TEST_ATTS);
        FormConfig formConfig = XmlConfigUtils.readFormConfig(xml);

        ViewController editCtl = formConfig.getEdits().get(0);
        List<UIForm> forms = UIComponentHelper.getChildrenByClass(editCtl.getView(), UIForm.class);

        // repo must be set with parent value "contacts"
        UIForm form = forms.get(0);
        Assert.assertEquals(form.getId(), "myFormId");
        Assert.assertEquals(otherRepo, form.getRepo());
    }


    /**
     * Tests the automatic creation of inputFields using properties attribute and the merge with already defined properties.
     * Creates a meta with 5 properties. Two of then has to be included due to "properties" filter and another
     * two has been defined with tags. Prop3 must have he tag information.
     *
     * @throws Exception
     */
    private static final String XML_TEST_INPUTS = "<form repo=\"otherRepo\" properties=\"prop2, prop3, prop4\">" +
            "  <text id=\"prop1\"/>" +
            "  <text id=\"prop3\" label=\"mycustomlabel\"/>" +
            "  <text id=\"noPropField\"/>" +
            "</form>";

    @Test
    public void testNestedInputs() throws Exception {
        EntityMetaDataBuilder metaBuilder = new EntityMetaDataBuilder();
        EntityMeta<DBPropertyType> meta = metaBuilder.withNumProps(0)
                .addProperties(new String[]{"prop1", "prop2", "prop3", "prop4", "prop5"},
                        new Class[]{String.class, String.class, String.class, String.class, String.class})
                .build();
        Repository otherRepo = RepositoryUtils.registerMock("otherRepo", meta);

        String xml = XmlConfigUtils.createMainEdit(XML_TEST_INPUTS);
        FormConfig formConfig = XmlConfigUtils.readFormConfig(xml);

        ViewController editCtl = formConfig.getEdits().get(0);
        List<UIForm> forms = UIComponentHelper.getChildrenByClass(editCtl.getView(), UIForm.class);

        // repo must be set with parent value "contacts"
        UIForm form = forms.get(0);
        String[] expectedProperties = new String[]{"prop1", "prop2", "prop3", "prop4", "noPropField"};
        Assert.assertEquals(expectedProperties.length, form.getFields().size());
        for (String expected : expectedProperties) {
            boolean found = false;
            for (UIInputComponent field : form.getFields()) {
                if (field.getId().equals(expected)) {
                    found = true;
                    if (field.getId().equals("prop3")) {
                        // check definition matches the tag and the field has not been created automatically
                        Assert.assertEquals("mycustomlabel", field.getLabel());
                    }
                    break;
                }
            }
            if (!found) {
                Assert.fail("Not found filed with id: " + expected);
            }
        }
    }

    /**
     * Tests the automatic creation of inputFields using properties attribute and the merge with already defined properties.
     * Creates a meta with 5 properties. Two of then has to be included due to "properties" filter and another
     * two has been defined with tags. Prop3 must have he tag information.
     *
     * @throws Exception
     */
    private static final String XML_TEST_FORM_WITH_TABS_AND_FIELDS = "<form repo=\"otherRepo\" properties=\"prop4\">" +
            "  <tab>" +
            "    <tabitem label=\"tabitem 1\">" +
            "      <text id=\"prop1\" label=\"mycustomlabel\"/>" +
            "    </tabitem>" +
            "    <tabitem label=\"tabitem 2\">" +
            "      <text id=\"prop2\" label=\"mycustomlabel2\"/>" +
            "    </tabitem>" +
            "  </tab>" +
            "  <text id=\"prop3\"/>" +
            "</form>";

    @Test
    public void testFormWithTabsAndFields() throws Exception {
        EntityMetaDataBuilder metaBuilder = new EntityMetaDataBuilder();
        EntityMeta<DBPropertyType> meta = metaBuilder.withNumProps(0)
                .addProperties(new String[]{"prop1", "prop2", "prop3", "prop4"},
                        new Class[]{String.class, String.class, String.class, String.class, String.class})
                .build();
        Repository otherRepo = RepositoryUtils.registerMock("otherRepo", meta);

        String xml = XmlConfigUtils.createMainEdit(XML_TEST_FORM_WITH_TABS_AND_FIELDS);
        FormConfig formConfig = XmlConfigUtils.readFormConfig(xml);

        ViewController editCtl = formConfig.getEdits().get(0);
        List<UIForm> forms = UIComponentHelper.getChildrenByClass(editCtl.getView(), UIForm.class);


        UIForm form = forms.get(0);
        Assert.assertEquals(3, form.getChildren().length);
        Assert.assertTrue(form.getChildren()[0] instanceof UITab);

        Assert.assertEquals("prop3", form.getChildren()[1].getId());
        Assert.assertEquals("prop4", form.getChildren()[2].getId());
    }

    /**
     * Tests the automatic creation of inputFields using properties attribute and the merge with already defined properties.
     * Creates a meta with 5 properties. Two of then has to be included due to "properties" filter and another
     * two has been defined with tags. Prop3 must have he tag information.
     *
     * @throws Exception
     */
    private static final String XML_TEST_FORM_WITH_TAB = "<form repo=\"otherRepo\">" +
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
    public void testFormWithTab() throws Exception {
        EntityMetaDataBuilder metaBuilder = new EntityMetaDataBuilder();
        EntityMeta<DBPropertyType> meta = metaBuilder.withNumProps(0)
                .addProperties(new String[]{"prop1", "prop2", "prop3", "prop4"},
                        new Class[]{String.class, String.class, String.class, String.class, String.class})
                .build();
        Repository otherRepo = RepositoryUtils.registerMock("otherRepo", meta);

        String xml = XmlConfigUtils.createMainEdit(XML_TEST_FORM_WITH_TAB);
        FormConfig formConfig = XmlConfigUtils.readFormConfig(xml);

        ViewController editCtl = formConfig.getEdits().get(0);
        List<UIForm> forms = UIComponentHelper.getChildrenByClass(editCtl.getView(), UIForm.class);


        UIForm form = forms.get(0);
        Assert.assertEquals(1, form.getChildren().length);
        Assert.assertTrue(form.getChildren()[0] instanceof UITab);
    }


    @AfterClass
    public static void tearDown() {
        RepositoryUtils.unregisterMock("contacts");
    }

}
