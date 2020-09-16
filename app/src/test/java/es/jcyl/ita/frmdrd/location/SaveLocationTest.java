package es.jcyl.ita.frmdrd.location;

/*
 * Copyright 2020 Javier Ramos (javier.ramos@itacyl.es), ITACyL (http://www.itacyl.es).
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
import android.location.Criteria;
import android.location.LocationManager;

import androidx.test.platform.app.InstrumentationRegistry;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.util.ReflectionHelpers;

import es.jcyl.ita.crtrepo.EditableRepository;
import es.jcyl.ita.crtrepo.Entity;
import es.jcyl.ita.crtrepo.builders.DevDbBuilder;
import es.jcyl.ita.crtrepo.context.CompositeContext;
import es.jcyl.ita.crtrepo.context.impl.BasicContext;
import es.jcyl.ita.crtrepo.context.impl.OrderedCompositeContext;
import es.jcyl.ita.crtrepo.db.meta.DBPropertyType;
import es.jcyl.ita.crtrepo.test.utils.RandomUtils;
import es.jcyl.ita.frmdrd.R;
import es.jcyl.ita.frmdrd.context.impl.LocationContext;

import static es.jcyl.ita.crtrepo.db.meta.DBPropertyType.CALC_METHOD.CONTEXT;
import static es.jcyl.ita.crtrepo.db.meta.DBPropertyType.CALC_MOMENT.INSERT;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

/**
 * @author Javier Ramos (javier.ramos@itacyl.es)
 * <p>
 * Tests the saving of the position when the repository has a location column configured
 */
@RunWith(RobolectricTestRunner.class)
public class SaveLocationTest {

    Context ctx;
    LocationManager mockLocationManager;

    TestLocationProvider testProvider;

    @Before
    public void setup() {
        ctx = InstrumentationRegistry.getInstrumentation().getContext();
        ctx.setTheme(R.style.FormudruidLight);

        mockLocationManager = mock(LocationManager.class);
        when(mockLocationManager.getBestProvider(any(Criteria.class), any(Boolean.class))).thenReturn(LocationManager.GPS_PROVIDER);
    }

    /**
     * Create empty database, insert one entity, and make sure the calculated values
     * are persisted reading them from the global context.
     */
    @Test
    public void testOnInsertContextProps() {
        // create database with random table with 1 entity
        Context ctx = InstrumentationRegistry.getInstrumentation().getContext();
        // create empty database
        DevDbBuilder dbBuilder = new DevDbBuilder();
        dbBuilder.withNumEntities(0).build(ctx, "testDB");

        // get meta and set one of the properties as calculated from context
        DBPropertyType[] props = (DBPropertyType[]) dbBuilder.getMeta().getProperties();
        DBPropertyType p2 = setPropertyAsCalculated(props[1], INSERT, CONTEXT, "location.lastlocation");
        props[1] = p2;

        Object expectedValue = RandomUtils.randomObject(p2.type);
        CompositeContext globalCxt = createContext(expectedValue);

        // Use a repo to insert and entity, read it using its pk and check stored value
        Entity entity = dbBuilder.buildEntities(dbBuilder.getMeta(), 1).get(0);
        // the value for the calculated property for the entity must be null simulating and insert
        entity.set(p2.getName(), null);

        EditableRepository repo = dbBuilder.getSQLiteRepository(dbBuilder.getSource(), dbBuilder.getMeta(), globalCxt);

        repo.save(entity);
        Entity e2 = repo.findById(entity.getId());
        Assert.assertEquals(expectedValue, e2.get(p2.name));

        // now modify the entity, save it and check that the data hasn't been modified.
        entity.set(p2.getName(), RandomUtils.randomObject(p2.getType()));
        Object modifiedValue = RandomUtils.randomObject(props[2].getType());
        entity.set(props[2].getName(), modifiedValue);
        repo.save(entity);

        e2 = (Entity) repo.listAll().get(0);
        // modified value has been set, and calculated-on-insert property stays unmodified
        Assert.assertEquals(expectedValue, e2.get(p2.name));
        Assert.assertEquals(modifiedValue, e2.get(props[2].name));

    }

    private CompositeContext createContext(Object expectedValue) {
        es.jcyl.ita.crtrepo.context.Context dataCtx = new BasicContext("b1");

        LocationService locationService = LocationService.getInstance();
        locationService.init(ctx);
        ReflectionHelpers.setField(locationService, "locationManager", mockLocationManager);

        LocationContext locationContext = new LocationContext();
        locationContext.setLocationService(locationService);
        dataCtx.put("a", expectedValue);
        CompositeContext globalCxt = new OrderedCompositeContext();
        globalCxt.addContext(dataCtx);
        globalCxt.addContext(locationContext);
        return globalCxt;
    }

    private DBPropertyType setPropertyAsCalculated(DBPropertyType property, DBPropertyType.CALC_MOMENT when, DBPropertyType.CALC_METHOD how, String expression) {
        DBPropertyType.DBPropertyTypeBuilder builder = new DBPropertyType.DBPropertyTypeBuilder(property);
        switch (how) {
            case CONTEXT:
                builder.withContextExpression(expression, when);
                break;
            case SQL_EXPRESSION:
                builder.withSQLExpression(expression, when);
        }
        return builder.build();
    }

}
