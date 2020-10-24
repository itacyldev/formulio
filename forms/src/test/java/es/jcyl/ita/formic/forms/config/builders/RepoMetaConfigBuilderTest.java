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

import android.util.Log;

import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mini2Dx.collections.CollectionUtils;
import org.robolectric.RobolectricTestRunner;

import java.io.File;
import java.util.List;

import es.jcyl.ita.formic.forms.config.Config;
import es.jcyl.ita.formic.forms.config.ConfigConverters;
import es.jcyl.ita.formic.forms.config.DevConsole;
import es.jcyl.ita.formic.forms.project.Project;
import es.jcyl.ita.formic.forms.project.ProjectRepository;
import es.jcyl.ita.formic.repo.Repository;
import es.jcyl.ita.formic.repo.RepositoryFactory;
import es.jcyl.ita.formic.repo.test.utils.TestUtils;

import static es.jcyl.ita.formic.repo.test.utils.AssertUtils.assertEquals;

/**
 * <Your description here>
 *
 * @author Gustavo Río (gustavo.rio@itacyl.es)
 */
@RunWith(RobolectricTestRunner.class)

public class RepoMetaConfigBuilderTest {

    private RepositoryFactory repoFactory = RepositoryFactory.getInstance();

    private static Project mainProject;

    @BeforeClass
    public static void setUp() {
        Config.init("");
        ConfigConverters confConverter = new ConfigConverters();
        confConverter.init();
        // register repos
        DevConsole.setLevel(Log.DEBUG);
    }

    private static final String XML_TEST_BASIC = "<repo id=\"test1\" dbFile=\"dbTest.sqlite\" " +
            "id=\"superHRepo\" dbTable=\"superheroes\" properties=\"name,age\"/>";

    @Test
    public void test1() throws Exception {
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

        Repository repo = repoFactory.getRepo("superHRepo");
        Assert.assertNotNull(repo);
        Assert.assertEquals(2,repo.getMeta().getProperties().length);
        Assert.assertNotNull(repo.getMeta().getPropertyByName("name"));
        Assert.assertNotNull(repo.getMeta().getPropertyByName("age"));

    }
}
