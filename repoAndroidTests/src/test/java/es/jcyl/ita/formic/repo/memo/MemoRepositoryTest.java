package es.jcyl.ita.formic.repo.memo;
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

/**
 * @author Gustavo Río (gustavo.rio@itacyl.es)
 */

import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.RobolectricTestRunner;

import java.util.List;

import es.jcyl.ita.formic.repo.EditableRepository;
import es.jcyl.ita.formic.repo.Entity;
import es.jcyl.ita.formic.repo.Repository;
import es.jcyl.ita.formic.repo.RepositoryFactory;
import es.jcyl.ita.formic.repo.builders.DevDbBuilder;
import es.jcyl.ita.formic.repo.builders.RepositoryBuilder;
import es.jcyl.ita.formic.repo.memo.source.MemoSource;
import es.jcyl.ita.formic.repo.meta.EntityMeta;
import es.jcyl.ita.formic.repo.meta.PropertyType;
import es.jcyl.ita.formic.repo.query.BaseFilter;
import es.jcyl.ita.formic.repo.query.Filter;
import es.jcyl.ita.formic.repo.query.FilterRepoUtils;
import es.jcyl.ita.formic.repo.query.JexlEntityExpression;

@RunWith(RobolectricTestRunner.class)
public class MemoRepositoryTest {

    @Test
    public void testCreateRepo() throws Exception {
        RepositoryFactory factory = RepositoryFactory.getInstance();
        RepositoryBuilder repoBuilder = factory.getBuilder(new MemoSource("memoRepoTest"));

        Repository repo = repoBuilder.build();
        Assert.assertNotNull(repo);
        Assert.assertNotNull(repo.getMeta());
        Assert.assertNotNull(repo.getSource());
    }

    /**
     * Dynamically changes entity metadata
     *
     * @throws Exception
     */
    @Test
    public void testChangeMetadata() throws Exception {
        RepositoryFactory factory = RepositoryFactory.getInstance();
        RepositoryBuilder repoBuilder = factory.getBuilder(new MemoSource("memoRepoTest"));

        MemoRepository repo = (MemoRepository) repoBuilder.build();
        String[] propNames = {"f1", "f2", "f3"};
        repo.setPropertyNames(propNames);

        EntityMeta meta = repo.getMeta();
        Assert.assertNotNull(meta);
        PropertyType[] properties = meta.getProperties();
        // properties
        Assert.assertNotNull(properties);
        Assert.assertNotNull(properties.length == propNames.length + 1); // props+Id
        // check Id props
        Assert.assertNotNull(meta.getIdProperties());
        Assert.assertNotNull(meta.getIdProperties()[0].name.equalsIgnoreCase("id"));
    }

    @Test
    public void testCreateFilter() {
        RepositoryFactory factory = RepositoryFactory.getInstance();
        RepositoryBuilder repoBuilder = factory.getBuilder(new MemoSource("memoRepoTest"));

        Repository repo = repoBuilder.build();
        Assert.assertNotNull(repo.getFilterClass());
    }

    @Test
    public void testFilterEntities() {
        RepositoryFactory factory = RepositoryFactory.getInstance();
        RepositoryBuilder repoBuilder = factory.getBuilder(new MemoSource("memoRepoTest"));

        MemoRepository repo = (MemoRepository) repoBuilder.build();
        Assert.assertNotNull(repo.getFilterClass());

        // create random entities and set metadata to memoRepo
        List<Entity> entities = DevDbBuilder.buildEntitiesRandomMeta(10);
        EntityMeta meta = entities.get(0).getMetadata();
        repo.setPropertyNames(meta.getPropertyNames());
        // set entities in repo
        repo.save(entities);

        // create a filter with a JEXL expression to find the third entity by id
        Filter filter = FilterRepoUtils.createInstance(repo);
        Entity expedtedEntity = entities.get(3);
        String strExpression = String.format("${entity.id == '%s'}", expedtedEntity.getId());
        filter.setExpression(new JexlEntityExpression(strExpression));

        // execute query against the repo and assert the retrieved entity Id
        List<Entity> foundEntities = repo.find((BaseFilter) filter);
        Assert.assertEquals(1, foundEntities.size());
        Assert.assertEquals(expedtedEntity.getId(), foundEntities.get(0).getId());
    }

    @Test
    public void testInsertEntity() throws Exception {
        EditableRepository repo = new MemoRepository(new MemoSource("memoRepoTest"));
        Entity entity = repo.newEntity();
        Assert.assertNotNull(entity);

        // after creation is not in the repo
        Entity e = repo.findById(entity.getId());
        Assert.assertNull(e);

        repo.save(entity);
        e = repo.findById(entity.getId());
        Assert.assertNotNull(e);
    }

    @Test
    public void testDeleteEntity() throws Exception {
        EditableRepository repo = new MemoRepository(new MemoSource("memoRepoTest"));
        Entity entity = repo.newEntity();
        repo.save(entity);

        repo.delete(entity);
        Entity e = repo.findById(entity.getId());
        Assert.assertNull(e);
    }

}
