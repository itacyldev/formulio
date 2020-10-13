package es.jcyl.ita.formic.repo.config.builders;
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

import es.jcyl.ita.crtrepo.builders.DevDbBuilder;
import es.jcyl.ita.crtrepo.builders.EntityMetaDataBuilder;
import es.jcyl.ita.crtrepo.meta.EntityMeta;
import es.jcyl.ita.formic.repo.config.Config;
import es.jcyl.ita.formic.repo.config.ConfigConverters;
import es.jcyl.ita.formic.repo.config.FormConfig;
import es.jcyl.ita.formic.forms.components.UIComponentHelper;
import es.jcyl.ita.formic.forms.components.column.UIColumn;
import es.jcyl.ita.formic.forms.components.datatable.UIDatatable;
import es.jcyl.ita.formic.forms.utils.RepositoryUtils;
import es.jcyl.ita.formic.forms.utils.XmlConfigUtils;

/**
 * @author Gustavo Río (gustavo.rio@itacyl.es)
 * <p>
 * Tests to check commons-converters functionallity
 */
@RunWith(RobolectricTestRunner.class)
public class UIDatatableBuilderTest {

    @BeforeClass
    public static void setUp() {
        Config.init("");
        ConfigConverters confConverter = new ConfigConverters();
        confConverter.init();
        // register repos
        RepositoryUtils.registerMock("contacts");
    }


    private static final String XML_TEST_BASIC = "<datatable/>";

    @Test
    public void testBasicDatatable() throws Exception {
        String xml = XmlConfigUtils.createMainList(XML_TEST_BASIC);

        FormConfig formConfig = XmlConfigUtils.readFormConfig(xml);
        List<UIDatatable> datables = UIComponentHelper.findByClass(formConfig.getList().getView(), UIDatatable.class);
        Assert.assertNotNull(datables);

        // repo must be set with parent value "contacts"
        UIDatatable datatable = datables.get(0);
        Assert.assertNotNull(datatable.getId());
        Assert.assertNotNull(datatable.getRoute());
        Assert.assertEquals(formConfig.getRepo(), datatable.getRepo());
    }

    private static final String XML_TEST_REPO = "<datatable id=\"mydatatable\" route=\"aRouteToForm\" repo=\"contacts2\"/>";

    @Test
    public void tesDatatableAttributes() throws Exception {
        RepositoryUtils.registerMock("contacts2");

        String xml = XmlConfigUtils.createMainList(XML_TEST_REPO);

        FormConfig formConfig = XmlConfigUtils.readFormConfig(xml);
        List<UIDatatable> datables = UIComponentHelper.findByClass(formConfig.getList().getView(), UIDatatable.class);

        // repo must be set with parent value "contacts"
        UIDatatable datatable = (UIDatatable) datables.get(0);

        Assert.assertEquals("mydatatable", datatable.getId());
        Assert.assertEquals("aRouteToForm", datatable.getRoute());
        Assert.assertEquals(RepositoryUtils.getRepo("contacts2"), datatable.getRepo());
    }

    /**
     * Create table with default columns and check there's and element UIColumn per each
     * repository property
     *
     * @throws Exception
     */
    private static final String XML_TEST_DEFAULT_COLS = "<datatable repo=\"defColsRepo\"/>";

    @Test
    public void testDefaultColumns() throws Exception {
        String xml = XmlConfigUtils.createMainList(XML_TEST_DEFAULT_COLS);
        // register repository mock with a random meta
        EntityMeta meta = DevDbBuilder.createRandomMeta();
        RepositoryUtils.registerMock("defColsRepo", meta);

        FormConfig formConfig = XmlConfigUtils.readFormConfig(xml);
        List<UIDatatable> datables = UIComponentHelper.findByClass(formConfig.getList().getView(), UIDatatable.class);
        UIDatatable datatable = datables.get(0);

        // check columns
        Assert.assertEquals(meta.getProperties().length, datatable.getColumns().length);
        String[] propNames = meta.getPropertyNames();
        boolean found;
        for (String name : propNames) {
            found = false;
            for (UIColumn col : datatable.getColumns()) {
                if (col.getId().equals(name)) {
                    found = true;
                    break;
                }
            }
            if (!found) {
                Assert.fail(String.format("Expected UIColumn with id [%s] for " +
                        "property [%s] but not found.", name, meta.getPropertyByName(name)));
            }
        }
    }

    /**
     * Tests the evaluation of "properties" attribute to filter the properties selected from repository.
     * Two columns are selected from the repo and one additional column is manually configured.
     *
     * @throws Exception
     */
    private static final String XML_TEST_PROP_ATT = "<datatable repo=\"defColsRepo\" properties=\"col1,col3\">" +
            "<column id=\"mycol\"/>" +
            "</datatable>";

    @Test
    public void testPropertiesAttribute() throws Exception {
        String xml = XmlConfigUtils.createMainList(XML_TEST_PROP_ATT);

        // set properties col1 y col2
        EntityMetaDataBuilder metaBuilder = new EntityMetaDataBuilder();
        EntityMeta meta = metaBuilder.withNumProps(1)
                .addProperties(new String[]{"col1", "col2", "col3"},
                        new Class[]{String.class, String.class, String.class})
                .build();

        RepositoryUtils.registerMock("defColsRepo", meta);

        FormConfig formConfig = XmlConfigUtils.readFormConfig(xml);
        List<UIDatatable> datables = UIComponentHelper.findByClass(formConfig.getList().getView(), UIDatatable.class);
        UIDatatable datatable = datables.get(0);

        // check columns
        String[] expectedColsIds = new String[]{"col1", "col3", "mycol"};

        Assert.assertEquals(expectedColsIds.length, datatable.getColumns().length);
        boolean found;
        for (String name : expectedColsIds) {
            found = false;
            for (UIColumn col : datatable.getColumns()) {
                if (col.getId().equals(name)) {
                    found = true;
                    break;
                }
            }
            if (!found) {
                Assert.fail(String.format("Expected UIColumn with id [%s] for " +
                        "property [%s] but not found.", name, meta.getPropertyByName(name)));
            }
        }
    }

    @AfterClass
    public static void tearDown() {
        RepositoryUtils.unregisterMock("contacts");
        RepositoryUtils.unregisterMock("contacts2");
    }

}
