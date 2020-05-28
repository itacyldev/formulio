package es.jcyl.ita.frmdrd.config.integration;
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

import android.util.Log;

import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mini2Dx.collections.CollectionUtils;
import org.robolectric.RobolectricTestRunner;

import java.io.File;
import java.util.Collection;
import java.util.List;
import java.util.Set;

import es.jcyl.ita.crtrepo.Repository;
import es.jcyl.ita.crtrepo.meta.EntityMeta;
import es.jcyl.ita.crtrepo.meta.PropertyType;
import es.jcyl.ita.crtrepo.test.utils.TestUtils;
import es.jcyl.ita.frmdrd.config.Config;
import es.jcyl.ita.frmdrd.config.ConfigConverters;
import es.jcyl.ita.frmdrd.config.DevConsole;
import es.jcyl.ita.frmdrd.config.FormConfig;
import es.jcyl.ita.frmdrd.forms.FormController;
import es.jcyl.ita.frmdrd.forms.FormControllerFactory;
import es.jcyl.ita.frmdrd.forms.FormEditController;
import es.jcyl.ita.frmdrd.forms.FormListController;
import es.jcyl.ita.frmdrd.project.FormConfigRepository;
import es.jcyl.ita.frmdrd.project.Project;
import es.jcyl.ita.frmdrd.project.ProjectRepository;
import es.jcyl.ita.frmdrd.ui.components.UIComponentHelper;
import es.jcyl.ita.frmdrd.ui.components.autocomplete.UIAutoComplete;
import es.jcyl.ita.frmdrd.ui.components.datatable.UIDatatable;
import es.jcyl.ita.frmdrd.ui.components.form.UIForm;
import es.jcyl.ita.frmdrd.ui.components.view.UIView;

import static es.jcyl.ita.crtrepo.test.utils.AssertUtils.assertEquals;

/**
 * @author Gustavo Río (gustavo.rio@itacyl.es)
 * <p>
 * Tests to check commons-converters functionallity
 */
@RunWith(RobolectricTestRunner.class)
public class ProjectConfigIntegrationTest {

    @BeforeClass
    public static void setUp() {
        Config.init("");
        ConfigConverters confConverter = new ConfigConverters();
        confConverter.init();
        // register repos
        DevConsole.setLevel(Log.DEBUG);
    }


    /**
     * Check formEdit and formList are properly created
     */
    private static final String TEST_BASIC1 = "<main repo=\"contacts\"/>";

    @Test
    public void testFormConfig() throws Exception {
        File baseFolder = TestUtils.findFile("config");
        Config.init(baseFolder.getAbsolutePath());
        Config config = Config.getInstance();

        ProjectRepository projectRepo = config.getProjectRepo();
        Assert.assertNotNull(projectRepo);
        List<Project> projectList = projectRepo.listAll();

        Assert.assertTrue(CollectionUtils.isNotEmpty(projectList));
        Project prj = projectList.get(0);
        prj.open();
        assertEquals("project1", prj.getName());
        Assert.assertTrue(CollectionUtils.isNotEmpty(prj.getConfigFiles()));
        // read config and check repositories and forms

        config.readConfig(prj);
        // there must be 4 repos
        Set<String> repoIds = config.getRepoConfigReader().getRepoFactory().getRepoIds();
        assertEquals(4, repoIds.size());

        // there must be two form configs
        List<FormConfig> formConfigs = config.getFormConfigRepo().listAll();
        assertEquals(2, formConfigs.size());

        // there must be two list controllers and two editControllers
        FormControllerFactory fctlFacotry = FormControllerFactory.getInstance();
        Collection<FormController> ctlList = fctlFacotry.getList();
        assertEquals(4, ctlList.size());
        assertEquals(2, fctlFacotry.getListControllers().size());

        // check list controller
        for (FormController ctl : fctlFacotry.getList()) {
            if (ctl instanceof FormListController) {
                assertListController(ctl);
            } else {
                assertEditController(ctl);
            }
        }
    }


    @Test
    public void testForm2Configuration() throws Exception {
        File baseFolder = TestUtils.findFile("config");

        Config config = Config.init(baseFolder.getAbsolutePath());
        ProjectRepository projectRepo = config.getProjectRepo();

        File projectFolder = TestUtils.findFile("config/project1");
        Project prj = new Project(projectRepo.getSource(), projectRepo.getMeta());
        prj.setBaseFolder(projectFolder.getAbsolutePath());

        config.readConfig(prj);

        FormConfigRepository formConfigRepo = config.getFormConfigRepo();
        FormConfig formConfig = formConfigRepo.findById("form2");

        FormEditController edit = formConfig.getEdits().get(0);
        Assert.assertNotNull(edit.getMainForm());

        UIView view = edit.getView();
        List<UIAutoComplete> list = UIComponentHelper.findByClass(view, UIAutoComplete.class);
        Assert.assertEquals("agentsRepo", edit.getRepo().getId());
        Assert.assertTrue(CollectionUtils.isNotEmpty(list));
        UIAutoComplete auto = list.get(0);
        Assert.assertEquals("provRepo", auto.getRepo().getId());

        // check edit controllers

        UIForm form = UIComponentHelper.findFirstByClass(view, UIForm.class);
        Repository repo = form.getRepo();
        EntityMeta meta = repo.getMeta();
        PropertyType[] properties = meta.getProperties();
        Assert.assertEquals(properties.length + 1, form.getFields().size());

        // check all autos have a binding expresions
        List<UIAutoComplete> autos = UIComponentHelper.findByClass(view, UIAutoComplete.class);
        for(UIAutoComplete atc: autos){
            Assert.assertNotNull(atc.getValueExpression());
        }

    }

    private void assertEditController(FormController ctl) {
        UIView view = ctl.getView();
        List<UIForm> lst = UIComponentHelper.findByClass(view, UIForm.class);
        Assert.assertTrue(CollectionUtils.isNotEmpty(lst));
        UIForm form = lst.get(0);
        Assert.assertNotNull(form.getRepo());
    }

    private void assertListController(FormController ctl) {
        UIView view = ctl.getView();
        List<UIDatatable> lst = UIComponentHelper.findByClass(view, UIDatatable.class);
        Assert.assertTrue(CollectionUtils.isNotEmpty(lst));
        UIDatatable datatable = lst.get(0);
        Assert.assertNotNull(datatable.getRepo());
    }

}