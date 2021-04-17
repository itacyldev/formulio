package es.jcyl.ita.formic.forms.integration;
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

import android.database.sqlite.SQLiteDatabase;

import org.greenrobot.greendao.database.StandardDatabase;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.RobolectricTestRunner;

import java.io.File;
import java.util.Date;
import java.util.List;

import es.jcyl.ita.formic.core.context.CompositeContext;
import es.jcyl.ita.formic.core.context.Context;
import es.jcyl.ita.formic.forms.config.Config;
import es.jcyl.ita.formic.forms.config.ConfigConverters;
import es.jcyl.ita.formic.forms.context.impl.DateTimeContext;
import es.jcyl.ita.formic.core.context.impl.UnPrefixedCompositeContext;
import es.jcyl.ita.formic.repo.EditableRepository;
import es.jcyl.ita.formic.repo.Entity;
import es.jcyl.ita.formic.repo.builders.DevDbBuilder;
import es.jcyl.ita.formic.repo.builders.EntityMetaDataBuilder;
import es.jcyl.ita.formic.repo.db.meta.DBPropertyType;
import es.jcyl.ita.formic.repo.db.sqlite.SQLiteRepository;
import es.jcyl.ita.formic.repo.meta.EntityMeta;
import es.jcyl.ita.formic.repo.test.utils.TestUtils;

import static org.hamcrest.Matchers.*;
import static org.junit.Assert.assertThat;

/**
 * @author Gustavo Río (gustavo.rio@itacyl.es)
 * <p>
 * Tests to check commons-converters functionallity
 */
@RunWith(RobolectricTestRunner.class)
public class ProjectWithMetaIntegrationTest {

    @BeforeClass
    public static void setUp() {
        Config.init("");
        ConfigConverters confConverter = new ConfigConverters();
        confConverter.init();
        // register repos
//        DevConsole.setLevel(Log.DEBUG);
    }


    /**
     * Check formEdit and formList are properly created
     */
    private static final String TEST_BASIC1 = "<main repo=\"contacts\"/>";

    private static String FCREATION = "fCreation";
    private static String FUPDATE = "fUpdate";

    @Test
    public void testInsertEntityWithCalculatedProps() throws Exception {
        File dbFile = TestUtils.findFile("dbTest.sqlite");
        SQLiteDatabase db = SQLiteDatabase.openOrCreateDatabase(dbFile, null);

        // create a database with some entities, and add a propoerty to support the FK
        DevDbBuilder dbDev = new DevDbBuilder();

        EntityMetaDataBuilder metaBuilder = new EntityMetaDataBuilder();
        EntityMeta<DBPropertyType> meta = metaBuilder.withNumProps(3)
                .withBasicTypes(true).withRandomData()
                .addProperty(FCREATION, Date.class)
                .addProperty(FUPDATE, Date.class)
                .build();
        setUpCalculatedProperties(meta);


        // create table and insert entities
        dbDev.withMeta(meta).withNumEntities(0).build(new StandardDatabase(db));
        // get created repo
        EditableRepository repo = dbDev.getRepo();

        // mock date context before repo is created
        Context dateCtx = new DateTimeContext();
        CompositeContext ctx = new UnPrefixedCompositeContext();
        ctx.addContext(dateCtx);
        ((SQLiteRepository) repo).setContext(ctx);

        List<Entity> entities = DevDbBuilder.buildEntities(meta, 3);
        for (Entity e : entities) {
            // remove random values from calculated properties
            e.set(FCREATION, null);
            e.set(FUPDATE, null);
            repo.save(e);
        }

        // list all entities
        List list = repo.listAll();

        // check the property "current_time" is set
        Entity e1 = (Entity) list.get(0);
        Date current_time = (Date) e1.get(FCREATION);
        assertThat(current_time, notNullValue());

        // modify the entity twice and check the creation_date and update_date have been set
        repo.save(e1);
        Entity e1Saved = repo.findById(e1.getId());
        Date creation_date = (Date) e1Saved.get(FCREATION);
        assertThat(creation_date, notNullValue());

        repo.save(e1);
        e1Saved = repo.findById(e1.getId());
        Date update_date = (Date) e1Saved.get(FUPDATE);
        assertThat(update_date, notNullValue());
        assertThat(update_date, greaterThan(creation_date));
        // check creation time hasn't been modified
        assertThat((Date) e1Saved.get(FCREATION), equalTo(creation_date));
    }

    private void setUpCalculatedProperties(EntityMeta<DBPropertyType> meta) {
        // get FCREATION and FUPDATE and make then calculated properties
        DBPropertyType property = (DBPropertyType) meta.getPropertyByName(FCREATION);
        DBPropertyType.DBPropertyTypeBuilder pBuilder = new DBPropertyType.DBPropertyTypeBuilder(property);
        DBPropertyType builtProp = pBuilder.withJexlExpresion("${date.now}", DBPropertyType.CALC_MOMENT.INSERT).build();
        meta.setProperty(builtProp.name, builtProp);

        // get FCREATION and FUPDATE and make then calculated properties
        property = (DBPropertyType) meta.getPropertyByName(FUPDATE);
        pBuilder = new DBPropertyType.DBPropertyTypeBuilder(property);
        builtProp = pBuilder.withJexlExpresion("${date.now}", DBPropertyType.CALC_MOMENT.UPDATE).build();
        meta.setProperty(builtProp.name, builtProp);

    }

}
