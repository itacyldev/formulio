package es.jcyl.ita.formic.forms.integration;
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

import es.jcyl.ita.formic.forms.components.FilterableComponent;
import es.jcyl.ita.formic.forms.components.UIComponentHelper;
import es.jcyl.ita.formic.forms.components.autocomplete.UIAutoComplete;
import es.jcyl.ita.formic.forms.components.form.UIForm;
import es.jcyl.ita.formic.forms.components.view.UIView;
import es.jcyl.ita.formic.forms.config.Config;
import es.jcyl.ita.formic.forms.config.ConfigConverters;
import es.jcyl.ita.formic.forms.config.DevConsole;
import es.jcyl.ita.formic.forms.config.elements.FormConfig;
import es.jcyl.ita.formic.forms.controllers.FormController;
import es.jcyl.ita.formic.forms.controllers.FormControllerFactory;
import es.jcyl.ita.formic.forms.controllers.FormEditController;
import es.jcyl.ita.formic.forms.controllers.FormListController;
import es.jcyl.ita.formic.forms.project.FormConfigRepository;
import es.jcyl.ita.formic.forms.project.Project;
import es.jcyl.ita.formic.forms.project.ProjectRepository;
import es.jcyl.ita.formic.repo.Repository;
import es.jcyl.ita.formic.repo.meta.EntityMeta;
import es.jcyl.ita.formic.repo.meta.PropertyType;
import es.jcyl.ita.formic.repo.test.utils.TestUtils;

import static es.jcyl.ita.formic.repo.test.utils.AssertUtils.assertEquals;

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

        config.setCurrentProject(prj);
        // there must be 4 repos
        Set<String> repoIds = config.getRepoConfigReader().getRepoFactory().getRepoIds();

        // there must be three form configs
        List<FormConfig> formConfigs = config.getFormConfigRepo().listAll();
        int expectedNumForms = TestUtils.findFile("config/project1/forms").list().length;
        assertEquals(expectedNumForms, formConfigs.size());

        // Check all list and edit controller have been loaded
        FormControllerFactory fctlFacotry = FormControllerFactory.getInstance();
        Collection<FormController> ctlList = fctlFacotry.getList();
        assertEquals(14, ctlList.size());
        assertEquals(7, fctlFacotry.getListControllers().size());

        // check list controller
        for (FormController ctl : fctlFacotry.getList()) {
            if (ctl instanceof FormListController) {
                assertListController(ctl);
            } else {
                assertEditController(ctl);
            }
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
        List<FilterableComponent> lst = UIComponentHelper.findByClass(view, FilterableComponent.class);
        Assert.assertTrue(CollectionUtils.isNotEmpty(lst));
        FilterableComponent filterableComponent = lst.get(0);
        Assert.assertNotNull(filterableComponent.getRepo());
    }

}
