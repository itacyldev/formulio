package es.jcyl.ita.formic.forms.config.builders.repo;
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
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.RobolectricTestRunner;

import java.io.File;

import es.jcyl.ita.formic.forms.config.Config;
import es.jcyl.ita.formic.forms.config.ConfigConverters;
import es.jcyl.ita.formic.forms.config.DevConsole;
import es.jcyl.ita.formic.forms.project.Project;
import es.jcyl.ita.formic.forms.project.ProjectRepository;
import es.jcyl.ita.formic.forms.utils.RepositoryUtils;
import es.jcyl.ita.formic.forms.utils.XmlConfigUtils;
import es.jcyl.ita.formic.repo.Repository;
import es.jcyl.ita.formic.repo.RepositoryFactory;
import es.jcyl.ita.formic.repo.db.meta.DBPropertyType;
import es.jcyl.ita.formic.repo.test.utils.TestUtils;

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
        RepositoryUtils.registerMock("contacts");

    }

    @Before
    public void before() {
        RepositoryUtils.clearSources();
    }

    private static final String XML_TEST_BASIC = "<repo id=\"test1\" dbFile=\"dbTest.sqlite\" " +
            "id=\"superHRepo\" dbTable=\"superheroes\">%s</repo>";

    @Test
    public void testAllPropsMeta() {
        // create basic project
        ProjectRepository prjRepo = RepositoryUtils.createTestProjectRepo();
        File dataFolder = TestUtils.findFile("config/project1");
        Project p = prjRepo.createFromFolder(dataFolder);

        // Act: define and read XML
        String metaTag = "<meta properties=\"*\"/>";
        String xml = XmlConfigUtils.createMainList(String.format(XML_TEST_BASIC, metaTag));
        XmlConfigUtils.readFormConfig(p, xml);

        Repository repo = repoFactory.getRepo("superHRepo");
        Assert.assertNotNull(repo);
        Assert.assertEquals(12, repo.getMeta().getProperties().length);
    }

    @Test
    public void testFilterProperties() {
        // create basic project
        ProjectRepository prjRepo = RepositoryUtils.createTestProjectRepo();
        File dataFolder = TestUtils.findFile("config/project1");
        Project p = prjRepo.createFromFolder(dataFolder);

        // Act: define and read XML
        String metaTag = "<meta properties=\"name,real_name\"/>";
        String xml = XmlConfigUtils.createMainList(String.format(XML_TEST_BASIC, metaTag));
        XmlConfigUtils.readFormConfig(p, xml);

        Repository repo = repoFactory.getRepo("superHRepo");
        Assert.assertNotNull(repo);
        Assert.assertEquals(2, repo.getMeta().getProperties().length);
        Assert.assertNotNull(repo.getMeta().getPropertyByName("name"));
        Assert.assertNotNull(repo.getMeta().getPropertyByName("real_name"));
    }

    @Test
    public void testCustomProperties() {
        // create basic project
        ProjectRepository prjRepo = RepositoryUtils.createTestProjectRepo();
        File dataFolder = TestUtils.findFile("config/project1");
        Project p = prjRepo.createFromFolder(dataFolder);

        // Act: define and read XML
        String metaTag = "<meta properties=\"name,age\">" +
                "<property name=\"real_identity\" columnName=\"real_name\"/>" +
                "<property name=\"real_identitie\" columnName=\"real_name\" converter=\"string\"/>" +
                "<property name=\"age\" columnName=\"age\" converter=\"float\"/>" +
                "</meta>";
        String xml = XmlConfigUtils.createMainList(String.format(XML_TEST_BASIC, metaTag));
        XmlConfigUtils.readFormConfig(p, xml);

        Repository repo = repoFactory.getRepo("superHRepo");
        Assert.assertNotNull(repo);
        Assert.assertEquals(4, repo.getMeta().getProperties().length);
        Assert.assertNotNull(repo.getMeta().getPropertyByName("real_identity"));
        Assert.assertNotNull(repo.getMeta().getPropertyByName("real_identitie"));
        DBPropertyType p1 = (DBPropertyType) repo.getMeta().getPropertyByName("real_identitie");
        Assert.assertNotNull(p1.getConverter());
        Assert.assertNotNull(p1.getConverter().javaType().equals(String.class));
        p1 = (DBPropertyType) repo.getMeta().getPropertyByName("age");
        Assert.assertNotNull(p1.getConverter());
        Assert.assertTrue(p1.getConverter().javaType().equals(Float.class));
    }

}
