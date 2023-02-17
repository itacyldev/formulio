package es.jcyl.ita.formic.repo;

import android.Manifest;
import android.content.Context;
import android.os.Environment;

import org.junit.Assert;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import androidx.test.ext.junit.runners.AndroidJUnit4;
import androidx.test.platform.app.InstrumentationRegistry;
import androidx.test.rule.GrantPermissionRule;
import es.jcyl.ita.formic.repo.builders.EntityDataBuilder;
import es.jcyl.ita.formic.repo.builders.EntityMetaDataBuilder;
import es.jcyl.ita.formic.repo.db.source.DBTableEntitySource;
import es.jcyl.ita.formic.repo.db.sqlite.converter.SQLiteConverterFactory;
import es.jcyl.ita.formic.repo.db.sqlite.converter.SQLitePropertyConverter;
import es.jcyl.ita.formic.repo.db.sqlite.greendao.DaoMaster;
import es.jcyl.ita.formic.repo.db.sqlite.greendao.DaoSession;
import es.jcyl.ita.formic.repo.db.sqlite.greendao.EntityDao;
import es.jcyl.ita.formic.repo.db.sqlite.greendao.EntityDaoConfig;
import es.jcyl.ita.formic.repo.db.spatialite.greendao.SpatialiteDataBase;
import es.jcyl.ita.formic.repo.meta.EntityMeta;
import es.jcyl.ita.formic.repo.meta.PropertyType;
import es.jcyl.ita.formic.repo.test.utils.AssertUtils;
import es.jcyl.ita.formic.repo.test.utils.TestUtils;
import jsqlite.Database;

/**
 * Instrumented test, which will execute on an Android device.
 *
 * @see <a href="http://d.android.com/tools/testing">Testing documentation</a>
 */
@RunWith(AndroidJUnit4.class)
public class BasicGreenDAODBAccessTest {

    private static final int PERMISSION_REQUEST = 1234;

    EntityMetaDataBuilder metaBuilder = new EntityMetaDataBuilder();
    SQLiteConverterFactory convFactory = SQLiteConverterFactory.getInstance();

    @Rule
    public GrantPermissionRule writePermisison
            = GrantPermissionRule.grant(Manifest.permission.WRITE_EXTERNAL_STORAGE);
    @Rule
    public GrantPermissionRule readPermisison
            = GrantPermissionRule.grant(Manifest.permission.READ_EXTERNAL_STORAGE);

    @Test
    public void testExistingSpatialiteDB() {

        //check sdcard availability
        Assert.assertEquals("Media is not mounted!", Environment.getExternalStorageState(), Environment.MEDIA_MOUNTED);

        Context appContext = InstrumentationRegistry.getInstrumentation().getTargetContext();

        File dbFile = TestUtils.copyTestResource("ribera.sqlite", "/sdcard/test/ribera.sqlite");
        if(!dbFile.exists()){
            Assert.fail("Test database not found: " + dbFile.getAbsolutePath());
        }


        SpatialiteDataBase db = new SpatialiteDataBase(dbFile.getAbsolutePath(), new Database());
        db.open(dbFile.getAbsolutePath(), jsqlite.Constants.SQLITE_OPEN_READWRITE);

        DaoMaster daoMaster = new DaoMaster(db);

        // create entity meta information
        EntityMeta meta = metaBuilder.withBasicTypes(true).withRandomData().build();
        DBTableEntitySource source = new DBTableEntitySource(db, meta.getName());
        String entityType = meta.getName();

        // create dao configuration
        Map<String, SQLitePropertyConverter> converters;
        converters = createDefaultConverters(meta);
        Map<String, String> mappers = createDefaultMapper(meta);
        EntityDaoConfig daoConfig = new EntityDaoConfig(meta, source);
        daoMaster.register(entityType, daoConfig);
        daoMaster.createAllTables(true);

        DaoSession daoSession = daoMaster.newSession();
        EntityDao dao = daoSession.getEntityDao(entityType);
        // create and insert test data
        List<Entity> lstEntities = createEntities(meta, entityType);
        for (Entity e : lstEntities) {
            daoSession.insert(e);
        }
        // try to retrieve first entity
        Entity entity = lstEntities.get(0);
        Entity e2 = dao.load(entity.getId());
        Assert.assertNotNull(e2);
        Assert.assertEquals(entity.getId(), e2.getId());
        AssertUtils.assertEquals(entity.getProperties(), e2.getProperties());
        Assert.assertEquals(entity.getProperties().size(), e2.getProperties().size());

    }

    private Map<String, String> createDefaultMapper(EntityMeta meta) {
        Map<String, String> mapper = new HashMap<String, String>();
        for (PropertyType p : meta.getProperties()) {
            mapper.put(p.getName(), p.getName()); // Property name as columnName
        }
        return mapper;
    }

    private Map<String, SQLitePropertyConverter> createDefaultConverters(EntityMeta meta) {
        Map<String, SQLitePropertyConverter> converters = new HashMap<String, SQLitePropertyConverter>();
        SQLitePropertyConverter conv;
        for (PropertyType p : meta.getProperties()) {
            conv = convFactory.getDefaultConverter(p.getType());
            converters.put(p.getName(), conv);
        }
        return converters;
    }


    private List<Entity> createEntities(EntityMeta meta, String entityType) {
        EntityDataBuilder entityBuilder = new EntityDataBuilder(meta);

        List<Entity> lst = new ArrayList<Entity>();
        for (int i = 0; i < 150; i++) {
            Entity entity = entityBuilder.withRandomData().build();
            lst.add(entity);
        }
        return lst;
    }
}
