package es.jcyl.ita.frmdrd.config.builders;
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

import es.jcyl.ita.frmdrd.config.Config;
import es.jcyl.ita.frmdrd.config.ConfigConverters;
import es.jcyl.ita.frmdrd.config.FormConfig;
import es.jcyl.ita.frmdrd.ui.components.UIComponent;
import es.jcyl.ita.frmdrd.ui.components.UIComponentHelper;
import es.jcyl.ita.frmdrd.ui.components.datatable.UIDatatable;
import es.jcyl.ita.frmdrd.utils.RepositoryUtils;
import es.jcyl.ita.frmdrd.utils.XmlConfigUtils;

/**
 * @author Gustavo Río (gustavo.rio@itacyl.es)
 * <p>
 * Tests to check commons-converters functionallity
 */
@RunWith(RobolectricTestRunner.class)
public class ValueBindingAttResolverTest {


    @BeforeClass
    public static void setUp() {
        Config config = new Config("");
        config.init();
        ConfigConverters confConverter = new ConfigConverters();
        confConverter.init();
        // register repos
    }


    private static final String XML_TEST_BASIC = "<datatable />";
    @Test
    public void testBasicDatatable() throws Exception {
        RepositoryUtils.registerMock("contacts");

        String xml = XmlConfigUtils.createMainList(XML_TEST_BASIC);

        FormConfig formConfig = XmlConfigUtils.readFormConfig(xml);
        List<UIDatatable> datables = UIComponentHelper.findByClass(formConfig.getList().getView(), UIDatatable.class);
        Assert.assertNotNull(datables);

        // repo must be set with parent value "contacts"
        UIDatatable datatable = (UIDatatable) datables.get(0);
        Assert.assertNotNull(datatable.getId());
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



    @AfterClass
    public static void tearDown() {
        RepositoryUtils.unregisterMock("contacts");
        RepositoryUtils.unregisterMock("contacts2");
    }

}
