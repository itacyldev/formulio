package es.jcyl.ita.frmdrd.config.resolvers;
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

import org.junit.AfterClass;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.RobolectricTestRunner;

import java.io.File;

import es.jcyl.ita.crtrepo.test.utils.TestUtils;
import es.jcyl.ita.frmdrd.config.Config;
import es.jcyl.ita.frmdrd.config.ConfigConverters;
import es.jcyl.ita.frmdrd.config.FormConfig;
import es.jcyl.ita.frmdrd.config.builders.ComponentBuilderFactory;
import es.jcyl.ita.frmdrd.project.Project;
import es.jcyl.ita.frmdrd.project.ProjectRepository;
import es.jcyl.ita.frmdrd.utils.RepositoryUtils;
import es.jcyl.ita.frmdrd.utils.XmlConfigUtils;

/**
 * @author Gustavo Río (gustavo.rio@itacyl.es)
 * <p>
 */
@RunWith(RobolectricTestRunner.class)
public class RelativePathAttResolverTest {

    @BeforeClass
    public static void setUp() {
        Config.init("");
        ConfigConverters confConverter = new ConfigConverters();
        confConverter.init();
        // register repos
        RepositoryUtils.registerMock("contacts");
    }

    private static final String XML_TEST_BASIC = "<repo id=\"myRepoTest01\" dbFile=\"dbTest.sqlite\" dbTable=\"contacts\"/>";

    @Test
    public void testAttributes() throws Exception {
        ProjectRepository prjRepo = RepositoryUtils.createTestProjectRepo();
        ComponentBuilderFactory factory = ComponentBuilderFactory.getInstance();
        File dataFolder = TestUtils.findFile("config/project1");
        Project p = prjRepo.createFromFolder(dataFolder);
        String xml = XmlConfigUtils.createMainList(XML_TEST_BASIC);

        FormConfig formConfig = XmlConfigUtils.readFormConfig(p, xml);

        Assert.assertNotNull(formConfig.getList().getRepo());
        // the repository is automatically assigned to the formlist
        Assert.assertEquals("myRepoTest01", formConfig.getList().getRepo().getId());
    }

    @AfterClass
    public static void tearDown() {
        RepositoryUtils.unregisterMock("contacts");
    }

}
