package es.jcyl.ita.crtrepo.repo;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;

import org.greenrobot.greendao.database.Database;
import org.greenrobot.greendao.database.StandardDatabase;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.RobolectricTestRunner;

import java.io.File;
import java.util.List;

import androidx.test.platform.app.InstrumentationRegistry;
import es.jcyl.ita.crtrepo.source.EntitySource;
import es.jcyl.ita.crtrepo.Repository;
import es.jcyl.ita.crtrepo.RepositoryFactory;
import es.jcyl.ita.crtrepo.builders.DevDbBuilder;
import es.jcyl.ita.crtrepo.db.builders.RawSQLiteRepoBuilder;
import es.jcyl.ita.crtrepo.builders.RepositoryBuilder;
import es.jcyl.ita.crtrepo.context.impl.BasicContext;
import es.jcyl.ita.crtrepo.context.impl.OrderedCompositeContext;
import es.jcyl.ita.crtrepo.db.source.NativeSQLEntitySource;
import es.jcyl.ita.crtrepo.meta.EntityMeta;
import es.jcyl.ita.crtrepo.test.utils.RandomUtils;
import es.jcyl.ita.crtrepo.test.utils.TestUtils;

/**
 *
 */
@RunWith(RobolectricTestRunner.class)
public class RawSQLRepositoryTest {

    /**
     * Creates a table randomly using SQLite basic types and executes a native SQL using a
     * RawSQLRepository
     */
    @Test
    public void testSelectAll() throws Exception {
//        Context ctx = InstrumentationRegistry.getInstrumentation().getContext();
        // create access to existing database
        File dbFile = TestUtils.findFile("dbTest.sqlite");
        SQLiteDatabase db = SQLiteDatabase.openOrCreateDatabase(dbFile, null);
        Database dBase = new StandardDatabase(db);

        int numExpected = RandomUtils.randomInt(1, 15);
        DevDbBuilder devBuilder = new DevDbBuilder();
        devBuilder.withNumEntities(numExpected).build(dBase);
        String entityType = devBuilder.getMeta().getName();

        String query = "select * from " + entityType;
        EntitySource eSource = DevDbBuilder.createNativeSQLEntitySource("test", entityType,
                dbFile.getAbsolutePath(), dBase, query);
        RepositoryBuilder builder = RepositoryFactory.getInstance().getBuilder(eSource);
        builder.withProperty(RawSQLiteRepoBuilder.DB_ENTITY_SOURCE, eSource);
        builder.build();

        Repository repo = RepositoryFactory.getInstance().getRepo(eSource.getEntityTypeId());

        List list = repo.listAll();
        Assert.assertNotNull(list);
        Assert.assertEquals(numExpected, list.size());
    }

    @Test
    public void testReadMetaFromQuery() throws Exception {
        Context ctx = InstrumentationRegistry.getInstrumentation().getContext();
        DevDbBuilder devBuilder = new DevDbBuilder();
        devBuilder.withNumEntities(20).build(ctx);
        String entityType = devBuilder.getMeta().getName();

        String query = "select * from " + entityType;
        EntitySource eSource = DevDbBuilder.createNativeSQLEntitySource("test", entityType,
                "test", devBuilder.getDb(), query);

        RepositoryBuilder builder = RepositoryFactory.getInstance().getBuilder(eSource);
        builder.withProperty(RawSQLiteRepoBuilder.DB_ENTITY_SOURCE, eSource);
        builder.build();

        Repository repo = RepositoryFactory.getInstance().getRepo(eSource.getEntityTypeId());

        EntityMeta meta = repo.getMeta();
        Assert.assertNotNull(meta);
    }

    /**
     * Creates a populated database with 100 entities and uses a variable in context to filter how
     * many entities have to be retrieved.
     *
     * @throws Exception
     */
    @Test
    public void testSelectWithVarSubstitution() throws Exception {
        File dbFile = TestUtils.findFile("dbTest.sqlite");
        SQLiteDatabase db = SQLiteDatabase.openOrCreateDatabase(dbFile, null);
        Database dBase = new StandardDatabase(db);

        DevDbBuilder devBuilder = new DevDbBuilder();
        devBuilder.withNumEntities(100).build(dBase);

        int numExpected = RandomUtils.randomInt(0, 20);
        String entityType = devBuilder.getMeta().getName();
        String query = String.format("select * from %s limit ${b1.limit}", entityType);

        // create context
        BasicContext b1 = new BasicContext("b1");
        b1.put("limit", numExpected);
        OrderedCompositeContext ctx = new OrderedCompositeContext();
        ctx.addContext(b1);
        NativeSQLEntitySource sqlSource = DevDbBuilder.createNativeSQLEntitySource("testDB", entityType,
                dbFile.getAbsolutePath(), devBuilder.getDb(), query);
//        NativeSQLEntitySource sqlSource = new NativeSQLEntitySource(devBuilder.getDb(), query);
        Repository repo = devBuilder.getRawSQLiteRepoBuilder(sqlSource, ctx);

        List list = repo.listAll();
        Assert.assertNotNull(list);
        Assert.assertEquals(numExpected, list.size());

    }

}