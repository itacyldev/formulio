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

import org.junit.AfterClass;
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
import es.jcyl.ita.formic.repo.EntityMapping;
import es.jcyl.ita.formic.repo.Repository;
import es.jcyl.ita.formic.repo.RepositoryFactory;
import es.jcyl.ita.formic.repo.builders.EntityMetaDataBuilder;
import es.jcyl.ita.formic.repo.db.meta.DBPropertyType;
import es.jcyl.ita.formic.repo.meta.EntityMeta;
import es.jcyl.ita.formic.repo.test.utils.TestUtils;

/**
 * @author Gustavo Río (gustavo.rio@itacyl.es)
 */
@RunWith(RobolectricTestRunner.class)

public class EntityMappingBuilderTest {

    private RepositoryFactory repoFactory = RepositoryFactory.getInstance();

    @BeforeClass
    public static void setUp() {
        Config.init("");
        ConfigConverters confConverter = new ConfigConverters();
        confConverter.init();
        // register repos
        DevConsole.setLevel(Log.DEBUG);
        RepositoryUtils.registerMock("contacts");
        // related repo
        EntityMetaDataBuilder metaBuilder = new EntityMetaDataBuilder();
        EntityMeta<DBPropertyType> meta = metaBuilder.withNumProps(0)
                .addProperties(new String[]{"prop1", "prop2"},
                        new Class[]{String.class, String.class, String.class})
                .build();
        RepositoryUtils.registerMock("relatedRepo", meta);
    }

    @Before
    public void before() {
        RepositoryUtils.clearSources();
    }

    private static final String XML_TEST_BASIC = "<repo id=\"test1\" dbFile=\"dbTest.sqlite\" " +
            "id=\"superHRepo\" dbTable=\"superheroes\">%s</repo>";

    @Test
    public void testRelatedRepo() {
        // create basic project
        ProjectRepository prjRepo = RepositoryUtils.createTestProjectRepo();
        File dataFolder = TestUtils.findFile("config/project1");
        Project p = prjRepo.createFromFolder(dataFolder);
        // Act: define and read XML
        String mappingTag = "<mapping repo=\"relatedRepo\" property=\"related\" fk=\"enemy_id\"" +
                "insertable=\"false\" updatable=\"false\" deletable=\"true\" "+
                "/>";
        String xml = XmlConfigUtils.createMainList(String.format(XML_TEST_BASIC, mappingTag));
        XmlConfigUtils.readFormConfig(p, xml);

        Repository repo = repoFactory.getRepo("superHRepo");
        Assert.assertNotNull(repo.getMappings());
        EntityMapping mapping = (EntityMapping) repo.getMappings().get(0);
        Assert.assertFalse(mapping.isInsertable());
        Assert.assertFalse(mapping.isUpdatable());
        Assert.assertTrue(mapping.isDeletable());
        Assert.assertTrue(mapping.getFk().equalsIgnoreCase("enemy_id"));
        Assert.assertTrue(mapping.getProperty().equalsIgnoreCase("related"));

    }
    @AfterClass
    public static void tearDown(){
        RepositoryUtils.unregisterMock("relatedRepo");
    }
}
