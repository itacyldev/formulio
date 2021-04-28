package es.jcyl.ita.formic.forms.location;

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
import android.location.Location;
import android.location.LocationManager;

import androidx.test.platform.app.InstrumentationRegistry;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.RobolectricTestRunner;

import java.util.Arrays;

import es.jcyl.ita.formic.core.context.CompositeContext;
import es.jcyl.ita.formic.forms.R;
import es.jcyl.ita.formic.forms.context.impl.DateTimeContext;
import es.jcyl.ita.formic.core.context.impl.UnPrefixedCompositeContext;
import es.jcyl.ita.formic.repo.EditableRepository;
import es.jcyl.ita.formic.repo.Entity;
import es.jcyl.ita.formic.repo.builders.DevDbBuilder;
import es.jcyl.ita.formic.repo.db.meta.DBPropertyType;
import es.jcyl.ita.formic.repo.db.sqlite.SQLiteRepository;
import es.jcyl.ita.formic.repo.db.sqlite.converter.SQLiteTextConverter;
import es.jcyl.ita.formic.repo.meta.EntityMeta;

import static es.jcyl.ita.formic.repo.db.meta.DBPropertyType.CALC_METHOD.JEXL;
import static es.jcyl.ita.formic.repo.db.meta.DBPropertyType.CALC_MOMENT.INSERT;
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
     * Create empty database, insert one entity whit a location property, and make sure the location value
     * is persisted getting it from the global context.
     */
    @Test
    public void testOnInsertLocation() {
        // create database with random table with 1 entity
        Context ctx = InstrumentationRegistry.getInstrumentation().getContext();

        DevDbBuilder dbBuilder = new DevDbBuilder();

        // Create calculated property for the location
        EntityMeta meta = dbBuilder.buildRandomMeta();
        DBPropertyType[] props = (DBPropertyType[]) meta.getProperties();
        DBPropertyType locationProperty = createLocationProperty(props[1], INSERT, JEXL, "${location.asString}");

        // Add location property to meta
        props = Arrays.copyOf(props, props.length + 1);
        props[props.length - 1] = locationProperty;
        meta.setProperties(props);

        // create empty database
        dbBuilder.withNumEntities(0).withMeta(meta).build(ctx, "testDB");

        //create a mock location
        LocationBuilder builder = new LocationBuilder();
        Location mockLocation = builder.withRandomData().withTime(System.currentTimeMillis()).build();
        when(mockLocationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER)).thenReturn(mockLocation);

        CompositeContext globalCxt = createContext();

        // Use a repo to insert and entity, read it using its pk and check stored value
        Entity entity = dbBuilder.buildEntities(dbBuilder.getMeta(), 1).get(0);
        // the value for the calculated property for the entity must be null simulating and insert
        entity.set(locationProperty.getName(), null);

        EditableRepository repo = dbBuilder.getSQLiteRepository(dbBuilder.getSource(), dbBuilder.getMeta(), globalCxt);
        ((SQLiteRepository) repo).setContext(globalCxt);

        // fetch the entity from database and check the property has been calculated
        repo.save(entity);
        Entity e2 = repo.findById(entity.getId());
        Assert.assertEquals(mockLocation.toString(), e2.get(locationProperty.name));
    }

    private CompositeContext createContext() {
        LocationService locationService = new LocationService(ctx);
        locationService.setLocationManager(mockLocationManager);

        CompositeContext globalContext = new UnPrefixedCompositeContext();
        globalContext.addContext(new DateTimeContext("date"));
        globalContext.put("location", locationService);

        return globalContext;
    }

    private DBPropertyType createLocationProperty(DBPropertyType property, DBPropertyType.CALC_MOMENT when, DBPropertyType.CALC_METHOD how, String expression) {
        DBPropertyType.DBPropertyTypeBuilder builder = new DBPropertyType.DBPropertyTypeBuilder("location", String.class, "TEXT", false);
        switch (how) {
            case JEXL:
                builder.withJexlExpresion(expression, when).withConverter(new SQLiteTextConverter(String.class));
                break;
            case SQL:
                builder.withSQLExpression(expression, when);
        }
        return builder.build();
    }

}
