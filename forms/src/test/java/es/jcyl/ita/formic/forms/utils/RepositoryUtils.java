package es.jcyl.ita.formic.forms.utils;
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

import org.mockito.Mockito;

import java.io.File;

import es.jcyl.ita.formic.forms.project.ProjectRepository;
import es.jcyl.ita.formic.repo.AbstractEditableRepository;
import es.jcyl.ita.formic.repo.Repository;
import es.jcyl.ita.formic.repo.RepositoryFactory;
import es.jcyl.ita.formic.repo.builders.DevDbBuilder;
import es.jcyl.ita.formic.repo.db.SQLQueryFilter;
import es.jcyl.ita.formic.repo.meta.EntityMeta;
import es.jcyl.ita.formic.repo.source.EntitySourceFactory;
import es.jcyl.ita.formic.repo.test.utils.TestUtils;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

/**
 * @author Gustavo Río (gustavo.rio@itacyl.es)
 */

public class RepositoryUtils {

    private static RepositoryFactory repoFactory = RepositoryFactory.getInstance();

    public static void register(String entityId, Repository repo) {
        repoFactory.register(entityId, repo);
    }

    public static ProjectRepository createTestProjectRepo() {
        File f = TestUtils.findFile("config/project1");
        return createProjectRepo(f);
    }

    public static ProjectRepository createProjectRepo(File baseFolder) {
        ProjectRepository repo = new ProjectRepository(baseFolder);
        return repo;
    }

    public static Repository registerMock(String id) {
        EntityMeta meta = DevDbBuilder.createRandomMeta();
        return registerMock(id, meta);
    }

    public static Repository registerMock(String id, EntityMeta meta) {
        AbstractEditableRepository mock = Mockito.mock(AbstractEditableRepository.class);
        when(mock.getMeta()).thenReturn(meta);
        when(mock.getFilterClass()).thenReturn(SQLQueryFilter.class);
        // force the mock to store entity mappings
        doCallRealMethod().when(mock).addMapping(any());
        doCallRealMethod().when(mock).getMappings();

        register(id, mock);
        return mock;
    }

    public static Repository getRepo(String id) {
        return repoFactory.getRepo(id);
    }

    public static void unregisterMock(String... repos) {
        for (String str : repos) {
            repoFactory.unregister(str);
        }
    }

    public static void clearSources() {
        // clear sources between tests to avoid problem with robolectric
        // https://github.com/robolectric/robolectric/issues/1890
        // https://stackoverflow.com/questions/34695552/robolectric-with-activeandroid-setup-nullpointerexception-on-activeandroidrefle
        EntitySourceFactory sourceFactory = EntitySourceFactory.getInstance();
        sourceFactory.clear();
    }
}
