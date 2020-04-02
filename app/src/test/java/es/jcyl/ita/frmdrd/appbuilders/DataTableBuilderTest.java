package es.jcyl.ita.frmdrd.appbuilders;
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

import androidx.test.platform.app.InstrumentationRegistry;

import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.RobolectricTestRunner;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

import es.jcyl.ita.crtrepo.EditableRepository;
import es.jcyl.ita.crtrepo.Entity;
import es.jcyl.ita.crtrepo.builders.DevDbBuilder;
import es.jcyl.ita.crtrepo.builders.EntityDataBuilder;
import es.jcyl.ita.crtrepo.builders.EntityMetaDataBuilder;
import es.jcyl.ita.crtrepo.meta.EntityMeta;
import es.jcyl.ita.crtrepo.meta.PropertyType;
import es.jcyl.ita.frmdrd.builders.DataTableBuilder;
import es.jcyl.ita.frmdrd.configuration.ConfigConverters;
import es.jcyl.ita.frmdrd.ui.components.column.UIColumn;
import es.jcyl.ita.frmdrd.ui.components.datatable.UIDatatable;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

/**
 * @author Gustavo Río (gustavo.rio@itacyl.es)
 * <p>
 */
@RunWith(RobolectricTestRunner.class)
public class DataTableBuilderTest {

    EntityDataBuilder entityBuilder;
    EntityMetaDataBuilder metaBuilder = new EntityMetaDataBuilder();
    DataTableBuilder dtBuilder = new DataTableBuilder();

    @BeforeClass
    public static void setUp() {
        ConfigConverters confConverter = new ConfigConverters();
        confConverter.init();
    }

    /**
     * Test dataTable creating from Repository, create mock(repo) with random meta and check the
     * table columns are properly configured.
     */
    @Test
    public void testDataTableWithAllFields() {
        Context ctx = InstrumentationRegistry.getInstrumentation().getContext();

        // create random entity meta and use databuilder to populate entity data
        EntityMeta meta = DevDbBuilder.createRandomMeta();
        entityBuilder = new EntityDataBuilder(meta);
        Entity entity = entityBuilder.withRandomData().build();

        // create a mock repository, set to form and load the entity
        EditableRepository mockRepo = mock(EditableRepository.class);
        when(mockRepo.findById(entity.getId())).thenReturn(entity);
        when(mockRepo.getMeta()).thenReturn(meta);

        UIDatatable datatable = dtBuilder.createDataTableFromRepo(mockRepo);

        // check all entity properties are included as columns
        Assert.assertEquals(datatable.getRepo(), mockRepo);
        for (PropertyType p : meta.getProperties()) {
            // find columns
            UIColumn c = datatable.getColumn(p.getName());
            Assert.assertNotNull("No column found for property " + p.getName(),c);
        }
    }

    /**
     * Tests datatable creation choosing some of the fields
     */
    @Test
    public void testDataTableWithFieldSelection() {
        Context ctx = InstrumentationRegistry.getInstrumentation().getContext();

        // create random entity meta and use databuilder to populate entity data
        EntityMeta meta = DevDbBuilder.createRandomMeta();
        entityBuilder = new EntityDataBuilder(meta);
        Entity entity = entityBuilder.withRandomData().build();

        // create a mock repository, set to form and load the entity
        EditableRepository mockRepo = mock(EditableRepository.class);
        when(mockRepo.findById(entity.getId())).thenReturn(entity);
        when(mockRepo.getMeta()).thenReturn(meta);

        // Get two fields and create expresion f1,f2
        PropertyType[] properties = meta.getProperties();
        Set<String> fieldFilter = new HashSet<>(Arrays.asList(properties[0].getName(), properties[1].getName()));
        UIDatatable datatable = dtBuilder.createDataTableFromRepo(mockRepo, fieldFilter);

        // check all entity properties are included as columns
        Assert.assertEquals(datatable.getRepo(), mockRepo);
        Assert.assertEquals(fieldFilter.size(), datatable.getColumns().length);

        for (String pName: fieldFilter) {
            // find columns
            UIColumn c = datatable.getColumn(pName);
            Assert.assertNotNull("No column found for property " + pName);
        }
    }
}
