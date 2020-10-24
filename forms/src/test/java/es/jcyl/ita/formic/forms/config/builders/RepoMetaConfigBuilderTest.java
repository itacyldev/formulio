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

import es.jcyl.ita.formic.forms.config.Config;
import es.jcyl.ita.formic.forms.config.ConfigConverters;
import es.jcyl.ita.formic.forms.config.FormConfig;
import es.jcyl.ita.formic.forms.project.Project;
import es.jcyl.ita.formic.forms.project.ProjectRepository;
import es.jcyl.ita.formic.forms.utils.RepositoryUtils;
import es.jcyl.ita.formic.forms.utils.XmlConfigUtils;
import es.jcyl.ita.formic.repo.Repository;
import es.jcyl.ita.formic.repo.RepositoryFactory;

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
        Config config = Config.init("");
        String projectPath = "src/test/resources/config/project1";
        mainProject = ProjectRepository.createFromFolder(new File(projectPath));
        ConfigConverters confConverter = new ConfigConverters();
        config.setCurrentProject(mainProject);

        // register repos
        RepositoryUtils.registerMock("contacts");
    }

    private static final String XML_TEST_BASIC = "<repo id=\"test1\" dbFile=\"dbTest.sqlite\" " +
            "id=\"superHRepo\" dbTable=\"superheroes\" properties=\"name,age\"/>";

    @Test
    public void testBasicFileRepo() throws Exception {
        String xml = XmlConfigUtils.createEditForm(XML_TEST_BASIC);
        FormConfig formConfig = XmlConfigUtils.readFormConfig(mainProject,xml);
        Repository repo = repoFactory.getRepo("superHRepo");
        Assert.assertNotNull(repo);
        Assert.assertEquals(2,repo.getMeta().getProperties().length);
        Assert.assertNotNull(repo.getMeta().getPropertyByName("name"));
        Assert.assertNotNull(repo.getMeta().getPropertyByName("age"));

    }
}
