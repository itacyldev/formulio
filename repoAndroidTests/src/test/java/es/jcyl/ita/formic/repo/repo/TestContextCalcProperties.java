package es.jcyl.ita.formic.repo.repo;
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

import android.content.Context;

import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.RobolectricTestRunner;

import java.util.List;

import androidx.test.platform.app.InstrumentationRegistry;
import es.jcyl.ita.formic.repo.EditableRepository;
import es.jcyl.ita.formic.repo.Entity;
import es.jcyl.ita.formic.repo.builders.DevDbBuilder;
import es.jcyl.ita.formic.core.context.CompositeContext;
import es.jcyl.ita.formic.core.context.impl.BasicContext;
import es.jcyl.ita.formic.core.context.impl.OrderedCompositeContext;
import es.jcyl.ita.formic.repo.db.meta.DBPropertyType;
import es.jcyl.ita.formic.repo.test.utils.RandomUtils;

import static es.jcyl.ita.formic.repo.db.meta.DBPropertyType.CALC_METHOD;
import static es.jcyl.ita.formic.repo.db.meta.DBPropertyType.CALC_METHOD.CONTEXT;
import static es.jcyl.ita.formic.repo.db.meta.DBPropertyType.CALC_MOMENT;
import static es.jcyl.ita.formic.repo.db.meta.DBPropertyType.CALC_MOMENT.INSERT;
import static es.jcyl.ita.formic.repo.db.meta.DBPropertyType.CALC_MOMENT.SELECT;
import static es.jcyl.ita.formic.repo.db.meta.DBPropertyType.CALC_MOMENT.UPDATE;
import static es.jcyl.ita.formic.repo.db.meta.DBPropertyType.DBPropertyTypeBuilder;

/**
 * @author Gustavo Río (gustavo.rio@itacyl.es)
 */
@RunWith(RobolectricTestRunner.class)
public class TestContextCalcProperties {

    @Test
    public void testOnSelectContextProps() {
        // create database with random table with 1 entity
        Context ctx = InstrumentationRegistry.getInstrumentation().getContext();

        // create database with one entity
        DevDbBuilder dbBuilder = new DevDbBuilder();
        dbBuilder.withNumEntities(1).build(ctx, "testDB");

        // get meta and set one of the properties as calculated from context
        DBPropertyType p = (DBPropertyType) dbBuilder.getMeta().getProperties()[1];
        DBPropertyType p2 = setPropertyAsCalculated(p, SELECT, CONTEXT, "b1.a");
        dbBuilder.getMeta().getProperties()[1] = p2;

        // set random value in context
        Object expectedValue = RandomUtils.randomObject(p2.type);
        CompositeContext globalCxt = createContext(expectedValue);

        // Use a repo to read from db and check entities have the expected values
        EditableRepository repo = dbBuilder.getSQLiteRepository(dbBuilder.getSource(), dbBuilder.getMeta(), globalCxt);
        List<Entity> lst = repo.listAll();
        Assert.assertEquals(expectedValue, lst.get(0).get(p2.name));
    }

    /**
     * Create empty database, insert one entity, and make sure the calculated values
     * are persisted reading them from the global context.
     */
    @Test
    public void testOnInsertContextProps() {
        // create database with random table with 1 entity
        Context ctx = InstrumentationRegistry.getInstrumentation().getContext();
        // create empty database
        DevDbBuilder dbBuilder = new DevDbBuilder();
        dbBuilder.withNumEntities(0).build(ctx, "testDB");

        // get meta and set one of the properties as calculated from context
        DBPropertyType[] props = (DBPropertyType[]) dbBuilder.getMeta().getProperties();
        DBPropertyType p2 = setPropertyAsCalculated(props[1], INSERT, CONTEXT, "b1.a");
        props[1] = p2;

        Object expectedValue = RandomUtils.randomObject(p2.type);
        CompositeContext globalCxt = createContext(expectedValue);

        // Use a repo to insert and entity, read it using its pk and check stored value
        Entity entity = dbBuilder.buildEntities(dbBuilder.getMeta(), 1).get(0);
        // the value for the calculated property for the entity must be null simulating and insert
        entity.set(p2.getName(), null);

        EditableRepository repo = dbBuilder.getSQLiteRepository(dbBuilder.getSource(), dbBuilder.getMeta(), globalCxt);

        repo.save(entity);
        Entity e2 = repo.findById(entity.getId());
        Assert.assertEquals(expectedValue, e2.get(p2.name));

        // now modify the entity, save it and check that the data hasn't been modified.
        entity.set(p2.getName(), RandomUtils.randomObject(p2.getType()));
        Object modifiedValue = RandomUtils.randomObject(props[2].getType());
        entity.set(props[2].getName(), modifiedValue);
        repo.save(entity);

        e2 = (Entity) repo.listAll().get(0);
        // modified value has been set, and calculated-on-insert property stays unmodified
        Assert.assertEquals(expectedValue, e2.get(p2.name));
        Assert.assertEquals(modifiedValue, e2.get(props[2].name));

    }

    /**
     * Create empty database, insert and update the entity. Check the persisted value has been modified both times.
     */
    @Test
    public void testOnUpdateContextProps() {
        // create database with random table with 1 entity
        Context ctx = InstrumentationRegistry.getInstrumentation().getContext();
        // create empty database
        DevDbBuilder dbBuilder = new DevDbBuilder();
        dbBuilder.withNumEntities(0).build(ctx, "testDB");

        // get meta and set one of the properties as calculated from context on each update
        DBPropertyType[] props = (DBPropertyType[]) dbBuilder.getMeta().getProperties();
        DBPropertyType p2 = setPropertyAsCalculated(props[1], UPDATE, CONTEXT, "b1.a");
        props[1] = p2;

        // create context
        Object expectedValue = RandomUtils.randomObject(p2.type);
        CompositeContext globalCxt = createContext(expectedValue);

        // Use a repo to insert and entity, read it using its pk and check stored value
        Entity entity = dbBuilder.buildEntities(dbBuilder.getMeta(), 1).get(0);

        // the value for the calculated property for the entity must be null simulating and insert
        entity.set(p2.getName(), null);
        EditableRepository repo = dbBuilder.getSQLiteRepository(dbBuilder.getSource(), dbBuilder.getMeta(), globalCxt);
        repo.save(entity);
        Entity e2 = repo.findById(entity.getId());
        Assert.assertEquals(expectedValue, e2.get(p2.name));

        // change context value and update entity
        expectedValue = RandomUtils.randomObject(p2.type);
        globalCxt.put("b1.a", expectedValue);
        repo.save(entity);
        e2 = repo.findById(entity.getId());
        Assert.assertEquals(expectedValue, e2.get(p2.name));
    }

    private CompositeContext createContext(Object expectedValue) {
        es.jcyl.ita.formic.core.context.Context dataCtx = new BasicContext("b1");
        dataCtx.put("a", expectedValue);
        CompositeContext globalCxt = new OrderedCompositeContext();
        globalCxt.addContext(dataCtx);
        return globalCxt;
    }

    private DBPropertyType setPropertyAsCalculated(DBPropertyType property, CALC_MOMENT when, CALC_METHOD how, String expression) {
        DBPropertyTypeBuilder builder = new DBPropertyTypeBuilder(property);
        switch (how) {
            case CONTEXT:
                builder.withContextExpression(expression, when);
                break;
            case SQL_EXPRESSION:
                builder.withSQLExpression(expression, when);
        }
        return builder.build();
    }
}
