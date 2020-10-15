package es.jcyl.ita.formic.repo.sql;
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

import org.greenrobot.greendao.database.Database;
import org.greenrobot.greendao.query.Query;
import org.greenrobot.greendao.query.QueryBuilder;
import org.greenrobot.greendao.query.WhereCondition;
import org.junit.Test;
import org.mockito.Mockito;

import es.jcyl.ita.formic.repo.Entity;
import es.jcyl.ita.formic.repo.builders.EntityMetaDataBuilder;
import es.jcyl.ita.formic.repo.db.source.DBTableEntitySource;
import es.jcyl.ita.formic.repo.db.ExpressionInterpreter;
import es.jcyl.ita.formic.repo.db.sqlite.greendao.EntityDao;
import es.jcyl.ita.formic.repo.db.sqlite.greendao.EntityDaoConfig;
import es.jcyl.ita.formic.repo.meta.EntityMeta;
import es.jcyl.ita.formic.repo.query.Condition;
import es.jcyl.ita.formic.repo.query.Criteria;

/**
 * @author Gustavo Río (gustavo.rio@itacyl.es)
 */
public class CriteriaInterpreterTests {

    EntityMetaDataBuilder metaBuilder = new EntityMetaDataBuilder();

    @Test
    public void testAndCriteria() {
        // create emptydatabase
        EntityDao dao = createEntityDao();

        QueryBuilder<Entity> queryBuilder = dao.queryBuilder();

        Criteria c = Criteria.and(Condition.eq("p1", 1),
                Condition.eq("p2", 2),
                Condition.eq("p3", 2));

        ExpressionInterpreter interpreter = new ExpressionInterpreter();
        WhereCondition where = interpreter.interpret(dao, queryBuilder, c);
        Query<Entity> query = queryBuilder.where(where).build();

        // very fragile testing, bu tcannot do more without access the internal whereCollector
        // without overloading greendao classes, and using spies will imply too much work constructing
        // the expected whereCond calls

    }

    @Test
    public void testOrCriteria() {

        EntityDao dao = createEntityDao();

        QueryBuilder<Entity> queryBuilder = dao.queryBuilder();

        Criteria c = Criteria.or(Condition.eq("p1", 1),
                Condition.eq("p2", 2),
                Condition.eq("p3", 2));

        ExpressionInterpreter interpreter = new ExpressionInterpreter();
        WhereCondition where = interpreter.interpret(dao, queryBuilder, c);
        Query<Entity> query = queryBuilder.where(where).build();

    }


    @Test
    public void testComposite() {

        EntityDao dao = createEntityDao();

        QueryBuilder<Entity> queryBuilder = dao.queryBuilder();

        Criteria c = Criteria.or(Condition.eq("p1", 1),
                Criteria.and(
                    Condition.eq("p2", 2),
                    Condition.eq("p3", 2))
        );

        ExpressionInterpreter interpreter = new ExpressionInterpreter();
        WhereCondition where = interpreter.interpret(dao, queryBuilder, c);
        Query<Entity> query = queryBuilder.where(where).build();

    }

    private EntityDao createEntityDao() {
        DBTableEntitySource entitySource = new DBTableEntitySource(Mockito.mock(Database.class), null);
        EntityMeta meta = metaBuilder.withNumProps(1)
                .addProperty("p1", String.class)
                .addProperty("p2", String.class)
                .addProperty("p3", String.class)
                .build();

        EntityDaoConfig config = new EntityDaoConfig(meta, entitySource);
        return new EntityDao(config);
    }
}
