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


import android.graphics.Color;

import org.junit.AfterClass;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.RobolectricTestRunner;

import java.util.List;

import es.jcyl.ita.formic.forms.components.UIComponentHelper;
import es.jcyl.ita.formic.forms.components.placeholders.UIDivisor;
import es.jcyl.ita.formic.forms.components.placeholders.UIParagraph;
import es.jcyl.ita.formic.forms.App;
import es.jcyl.ita.formic.forms.config.ConfigConverters;
import es.jcyl.ita.formic.forms.config.elements.FormConfig;
import es.jcyl.ita.formic.forms.utils.RepositoryUtils;
import es.jcyl.ita.formic.forms.utils.XmlConfigUtils;
import es.jcyl.ita.formic.repo.media.meta.FileMeta;

/**
 * @author Javier Ramos (javier.ramos@itacyl.es)
 * <p>
 * Tests to check commons-converters functionallity
 */
@RunWith(RobolectricTestRunner.class)
public class PlaceHoldersTest {


    @BeforeClass
    public static void setUp() {
        App.init("");
        ConfigConverters confConverter = new ConfigConverters();
        confConverter.init();
        // register repos
        RepositoryUtils.registerMock("contacts");
        RepositoryUtils.registerMock("DEFAULT_PROJECT_IMAGES", new FileMeta());
    }


    private static final String XML_TEST_BASIC_HEAD = "<p/>";

    @Test
    public void testBasicHead() throws Exception {
        String xml = XmlConfigUtils.createMainList(XML_TEST_BASIC_HEAD);

        FormConfig formConfig = XmlConfigUtils.readFormConfig(xml);
        List<UIParagraph> headings = UIComponentHelper.getChildrenByClass(formConfig.getList().getView(), UIParagraph.class);
        Assert.assertNotNull(headings);
        UIParagraph heading = headings.get(0);
        Assert.assertNotNull(heading.getId());

    }

    /**
     * Tests the evaluation of "properties" attribute to filter the properties selected from repository.*
     *
     * @throws Exception
     */
    private static final String XML_TEST_HEAD_WITH_ATTRIBUTES = "<p fontColor=\"red\" " +
            "fontSize=\"20\" value=\"heading_value\" uppercase=\"true\" italic=\"true\" " +
            "bold=\"true\" underlined=\"true\"/>";


    @Test
    public void testHeadWithAttributes() throws Exception {
        String xml = XmlConfigUtils.createMainList(XML_TEST_HEAD_WITH_ATTRIBUTES);

        FormConfig formConfig = XmlConfigUtils.readFormConfig(xml);
        List<UIParagraph> headings = UIComponentHelper.getChildrenByClass(formConfig.getList().getView(), UIParagraph.class);
        Assert.assertNotNull(headings);

        UIParagraph heading = headings.get(0);

        Assert.assertEquals(20, heading.getFontSize());
        Assert.assertEquals(Color.RED, heading.getFontColor());
        Assert.assertTrue(heading.isItalic());
        Assert.assertTrue(heading.isBold());
        Assert.assertTrue(heading.isUnderlined());
        Assert.assertTrue(heading.isUppercase());
    }

    private static final String XML_TEST_DIVISOR = "<divisor/>";

    @Test
    public void testDivisor() throws Exception {
        String xml = XmlConfigUtils.createMainList(XML_TEST_DIVISOR);

        FormConfig formConfig = XmlConfigUtils.readFormConfig(xml);
        List<UIDivisor> divisors = UIComponentHelper.getChildrenByClass(formConfig.getList().getView(), UIDivisor.class);
        Assert.assertNotNull(divisors);
        UIDivisor divisor = divisors.get(0);
        Assert.assertNotNull(divisor.getId());

    }

    @AfterClass
    public static void tearDown() {
        RepositoryUtils.unregisterMock("contacts");
        RepositoryUtils.unregisterMock("contacts2");
    }

}
