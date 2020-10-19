package es.jcyl.ita.formic.repo.greendao;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;

import org.greenrobot.greendao.database.StandardDatabase;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.RobolectricTestRunner;

import java.util.Date;
import java.util.List;

import androidx.test.platform.app.InstrumentationRegistry;
import es.jcyl.ita.formic.repo.Entity;
import es.jcyl.ita.formic.repo.builders.DevDbBuilder;
import es.jcyl.ita.formic.repo.builders.EntityDataBuilder;
import es.jcyl.ita.formic.repo.builders.EntityMetaDataBuilder;
import es.jcyl.ita.formic.repo.db.source.DBTableEntitySource;
import es.jcyl.ita.formic.repo.db.meta.DBPropertyType;
import es.jcyl.ita.formic.repo.db.sqlite.converter.SQLiteConverterFactory;
import es.jcyl.ita.formic.repo.db.sqlite.greendao.DaoMaster;
import es.jcyl.ita.formic.repo.db.sqlite.greendao.DaoSession;
import es.jcyl.ita.formic.repo.db.sqlite.greendao.EntityDao;
import es.jcyl.ita.formic.repo.db.sqlite.greendao.EntityDaoConfig;
import es.jcyl.ita.formic.repo.meta.EntityMeta;
import es.jcyl.ita.formic.repo.test.utils.AssertUtils;

/**
 * Example local unit test, which will execute on the development machine (host).
 *
 * @see <a href="http://d.android.com/tools/testing">Testing documentation</a>
 */
@RunWith(RobolectricTestRunner.class)
public class GreenDAOIntegrationTest {

    // databuilders
    EntityMetaDataBuilder metaBuilder = new EntityMetaDataBuilder();
    EntityDataBuilder entityBuilder;
    SQLiteConverterFactory convFactory = SQLiteConverterFactory.getInstance();

    @Test
    public void testCreateTableSimplePk() {
        Context ctx = InstrumentationRegistry.getInstrumentation().getContext();
        DaoMaster.DevOpenHelper helper = new DaoMaster.DevOpenHelper(ctx, "my-db", null);
        SQLiteDatabase db = helper.getWritableDatabase();
        DaoMaster daoMaster = new DaoMaster(db);

        // create entity meta information
        Class[] pkClazzes = new Class[]{Integer.class, Long.class, String.class, Date.class, Float.class, Double.class};

        // Iterate testing different datatypes for one-column PK
        int p = 0;
        for (Class pkClazz : pkClazzes) {
            System.out.println("Testing: " + pkClazz.getName());

            EntityMeta<DBPropertyType> meta = metaBuilder.withRandomData().build();
            DBPropertyType pk = meta.getProperties()[0];
            meta.getProperties()[0] = new DBPropertyType.DBPropertyTypeBuilder(pk).build();
            DBTableEntitySource source = new DBTableEntitySource(new StandardDatabase(db), meta.getName());
            String entityType = meta.getName();

            // create green dao configuration
            DevDbBuilder.setDefaultConverters(meta);
            EntityDaoConfig daoConfig = new EntityDaoConfig(meta, source);
            daoMaster.register(entityType, daoConfig);
            daoMaster.createAllTables(true);

            DaoSession daoSession = daoMaster.newSession();
            EntityDao dao = daoSession.getEntityDao(entityType);
            // create and insert test data
            List<Entity> lstEntities = DevDbBuilder.buildEntities(meta, 10, null);
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
            p++;
        }
    }

}