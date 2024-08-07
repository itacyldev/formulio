package es.jcyl.ita.formic.forms.config.builders.ui;
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

import es.jcyl.ita.formic.forms.App;
import es.jcyl.ita.formic.repo.Repository;
import es.jcyl.ita.formic.repo.builders.EntityMetaDataBuilder;
import es.jcyl.ita.formic.repo.db.meta.DBPropertyType;
import es.jcyl.ita.formic.repo.meta.EntityMeta;
import es.jcyl.ita.formic.forms.config.ConfigConverters;
import es.jcyl.ita.formic.forms.config.elements.FormConfig;
import es.jcyl.ita.formic.forms.components.UIComponentHelper;
import es.jcyl.ita.formic.forms.components.column.UIColumn;
import es.jcyl.ita.formic.forms.components.column.UIColumnFilter;
import es.jcyl.ita.formic.forms.components.datatable.UIDatatable;
import es.jcyl.ita.formic.forms.utils.RepositoryUtils;
import es.jcyl.ita.formic.forms.utils.XmlConfigUtils;

/**
 * @author Javier Ramos(javier.ramos@itacyl.es)
 * <p>
 * Tests to check commons-converters functionallity
 */
@RunWith(RobolectricTestRunner.class)
public class UIColumnBuilderTest {

    @BeforeClass
    public static void setUp() {
        App.init("");
        ConfigConverters confConverter = new ConfigConverters();
        confConverter.init();
        // register repos
        RepositoryUtils.registerMock("contacts");
    }


    private static final String XML_TEST_BASIC = "<datatable>" + "  <column/>" + "</datatable>";

    @Test
    public void testBasicColumn() throws Exception {
        String xml = XmlConfigUtils.createMainList(XML_TEST_BASIC);

        FormConfig formConfig = XmlConfigUtils.readFormConfig(xml);
        List<UIDatatable> datatables =
                UIComponentHelper.getChildrenByClass(formConfig.getList().getView(),
                        UIDatatable.class);
        UIDatatable datatable = datatables.get(0);
        UIColumn[] columns = datatable.getColumns();

        Assert.assertNotNull(columns);
        Assert.assertEquals(1, columns.length);
        UIColumn column = columns[0];
        Assert.assertNotNull(column.getId());
    }

    private static final String XML_TEST_COLUMN_WITH_ATTS = "<datatable>" +
            "  <column filtering=\"true\" ordering=\"true\"/>" +
            "</datatable>";

    @Test
    public void setXmlTestColumnWithAtts() throws Exception {
        String xml = XmlConfigUtils.createMainList(XML_TEST_COLUMN_WITH_ATTS);

        FormConfig formConfig = XmlConfigUtils.readFormConfig(xml);
        List<UIDatatable> datatables =
                UIComponentHelper.getChildrenByClass(formConfig.getList().getView(),
                        UIDatatable.class);
        UIDatatable datatable = datatables.get(0);
        UIColumn[] columns = datatable.getColumns();

        Assert.assertNotNull(columns);
        Assert.assertEquals(1, columns.length);
        UIColumn column = columns[0];
        Assert.assertNotNull(column.getId());
        Assert.assertEquals(true, column.isFiltering());
        Assert.assertEquals(true, column.isOrdering());
    }

    private static final String XML_TEST_COLUMN_WITH_FILTER = "<form repo=\"otherRepo\">" +
            "  <datatable>" +
            "    <column id=\"col1\">" +
            "      <filter property=\"prop1\" matching=\"eq\" valueExpression=\"${this.col1.substring(0,1)}\"/>" +
            "    </column>" +
            "  </datatable>" +
            "</form>";

    @Test
    public void setXmlTestColumnWithFilter() {
        EntityMetaDataBuilder metaBuilder = new EntityMetaDataBuilder();
        EntityMeta<DBPropertyType> meta = metaBuilder.withNumProps(0)
                .addProperties(new String[]{"prop1", "prop2"},
                        new Class[]{String.class, String.class, String.class})
                .build();
        Repository otherRepo = RepositoryUtils.registerMock("otherRepo", meta);


        String xml = XmlConfigUtils.createMainList(XML_TEST_COLUMN_WITH_FILTER);

        FormConfig formConfig = XmlConfigUtils.readFormConfig(xml);
        List<UIDatatable> datatables =
                UIComponentHelper.getChildrenByClass(formConfig.getList().getView(),
                        UIDatatable.class);
        UIDatatable datatable = datatables.get(0);
        UIColumn[] columns = datatable.getColumns();

        Assert.assertEquals(1, columns.length);
        UIColumn column = columns[0];
        Assert.assertNotNull(column.getId());
        UIColumnFilter filter = column.getHeaderFilter();
        Assert.assertEquals("prop1", filter.getFilterProperty());
        Assert.assertEquals("EQ", filter.getMatchingOperator().toString());
        Assert.assertEquals("${this.col1.substring(0,1)}", filter.getFilterValueExpression().getExpression().asString());

    }

    private static final String XML_TEST_COLUMN_WITH_ORDERING = "<form repo=\"otherRepo\">" +
            "  <datatable>" +
            "    <column id=\"col1\">" +
            "      <order property=\"prop1\"/>" +
            "    </column>" +
            "  </datatable>" +
            "</form>";

    @Test
    public void setXmlTestColumnWithOrdering() {
        EntityMetaDataBuilder metaBuilder = new EntityMetaDataBuilder();
        EntityMeta<DBPropertyType> meta = metaBuilder.withNumProps(0)
                .addProperties(new String[]{"prop1", "prop2"},
                        new Class[]{String.class, String.class, String.class})
                .build();
        Repository otherRepo = RepositoryUtils.registerMock("otherRepo", meta);


        String xml = XmlConfigUtils.createMainList(XML_TEST_COLUMN_WITH_ORDERING);

        FormConfig formConfig = XmlConfigUtils.readFormConfig(xml);
        List<UIDatatable> datatables =
                UIComponentHelper.getChildrenByClass(formConfig.getList().getView(),
                        UIDatatable.class);
        UIDatatable datatable = datatables.get(0);
        UIColumn[] columns = datatable.getColumns();

        Assert.assertEquals(1, columns.length);
        UIColumn column = columns[0];
        Assert.assertNotNull(column.getId());
        UIColumnFilter filter = column.getHeaderFilter();
        Assert.assertEquals("prop1", filter.getOrderProperty());

    }


    @AfterClass
    public static void tearDown() {
        RepositoryUtils.unregisterMock("contacts");
    }

}
