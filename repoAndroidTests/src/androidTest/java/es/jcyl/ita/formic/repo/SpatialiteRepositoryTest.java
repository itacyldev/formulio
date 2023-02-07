package es.jcyl.ita.formic.repo;

import android.Manifest;
import android.content.Context;
import android.os.Environment;

import androidx.test.ext.junit.runners.AndroidJUnit4;
import androidx.test.platform.app.InstrumentationRegistry;
import androidx.test.rule.GrantPermissionRule;

import org.apache.commons.io.FileUtils;
import org.junit.Assert;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import es.jcyl.ita.formic.repo.builders.EntityDataBuilder;
import es.jcyl.ita.formic.repo.builders.EntitySourceBuilder;
import es.jcyl.ita.formic.repo.builders.RepositoryBuilder;
import es.jcyl.ita.formic.repo.db.builders.SQLiteGreenDAORepoBuilder;
import es.jcyl.ita.formic.repo.db.source.DBTableEntitySource;
import es.jcyl.ita.formic.repo.db.spatialite.SpatialitePropertyBinder;
import es.jcyl.ita.formic.repo.db.spatialite.greendao.SpatialTableStatementsProvider;
import es.jcyl.ita.formic.repo.db.spatialite.greendao.SpatialiteDataBase;
import es.jcyl.ita.formic.repo.db.spatialite.meta.SpatiaLiteMetaModeler;
import es.jcyl.ita.formic.repo.db.sqlite.greendao.EntityDaoConfig;
import es.jcyl.ita.formic.repo.db.sqlite.meta.SQLiteMetaModeler;
import es.jcyl.ita.formic.repo.meta.EntityMeta;
import es.jcyl.ita.formic.repo.source.EntitySourceFactory;
import es.jcyl.ita.formic.repo.source.Source;
import es.jcyl.ita.formic.repo.test.utils.RandomUtils;
import es.jcyl.ita.formic.repo.meta.types.Geometry;
import es.jcyl.ita.formic.repo.test.utils.TestUtils;
import jsqlite.Database;

/**
 * Test access to SQLite database using Greendao-Repository and Sqlite-spatialite drivers
 *
 * @see <a href="http://d.android.com/tools/testing">Testing documentation</a>
 */
@RunWith(AndroidJUnit4.class)
public class SpatialiteRepositoryTest {

    private static final int PERMISSION_REQUEST = 1234;

    es.jcyl.ita.formic.repo.source.EntitySourceFactory sourceFactory = es.jcyl.ita.formic.repo.source.EntitySourceFactory.getInstance();
    RepositoryFactory repoFactory = RepositoryFactory.getInstance();

    @Rule
    public GrantPermissionRule writePermission
            = GrantPermissionRule.grant(Manifest.permission.WRITE_EXTERNAL_STORAGE);
    @Rule
    public GrantPermissionRule readPermission
            = GrantPermissionRule.grant(Manifest.permission.READ_EXTERNAL_STORAGE);

    private static final String SQLITE_SRC = "sqlite-src";
    private static final String SQLITE_ENTITY_TYPE = "sqliteEntityType";
    private static final String SPATIALITE_SRC = "spatialite-src";
    private static final String SPATIALITE_ENTITY_TYPE = "spatialiteEntityType";

    @Test
    public void testExistingSpatialiteDB() throws IOException {
        Assert.assertEquals("Media is not mounted!", Environment.getExternalStorageState(), Environment.MEDIA_MOUNTED);

        File dbFile = TestUtils.copyTestResource("ribera.sqlite", "/sdcard/test/ribera.sqlite");
        DBTableEntitySource source = createDBSources(dbFile);
        // read meta information from table using one of the sources
        SQLiteMetaModeler modeler = new SpatiaLiteMetaModeler();
        EntityMeta meta = modeler.readFromSource(source);

        // create repository using builder and register it
        RepositoryBuilder builder = RepositoryFactory.getInstance().getBuilder(source);
        EntityDaoConfig conf = new EntityDaoConfig(meta, source);
        conf.setPropertyBinder(new SpatialitePropertyBinder());
        conf.setTableStatementsProvider(new SpatialTableStatementsProvider());
        builder.withProperty(SQLiteGreenDAORepoBuilder.ENTITY_CONFIG, conf);
        builder.build(); // automatic registering

        // get repositories and insert/update data iteratively to the same table
        EditableRepository repo = repoFactory.getEditableRepo(source.getEntityTypeId());

        Long entityId = 1l; // existing entity
        Entity entity = repo.findById(entityId);
        Assert.assertNotNull(entity);
        // modify entity
        Geometry geo = RandomUtils.randomGeometry(25830);
        entity.set("Geometry", geo);
        repo.save(entity);

        // get the entity again and make sure changes has been applied
        entity = repo.findById(entityId);
        Geometry read = (Geometry) entity.get("Geometry");
        Assert.assertEquals(geo.getValue(), read.getValue());
    }

    /**
     * Deleting two entities in a row using spatialite repo makes the application fail.
     */
    /*@Test
    public void testsDeleteEntities() {

    }*/


    private DBTableEntitySource createDBSources(File dbFile) {
        // create database and register the data source
        SpatialiteDataBase db = new SpatialiteDataBase(dbFile.getAbsolutePath(), new Database());
        sourceFactory.registerSource(new Source<>(SPATIALITE_SRC, dbFile.getAbsolutePath(), db));
        // create entity dataSource
        EntitySourceBuilder builder = sourceFactory.getBuilder(EntitySourceFactory.SOURCE_TYPE.SQLITE);
        builder.withProperty(DBTableEntitySource.DBTableEntitySourceBuilder.ENTITY_TYPE_ID, SPATIALITE_ENTITY_TYPE);
        builder.withProperty(DBTableEntitySource.DBTableEntitySourceBuilder.SOURCE, sourceFactory.getSource(SPATIALITE_SRC));
        builder.withProperty(DBTableEntitySource.DBTableEntitySourceBuilder.TABLE_NAME, "inspecciones");
        // construction and automatic registering
        return (DBTableEntitySource) builder.build();
    }

    private List<Entity> createEntities(EntityMeta meta, int num) {
        EntityDataBuilder entityBuilder = new EntityDataBuilder(meta);

        List<Entity> lst = new ArrayList<Entity>();
        for (int i = 0; i < num; i++) {
            Entity entity = entityBuilder.withRandomData().build();
            lst.add(entity);
        }
        return lst;
    }
}
