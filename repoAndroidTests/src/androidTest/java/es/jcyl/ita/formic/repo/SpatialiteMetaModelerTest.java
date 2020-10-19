package es.jcyl.ita.formic.repo;

import android.Manifest;
import android.content.Context;
import android.os.Environment;

import org.junit.Assert;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.io.File;

import androidx.test.ext.junit.runners.AndroidJUnit4;
import androidx.test.platform.app.InstrumentationRegistry;
import androidx.test.rule.GrantPermissionRule;
import es.jcyl.ita.formic.repo.builders.EntityMetaDataBuilder;
import es.jcyl.ita.formic.repo.builders.EntitySourceBuilder;
import es.jcyl.ita.formic.repo.db.source.DBTableEntitySource;
import es.jcyl.ita.formic.repo.db.meta.GeometryType;
import es.jcyl.ita.formic.repo.db.spatialite.meta.SpatialitePropertyType;
import es.jcyl.ita.formic.repo.db.sqlite.converter.SQLiteConverterFactory;
import es.jcyl.ita.formic.repo.db.spatialite.converter.SpatialiteBlobConverter;
import es.jcyl.ita.formic.repo.db.spatialite.greendao.SpatialiteDataBase;
import es.jcyl.ita.formic.repo.db.sqlite.meta.SQLiteMetaModeler;
import es.jcyl.ita.formic.repo.db.spatialite.meta.SpatiaLiteMetaModeler;
import es.jcyl.ita.formic.repo.meta.EntityMeta;
import es.jcyl.ita.formic.repo.source.EntitySourceFactory;
import es.jcyl.ita.formic.repo.source.Source;
import jsqlite.Database;

/**
 * Test access to SQLite database using Greendao and Sqlite-spatialite drivers
 *
 * @see <a href="http://d.android.com/tools/testing">Testing documentation</a>
 */
@RunWith(AndroidJUnit4.class)
public class SpatialiteMetaModelerTest {

    private static final int PERMISSION_REQUEST = 1234;

    EntityMetaDataBuilder metaBuilder = new EntityMetaDataBuilder();
    SQLiteConverterFactory convFactory = SQLiteConverterFactory.getInstance();

    es.jcyl.ita.formic.repo.source.EntitySourceFactory sourceFactory = es.jcyl.ita.formic.repo.source.EntitySourceFactory.getInstance();
    RepositoryFactory repoFactory = RepositoryFactory.getInstance();

    @Rule
    public GrantPermissionRule writePermisison
            = GrantPermissionRule.grant(Manifest.permission.WRITE_EXTERNAL_STORAGE);
    @Rule
    public GrantPermissionRule readPermisison
            = GrantPermissionRule.grant(Manifest.permission.READ_EXTERNAL_STORAGE);

    private static final String SPATIALITE_SRC = "spatialite-src";
    private static final String SPATIALITE_ENTITY_TYPE = "spatialiteEntityType";


    /**
     * Read metadata from a table and check then returning EntityMeta contains the expected srid and geometry Type
     */
    @Test
    public void testExistingSpatialiteDB() {
        //check sdcard availability
        Assert.assertEquals("Media is not mounted!", Environment.getExternalStorageState(), Environment.MEDIA_MOUNTED);
        Context appContext = InstrumentationRegistry.getInstrumentation().getTargetContext();
        File dbFile = new File("/sdcard/test/ribera.sqlite");

        DBTableEntitySource source = createDBSources(dbFile);

        // read meta information from table using one of the sources
        SQLiteMetaModeler modeler = new SpatiaLiteMetaModeler();
        EntityMeta meta = modeler.readFromSource(source);

        Assert.assertTrue(meta.getPropertyByName("Geometry") instanceof SpatialitePropertyType);
        SpatialitePropertyType sp = (SpatialitePropertyType) meta.getPropertyByName("Geometry");
        Assert.assertEquals(25830, sp.getSrid());
        Assert.assertEquals(GeometryType.MULTIPOLYGON, sp.getGeometryType());
        // check converters
        Assert.assertTrue(sp.getConverter() instanceof SpatialiteBlobConverter);
    }


    private DBTableEntitySource createDBSources(File dbFile) {
        // create database and register the data source
        SpatialiteDataBase db = new SpatialiteDataBase(dbFile.getAbsolutePath(), new Database());
        sourceFactory.registerSource(new Source<>(SPATIALITE_SRC, dbFile.getAbsolutePath(), db));
        // create entity datasource
        EntitySourceBuilder builder = sourceFactory.getBuilder(EntitySourceFactory.SOURCE_TYPE.SQLITE);
        builder.withProperty(DBTableEntitySource.DBTableEntitySourceBuilder.ENTITY_TYPE_ID, SPATIALITE_ENTITY_TYPE);
        builder.withProperty(DBTableEntitySource.DBTableEntitySourceBuilder.SOURCE, sourceFactory.getSource(SPATIALITE_SRC));
        builder.withProperty(DBTableEntitySource.DBTableEntitySourceBuilder.TABLE_NAME, "inspecciones");
        // construction and automatic registering
        return (DBTableEntitySource) builder.build();
    }

}
