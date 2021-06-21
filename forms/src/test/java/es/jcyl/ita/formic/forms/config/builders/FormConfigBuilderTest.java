package es.jcyl.ita.formic.forms.config.builders;
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
import org.junit.runner.RunWith;
import org.robolectric.RobolectricTestRunner;

import java.io.File;
import java.util.List;

import es.jcyl.ita.formic.forms.components.UIComponent;
import es.jcyl.ita.formic.forms.components.buttonbar.UIButtonBar;
import es.jcyl.ita.formic.forms.components.link.UIButton;
import es.jcyl.ita.formic.forms.utils.ProjectUtils;
import es.jcyl.ita.formic.repo.test.utils.TestUtils;
import es.jcyl.ita.formic.forms.config.Config;
import es.jcyl.ita.formic.forms.config.ConfigConverters;
import es.jcyl.ita.formic.forms.config.elements.FormConfig;
import es.jcyl.ita.formic.forms.controllers.UIAction;
import es.jcyl.ita.formic.forms.controllers.FormEditController;
import es.jcyl.ita.formic.forms.controllers.FormListController;
import es.jcyl.ita.formic.forms.components.UIComponentHelper;
import es.jcyl.ita.formic.forms.components.datatable.UIDatatable;
import es.jcyl.ita.formic.forms.components.form.UIForm;
import es.jcyl.ita.formic.forms.components.view.UIView;
import es.jcyl.ita.formic.forms.utils.RepositoryUtils;
import es.jcyl.ita.formic.forms.utils.XmlConfigUtils;

/**
 * @author Gustavo Río (gustavo.rio@itacyl.es)
 * <p>
 * Tests to check commons-converters functionallity
 */
@RunWith(RobolectricTestRunner.class)
public class FormConfigBuilderTest {

    @BeforeClass
    public static void setUp() {
        Config.init("");
        ConfigConverters confConverter = new ConfigConverters();
        confConverter.init();
        // register repos
        RepositoryUtils.registerMock("contacts");
    }

    @Test
    public void testFileconfig() throws Exception {
        ProjectUtils.readProjectConfig("src/test/resources/config/project1");
        Config config = Config.getInstance();
        config.getFormConfigRepo().findById("formPrueba");

    }

    /**
     * Check formEdit and formList are properly created
     */
    private static final String TEST_BASIC1 = "<main repo=\"contacts\"/>";

    @Test
    public void testFormConfigDefaultCreation() throws Exception {

        FormConfig formConfig = XmlConfigUtils.readFormConfig(TEST_BASIC1);

        Assert.assertNotNull(formConfig);
        Assert.assertNotNull(formConfig.getList());
        Assert.assertNotNull(formConfig.getEdits());
        Assert.assertTrue(formConfig.getEdits().size() > 0);
        Assert.assertNotNull(formConfig.getRepo());

        //////////////////////////
        // check editController
        //////////////////////////
        FormEditController ctl = formConfig.getEdits().get(0);
        Assert.assertNotNull(ctl.getView().getRepo());
        Assert.assertNotNull(ctl.getMainForm());
        // check editActions
        UIAction[] actions = ctl.getActions();
        String listCtlId = formConfig.getList().getId(); // id of related listController
        UIView view = ctl.getView();
        UIButtonBar btbar = view.getBottomNav();
        for(UIComponent c: btbar.getChildren()){
            if(!(c instanceof UIButton)){ continue;}
            UIButton btn = (UIButton)c;
            if (btn.getLabel().equalsIgnoreCase("save")) {
                Assert.assertEquals(listCtlId, btn.getAction().getRoute());
            } else if (btn.getLabel().equalsIgnoreCase("cancel")) {
                Assert.assertEquals("back", btn.getAction().getRoute());
            }
        }
        // check FormEditController has a form
        List<UIForm> forms = UIComponentHelper.getChildrenByClass(ctl.getView(), UIForm.class);
        Assert.assertTrue("No default FORM found for FormEditController", forms.size() == 1);

        //////////////////////////
        // check listController
        //////////////////////////
        FormListController ctlList = formConfig.getList();
        Assert.assertNotNull(ctlList.getView().getRepo());
        Assert.assertNotNull(ctlList.getView().getFabBar());

        // check listActions
        String editCtrlId = ctl.getId(); // id of related editController
        UIView listView = ctlList.getView();
        UIButtonBar fabBar = listView.getFabBar();

        for(UIComponent c: fabBar.getChildren()){
            if(!(c instanceof UIButton)){ continue;}
            UIButton btn = (UIButton)c;
            UIAction action = btn.getAction();
            if (action.getType().equalsIgnoreCase("add")
                    || action.getType().equalsIgnoreCase("update")) {
                Assert.assertEquals(editCtrlId, action.getRoute());
            }
        }
        // check listAction has a table
        List<UIDatatable> tables = UIComponentHelper.getChildrenByClass(ctlList.getView(), UIDatatable.class);
        Assert.assertTrue("No default table found for FormListController", tables.size() == 1);
    }


    public static void assertComponent(UIComponent root, UIView view) {
        if (!root.hasChildren()) {
            Assert.assertEquals("the view is not properly linked", view, root.getRoot());
        } else {
            for (UIComponent kid : root.getChildren()) {
                assertComponent(kid, view);
            }
        }
    }

    @Test
    public void testFormConfigCreation() throws Exception {
        File file = TestUtils.findFile("config/formConfig.xml");
        FormConfig formConfig = XmlConfigUtils.readFormConfig(file);

        Assert.assertNotNull(formConfig);
        Assert.assertNotNull(formConfig.getRepo());
        Assert.assertNotNull(formConfig.getList());
        Assert.assertNotNull(formConfig.getEdits());
        Assert.assertEquals(2, formConfig.getEdits().size());
        Assert.assertNotNull(formConfig.getEdits().get(0));
        Assert.assertNotNull(formConfig.getEdits().get(1));

        // check every object has a repo value
        Assert.assertNotNull(formConfig.getRepo());
        Assert.assertNotNull(formConfig.getList().getView().getRepo());

        for (FormEditController c : formConfig.getEdits()) {
            Assert.assertNotNull(c.getId());
            Assert.assertNotNull(c.getView().getRepo());
        }

        // check FormListController actions
        UIAction[] actions = formConfig.getList().getActions();
        // check is not null, and all of them have a route and label
        for (UIAction action : actions) {
            Assert.assertNotNull(action.getLabel());
            Assert.assertNotNull(action.getRoute());
            Assert.assertNotNull(action.getType());
        }

        // check editViews have a defaultForm nested
        for (FormEditController edit : formConfig.getEdits()) {
            Assert.assertNotNull(edit.getMainForm());
            Assert.assertNotNull(edit.getView().getBottomNav());
        }
    }

}
