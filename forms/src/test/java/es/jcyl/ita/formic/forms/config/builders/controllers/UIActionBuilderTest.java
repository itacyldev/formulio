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

import org.apache.commons.lang3.ArrayUtils;
import org.junit.AfterClass;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.RobolectricTestRunner;

import java.util.List;

import es.jcyl.ita.formic.forms.App;
import es.jcyl.ita.formic.forms.components.UIComponentHelper;
import es.jcyl.ita.formic.forms.components.link.UIButton;
import es.jcyl.ita.formic.forms.config.ConfigConverters;
import es.jcyl.ita.formic.forms.config.elements.FormConfig;
import es.jcyl.ita.formic.forms.controllers.UIAction;
import es.jcyl.ita.formic.forms.controllers.UIActionGroup;
import es.jcyl.ita.formic.forms.utils.RepositoryUtils;
import es.jcyl.ita.formic.forms.utils.XmlConfigUtils;


/**
 * @author Gustavo Río (gustavo.rio@itacyl.es)
 * <p>
 * Test UIAction contruction from XML definition
 */
@RunWith(RobolectricTestRunner.class)
public class UIActionBuilderTest {

    @BeforeClass
    public static void setUp() {
        App.init("");
        ConfigConverters confConverter = new ConfigConverters();
        confConverter.init();
        // register repos
        RepositoryUtils.registerMock("contacts");
    }


    private static final String XML_ACTION = "<main name=\"myFirstForm\" description=\"An example of short description\" id=\"form1\" repo=\"contacts\">" +
            "<list name=\"Form2\">" +
            "<datatable />" +
            "</list>" +
            "<edit id=\"form1#edit\">" +
            "<actions>" +
            "<action type=\"create\" label=\"create\" controller=\"widget1\">" +
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
        Assert.assertEquals(actions[0].getController(), "widget1");
    }

    private static final String XML_BUTTON[] = new String[]{
            "<button id=\"myButton\" label=\"guardar\">\n" +
                    "  <save controller=\"widget1\"/>\n" +
                    "</button>\n",
            ///
            "<button id=\"myButton\" label=\"guardar\">\n" +
                    "  <action type=\"save\" controller=\"widget1\"/>\n" +
                    "</button>\n"
    };

    @Test
    public void testButtonAction() throws Exception {
        for (String xmlDef : XML_BUTTON) {
            String configXML = XmlConfigUtils.createEditForm(xmlDef);
            FormConfig formConfig = XmlConfigUtils.readFormConfig(configXML);
            // find the button
            List<UIButton> buttonList = UIComponentHelper.getChildrenByClass(formConfig.getEdits().get(0).getView(), UIButton.class);
            UIButton button = buttonList.get(0);
            Assert.assertNotNull(button);
            UIAction action = button.getAction();
            Assert.assertEquals("Error in definition " + xmlDef, "save", action.getType());
            Assert.assertEquals("Error in definition " + xmlDef, "widget1", action.getController());
        }
    }

    private static final String XML_NESTED_ACTIONS = "" +
            "<button id=\"myButton\" label=\"guardar\">\n" +
            "    <action id=\"compositeAction\">\n" +
            "      <action id=\"nestedAction1\" type=\"save\" controller=\"widget1\">\n" +
            "        <params>\n" +
            "            <param name=\"parametro1\" value=\"value1\"/>\n" +
            "        </params>\n" +
            "      </action>\n" +
            "      <action id=\"nestedAction2\" type=\"navigate\" controller=\"widget2\"/>\n" +
            "    </action>\n" +
            "</button>";

    @Test
    public void testButtonNestedAction() throws Exception {
        String configXML = XmlConfigUtils.createEditForm(XML_NESTED_ACTIONS);
        FormConfig formConfig = XmlConfigUtils.readFormConfig(configXML);
        // find the button
        List<UIButton> buttonList = UIComponentHelper.getChildrenByClass(formConfig.getEdits().get(0).getView(), UIButton.class);
        UIButton button = buttonList.get(0);
        Assert.assertNotNull(button);
        UIAction action = button.getAction();
        // direct action attached to the component is of type COMPOSITE and it has no params
        Assert.assertEquals("composite", action.getType());
        Assert.assertEquals(UIActionGroup.class, action.getClass());
        Assert.assertEquals("compositeAction", action.getId());
        // Composite action has no nested parameters
        Assert.assertTrue(ArrayUtils.isEmpty(action.getParams()));
        // The composite action has two nested actions
        UIActionGroup compositeAction = (UIActionGroup) action;
        Assert.assertEquals(2, compositeAction.getActions().length);
    }

    private static final String XML_BUTTON_JS_ACTION[] = new String[]{
            "<button id=\"myButton\" label=\"guardar\">\n" +
                    "  <js method=\"console.log()\">\n" +
                    "    <param name=\"param1\" value=\"value1\"/>" +
                    "  </js>" +
                    "</button>\n",
            ////
            "<button id=\"myButton\" label=\"guardar\">\n" +
                    "  <action type=\"js\" method=\"console.log()\">\n" +
                    "    <param name=\"param1\" value=\"value1\"/>" +
                    "  </action>" +
                    "</button>\n",
            ////
            "<button id=\"myButton\" label=\"guardar\" action=\"js\" method=\"console.log()\">\n" +
                    "    <param name=\"param1\" value=\"value1\"/>" +
                    "</button>\n",
            ///
            "<button id=\"myButton\" label=\"guardar\">\n" +
                    "  <action type=\"js\">\n" +
                    "    <param name=\"method\" value=\"console.log\" />\n" +
                    "    <param name=\"param1\" value=\"value1\"/>" +
                    "  </action>\n" +
                    "</button>"
    };

    @Test
    public void testJsAction() {
        for (String xmlDef : XML_BUTTON_JS_ACTION) {
            String configXML = XmlConfigUtils.createEditForm(xmlDef);
            FormConfig formConfig = XmlConfigUtils.readFormConfig(configXML);

            UIButton button = UIComponentHelper.findFirstByClass(formConfig.getEdits().get(0).getView(), UIButton.class);
            UIAction action = button.getAction();
            Assert.assertEquals("Error in xmldef" + xmlDef, "js", action.getType());
            Assert.assertEquals("Error in xmldef" + xmlDef, 2, action.getParams().length);
        }
    }

    @AfterClass
    public static void tearDown() {
        RepositoryUtils.unregisterMock("contacts");
    }

}
