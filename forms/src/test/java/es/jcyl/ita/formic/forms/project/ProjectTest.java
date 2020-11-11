package es.jcyl.ita.formic.forms.project;
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

import es.jcyl.ita.formic.forms.config.Config;
import es.jcyl.ita.formic.forms.config.ConfigConverters;
import es.jcyl.ita.formic.repo.test.utils.TestUtils;

/**
 * @author Gustavo Río (gustavo.rio@itacyl.es)
 * <p>
 * Tests project reading from folder
 */
@RunWith(RobolectricTestRunner.class)
public class ProjectTest {

    @BeforeClass
    public static void setUp() {
        Config.init("");
        ConfigConverters confConverter = new ConfigConverters();
        confConverter.init();
        // register repos
    }

    @Test
    public void testReadProjectFromFolder() {
        File templateFolder = TestUtils.findFile("config");
        Assert.assertNotNull(templateFolder);
        Assert.assertTrue(templateFolder.exists());

        // read configuration
        ProjectRepository projectRepo = new ProjectRepository(templateFolder);
        List<Project> list = projectRepo.listAll();
        Assert.assertNotNull(list);
        Assert.assertEquals(1, list.size());
        // open project and assert read values
        Project prj = list.get(0);
        prj.open();;
        List<ProjectResource> configFiles = prj.getConfigFiles();
        Assert.assertEquals(7, configFiles.size());

        // check repositories are loaded
    }

    @Test
    public void testReadWithConfig(){
        File templateFolder = TestUtils.findFile("config");
        // read one project
        ProjectRepository projectRepo = new ProjectRepository(templateFolder);
        List<Project> list = projectRepo.listAll();
        // open project and assert read values
        Project prj = list.get(0);
        prj.open();;

        Config.init(templateFolder.getAbsolutePath());
        Config.getInstance().setCurrentProject(prj);
//        Config.getInstance().readConfig(prj);
    }

}
