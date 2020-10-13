package es.jcyl.ita.formic.repo.config.builders;
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

import es.jcyl.ita.crtrepo.test.utils.TestUtils;
import es.jcyl.ita.formic.repo.config.Config;
import es.jcyl.ita.formic.repo.config.ConfigConverters;
import es.jcyl.ita.formic.repo.config.FormConfig;
import es.jcyl.ita.formic.forms.controllers.FCAction;
import es.jcyl.ita.formic.forms.controllers.FormEditController;
import es.jcyl.ita.formic.forms.controllers.FormListController;
import es.jcyl.ita.formic.forms.components.UIComponent;
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
        Assert.assertNotNull(ctl.getRepo());
        Assert.assertNotNull(ctl.getMainForm());
        Assert.assertNotNull(ctl.getActions());
        Assert.assertTrue(ctl.getActions().length > 0);
        // check editActions
        FCAction[] actions = ctl.getActions();
        String listCtlId = formConfig.getList().getId(); // id of related listController
        for (FCAction action : actions) {
            if (action.getType().equalsIgnoreCase("save")) {
                Assert.assertEquals(listCtlId, action.getRoute());
            } else if (action.getType().equalsIgnoreCase("cancel")) {
                Assert.assertEquals("back", action.getRoute());
            }
        }
        // check FormEditController has a form
        List<UIForm> forms = UIComponentHelper.findByClass(ctl.getView(), UIForm.class);
        Assert.assertTrue("No default FORM found for FormEditController", forms.size() == 1);
        assertRootIsLinked(ctl.getView());

        //////////////////////////
        // check listController
        //////////////////////////
        FormListController ctlList = formConfig.getList();
        Assert.assertNotNull(ctlList.getRepo());
        Assert.assertNotNull(ctlList.getActions());
        Assert.assertTrue(ctlList.getActions().length > 0);
        assertRootIsLinked(ctlList.getView());
        // list.add/edit must point to editController

        // check listActions
        actions = ctl.getActions();
        String editCtrlId = ctl.getId(); // id of related editController
        for (FCAction action : actions) {
            if (action.getType().equalsIgnoreCase("add")
                    || action.getType().equalsIgnoreCase("update")) {
                Assert.assertEquals(editCtrlId, action.getRoute());
            }
        }
        // check listAction has a table
        List<UIDatatable> tables = UIComponentHelper.findByClass(ctlList.getView(), UIDatatable.class);
        Assert.assertTrue("No default table found for FormListController", tables.size() == 1);

//        Assert.assertNotNull(ctlList.getEntitySelector()); pending of #203650
    }

    /**
     * check every element in the tree is linked to the root
     *
     * @param view
     */
    private void assertRootIsLinked(UIView view) {
        assertComponent(view, view);

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
        assertRootIsLinked(formConfig.getEdits().get(0).getView());
        Assert.assertNotNull(formConfig.getEdits().get(1));
        assertRootIsLinked(formConfig.getEdits().get(1).getView());

        // check every object has a repo value
        Assert.assertNotNull(formConfig.getRepo());
        Assert.assertNotNull(formConfig.getList().getRepo());
        assertRootIsLinked(formConfig.getList().getView());

        for (FormEditController c : formConfig.getEdits()) {
            Assert.assertNotNull(c.getId());
            Assert.assertNotNull(c.getRepo());
        }

        // check FormListController actions
        FCAction[] actions = formConfig.getList().getActions();
        // check is not null, and all of them have a route and label
        for (FCAction action : actions) {
            Assert.assertNotNull(action.getLabel());
            Assert.assertNotNull(action.getRoute());
            Assert.assertNotNull(action.getType());
        }

        // check editViews have a defaultForm nested
        for (FormEditController edit : formConfig.getEdits()) {
            Assert.assertNotNull(edit.getMainForm());
            Assert.assertNotNull(edit.getActions());
            for (FCAction action : edit.getActions()) {
                Assert.assertNotNull(action.getType());
            }
            Assert.assertNotNull(edit.getAction("cancel"));
            Assert.assertNotNull(edit.getAction("save"));
        }
    }

}
