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

import org.junit.AfterClass;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mini2Dx.collections.CollectionUtils;
import org.robolectric.RobolectricTestRunner;

import java.io.File;
import java.io.FileFilter;
import java.util.List;
import java.util.Set;

import es.jcyl.ita.formic.forms.App;
import es.jcyl.ita.formic.forms.components.FilterableComponent;
import es.jcyl.ita.formic.forms.components.UIComponentHelper;
import es.jcyl.ita.formic.forms.components.form.UIForm;
import es.jcyl.ita.formic.forms.components.view.UIView;
import es.jcyl.ita.formic.forms.config.DevConsole;
import es.jcyl.ita.formic.forms.config.elements.FormConfig;
import es.jcyl.ita.formic.forms.controllers.ViewController;
import es.jcyl.ita.formic.forms.project.Project;
import es.jcyl.ita.formic.forms.project.ProjectRepository;
import es.jcyl.ita.formic.repo.test.utils.TestUtils;

import static es.jcyl.ita.formic.repo.test.utils.AssertUtils.assertEquals;

/**
 * @author Gustavo Río (gustavo.rio@itacyl.es)
 * <p>
 * Tests to check project config reading
 */
@RunWith(RobolectricTestRunner.class)
public class ProjectConfigIntegrationTest {

    @BeforeClass
    public static void setUp() {
        App.init("");
        // register repos
        DevConsole.setLevel(Log.DEBUG);
    }

    @AfterClass
    public static void tearDown() {
//        RepositoryFactory.getInstance().clear();
    }

    /**
     * Check formEdit and formList are properly created
     */
    @Test
    @Ignore
    public void testFormConfig() throws Exception {
        File baseFolder = TestUtils.findFile("config");
        App.init(baseFolder.getAbsolutePath());
        App app = App.getInstance();

        ProjectRepository projectRepo = app.getProjectRepo();
        Assert.assertNotNull(projectRepo);
        List<Project> projectList = projectRepo.listAll();

        Assert.assertTrue(CollectionUtils.isNotEmpty(projectList));
        Project prj = projectList.get(0);
        prj.open();
        assertEquals("project1", prj.getName());
        Assert.assertTrue(CollectionUtils.isNotEmpty(prj.getConfigFiles()));
        // read config and check repositories and forms

        app.openProject(prj);
        // there must be 4 repos
        Set<String> repoIds = app.getRepoFactory().getRepoIds();

        // there must be three form configs
        List<FormConfig> formConfigs = app.getProjectManager().getFormConfigRepo().listAll();
        int expectedNumForms = getNunFilesInFolder(TestUtils.findFile("config/project1/forms"));
        assertEquals(expectedNumForms, formConfigs.size());
    }

    private int getNunFilesInFolder(File folder) {
        File[] files = folder.listFiles(new FileFilter() {
            @Override
            public boolean accept(File f) {
                return !f.isDirectory();
            }
        });
        return files.length;
    }

    private void assertEditController(ViewController ctl) {
        UIView view = ctl.getView();
        List<UIForm> lst = UIComponentHelper.getChildrenByClass(view, UIForm.class);
        Assert.assertTrue(CollectionUtils.isNotEmpty(lst));
        UIForm form = lst.get(0);
        Assert.assertNotNull(form.getRepo());
    }

    private void assertListController(ViewController ctl) {
        UIView view = ctl.getView();
        List<FilterableComponent> lst = UIComponentHelper.getChildrenByClass(view, FilterableComponent.class);
        Assert.assertTrue(CollectionUtils.isNotEmpty(lst));
        FilterableComponent filterableComponent = lst.get(0);
        Assert.assertNotNull(filterableComponent.getRepo());
    }

}
