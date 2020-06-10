package es.jcyl.ita.frmdrd.project;
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

import org.apache.commons.io.FileUtils;
import org.junit.AfterClass;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.RobolectricTestRunner;

import java.io.File;
import java.nio.charset.Charset;

import es.jcyl.ita.crtrepo.EditableRepository;
import es.jcyl.ita.crtrepo.RepositoryFactory;
import es.jcyl.ita.crtrepo.builders.EntityMetaDataBuilder;
import es.jcyl.ita.crtrepo.meta.EntityMeta;
import es.jcyl.ita.frmdrd.config.Config;
import es.jcyl.ita.frmdrd.config.ConfigConverters;
import es.jcyl.ita.frmdrd.config.ConfigurationException;
import es.jcyl.ita.frmdrd.project.handlers.RepositoryConfHandler;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

/**
 * @author Gustavo Río (gustavo.rio@itacyl.es)
 */
@RunWith(RobolectricTestRunner.class)
public class RepositoryConfHandlerTest {

    EntityMetaDataBuilder metaBuilder = new EntityMetaDataBuilder();

    @BeforeClass
    public static void setUp() {
        Config.init("");
        ConfigConverters confConverter = new ConfigConverters();
        confConverter.init();
        // register repos
    }

    @AfterClass
    public static void tearDown() {
        // remove invalid repos

    }

    /**
     * If a repo with no pk columns is defined the config process must fail
     */
    @Test
    public void testErrorWithNoPKRepo() throws Exception {
        // create a repo with a meta with no column. Mockup everything and call the handler to make it fail
        EntityMeta meta = metaBuilder.addProperties(new String[]{"one"}, new Class[]{String.class})
                .withIdProperties(null).build();
        EditableRepository repo = mock(EditableRepository.class);
        when(repo.getMeta()).thenReturn(meta);
        String REPO_ID = "RepositoryConfHandlerTestInvalidRepo";
        when(repo.getId()).thenReturn(REPO_ID);

        File emptyF = File.createTempFile("empty", ".xml");
        FileUtils.write(emptyF, "", Charset.defaultCharset());

        ProjectResource pr = new ProjectResource(emptyF, ProjectResource.ResourceType.DATA);

        RepositoryFactory.getInstance().register(REPO_ID, repo);

        RepositoryConfHandler handler = new RepositoryConfHandler();
        // make it read an empty f
        try {
            handler.handle(pr);
            Assert.fail("The test should've failed!.");
        } catch (ConfigurationException e) {
            e.getMessage().contains("The repository repo1 has no primary key defined");
        } finally {
            // teardown
            RepositoryFactory.getInstance().unregister(REPO_ID);
        }
    }

    /**
     * If a repo with no pk columns is defined the config process must fail
     */
    @Test
    public void testOkRepoReading() throws Exception {
        // create a repo with a meta with no column. Mockup everything and call the handler to make it fail
        EntityMeta meta = metaBuilder.withNumProps(1).build();
        EditableRepository repo = mock(EditableRepository.class);
        when(repo.getMeta()).thenReturn(meta);
        String REPO_ID = "RepositoryConfHandlerTestValidRepo";

        when(repo.getId()).thenReturn(REPO_ID);

        File emptyF = File.createTempFile("empty", ".xml");
        FileUtils.write(emptyF, "", Charset.defaultCharset());

        ProjectResource pr = new ProjectResource(emptyF, ProjectResource.ResourceType.DATA);

        RepositoryFactory.getInstance().register(REPO_ID, repo);

        RepositoryConfHandler handler = new RepositoryConfHandler();
        // make it read an empty f
        handler.handle(pr);

    }

}