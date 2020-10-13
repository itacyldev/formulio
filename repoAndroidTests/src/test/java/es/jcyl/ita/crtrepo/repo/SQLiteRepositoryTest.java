package es.jcyl.ita.crtrepo.repo;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;

import androidx.test.platform.app.InstrumentationRegistry;

import org.greenrobot.greendao.database.Database;
import org.greenrobot.greendao.database.StandardDatabase;
import org.junit.Assert;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.RobolectricTestRunner;

import java.io.File;
import java.util.List;

import es.jcyl.ita.crtrepo.EditableRepository;
import es.jcyl.ita.crtrepo.Entity;
import es.jcyl.ita.crtrepo.source.EntitySourceFactory;
import es.jcyl.ita.crtrepo.RepositoryFactory;
import es.jcyl.ita.crtrepo.builders.DevDbBuilder;
import es.jcyl.ita.crtrepo.builders.EntityDataBuilder;
import es.jcyl.ita.crtrepo.builders.EntityMetaDataBuilder;
import es.jcyl.ita.crtrepo.db.source.DBTableEntitySource;
import es.jcyl.ita.crtrepo.db.sqlite.SQLiteRepository;
import es.jcyl.ita.crtrepo.meta.EntityMeta;
import es.jcyl.ita.crtrepo.meta.PropertyType;
import es.jcyl.ita.crtrepo.test.utils.AssertUtils;
import es.jcyl.ita.crtrepo.test.utils.RandomUtils;
import es.jcyl.ita.crtrepo.test.utils.TestUtils;

/**
 * Example local unit test, which will execute on the development machine (host).
 *
 * @see <a href="http://d.android.com/tools/testing">Testing documentation</a>
 */
@RunWith(RobolectricTestRunner.class)
public class SQLiteRepositoryTest {

    // databuilders
    RepositoryFactory repoFactory = RepositoryFactory.getInstance();
    EntitySourceFactory sourceFactory = EntitySourceFactory.getInstance();

    @Test
    @Ignore("Just for creating test database")
    public void testCreateData() {
        EntityMetaDataBuilder metaBuilder = new EntityMetaDataBuilder();
        EntityMeta meta = metaBuilder.withRandomData().withNumProps(100).build();
        Context ctx = InstrumentationRegistry.getInstrumentation().getContext();
        Database db = DevDbBuilder.createDevSQLiteDb(ctx, "dbTestBig");

        DevDbBuilder dbBuilder = new DevDbBuilder();
        dbBuilder.withMeta(meta).withNumEntities(200000).build(db);
        System.out.println("go");
    }


    /**
     * Creates a table randomly using SQLite basic types and checks that the read metadata obtained
     * from the MetaModeler matches the expected properties
     */
    @Test
    public void testInsertAndListExistingDB() throws Exception {
//        Context ctx = InstrumentationRegistry.getInstrumentation().getContext();
        // create access to existing database
        File dbFile = TestUtils.findFile("dbTest.sqlite");
        SQLiteDatabase db = SQLiteDatabase.openOrCreateDatabase(dbFile, null);

        // create entity source to table
        DevDbBuilder devBuilder = new DevDbBuilder().withNumEntities(0);
        devBuilder.build(new StandardDatabase(db));

        EditableRepository repo = devBuilder.getSQLiteRepository();

        // Action: delete table, insert entities and fetchAll
        int expectedNumEntities = 150;
        List<Entity> lstEntities = DevDbBuilder.buildEntities(devBuilder.getMeta(), expectedNumEntities, null);
        repo.deleteAll();
        for (Entity e : lstEntities) {
            repo.save(e);
        }
        List list = repo.listAll();
        Assert.assertNotNull(list);
        Assert.assertEquals(expectedNumEntities, list.size());
    }

    /**
     * Creates a repository using and existing table, inserts new content
     *
     * @throws Exception
     */
    @Test
    public void testInsertInExistingTable() throws Exception {
//        Context ctx = InstrumentationRegistry.getInstrumentation().getContext();
        // create access to existing database
        File dbFile = TestUtils.findFile("dbTest.sqlite");
        SQLiteDatabase db = SQLiteDatabase.openOrCreateDatabase(dbFile, null);

        // create repository
        DBTableEntitySource entitySource = DevDbBuilder.createEntitySource("dbTest",
                "contacts", dbFile.getAbsolutePath(), new StandardDatabase(db));
        EditableRepository repo = DevDbBuilder.createSQLiteRepository(entitySource);


        // Action: delete table, insert entities and fetchAll
        int expectedNumEntities = 150;
        List<Entity> lstEntities = DevDbBuilder.buildEntities(repo.getMeta(), expectedNumEntities, null);
        repo.deleteAll();
        for (Entity e : lstEntities) {
            repo.save(e);
        }
        List list = repo.listAll();
        Assert.assertNotNull(list);
        Assert.assertEquals(expectedNumEntities, list.size());
    }

    /**
     * Users a repository to insert a new entity  with an emtpy id and checks the created entity
     * as the pk fields set.
     *
     * @throws Exception
     */
    @Test
    public void testInsertAndCheckPk() throws Exception {
        Context ctx = InstrumentationRegistry.getInstrumentation().getContext();

        Database db = DevDbBuilder.createDevSQLiteDb(ctx, "test");
        DevDbBuilder dbBuilder = new DevDbBuilder();
        dbBuilder.withNumEntities(10).build(db);
        SQLiteRepository repo = dbBuilder.getSQLiteRepository();

        // create new entity
        EntityDataBuilder entityBuilder = new EntityDataBuilder(dbBuilder.getMeta());
        Entity newEntity = entityBuilder.withRandomData().build();
        // set id to null to make sure it is created by the repository
        //db builder creates meta with single-column props
        String idProperty = dbBuilder.getMeta().getIdPropertiesName()[0];
        newEntity.setId(null);
        repo.save(newEntity);
        Assert.assertNotNull(newEntity.getId());

        // try to find the entity using the give Id and make sure the data match
        Entity dbEntity = repo.findById(newEntity.getId());
        Assert.assertNotNull(dbEntity);
        AssertUtils.assertEquals(newEntity, dbEntity);
    }

    /**
     * Insert entity, delete and make sure it is not in the bd anymore
     *
     * @throws Exception
     */
    @Test
    public void testDeleteByPK() throws Exception {

        Context ctx = InstrumentationRegistry.getInstrumentation().getContext();
        // create empty database
        DevDbBuilder dbBuilder = new DevDbBuilder();
        dbBuilder.withNumEntities(0).build(ctx, "testDB");
        EditableRepository repo = dbBuilder.getSQLiteRepository();

        // Action: delete table, insert entities and fetchAll
        List<Entity> lstEntities = DevDbBuilder.buildEntities(dbBuilder.getMeta(), 1, null);
        Entity entity = lstEntities.get(0);
        repo.save(entity);
        // assert it's in the BD
        Entity e1 = repo.findById(entity.getId());
        Assert.assertNotNull(e1);

        // delete and check it doesn't exists
        repo.deleteById(entity.getId());
        Entity e2 = repo.findById(entity.getId());
        Assert.assertNull(e2);
    }

    /**
     * Checks update consistency
     *
     * @throws Exception
     */
    @Test
    public void testUpdateEntity() throws Exception {
        Context ctx = InstrumentationRegistry.getInstrumentation().getContext();
        // create empty database
        DevDbBuilder dbBuilder = new DevDbBuilder();
        dbBuilder.withNumEntities(0).build(ctx, "testDB");

        EditableRepository repo = dbBuilder.getSQLiteRepository();

        // Action: delete table, insert entities and fetchAll
        List<Entity> lstEntities = DevDbBuilder.buildEntities(dbBuilder.getMeta(), 1, null);
        Entity entity = lstEntities.get(0);
        repo.save(entity);
        // update some information randomly
        PropertyType property = entity.getMetadata().getProperties()[1];
        Object newValue = RandomUtils.randomObject(property.getType());
        entity.set(property.getName(), newValue);
        repo.save(entity);

        // retrieve from db and assert
        Entity e2 = repo.findById(entity.getId());

        Assert.assertNotNull(e2);
        Assert.assertEquals(newValue, e2.get(property.getName()));
    }


    @Test
    public void testCount() throws Exception {
        Context ctx = InstrumentationRegistry.getInstrumentation().getContext();
        // create empty database
        DevDbBuilder dbBuilder = new DevDbBuilder();
        int expected = RandomUtils.randomInt(2, 55);
        dbBuilder.withNumEntities(expected).build(ctx, "testDB");

        EditableRepository repo = dbBuilder.getSQLiteRepository();

        Long actual = repo.count(null);
        Assert.assertEquals(expected, actual.intValue());
    }

}