package es.jcyl.ita.formic.forms.config.builders.repo;
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

import java.util.List;

import es.jcyl.ita.formic.forms.components.datalist.UIDatalist;
import es.jcyl.ita.formic.forms.controllers.ViewController;
import es.jcyl.ita.formic.repo.query.Criteria;
import es.jcyl.ita.formic.repo.query.Filter;
import es.jcyl.ita.formic.forms.config.Config;
import es.jcyl.ita.formic.forms.config.ConfigConverters;
import es.jcyl.ita.formic.forms.config.elements.FormConfig;
import es.jcyl.ita.formic.forms.controllers.FormEditController;
import es.jcyl.ita.formic.forms.components.UIComponentHelper;
import es.jcyl.ita.formic.forms.components.form.UIForm;
import es.jcyl.ita.formic.forms.utils.RepositoryUtils;
import es.jcyl.ita.formic.forms.utils.XmlConfigUtils;

/**
 * @author Javier Ramos (javier.ramos@itacyl.es)
 */
@RunWith(RobolectricTestRunner.class)
public class RepoFilterBuilderTest {

    @BeforeClass
    public static void setUp() {
        Config.init("");
        ConfigConverters confConverter = new ConfigConverters();
        confConverter.init();

        // register repos
        RepositoryUtils.registerMock("contacts");
    }


    private static final String XML_TEST_BASIC =
            "  <form repo=\"contacts\">" +
                    "  <repofilter>" +
                    "    <eq property=\"prop1\" value=\"field1\"/>" +
                    "    <eq property=\"prop2\" value=\"field2\" />" +
                    "  </repofilter>" +
                    "</form>";

    @Test
    public void testBasicRepoFilter() throws Exception {
        String xml = XmlConfigUtils.createMainEdit(XML_TEST_BASIC);

        FormConfig formConfig = XmlConfigUtils.readFormConfig(xml);
        ViewController editCtl = formConfig.getEdits().get(0);
        List<UIForm> formList = UIComponentHelper.getChildrenByClass(editCtl.getView(), UIForm.class);
        Assert.assertNotNull(formList);

        UIForm form = formList.get(0);
        Filter filter = form.getFilter();
        Assert.assertNotNull(filter);

        Criteria criteria = (Criteria) filter.getExpression();
        Assert.assertEquals(Criteria.CriteriaType.AND, criteria.getType());
        Assert.assertEquals(2, criteria.getChildren().length);
    }


    private static final String XML_DATA_LIST =
            "  <form repo=\"contacts\">" +
                    "  <datalist>" +
                    "  <repofilter>" +
                    "    <eq property=\"prop1\" value=\"field1\"/>" +
                    "    <eq property=\"prop2\" value=\"field2\" />" +
                    "  </repofilter>" +
                    "  </datalist>" +
                    "</form>";

    @Test
    public void testDataListRepoFilter() throws Exception {
        String xml = XmlConfigUtils.createMainEdit(XML_DATA_LIST);

        FormConfig formConfig = XmlConfigUtils.readFormConfig(xml);
        ViewController editCtl = formConfig.getEdits().get(0);
        List<UIDatalist> lst = UIComponentHelper.getChildrenByClass(editCtl.getView(), UIDatalist.class);
        Assert.assertNotNull(lst);

        UIDatalist dataList = lst.get(0);
        Filter filter = dataList.getFilter();
        Assert.assertNotNull(filter);

        Criteria criteria = (Criteria) filter.getExpression();
        Assert.assertEquals(Criteria.CriteriaType.AND, criteria.getType());
        Assert.assertEquals(2, criteria.getChildren().length);
    }

    private static final String XML_TEST_MANDATORY_FIELDS =
            "  <form repo=\"contacts\">" +
                    "  <repofilter>" +
                    "    <eq property=\"prop1\" value=\"field1\" mandatory=\"true\"/>" +
                    "    <eq property=\"prop2\" value=\"field2\"  mandatory=\"True\"/>" +
                    "  </repofilter>" +
                    "</form>";

    @Test
    public void testMandatoryRepoFilter() throws Exception {
        String xml = XmlConfigUtils.createMainEdit(XML_TEST_MANDATORY_FIELDS);

        FormConfig formConfig = XmlConfigUtils.readFormConfig(xml);
        ViewController editCtl = formConfig.getEdits().get(0);
        List<UIForm> formList = UIComponentHelper.getChildrenByClass(editCtl.getView(), UIForm.class);
        Assert.assertNotNull(formList);


        UIForm form = formList.get(0);
        String[] mandatoryFields = form.getMandatoryFilters();
        Assert.assertNotNull(mandatoryFields);

        Assert.assertEquals(2, mandatoryFields.length);
    }

    private static final String XML_TEST_NESTED_CRITERIA =
            "  <form repo=\"contacts\">" +
                    "  <repofilter>" +
                    "    <or>" +
                    "      <and>" +
                    "        <eq property=\"prop1\" value=\"field1\"/>" +
                    "        <eq property=\"prop2\" value=\"field2\"/>" +
                    "      </and>" +
                    "      <and>" +
                    "        <eq property=\"prop3\" value=\"field3\"/>" +
                    "        <eq property=\"prop4\" value=\"field4\"/>" +
                    "      </and>" +
                    "    </or>" +
                    "  </repofilter>" +
                    "</form>";

    @Test
    public void testNestedCriteriaRepoFilter() throws Exception {
        String xml = XmlConfigUtils.createMainEdit(XML_TEST_NESTED_CRITERIA);

        FormConfig formConfig = XmlConfigUtils.readFormConfig(xml);
        ViewController editCtl = formConfig.getEdits().get(0);
        List<UIForm> formList = UIComponentHelper.getChildrenByClass(editCtl.getView(), UIForm.class);
        Assert.assertNotNull(formList);

        UIForm form = formList.get(0);
        Filter filter = form.getFilter();
        Assert.assertNotNull(filter);

        Criteria criteria = (Criteria) ((Criteria) filter.getExpression()).getChildren()[0];
        Assert.assertEquals(Criteria.CriteriaType.OR, criteria.getType());
        Assert.assertEquals(2, criteria.getChildren().length);

        Assert.assertEquals(Criteria.CriteriaType.AND, ((Criteria) (criteria.getChildren()[0])).getType());
        Assert.assertEquals(Criteria.CriteriaType.AND, ((Criteria) (criteria.getChildren()[1])).getType());

        Assert.assertEquals(2, ((Criteria) (criteria.getChildren()[0])).getChildren().length);
        Assert.assertEquals(2, ((Criteria) (criteria.getChildren()[1])).getChildren().length);
    }

    @AfterClass
    public static void tearDown() {
        RepositoryUtils.unregisterMock("contacts");
    }

}
