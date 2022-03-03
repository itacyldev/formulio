package es.jcyl.ita.formic.forms.config.resolvers;
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

import es.jcyl.ita.formic.forms.App;
import es.jcyl.ita.formic.forms.actions.ActionType;
import es.jcyl.ita.formic.forms.components.UIComponentHelper;
import es.jcyl.ita.formic.forms.components.UIComponent;
import es.jcyl.ita.formic.forms.config.ConfigConverters;
import es.jcyl.ita.formic.forms.config.ConfigurationException;
import es.jcyl.ita.formic.forms.config.elements.FormConfig;
import es.jcyl.ita.formic.forms.controllers.UIAction;
import es.jcyl.ita.formic.forms.controllers.UIParam;
import es.jcyl.ita.formic.forms.controllers.ViewController;
import es.jcyl.ita.formic.forms.utils.RepositoryUtils;
import es.jcyl.ita.formic.forms.utils.XmlConfigUtils;

import static org.hamcrest.Matchers.*;

/**
 * @author Gustavo Río (gustavo.rio@itacyl.es)
 */
@RunWith(RobolectricTestRunner.class)
public class ActionAttributeResolverTest {

    @BeforeClass
    public static void setUp() {
        App.init("");
        ConfigConverters confConverter = new ConfigConverters();
        confConverter.init();
        // register repos
        RepositoryUtils.registerMock("contacts");
    }

    @AfterClass
    public static void tearDown() {
        RepositoryUtils.unregisterMock("contacts");
    }


    private static final String XML_TEST_BASIC = "<button id=\"myButton\" action=\"delete\" />";

    @Test
    public void testBasicPredefinedAction() {
        // prepare
        String xml = XmlConfigUtils.createEditForm(XML_TEST_BASIC);
        // act
        FormConfig formConfig = XmlConfigUtils.readFormConfig(xml);

        // find myButton
        ViewController editCtl = formConfig.getEdits().get(0);
        UIComponent myButton = UIComponentHelper.findChild(editCtl.getView(), "myButton");

        Assert.assertNotNull(myButton);
        Assert.assertNotNull(myButton.getAction());
        Assert.assertEquals(ActionType.DELETE.name().toLowerCase(), myButton.getAction().getType());
    }

    private static final String XML_TEST_BUTTON_ROUTE = "<button id=\"myButton\" route=\"aRouteToGo\" />";

    @Test
    public void testActionWithRouteParam() {
        // prepare
        String xml = XmlConfigUtils.createEditForm(XML_TEST_BUTTON_ROUTE);
        // act
        FormConfig formConfig = XmlConfigUtils.readFormConfig(xml);

        // find myButton
        ViewController editCtl = formConfig.getEdits().get(0);
        UIComponent myButton = UIComponentHelper.findChild(editCtl.getView(), "myButton");

        Assert.assertNotNull(myButton);
        Assert.assertNotNull(myButton.getAction());
        Assert.assertEquals(ActionType.NAV.name(), myButton.getAction().getType());
    }

    private static final String XML_TEST_BUTTON_ACTION_PARAMS = "<button id=\"myButton\" action=\"nav\" route=\"aRouteToGo\" refresh=\"this\" registerInHistory=\"false\" />";

    @Test
    public void testActionButtonWithActionParams() {
        // prepare
        String xml = XmlConfigUtils.createEditForm(XML_TEST_BUTTON_ACTION_PARAMS);
        // act
        FormConfig formConfig = XmlConfigUtils.readFormConfig(xml);

        // find myButton
        ViewController editCtl = formConfig.getEdits().get(0);
        UIComponent myButton = UIComponentHelper.findChild(editCtl.getView(), "myButton");

        Assert.assertNotNull(myButton);
        Assert.assertNotNull(myButton.getAction());
        Assert.assertEquals(ActionType.NAV.name().toLowerCase(), myButton.getAction().getType());
        Assert.assertEquals("this", myButton.getAction().getRefresh());
        Assert.assertEquals(false, myButton.getAction().isRegisterInHistory());
        Assert.assertEquals("aRouteToGo", myButton.getAction().getRoute());
    }

    private static final String XML_NESTED_ACTION = "<button id=\"myButton\" >" +
            "<action type=\"save\" refresh=\"all\">" +
            "<param name=\"param1\" value=\"valor1\"/>" +
            "<param name=\"param2\" value=\"valor2\"/>" +
            "</action>" +
            "</button>";

    @Test
    public void testNestedActionWithParameters() {
        // prepare
        String xml = XmlConfigUtils.createEditForm(XML_NESTED_ACTION);
        // act
        FormConfig formConfig = XmlConfigUtils.readFormConfig(xml);

        // find myButton
        ViewController editCtl = formConfig.getEdits().get(0);
        UIComponent myButton = UIComponentHelper.findChild(editCtl.getView(), "myButton");

        Assert.assertNotNull(myButton);
        UIAction action = myButton.getAction();
        Assert.assertNotNull(action);

        Assert.assertEquals(ActionType.SAVE.name().toLowerCase(), action.getType());
        Assert.assertEquals("all", action.getRefresh());
        // check parameters
        UIParam[] params = action.getParams();
        Assert.assertThat(params, not(emptyArray()));
        Assert.assertThat(params, arrayWithSize(2));
    }

    private static final String XML_NESTED_WITH_ERROR = "<button id=\"myButton\" action=\"nav\">" +
            "<action type=\"delete\" refresh=\"all\"/>" +
            "</button>";

    /**
     * If "action" attribute is set, no nested action should exists.
     */
    @Test(expected = ConfigurationException.class)
    public void testNestedActionWithError() {
        // prepare
        String xml = XmlConfigUtils.createEditForm(XML_NESTED_WITH_ERROR);
        // act
        FormConfig formConfig = XmlConfigUtils.readFormConfig(xml);

    }

    private static final String XML_CUSTOM_ACTION = "<form>" +
            "<button id=\"myButton\" action=\"myCustomAction\"/>" +
            "</form>" +
            "<actions>" +
            "<action id=\"myCustomAction\" type=\"create\"/>" +
            "</actions>";

    @Test
    public void testCustomActionReference() {
        // prepare
        String xml = XmlConfigUtils.createMainEdit(XML_CUSTOM_ACTION);
        // act
        FormConfig formConfig = XmlConfigUtils.readFormConfig(xml);

        // find myButton
        ViewController editCtl = formConfig.getEdits().get(0);
        UIComponent myButton = UIComponentHelper.findChild(editCtl.getView(), "myButton");

        Assert.assertNotNull(myButton);
        UIAction action = myButton.getAction();
        Assert.assertNotNull(action);
        Assert.assertEquals(ActionType.CREATE.name().toLowerCase(), action.getType());
    }


    private static final String XML_CUSTOM_ACTION_WRONG_NAME = "<form>" +
            "<button id=\"myButton\" action=\"MyWrongName\"/>" +
            "</form>" +
            "<actions>" +
            "<action id=\"myCustomAction\" type=\"create\"/>" +
            "</actions>";

    /**
     * Wrong custom action name, and no "script" tag defined, and exception is expected.
     */
    @Test(expected = ConfigurationException.class)
    public void testCustomActionReferenceWithWrongName() {
        // prepare
        String xml = XmlConfigUtils.createMainEdit(XML_CUSTOM_ACTION_WRONG_NAME);
        // act
        XmlConfigUtils.readFormConfig(xml);
    }


    private static final String XML_JS_METHOD_REFERENCE = "<form>" +
            "<button id=\"myButton\" action=\"myJsMethod\">" +
            "<param name=\"param1\" value=\"value1\"/>" +
            "</button>" +
            "</form>" +
            "<script> var message='Some js here!'; </script>";

    /**
     * The "action" attribute refers to a js method, check the button has an Action
     * object of type "JS" with parameter method = "myJsMethod"
     */
    @Test
    public void testJSMethodReference() {
        // prepare
        String xml = XmlConfigUtils.createMainEdit(XML_JS_METHOD_REFERENCE);
        // act
        FormConfig formConfig = XmlConfigUtils.readFormConfig(xml);

        // find myButton
        ViewController editCtl = formConfig.getEdits().get(0);
        UIComponent myButton = UIComponentHelper.findChild(editCtl.getView(), "myButton");

        Assert.assertNotNull(myButton);
        UIAction action = myButton.getAction();
        // assert
        Assert.assertNotNull(action);
        Assert.assertEquals(ActionType.JS.name(), action.getType());
        UIParam[] params = action.getParams();
        Assert.assertThat(params, not(emptyArray()));
        Assert.assertThat(params, arrayWithSize(2));
        Assert.assertThat(params[0].getValue().toString(), equalTo("myJsMethod"));
        Assert.assertThat(params[1].getValue().toString(), equalTo("value1"));
    }
}
