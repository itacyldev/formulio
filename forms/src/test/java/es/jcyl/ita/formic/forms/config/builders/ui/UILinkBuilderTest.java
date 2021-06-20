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

import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mini2Dx.beanutils.ConvertUtils;
import org.robolectric.RobolectricTestRunner;

import java.util.List;

import es.jcyl.ita.formic.core.context.impl.BasicContext;
import es.jcyl.ita.formic.forms.components.UIComponentHelper;
import es.jcyl.ita.formic.forms.components.link.UILink;
import es.jcyl.ita.formic.forms.config.Config;
import es.jcyl.ita.formic.forms.config.ConfigConverters;
import es.jcyl.ita.formic.forms.config.elements.FormConfig;
import es.jcyl.ita.formic.forms.controllers.UIAction;
import es.jcyl.ita.formic.forms.el.JexlFormUtils;
import es.jcyl.ita.formic.forms.el.LiteralBindingExpression;
import es.jcyl.ita.formic.forms.el.ValueBindingExpression;
import es.jcyl.ita.formic.forms.el.ValueExpressionFactory;
import es.jcyl.ita.formic.forms.utils.RepositoryUtils;
import es.jcyl.ita.formic.forms.utils.XmlConfigUtils;
import es.jcyl.ita.formic.repo.test.utils.AssertUtils;


/**
 *
 * <p>
 * Tests to check commons-converters functionallity
 */
@RunWith(RobolectricTestRunner.class)
public class UILinkBuilderTest {

    ValueExpressionFactory factory = ValueExpressionFactory.getInstance();

    private static final String XML_TEST_BASIC = "<link/>";

    @BeforeClass
    public static void setUp() {
        Config.init("");
        ConfigConverters confConverter = new ConfigConverters();
        confConverter.init();
        // register repos
        // register repos
        RepositoryUtils.registerMock("contacts");
    }

    @Test
    public void testBasicLink() throws Exception {
        String xml = XmlConfigUtils.createMainList(XML_TEST_BASIC);

        FormConfig formConfig = XmlConfigUtils.readFormConfig(xml);
        List<UILink> links = UIComponentHelper.getChildrenByClass(formConfig.getList().getView(), UILink.class);
        Assert.assertNotNull(links);
        UILink link = links.get(0);
        Assert.assertNotNull(link.getId());

    }

    private static final String XML_TEST_LINK_WITH_ATTS = "<link id=\"myLink\" route=\"aRouteToForm\"/>";


    @Test
    public void testLinkWithAtts() throws Exception {
        String xml = XmlConfigUtils.createMainList(XML_TEST_LINK_WITH_ATTS);

        FormConfig formConfig = XmlConfigUtils.readFormConfig(xml);
        List<UILink> links = UIComponentHelper.getChildrenByClass(formConfig.getList().getView(), UILink.class);
        Assert.assertNotNull(links);

        UILink link = links.get(0);

        Assert.assertEquals("myLink", link.getId());
        Assert.assertEquals("aRouteToForm", link.getRoute());
    }


    private static final String XML_TEST_LINK_WITH_PARAMS =
            "<link>" +
            "<param name=\"name1\" value=\"value1\"/>" +
            "<param name=\"name2\" value=\"value2\"/>" +
            "</link>";


    @Test
    public void testLinkWithParams() throws Exception {
        String xml = XmlConfigUtils.createMainList(XML_TEST_LINK_WITH_PARAMS);

        FormConfig formConfig = XmlConfigUtils.readFormConfig(xml);
        List<UILink> links = UIComponentHelper.getChildrenByClass(formConfig.getList().getView(), UILink.class);
        Assert.assertNotNull(links);

        UILink link = links.get(0);
        UIAction uiAction = link.getAction();
        Assert.assertNotNull(uiAction.getParams());
        Assert.assertEquals(2, uiAction.getParams().length);
        for (int i = 0; i < uiAction.getParams().length; i++) {
            Assert.assertNotNull(uiAction.getParams()[i].getName());
            Assert.assertEquals("name" + (i + 1), uiAction.getParams()[i].getName());

            String strValue = (String) ConvertUtils.convert("value" + (i + 1), String.class);
            ValueBindingExpression ve = factory.create(strValue, String.class);
            Assert.assertNotNull(ve);
            Assert.assertEquals(ve.getClass(), LiteralBindingExpression.class);
            AssertUtils.assertEquals("value" + (i + 1), JexlFormUtils.eval(new BasicContext("t"), ve));

        }
    }

}
