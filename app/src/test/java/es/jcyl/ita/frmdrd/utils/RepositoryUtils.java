package es.jcyl.ita.frmdrd.utils;
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

import es.jcyl.ita.crtrepo.EditableRepository;
import es.jcyl.ita.crtrepo.Repository;
import es.jcyl.ita.crtrepo.RepositoryFactory;
import es.jcyl.ita.crtrepo.builders.DevDbBuilder;
import es.jcyl.ita.crtrepo.db.SQLQueryFilter;
import es.jcyl.ita.crtrepo.meta.EntityMeta;
import es.jcyl.ita.crtrepo.test.utils.TestUtils;
import es.jcyl.ita.frmdrd.project.ProjectRepository;

import static org.mockito.Mockito.when;

/**
 * @author Gustavo Río (gustavo.rio@itacyl.es)
 */

public class RepositoryUtils {

    private static RepositoryFactory repoFactory = RepositoryFactory.getInstance();

    public static void register(String entityId, Repository repo) {
        repoFactory.register(entityId, repo);
    }

    public static ProjectRepository createTestProjectRepo(){
        File f = TestUtils.findFile("config/project1");
        return createProjectRepo(f);
    }

    public static ProjectRepository createProjectRepo(File baseFolder){
        ProjectRepository repo = new  ProjectRepository(baseFolder);
        return repo;
    }

    public static Repository registerMock(String id) {
        EntityMeta meta = DevDbBuilder.createRandomMeta();
        return registerMock(id, meta);
    }

    public static Repository registerMock(String id, EntityMeta meta) {
        EditableRepository mock = Mockito.mock(EditableRepository.class);
        when(mock.getMeta()).thenReturn(meta);
        when(mock.getFilterClass()).thenReturn(SQLQueryFilter.class);
        register(id, mock);
        return mock;
    }

    public static Repository getRepo(String id) {
        return repoFactory.getRepo(id);
    }

    public static void unregisterMock(String... repos) {
        for (String str : repos) {
            repoFactory.unregiser(str);
        }
    }
}
