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

import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mini2Dx.beanutils.ConvertUtils;
import org.robolectric.RobolectricTestRunner;

import es.jcyl.ita.formic.core.context.impl.BasicContext;
import es.jcyl.ita.formic.forms.components.UIComponentHelper;
import es.jcyl.ita.formic.forms.components.inputfield.UIField;
import es.jcyl.ita.formic.forms.App;
import es.jcyl.ita.formic.forms.config.ConfigConverters;
import es.jcyl.ita.formic.forms.config.elements.FormConfig;
import es.jcyl.ita.formic.forms.utils.RepositoryUtils;
import es.jcyl.ita.formic.forms.utils.XmlConfigUtils;

import static org.hamcrest.Matchers.equalTo;
import static org.junit.Assert.assertThat;

/**
 * Checks UIField creation from xml
 *
 * @author Gustavo Río (gustavo.rio@itacyl.es)
 */
@RunWith(RobolectricTestRunner.class)
public class UIFieldBuilderTest {

    @BeforeClass
    public static void setUp() {
        App.init("");
        ConfigConverters confConverter = new ConfigConverters();
        confConverter.init();
        // register repos
        RepositoryUtils.registerMock("contacts");
    }

    private static final String XML_TEST_BASIC = "<input id=\"myId\" readonly=\"true\"/>";

    @Test
    public void testBasicInput() throws Exception {
        RepositoryUtils.registerMock("contacts2");

        String xml = XmlConfigUtils.createMainList(XML_TEST_BASIC);

        FormConfig formConfig = XmlConfigUtils.readFormConfig(xml);
        UIField field = UIComponentHelper.getChildrenByClass(formConfig.getList().getView(), UIField.class).get(0);

        assertThat(field.getId(), equalTo("myId"));
        assertThat(ConvertUtils.convert(field.isReadonly(new BasicContext("bc")), Boolean.class), equalTo(true));
    }

    private static final String XML_TEXT_AREA = "<textarea id=\"myId\" readonly=\"true\" lines=\"5\"/>";

    @Test
    public void testTextArea() throws Exception {
//        RepositoryUtils.registerMock("contacts2");

        String xml = XmlConfigUtils.createMainList(XML_TEXT_AREA);

        FormConfig formConfig = XmlConfigUtils.readFormConfig(xml);
        UIField field = UIComponentHelper.getChildrenByClass(formConfig.getList().getView(), UIField.class).get(0);

        assertThat(field.getId(), equalTo("myId"));
        assertThat(ConvertUtils.convert(field.isReadonly(new BasicContext("bc")), Boolean.class), equalTo(true));
        assertThat(field.getType(), equalTo("TEXTAREA"));
        assertThat(field.getLines(), equalTo(5));
    }
}
