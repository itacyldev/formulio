package es.jcyl.ita.formic.repo.meta;

import android.database.sqlite.SQLiteDatabase;

import org.greenrobot.greendao.database.StandardDatabase;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.robolectric.RobolectricTestRunner;

import java.io.File;
import java.util.Date;

import es.jcyl.ita.formic.repo.Entity;
import es.jcyl.ita.formic.repo.builders.DevDbBuilder;
import es.jcyl.ita.formic.repo.builders.EntityDataBuilder;
import es.jcyl.ita.formic.repo.builders.EntityMetaDataBuilder;
import es.jcyl.ita.formic.repo.db.meta.MaxRowIdKeyGenerator;
import es.jcyl.ita.formic.repo.db.meta.TimeStampKeyGenerator;
import es.jcyl.ita.formic.repo.db.meta.UUIDKeyGenerator;
import es.jcyl.ita.formic.repo.db.sqlite.SQLiteRepository;
import es.jcyl.ita.formic.repo.db.sqlite.greendao.EntityDao;
import es.jcyl.ita.formic.repo.meta.EntityMeta;
import es.jcyl.ita.formic.repo.test.utils.TestUtils;
import es.jcyl.ita.formic.repo.meta.types.ByteArray;

/**
 * Example local unit test, which will execute on the development machine (host).
 *
 * @see <a href="http://d.android.com/tools/testing">Testing documentation</a>
 */
@RunWith(RobolectricTestRunner.class)
public class KeyGeneratorStrategyTest {

    EntityMetaDataBuilder metaBuilder = new EntityMetaDataBuilder();

    /**
     * Tests UUID genrator making sure it only works if the pk type is String
     */
    @Test
    public void testUUIDGenerator() {
        UUIDKeyGenerator generator = new UUIDKeyGenerator();

        EntityMeta meta = metaBuilder.withNumProps(1).build();
        EntityDataBuilder entityBuilder = new EntityDataBuilder(meta);
        Entity entity = entityBuilder.withRandomData().build();

        EntityDao daoMock = Mockito.mock(EntityDao.class);

        Class[] pkTypes = new Class[]{String.class, Integer.class, Long.class, Double.class, ByteArray.class, Date.class};

        for (Class pkType : pkTypes) {
            if (generator.supports(pkType)) {
                Object value = generator.getKey(daoMock, entity, pkType);
                Assert.assertNotNull(value);
                Assert.assertEquals(pkType, value.getClass());
            } else {
                boolean hasfailed = false;
                try {
                    generator.getKey(daoMock, entity, pkType);
                } catch (Exception e) {
                    hasfailed = true;
                }
                Assert.assertTrue(hasfailed);
            }
        }
    }

    @Test
    public void testTimeStampGenerator() {
        TimeStampKeyGenerator generator = new TimeStampKeyGenerator();

        EntityMeta meta = metaBuilder.withNumProps(1).build();
        EntityDataBuilder entityBuilder = new EntityDataBuilder(meta);
        Entity entity = entityBuilder.withRandomData().build();

        EntityDao daoMock = Mockito.mock(EntityDao.class);

        Class[] pkTypes = new Class[]{String.class, Integer.class, Long.class, Double.class, ByteArray.class, Date.class};

        for (Class pkType : pkTypes) {
            if (generator.supports(pkType)) {
                Object value = generator.getKey(daoMock, entity, pkType);
                Assert.assertNotNull(value);
                Assert.assertEquals(pkType, value.getClass());
            } else {
                boolean hasfailed = false;
                try {
                    generator.getKey(daoMock, entity, pkType);
                } catch (Exception e) {
                    hasfailed = true;
                }
                Assert.assertTrue("It should have failed with type: " + pkType, hasfailed);
            }
        }
    }

    @Test
    public void testMaxRowIdGenerator() {
        MaxRowIdKeyGenerator generator = new MaxRowIdKeyGenerator();

        File dbFile = TestUtils.findFile("dbTest.sqlite");
        SQLiteDatabase db = SQLiteDatabase.openOrCreateDatabase(dbFile, null);

        // create entity source to table
        DevDbBuilder devBuilder = new DevDbBuilder();
        devBuilder.build(new StandardDatabase(db));
        SQLiteRepository repo = devBuilder.getSQLiteRepository();
        EntityDao dao = (EntityDao) repo.getImplementor();
        EntityMeta meta = devBuilder.getMeta();
        EntityDataBuilder entityBuilder = new EntityDataBuilder(meta);
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


}