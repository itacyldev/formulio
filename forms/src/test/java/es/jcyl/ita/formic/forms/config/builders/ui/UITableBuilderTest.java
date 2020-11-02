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

import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;

import java.util.List;

import es.jcyl.ita.formic.forms.components.UIComponentHelper;
import es.jcyl.ita.formic.forms.components.table.UIRow;
import es.jcyl.ita.formic.forms.components.table.UITable;
import es.jcyl.ita.formic.forms.config.Config;
import es.jcyl.ita.formic.forms.config.ConfigConverters;
import es.jcyl.ita.formic.forms.config.elements.FormConfig;
import es.jcyl.ita.formic.forms.controllers.FormEditController;
import es.jcyl.ita.formic.forms.utils.RepositoryUtils;
import es.jcyl.ita.formic.forms.utils.XmlConfigUtils;
import es.jcyl.ita.formic.repo.builders.EntityMetaDataBuilder;
import es.jcyl.ita.formic.repo.meta.EntityMeta;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;

/**
 * @author Gustavo Río (gustavo.rio@itacyl.es)
 */
public class UITableBuilderTest {


    @BeforeClass
    public static void setUp() {
        Config.init("");
        ConfigConverters confConverter = new ConfigConverters();
        confConverter.init();
        // register repos

        EntityMetaDataBuilder metaBuilder = new EntityMetaDataBuilder();
        metaBuilder.addProperties(new String[]{
                "email", "phone", "first_name", "last_name"
        }, new Class[]{
                String.class, String.class, String.class, String.class
        });
        EntityMeta meta = metaBuilder.build();
        RepositoryUtils.registerMock("contacts", meta);
    }

    private static final String XML_EMPTY_TABLE = "<table></table>";

    /**
     * Check no error will show when empty table is confgured
     *
     * @throws Exception
     */
    @Test
    public void testEmptyTable() throws Exception {
        String xml = XmlConfigUtils.createEditForm(XML_EMPTY_TABLE);
        FormConfig formConfig = XmlConfigUtils.readFormConfig(xml);

        FormEditController editCtl = formConfig.getEdits().get(0);
        List<UITable> tables = UIComponentHelper.findByClass(editCtl.getView(), UITable.class);
        Assert.assertNotNull(tables);
        Assert.assertTrue("One table is expected, found: " + tables.size(), tables.size() == 1);

        // repo must be set with parent value "contacts"
        UITable table = tables.get(0);
        Assert.assertNotNull(table.getId());
        assertThat(table.getChildren().length, equalTo(0));
    }

    private static final String XML_TWO_ROWS = "<table>" +
            "<row label=\"firstRow\">" +
            "<input id=\"input1\"/>" +
            "<input id=\"input2\"/>" +
            "</row>" +
            "<row label=\"secondRow\">" +
            "<input id=\"input3\"/>" +
            "<input id=\"input4\"/>" +
            "</row>" +
            "</table>";

    /**
     * Check no error will show when empty table is confgured
     *
     * @throws Exception
     */
    @Test
    public void testSimpleRows() throws Exception {
        String xml = XmlConfigUtils.createEditForm(XML_TWO_ROWS);
        FormConfig formConfig = XmlConfigUtils.readFormConfig(xml);

        FormEditController editCtl = formConfig.getEdits().get(0);
        List<UITable> tables = UIComponentHelper.findByClass(editCtl.getView(), UITable.class);

        // repo must be set with parent value "contacts"
        UITable table = tables.get(0);
        Assert.assertNotNull(table.getId());
        assertThat(table.getChildren().length, equalTo(2));
        UIRow firstRow = (UIRow) table.getChildren()[0];
        assertThat("firstRow", equalTo(firstRow.getLabel()));
        assertThat("input1", equalTo(firstRow.getChildren()[0].getId()));
        UIRow secondRow = (UIRow) table.getChildren()[1];
        assertThat("secondRow", equalTo(secondRow.getLabel()));
        assertThat("input3", equalTo(secondRow.getChildren()[0].getId()));
    }

    /**
     * Check no error will show when empty table is confgured
     *
     * @throws Exception
     */
    private static final String XML_ROW_PROPERTIES = "<table>" +
            "<row label=\"firstRow\" properties=\"first_name,last_name\">" +
            "<input id=\"input1\"/>" +
            "</row>" +
            "<row label=\"secondRow\" properties=\"email,phone\"/>" +
            "</table>";

    @Test
    public void testRowFromProperties() throws Exception {
        String xml = XmlConfigUtils.createEditForm(XML_ROW_PROPERTIES);
        FormConfig formConfig = XmlConfigUtils.readFormConfig(xml);

        FormEditController editCtl = formConfig.getEdits().get(0);
        List<UITable> tables = UIComponentHelper.findByClass(editCtl.getView(), UITable.class);

        // repo must be set with parent value "contacts"
        UITable table = tables.get(0);
        Assert.assertNotNull(table.getId());
        assertThat(table.getChildren().length, equalTo(2));
        UIRow firstRow = (UIRow) table.getChildren()[0];
        assertThat("firstRow", equalTo(firstRow.getLabel()));
        assertThat("input1", equalTo(firstRow.getChildren()[0].getId()));
        assertThat("first_name", equalTo(firstRow.getChildren()[1].getId()));
        assertThat("last_name", equalTo(firstRow.getChildren()[2].getId()));
        UIRow secondRow = (UIRow) table.getChildren()[1];
        assertThat("secondRow", equalTo(secondRow.getLabel()));
        assertThat("email", equalTo(secondRow.getChildren()[0].getId()));
        assertThat("phone", equalTo(secondRow.getChildren()[1].getId()));
    }
}
