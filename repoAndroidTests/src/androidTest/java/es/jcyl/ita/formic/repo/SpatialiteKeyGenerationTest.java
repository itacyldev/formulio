package es.jcyl.ita.formic.repo;

import android.database.sqlite.SQLiteDatabase;

import androidx.test.ext.junit.runners.AndroidJUnit4;

import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.io.File;
import java.util.Date;

import es.jcyl.ita.formic.repo.builders.EntityDataBuilder;
import es.jcyl.ita.formic.repo.EditableRepository;
import es.jcyl.ita.formic.repo.Entity;
import es.jcyl.ita.formic.repo.RepositoryFactory;
import es.jcyl.ita.formic.repo.builders.EntitySourceBuilder;
import es.jcyl.ita.formic.repo.builders.RepositoryBuilder;
import es.jcyl.ita.formic.repo.db.builders.SQLiteGreenDAORepoBuilder;
import es.jcyl.ita.formic.repo.db.meta.MaxRowIdKeyGenerator;
import es.jcyl.ita.formic.repo.db.source.DBTableEntitySource;
import es.jcyl.ita.formic.repo.db.spatialite.SpatialitePropertyBinder;
import es.jcyl.ita.formic.repo.db.spatialite.greendao.SpatialTableStatementsProvider;
import es.jcyl.ita.formic.repo.db.spatialite.greendao.SpatialiteDataBase;
import es.jcyl.ita.formic.repo.db.spatialite.meta.SpatiaLiteMetaModeler;
import es.jcyl.ita.formic.repo.db.sqlite.converter.SQLiteConverterFactory;
import es.jcyl.ita.formic.repo.db.sqlite.greendao.EntityDao;
import es.jcyl.ita.formic.repo.db.sqlite.greendao.EntityDaoConfig;
import es.jcyl.ita.formic.repo.meta.EntityMeta;
import es.jcyl.ita.formic.repo.meta.MetaModeler;
import es.jcyl.ita.formic.repo.meta.types.ByteArray;
import es.jcyl.ita.formic.repo.source.EntitySource;
import es.jcyl.ita.formic.repo.source.EntitySourceFactory;
import es.jcyl.ita.formic.repo.source.Source;

/**
 * Test MaxRowId generation strategy throw spatialite db access api.
 *
 * @see <a href="http://d.android.com/tools/testing">Testing documentation</a>
 */
@RunWith(AndroidJUnit4.class)
public class SpatialiteKeyGenerationTest {

    protected RepositoryFactory repoFactory = RepositoryFactory.getInstance();
    protected es.jcyl.ita.formic.repo.source.EntitySourceFactory sourceFactory = es.jcyl.ita.formic.repo.source.EntitySourceFactory.getInstance();
    protected SQLiteConverterFactory converterFactory = SQLiteConverterFactory.getInstance();

    @Test
    public void testMaxRowIdGenerator() {
        // create repository from spatialite DB
        createDBSource();
        createEntitySources();
        createRepositories();
        EditableRepository repo = repoFactory.getEditableRepo("inspecciones");
        EntityDao dao = (EntityDao) repo.getImplementor();

        MaxRowIdKeyGenerator generator = new MaxRowIdKeyGenerator();


        // create entity source to table
        EntityDataBuilder entityBuilder = new EntityDataBuilder(repo.getMeta());
        Entity entity = entityBuilder.withRandomData().build();


        Class[] pkTypes = new Class[]{String.class, Integer.class, Long.class, Double.class, ByteArray.class, Date.class};

        for (Class pkType : pkTypes) {
            if (generator.supports(pkType)) {
                Object value = generator.getKey(dao, entity, pkType);
                Assert.assertNotNull(value);
                Assert.assertEquals(pkType, value.getClass());
            } else {
                boolean hasfailed = false;
                try {
                    generator.getKey(dao, entity, pkType);
                } catch (Exception e) {
                    hasfailed = true;
                }
                Assert.assertTrue("It should have failed with type: " + pkType, hasfailed);
            }
        }
    }


    private void createRepositories() {
//         create repository against spatialite database
        EntitySource eSource = sourceFactory.getEntitySource("inspecciones");
        MetaModeler metaModeler = new SpatiaLiteMetaModeler();
        EntityMeta meta = metaModeler.readFromSource(eSource);
        RepositoryBuilder builder = repoFactory.getBuilder(eSource);
        EntityDaoConfig conf = new EntityDaoConfig(meta, (DBTableEntitySource) eSource);
        conf.setPropertyBinder(new SpatialitePropertyBinder());
        conf.setTableStatementsProvider(new SpatialTableStatementsProvider());
        builder.withProperty(SQLiteGreenDAORepoBuilder.ENTITY_CONFIG, conf);
        builder.build();
    }

    private void createEntitySources() {
        EntitySourceBuilder builder;
        builder = sourceFactory.getBuilder(EntitySourceFactory.SOURCE_TYPE.SQLITE);
        builder.withProperty(DBTableEntitySource.DBTableEntitySourceBuilder.SOURCE, this.sourceFactory.getSource("ribera"));
        builder.withProperty(DBTableEntitySource.DBTableEntitySourceBuilder.TABLE_NAME, "inspecciones");
        builder.withProperty(DBTableEntitySource.DBTableEntitySourceBuilder.ENTITY_TYPE_ID, "inspecciones");
        builder.build();
    }

    private void createDBSource() {
        File dbFile = new File("/sdcard/test/ribera.sqlite");
        SQLiteDatabase sqDb = SQLiteDatabase.openOrCreateDatabase(dbFile, null);
        SpatialiteDataBase db = new SpatialiteDataBase(dbFile.getAbsolutePath(), new jsqlite.Database());
        this.sourceFactory.registerSource(new Source<>("ribera", dbFile.getAbsolutePath(), db));
    }
}
