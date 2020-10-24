package es.jcyl.ita.formic.forms.config.builders;
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

import org.junit.AfterClass;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.RobolectricTestRunner;

import java.io.File;

import es.jcyl.ita.formic.repo.RepositoryFactory;
import es.jcyl.ita.formic.forms.config.Config;
import es.jcyl.ita.formic.forms.config.ConfigConverters;
import es.jcyl.ita.formic.forms.config.FormConfig;
import es.jcyl.ita.formic.forms.project.Project;
import es.jcyl.ita.formic.forms.project.ProjectRepository;
import es.jcyl.ita.formic.forms.utils.RepositoryUtils;
import es.jcyl.ita.formic.forms.utils.XmlConfigUtils;

/**
 * @author Javier Ramos (javier.ramos@itacyl.es)
 */
@RunWith(RobolectricTestRunner.class)
public class FileRepoBuilderTest {
    private RepositoryFactory repoFactory = RepositoryFactory.getInstance();

    @BeforeClass
    public static void setUp() {
        Config config = Config.init("");
        String projectPath = "src/test/resources/config/project1";
        Project p = ProjectRepository.createFromFolder(new File(projectPath));
        config.setCurrentProject(p);
        ConfigConverters confConverter = new ConfigConverters();
        confConverter.init();

        // register repos
        RepositoryUtils.registerMock("contacts");
    }

    private static final String XML_TEST_BASIC = "<fileRepo id=\"fileRepo1\" folder=\".\"/>";


    @Test
    public void testBasicFileRepo() throws Exception {
        String xml = XmlConfigUtils.createEditForm(XML_TEST_BASIC);
        FormConfig formConfig = XmlConfigUtils.readFormConfig(xml);
        Assert.assertNotNull(repoFactory.getRepo("fileRepo1"));
    }

    @AfterClass
    public static void tearDown() {
        RepositoryUtils.unregisterMock("contacts");
    }

}
