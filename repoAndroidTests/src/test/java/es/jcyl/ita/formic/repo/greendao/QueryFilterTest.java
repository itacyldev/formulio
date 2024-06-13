package es.jcyl.ita.formic.repo.greendao;
/*
 * Copyright 2020 Gustavo Río (gustavo.rio@itacyl.es), ITACyL (http://www.itacyl.es).
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;

import androidx.test.platform.app.InstrumentationRegistry;

import org.greenrobot.greendao.database.StandardDatabase;
import org.greenrobot.greendao.query.Query;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.RobolectricTestRunner;

import java.util.List;

import es.jcyl.ita.formic.repo.Entity;
import es.jcyl.ita.formic.repo.Repository;
import es.jcyl.ita.formic.repo.builders.DevDbBuilder;
import es.jcyl.ita.formic.repo.db.sqlite.greendao.DaoMaster;
import es.jcyl.ita.formic.repo.db.sqlite.greendao.EntityDao;
import es.jcyl.ita.formic.repo.test.utils.RandomUtils;

/**
 * @author Gustavo Río (gustavo.rio@itacyl.es)
 */

@RunWith(RobolectricTestRunner.class)
public class QueryFilterTest {


    @Test
    public void testPaginateResults() {
        Context ctx = InstrumentationRegistry.getInstrumentation().getContext();
        DaoMaster.DevOpenHelper helper = new DaoMaster.DevOpenHelper(ctx, "my-db", null);
        SQLiteDatabase db = helper.getWritableDatabase();

        DevDbBuilder dbBuilder = new DevDbBuilder();
        int expectedNumRecords = 77;
        dbBuilder.withNumEntities(expectedNumRecords)
                .withPropertyValues("f1", new String[]{"a", "b", "c"})
                .build(new StandardDatabase(db));
//        dbBuilder.withNumEntities(expectedNumRecords).build(new StandardDatabase(db));

        Repository repo = dbBuilder.getSQLiteRepository();
        EntityDao dao = (EntityDao) repo.getImplementor();

        int pageSize = RandomUtils.randomInt(5, 17);
        int offset = 0;
        int numRecords = 0;
        List<Entity> lst = null;
        Query<Entity> query = dao.queryBuilder().limit(pageSize).offset(offset).build();
        do {
            query.setOffset(offset);
            lst = query.list();
            offset += pageSize;
            numRecords += lst.size();
        } while (lst.size() > 0);
        Assert.assertEquals(expectedNumRecords, numRecords);
    }

}
