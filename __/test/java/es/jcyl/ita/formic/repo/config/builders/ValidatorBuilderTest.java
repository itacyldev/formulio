package es.jcyl.ita.formic.repo.config.builders;
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

import org.apache.commons.validator.routines.EmailValidator;
import org.junit.AfterClass;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.RobolectricTestRunner;

import java.util.List;

import es.jcyl.ita.crtrepo.Repository;
import es.jcyl.ita.crtrepo.builders.EntityMetaDataBuilder;
import es.jcyl.ita.crtrepo.db.meta.DBPropertyType;
import es.jcyl.ita.crtrepo.meta.EntityMeta;
import es.jcyl.ita.formic.repo.config.Config;
import es.jcyl.ita.formic.repo.config.ConfigConverters;
import es.jcyl.ita.formic.repo.config.FormConfig;
import es.jcyl.ita.formic.forms.controllers.FormEditController;
import es.jcyl.ita.formic.forms.components.UIComponentHelper;
import es.jcyl.ita.formic.forms.components.UIInputComponent;
import es.jcyl.ita.formic.forms.components.form.UIForm;
import es.jcyl.ita.formic.forms.utils.RepositoryUtils;
import es.jcyl.ita.formic.forms.utils.XmlConfigUtils;
import es.jcyl.ita.formic.forms.validation.CommonsValidatorWrapper;
import es.jcyl.ita.formic.forms.validation.RequiredValidator;
import es.jcyl.ita.formic.forms.validation.Validator;

/**
 * @author Javier Ramos (javier.ramos@itacyl.es)
 * <p>
 * Tests to check commons-converters functionallity
 */
@RunWith(RobolectricTestRunner.class)
public class ValidatorBuilderTest {

    @BeforeClass
    public static void setUp() {
        Config.init("");
        ConfigConverters confConverter = new ConfigConverters();
        confConverter.init();
        // register repos
        RepositoryUtils.registerMock("contacts");
    }


    /**
     * Tests the creation of validators with tag validator
     *
     * @throws Exception
     */
    private static final String XML_TEST_REQUIRED_VALIDATOR = "<form repo=\"otherRepo\">" +
            "  <text id=\"prop1\">" +
            "    <validator type=\"required\"/>" +
            "  </text>" +
            "</form>";

    @Test
    public void testTagValidator() throws Exception {
        EntityMetaDataBuilder metaBuilder = new EntityMetaDataBuilder();
        EntityMeta<DBPropertyType> meta = metaBuilder.withNumProps(0)
                .addProperties(new String[]{"prop1"},
                        new Class[]{String.class})
                .build();
        Repository otherRepo = RepositoryUtils.registerMock("otherRepo", meta);

        String xml = XmlConfigUtils.createMainEdit(XML_TEST_REQUIRED_VALIDATOR);
        FormConfig formConfig = XmlConfigUtils.readFormConfig(xml);

        FormEditController editCtl = formConfig.getEdits().get(0);
        List<UIForm> forms = UIComponentHelper.findByClass(editCtl.getView(), UIForm.class);


        List<UIInputComponent> fields = forms.get(0).getFields();

        UIInputComponent field = fields.get(0);
        Assert.assertEquals(1, field.getValidators().length);

        Validator validator = field.getValidators()[0];
        Assert.assertEquals(RequiredValidator.class, validator.getClass());
    }

    /**
     * Tests the creation of validators using validator attribute
     *
     * @throws Exception
     */
    private static final String XML_TEST_ATTRIBUTE_VALIDATOR = "<form repo=\"otherRepo\">" +
            "  <text id=\"prop1\" validator=\"required,email\" />" +
            "</form>";

    @Test
    public void testAttributeValidator() throws Exception {
        EntityMetaDataBuilder metaBuilder = new EntityMetaDataBuilder();
        EntityMeta<DBPropertyType> meta = metaBuilder.withNumProps(0)
                .addProperties(new String[]{"prop1"},
                        new Class[]{String.class})
                .build();
        Repository otherRepo = RepositoryUtils.registerMock("otherRepo", meta);

        String xml = XmlConfigUtils.createMainEdit(XML_TEST_ATTRIBUTE_VALIDATOR);
        FormConfig formConfig = XmlConfigUtils.readFormConfig(xml);

        FormEditController editCtl = formConfig.getEdits().get(0);
        List<UIForm> forms = UIComponentHelper.findByClass(editCtl.getView(), UIForm.class);


        List<UIInputComponent> fields = forms.get(0).getFields();

        UIInputComponent field = fields.get(0);
        Assert.assertEquals(2, field.getValidators().length);

        Validator validator = field.getValidators()[0];
        Assert.assertEquals(RequiredValidator.class, validator.getClass());

        CommonsValidatorWrapper validator2 = (CommonsValidatorWrapper) field.getValidators()[1];
        Assert.assertEquals(EmailValidator.class, validator2.getDelegateClass());
    }

    /**
     *
     * @throws Exception
     */
    private static final String XML_TEST_ATTRIBUTE_VOID_VALIDATOR = "<form repo=\"otherRepo\">" +
            "  <text id=\"prop1\" validator=\"\" />" +
            "</form>";

    @Test
    public void testAttributeVoidValidator() throws Exception {
        EntityMetaDataBuilder metaBuilder = new EntityMetaDataBuilder();
        EntityMeta<DBPropertyType> meta = metaBuilder.withNumProps(0)
                .addProperties(new String[]{"prop1"},
                        new Class[]{String.class})
                .build();
        Repository otherRepo = RepositoryUtils.registerMock("otherRepo", meta);

        String xml = XmlConfigUtils.createMainEdit(XML_TEST_ATTRIBUTE_VOID_VALIDATOR);
        FormConfig formConfig = XmlConfigUtils.readFormConfig(xml);

        FormEditController editCtl = formConfig.getEdits().get(0);
        List<UIForm> forms = UIComponentHelper.findByClass(editCtl.getView(), UIForm.class);


        List<UIInputComponent> fields = forms.get(0).getFields();

        UIInputComponent field = fields.get(0);
        Assert.assertEquals(0, field.getValidators().length);

    }

    /**
     * Tests that tag and attribute defined validator is not added twice
     *
     * @throws Exception
     */
    private static final String XML_TEST_TAG_ATTRIBUTE_VALIDATOR = "<form repo=\"otherRepo\">" +
            "  <text id=\"prop1\" validator=\"required\">" +
            "    <validator type=\"required\"/>" +
            "  </text>" +
            "</form>";

    @Test
    public void testTagAttributeValidator() throws Exception {
        EntityMetaDataBuilder metaBuilder = new EntityMetaDataBuilder();
        EntityMeta<DBPropertyType> meta = metaBuilder.withNumProps(0)
                .addProperties(new String[]{"prop1"},
                        new Class[]{String.class})
                .build();
        Repository otherRepo = RepositoryUtils.registerMock("otherRepo", meta);

        String xml = XmlConfigUtils.createMainEdit(XML_TEST_TAG_ATTRIBUTE_VALIDATOR);
        FormConfig formConfig = XmlConfigUtils.readFormConfig(xml);

        FormEditController editCtl = formConfig.getEdits().get(0);
        List<UIForm> forms = UIComponentHelper.findByClass(editCtl.getView(), UIForm.class);

        List<UIInputComponent> fields = forms.get(0).getFields();

        UIInputComponent field = fields.get(0);
        Assert.assertEquals(1, field.getValidators().length);
    }


    /**
     * Tests that tag and attribute defined validator is not added twice
     *
     * @throws Exception
     */
    private static final String XML_TEST_VALIDATOR_WITH_PARAMS = "<form repo=\"otherRepo\">" +
            "  <text id=\"prop1\">" +
            "    <validator type=\"regex\">" +
            "      <param name=\"pattern\">[A-Z0-9]{5}</param>" +
            "      <param name=\"caseSensitive\">true</param>" +
            "    </validator>" +
            "  </text>" +
            "</form>";

    @Test
    public void testValidatorWithParams() throws Exception {
        EntityMetaDataBuilder metaBuilder = new EntityMetaDataBuilder();
        EntityMeta<DBPropertyType> meta = metaBuilder.withNumProps(0)
                .addProperties(new String[]{"prop1"},
                        new Class[]{String.class})
                .build();
        Repository otherRepo = RepositoryUtils.registerMock("otherRepo", meta);

        String xml = XmlConfigUtils.createMainEdit(XML_TEST_VALIDATOR_WITH_PARAMS);
        FormConfig formConfig = XmlConfigUtils.readFormConfig(xml);

        FormEditController editCtl = formConfig.getEdits().get(0);
        List<UIForm> forms = UIComponentHelper.findByClass(editCtl.getView(), UIForm.class);

        List<UIInputComponent> fields = forms.get(0).getFields();

        UIInputComponent field = fields.get(0);
        Assert.assertEquals(1, field.getValidators().length);
    }

    @AfterClass
    public static void tearDown() {
        RepositoryUtils.unregisterMock("contacts");
    }

}
