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

import org.apache.commons.lang3.ArrayUtils;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.RobolectricTestRunner;

import java.util.List;

import es.jcyl.ita.formic.forms.components.UIComponentHelper;
import es.jcyl.ita.formic.forms.components.select.UISelect;
import es.jcyl.ita.formic.forms.config.Config;
import es.jcyl.ita.formic.forms.config.ConfigConverters;
import es.jcyl.ita.formic.forms.config.elements.FormConfig;
import es.jcyl.ita.formic.forms.controllers.FormEditController;
import es.jcyl.ita.formic.forms.utils.RepositoryUtils;
import es.jcyl.ita.formic.forms.utils.XmlConfigUtils;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.junit.Assert.assertThat;

/**
 * @author Gustavo Río (gustavo.rio@itacyl.es)
 * <p>
 * Tests to check commons-converters functionallity
 */
@RunWith(RobolectricTestRunner.class)
public class UIRadioBuilderTest {

    @BeforeClass
    public static void setUp() {
        Config.init("");
        ConfigConverters confConverter = new ConfigConverters();
        confConverter.init();
        // register repos
        // register repos
        RepositoryUtils.registerMock("contacts");
    }


    /**
     * Create empty form and check its created and repo attribute is set
     *
     * @throws Exception
     */
    private static final String XML_TEST_BASIC = "<radio orientation=\"horizontal\"/>";

    @Test
    public void testEmptyRadio() throws Exception {
        String xml = XmlConfigUtils.createEditForm(XML_TEST_BASIC);
        FormConfig formConfig = XmlConfigUtils.readFormConfig(xml);

        FormEditController editCtl = formConfig.getEdits().get(0);
        List<es.jcyl.ita.formic.forms.components.radio.UIRadio> lstRadio = UIComponentHelper.findByClass(editCtl.getView(), es.jcyl.ita.formic.forms.components.radio.UIRadio.class);
        Assert.assertNotNull(lstRadio);
        Assert.assertTrue("One radio is expected, found: " + lstRadio.size(), lstRadio.size() == 1);

        // repo must be set with parent value "contacts"
        es.jcyl.ita.formic.forms.components.radio.UIRadio radio = lstRadio.get(0);
        Assert.assertNotNull(radio.getId());
        Assert.assertTrue(ArrayUtils.isEmpty(radio.getOptions()));
        assertThat(radio.getOrientation(),equalTo("horizontal"));
    }

    private static final String XML_OPTIONS = "<radio>" +
            "<options>" +
            "<option value=\"optionVal1\" label=\"option1\"/>" +
            "<option value=\"optionVal2\" label=\"option2\"/>" +
            "<option value=\"optionVal3\" label=\"option3\"/>" +
            "</options>" +
            "</radio>";

    @Test
    public void testOptions() throws Exception {
        String xml = XmlConfigUtils.createEditForm(XML_OPTIONS);
        FormConfig formConfig = XmlConfigUtils.readFormConfig(xml);

        FormEditController editCtl = formConfig.getEdits().get(0);
        List<UISelect> selects = UIComponentHelper.findByClass(editCtl.getView(), UISelect.class);
        Assert.assertNotNull(selects);
        Assert.assertTrue("One radio is expected, found: " + selects.size(), selects.size() == 1);

        // repo must be set with parent value "contacts"
        UISelect select = selects.get(0);
        Assert.assertNotNull(select.getId());
        Assert.assertNotNull(select.getOptions());
        Assert.assertEquals(3, select.getOptions().length);
        for (int i = 0; i < select.getOptions().length; i++) {
            Assert.assertNotNull(select.getOptions()[i].getId());
            Assert.assertEquals("optionVal" + (i + 1), select.getOptions()[i].getValue());
            Assert.assertEquals("option" + (i + 1), select.getOptions()[i].getLabel());
        }
    }
//
//    @AfterClass
//    public static void tearDown() {
//        RepositoryUtils.unregisterMock("contacts");
//    }


    }
