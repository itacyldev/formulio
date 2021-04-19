package es.jcyl.ita.formic.forms.config.builders.controllers;
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

import es.jcyl.ita.formic.forms.config.Config;
import es.jcyl.ita.formic.forms.config.ConfigConverters;
import es.jcyl.ita.formic.forms.config.elements.FormConfig;
import es.jcyl.ita.formic.forms.controllers.UIAction;
import es.jcyl.ita.formic.forms.utils.RepositoryUtils;
import es.jcyl.ita.formic.forms.utils.XmlConfigUtils;


/**
 * @author Javier Ramos (javier.ramos@itacyl.es)
 * <p>
 * Tests to check commons-converters functionallity
 */
@RunWith(RobolectricTestRunner.class)
public class FCActionBuilderTest {

    @BeforeClass
    public static void setUp() {
        Config.init("");
        ConfigConverters confConverter = new ConfigConverters();
        confConverter.init();
        // register repos
        RepositoryUtils.registerMock("contacts");
    }


    private static final String XML_ACTION = "<main name=\"myFirstForm\" description=\"An example of short description\" id=\"form1\" repo=\"contacts\">" +
            "<list name=\"Form2\">"+
            "<datatable />"+
            "</list>"+
            "<edit id=\"form1#edit\">" +
            "<actions>" +
            "<action type=\"create\" label=\"create\">" +
            "<param name=\"entityType\" value=\"contacts\"/>" +
            "<param name=\"email\" value=\"mivalor@gmail.com\"/>" +
            "</action>" +
            "</actions>" +
            "</edit>" +
            "</main>";

    @Test
    public void testGenericAction() throws Exception {
        FormConfig formConfig = XmlConfigUtils.readFormConfig(XML_ACTION);
        UIAction[] actions = formConfig.getEdits().get(0).getActions();
        Assert.assertNotNull(actions);
        Assert.assertEquals(actions[0].getType(), "create");

    }

    @AfterClass
    public static void tearDown() {
        RepositoryUtils.unregisterMock("contacts");
    }

}
