package es.jcyl.ita.crtrepo.sources;

import android.content.Context;

import org.apache.commons.lang3.RandomStringUtils;
import org.greenrobot.greendao.database.Database;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.RobolectricTestRunner;

import java.util.List;

import androidx.test.platform.app.InstrumentationRegistry;
import es.jcyl.ita.crtrepo.EditableRepository;
import es.jcyl.ita.crtrepo.Entity;
import es.jcyl.ita.crtrepo.source.EntitySource;
import es.jcyl.ita.crtrepo.source.EntitySourceFactory;
import es.jcyl.ita.crtrepo.RepositoryFactory;
import es.jcyl.ita.crtrepo.source.Source;
import es.jcyl.ita.crtrepo.builders.DevDbBuilder;
import es.jcyl.ita.crtrepo.builders.EntitySourceBuilder;
import es.jcyl.ita.crtrepo.builders.RepositoryBuilder;
import es.jcyl.ita.crtrepo.db.builders.SQLiteGreenDAORepoBuilder;
import es.jcyl.ita.crtrepo.db.source.DBTableEntitySource;
import es.jcyl.ita.crtrepo.db.sqlite.greendao.EntityDaoConfig;
import es.jcyl.ita.crtrepo.meta.EntityMeta;
import es.jcyl.ita.crtrepo.test.utils.RandomUtils;

/**
 * Example local unit test, which will execute on the development machine (host).
 *
 * @see <a href="http://d.android.com/tools/testing">Testing documentation</a>
 */
@RunWith(RobolectricTestRunner.class)
public class DatabaseEntitySourceTests {


    EntitySourceFactory factory = EntitySourceFactory.getInstance();
    RepositoryFactory repoFactory = RepositoryFactory.getInstance();

    /**
     * Test source creation using factory builders.
     */
    @Test
    public void testDBTableEntitySource() throws Exception {
        Context ctx = InstrumentationRegistry.getInstrumentation().getContext();
        Database db = createDB();

        String sourceId = "myDBSource";
        String entityType = RandomStringUtils.randomAlphanumeric(10);
        EntitySource eSource = createDBSource(entityType, "contacts", new Source(sourceId, "uri", db));

        // check entitySource info
        Assert.assertNotNull(eSource);
        Assert.assertEquals(sourceId, eSource.getSourceId());
        Assert.assertEquals(entityType, eSource.getEntityTypeId());
    }


    /**
     * Creates one database and multiple repository instances using the same source.
     * Check no side effect appears when database object is reused among repos.
     */
    @Test
    public void testReuseEntitySource() {
        Database db = createDB();
        String entityType;
        String sourceId = "commonSource";
        Source source = new Source(sourceId, "uri", db);

        for (int i = 0; i < 5; i++) {
            entityType = RandomStringUtils.randomAlphabetic(15).toUpperCase();
            // use same string for sourceId and entityId
            DBTableEntitySource eSource = createDBSource(entityType, entityType, source);
            // create random Meta and create related table to persist data
            EntityMeta meta = DevDbBuilder.createRandomMeta(entityType);
            DevDbBuilder.createTable(db, meta);
            DevDbBuilder.setDefaultConverters(meta);

            // obtain repository builder
            RepositoryBuilder builder = repoFactory.getBuilder(eSource);
            // create repository using repo builder using entityDaoConfig
            EntityDaoConfig conf = new EntityDaoConfig(meta, eSource);
            builder.withProperty(SQLiteGreenDAORepoBuilder.ENTITY_CONFIG, conf);
            builder.build();

            // create repository and insert some entities
            EditableRepository repo = repoFactory.getEditableRepo(entityType);

            // Action: delete table, insert entities and fetchAll
            int numEntities = RandomUtils.randomInt(0, 11);
            List<Entity> lstEntities = DevDbBuilder.buildEntities(meta, numEntities, null);
            for (Entity entity : lstEntities) {
                repo.save(entity);
            }
            long count = DevDbBuilder.countRecords(db, meta.getName());
            Assert.assertEquals(numEntities, count);
        }
    }

    /**
     * Creates empty database
     *
     * @return
     */
    private Database createDB() {
        Context ctx = InstrumentationRegistry.getInstrumentation().getContext();
        // create empty database
        DevDbBuilder dbBuilder = new DevDbBuilder();
        dbBuilder.withNumEntities(0).build(ctx, "testDB");
        Database db = dbBuilder.getDb();
        return db;
    }

    private DBTableEntitySource createDBSource(String entityType, String tableName, Source source) {
        EntitySourceBuilder builder = factory.getBuilder(EntitySourceFactory.SOURCE_TYPE.SQLITE);
        builder.withProperty(DBTableEntitySource.DBTableEntitySourceBuilder.SOURCE, source);
        builder.withProperty(DBTableEntitySource.DBTableEntitySourceBuilder.TABLE_NAME, tableName);
        builder.withProperty(DBTableEntitySource.DBTableEntitySourceBuilder.ENTITY_TYPE_ID, entityType);
        return (DBTableEntitySource) builder.build();
    }

}