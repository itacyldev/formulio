package es.jcyl.ita.crtrepo.meta;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;

import org.greenrobot.greendao.database.StandardDatabase;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.RobolectricTestRunner;

import java.io.File;
import java.util.HashMap;
import java.util.Map;

import androidx.test.platform.app.InstrumentationRegistry;
import es.jcyl.ita.crtrepo.builders.EntityMetaDataBuilder;
import es.jcyl.ita.crtrepo.db.source.DBTableEntitySource;
import es.jcyl.ita.crtrepo.db.sqlite.converter.SQLiteConverterFactory;
import es.jcyl.ita.crtrepo.db.sqlite.converter.SQLitePropertyConverter;
import es.jcyl.ita.crtrepo.db.sqlite.greendao.DaoMaster;
import es.jcyl.ita.crtrepo.db.sqlite.greendao.EntityDaoConfig;
import es.jcyl.ita.crtrepo.db.sqlite.meta.SQLiteMetaModeler;
import es.jcyl.ita.crtrepo.test.utils.AssertUtils;
import es.jcyl.ita.crtrepo.test.utils.TestUtils;

/**
 * Example local unit test, which will execute on the development machine (host).
 *
 * @see <a href="http://d.android.com/tools/testing">Testing documentation</a>
 */
@RunWith(RobolectricTestRunner.class)
public class MetaModelerTest {

    // databuilders
    EntityMetaDataBuilder metaBuilder = new EntityMetaDataBuilder();
    SQLiteConverterFactory convFactory = SQLiteConverterFactory.getInstance();

    /**
     * Creates a table randomly using SQLite basic types and checks that the read metadata obtained
     * from the MetaModeler matches the expected properties
     */
    @Test
    public void testCreateRandomTable() {
        Context ctx = InstrumentationRegistry.getInstrumentation().getContext();
        DaoMaster.DevOpenHelper helper = new DaoMaster.DevOpenHelper(ctx, "my-db", null);
        SQLiteDatabase db = helper.getWritableDatabase();

        DaoMaster daoMaster = new DaoMaster(db);

        // create entity meta information
        EntityMeta meta = metaBuilder.withBasicTypes(true).withRandomData().build();
        DBTableEntitySource source = new DBTableEntitySource(new StandardDatabase(db), meta.getName());
        String entityType = meta.getName();

        // create dao configuration
        EntityDaoConfig daoConfig = new EntityDaoConfig(meta, source);
        daoMaster.register(entityType, daoConfig);
        daoMaster.createAllTables(true);

        // read table properties
        SQLiteMetaModeler modeler = new SQLiteMetaModeler();
        EntityMeta meta2 = modeler.readFromSource(source);
        AssertUtils.assertEquals(meta.getProperties(), meta2.getProperties());
    }

    @Test
    public void testReadExistingDB() {
        File dbFile = TestUtils.findFile("dbTest.sqlite");
        SQLiteDatabase db = SQLiteDatabase.openOrCreateDatabase(dbFile, null);

        // create entity meta information
        String entityType = "contacts";
        DBTableEntitySource source = new DBTableEntitySource(new StandardDatabase(db),entityType);

        // read table properties
        SQLiteMetaModeler modeler = new SQLiteMetaModeler();
        EntityMeta meta = modeler.readFromSource(source);
        Assert.assertNotNull(meta);
        Assert.assertNotNull(meta.getProperties());
        Assert.assertTrue(meta.getProperties().length > 0);
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


}