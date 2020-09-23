package es.jcyl.ita.frmdrd.config.builders;
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

import es.jcyl.ita.crtrepo.RepositoryFactory;
import es.jcyl.ita.frmdrd.config.Config;
import es.jcyl.ita.frmdrd.config.ConfigConverters;
import es.jcyl.ita.frmdrd.config.FormConfig;
import es.jcyl.ita.frmdrd.project.Project;
import es.jcyl.ita.frmdrd.project.ProjectRepository;
import es.jcyl.ita.frmdrd.utils.RepositoryUtils;
import es.jcyl.ita.frmdrd.utils.XmlConfigUtils;

/**
 * @author Javier Ramos (javier.ramos@itacyl.es)
 */
@RunWith(RobolectricTestRunner.class)
public class FileRepoBuilderTest {

    @BeforeClass
    public static void setUp() {
        Config config = Config.init("");
        String projectPath = "src/test/resources/config/project1";
        Project p = ProjectRepository.createFromFolder(new File(projectPath));
        config.readConfig(p);
        ConfigConverters confConverter = new ConfigConverters();
        confConverter.init();

        // register repos
        RepositoryUtils.registerMock("contacts");
    }

    private RepositoryFactory repoFactory = RepositoryFactory.getInstance();


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
