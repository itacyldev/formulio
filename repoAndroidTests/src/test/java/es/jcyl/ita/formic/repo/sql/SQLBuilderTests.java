package es.jcyl.ita.formic.repo.sql;

import android.content.Context;

import androidx.test.platform.app.InstrumentationRegistry;

import org.greenrobot.greendao.database.Database;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.RobolectricTestRunner;

import java.util.List;

import es.jcyl.ita.formic.repo.Entity;
import es.jcyl.ita.formic.repo.builders.DevDbBuilder;
import es.jcyl.ita.formic.repo.builders.EntityMetaDataBuilder;
import es.jcyl.ita.formic.repo.db.SQLQueryFilter;
import es.jcyl.ita.formic.repo.db.sqlite.SQLiteRepository;
import es.jcyl.ita.formic.repo.meta.EntityMeta;
import es.jcyl.ita.formic.repo.query.Condition;
import es.jcyl.ita.formic.repo.query.Criteria;
import es.jcyl.ita.formic.repo.db.query.RawWhereCondition;
import es.jcyl.ita.formic.repo.query.Sort;
import es.jcyl.ita.formic.repo.test.utils.AssertUtils;

/**
 *
 */
@RunWith(RobolectricTestRunner.class)
public class SQLBuilderTests {

    EntityMetaDataBuilder metaBuilder = new EntityMetaDataBuilder();

    /**
     * Test single field filtering
     */
    @Test
    public void testFilterWithSingleCondition() {
        Context ctx = InstrumentationRegistry.getInstrumentation().getContext();
        CreateDBAndPopulate createDBAndPopulate = new CreateDBAndPopulate(ctx).invoke();
        String filteringProp = createDBAndPopulate.getFilteringProp1();
        DevDbBuilder dbBuilder = createDBAndPopulate.getDbBuilder();
        SQLiteRepository repo = dbBuilder.getSQLiteRepository();

        // create filter
        SQLQueryFilter filter = new SQLQueryFilter();
        String expectedValue = "a";
        filter.setExpression(Condition.eq(filteringProp, expectedValue));
        List<Entity> entities = repo.find(filter);
        for (Entity e : entities) {
            Assert.assertEquals(expectedValue, e.get(filteringProp));
        }
    }

    @Test
    public void testFilterWithInOperator() {
        Context ctx = InstrumentationRegistry.getInstrumentation().getContext();
        CreateDBAndPopulate createDBAndPopulate = new CreateDBAndPopulate(ctx).invoke();
        String filteringProp = createDBAndPopulate.getFilteringProp2();
        DevDbBuilder dbBuilder = createDBAndPopulate.getDbBuilder();
        SQLiteRepository repo = dbBuilder.getSQLiteRepository();

        // create filter
        SQLQueryFilter filter = new SQLQueryFilter();
        String[] expectedValues = new String[]{"a", "b", "c"};
        filter.setExpression(Condition.in(filteringProp, expectedValues));
        List<Entity> entities = repo.find(filter);
        Assert.assertTrue(entities.size() > 0);
        for (Entity e : entities) {
            AssertUtils.assertAnyOf(expectedValues, e.get(filteringProp));
        }
    }


    @Test
    public void testFilterCombinedConditions() {
        Context ctx = InstrumentationRegistry.getInstrumentation().getContext();
        CreateDBAndPopulate createDBAndPopulate = new CreateDBAndPopulate(ctx).invoke();
        String filteringProp1 = createDBAndPopulate.getFilteringProp1();
        String filteringProp2 = createDBAndPopulate.getFilteringProp2();
        DevDbBuilder dbBuilder = createDBAndPopulate.getDbBuilder();
        SQLiteRepository repo = dbBuilder.getSQLiteRepository();

        // create filter
        SQLQueryFilter filter = new SQLQueryFilter();
        Long expectedValue1 = 2l;
        String[] expectedValues2 = new String[]{"a", "b", "c"};

        filter.setExpression(Criteria.and(new Condition[]{
                Condition.eq(filteringProp1, expectedValue1),
                Condition.in(filteringProp2, expectedValues2)
        }));
        List<Entity> entities = repo.find(filter);
        Assert.assertTrue(entities.size() > 0);
        for (Entity e : entities) {
            Assert.assertEquals(expectedValue1, e.get(filteringProp1));
            AssertUtils.assertAnyOf(expectedValues2, e.get(filteringProp2));
        }
    }

    @Test
    public void testFilterWithWhereCondition() {
        Context ctx = InstrumentationRegistry.getInstrumentation().getContext();
        CreateDBAndPopulate createDBAndPopulate = new CreateDBAndPopulate(ctx).invoke();
        String filteringProp = createDBAndPopulate.getFilteringProp1();
        DevDbBuilder dbBuilder = createDBAndPopulate.getDbBuilder();
        SQLiteRepository repo = dbBuilder.getSQLiteRepository();

        // create filter
        SQLQueryFilter filter = new SQLQueryFilter();
        String expectedValue = "a";
        String whereCondition = String.format("%s  = '%s'",filteringProp, expectedValue);
        filter.setExpression(RawWhereCondition.fromString(whereCondition));
        // equivalent to: filter.setCriteria(Condition.eq(filteringProp, expectedValue));

        List<Entity> entities = repo.find(filter);
        for (Entity e : entities) {
            Assert.assertEquals(expectedValue, e.get(filteringProp));
        }
    }

    @Test
    public void testSingleSorting() {
        Context ctx = InstrumentationRegistry.getInstrumentation().getContext();
        CreateDBAndPopulate createDBAndPopulate = new CreateDBAndPopulate(ctx).invoke();
        String filteringProp1 = createDBAndPopulate.getFilteringProp1();
        DevDbBuilder dbBuilder = createDBAndPopulate.getDbBuilder();
        SQLiteRepository repo = dbBuilder.getSQLiteRepository();

        // create filter descending sorting
        SQLQueryFilter filter = new SQLQueryFilter();
        filter.setSorting(new Sort[]{Sort.desc(filteringProp1)});

        List<Entity> entities = repo.find(filter);
        Assert.assertTrue(entities.size() > 0);
        Long lastValue = 999999999999l;
        for (Entity e : entities) {
            Long current = (Long) e.get(filteringProp1);
            Assert.assertTrue(lastValue >= current);
            lastValue = current;
        }
    }

    @Test
    public void testMultipleSorting() {
        Context ctx = InstrumentationRegistry.getInstrumentation().getContext();
        CreateDBAndPopulate createDBAndPopulate = new CreateDBAndPopulate(ctx).invoke();
        String filteringProp1 = createDBAndPopulate.getFilteringProp1();
        String filteringProp2 = createDBAndPopulate.getFilteringProp2();
        DevDbBuilder dbBuilder = createDBAndPopulate.getDbBuilder();
        SQLiteRepository repo = dbBuilder.getSQLiteRepository();

        // create filter descending sorting
        SQLQueryFilter filter = new SQLQueryFilter();
        filter.setSorting(new Sort[]{Sort.desc(filteringProp1), Sort.asc(filteringProp2)});

        List<Entity> entities = repo.find(filter);
        Assert.assertTrue(entities.size() > 0);
        Long lastValue = 999999999999l;
        String lastValueStr = "";
        for (Entity e : entities) {
            Long current = (Long) e.get(filteringProp1);
            String currentStr = (String) e.get(filteringProp2);
            Assert.assertTrue(lastValue >= current);
            Assert.assertTrue(lastValueStr.compareTo(currentStr) <= 0);
            lastValue = current;
        }
    }



    private class CreateDBAndPopulate {
        private Context ctx;
        private String filteringProp1;
        private String filteringProp2;
        private DevDbBuilder dbBuilder;

        public CreateDBAndPopulate(Context ctx) {
            this.ctx = ctx;
        }

        public String getFilteringProp1() {
            return filteringProp1;
        }

        public String getFilteringProp2() {
            return filteringProp2;
        }

        public DevDbBuilder getDbBuilder() {
            return dbBuilder;
        }

        public CreateDBAndPopulate invoke() {
            EntityMeta entityMeta = metaBuilder
                    .withBasicTypes(true)
                    .withNumProps(1)
                    .addProperties(new Class[]{Long.class, String.class})
                    .build();

//        EntityMeta entityMeta = DevDbBuilder.createRandomMeta();
            filteringProp1 = entityMeta.getProperties()[1].getName();
            filteringProp2 = entityMeta.getProperties()[2].getName();
            Database db = DevDbBuilder.createDevSQLiteDb(ctx, "test");
            // create a random entity meta, create table and populate with random data
            dbBuilder = new DevDbBuilder();
            dbBuilder.withMeta(entityMeta)
                    .withPropertyValues(filteringProp1, new Long[]{1l, 2l, 3l, 4l, 5l})
                    .withPropertyValues(filteringProp2, new String[]{"a", "b", "c", "d"})
                    .withNumEntities(50)
                    .build(db);
            return this;
        }
    }
}