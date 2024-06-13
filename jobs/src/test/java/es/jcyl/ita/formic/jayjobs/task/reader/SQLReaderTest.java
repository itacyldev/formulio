package es.jcyl.ita.formic.jayjobs.task.reader;
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
 * distributed under the License is distributed on an "AS IS" Bom.android.toolsASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

import org.junit.Assert;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mini2Dx.collections.CollectionUtils;
import org.robolectric.RobolectricTestRunner;

import es.jcyl.ita.formic.core.context.Context;
import es.jcyl.ita.formic.core.context.impl.BasicContext;
import es.jcyl.ita.formic.jayjobs.task.exception.TaskException;
import es.jcyl.ita.formic.jayjobs.task.models.RecordPage;
import es.jcyl.ita.formic.jayjobs.task.models.Task;
import es.jcyl.ita.formic.sharedTest.utils.DbTestUtils;
import es.jcyl.ita.formic.sharedTest.utils.JobContextTestUtils;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

/**
 * @author Gustavo Río (gustavo.rio@itacyl.es)
 */
@RunWith(RobolectricTestRunner.class)
public class SQLReaderTest {

    @Test
    @Ignore
    public void testQuery() throws TaskException {
        // create test database
        int NUM_EXPECTED_RESULTS = 100;
        String[] dbInfo = DbTestUtils.createPopulatedDatabase(NUM_EXPECTED_RESULTS);
        String dbFileName = dbInfo[0];
        String tableName = dbInfo[1];

        // create task mock and set global and task contexts
        Task taskMock = createTaskMock();

        // Create reader and link to task
        SQLReader reader = new SQLReader();
        reader.setTask(taskMock);
        reader.setDbFile(dbFileName);
        reader.setSqlQuery("select * from " + tableName);

        reader.open();
        RecordPage page = reader.read();
        reader.close();

        Assert.assertNotNull(page);
        Assert.assertTrue(page.getResults().size() == NUM_EXPECTED_RESULTS);
    }

    @Test
    @Ignore
    public void testPaginatedQuery() throws TaskException {
        // create test database
        int NUM_EXPECTED_RESULTS = 100;
        String[] dbInfo = DbTestUtils.createPopulatedDatabase(NUM_EXPECTED_RESULTS);
        String dbFileName = dbInfo[0];
        String tableName = dbInfo[1];

        // create task mock and set global and task contexts
        Task taskMock = createTaskMock();

        // Create reader and link to task
        SQLReader reader = new SQLReader();
        reader.setTask(taskMock);
        reader.setDbFile(dbFileName);
        int PAGESIZE = 3;
        reader.setPageSize(PAGESIZE);
        reader.setSqlQuery("select * from " + tableName);

        reader.open();
        // call the reader simulating pagination
        int offset = 0;
        RecordPage page = reader.read();
        int numResults = page.getResults().size();
        while (page != null && CollectionUtils.isNotEmpty(page.getResults())) {
            Assert.assertTrue(page.getResults().size() <= PAGESIZE);
            offset += PAGESIZE;
            reader.setOffset(offset);
            page = reader.read();
            numResults += page.getResults().size();
        }
        reader.close();

        Assert.assertEquals(NUM_EXPECTED_RESULTS, numResults);
    }

    private Task createTaskMock() {
        Context taskContext = new BasicContext("t1");
        Task taskMock = mock(Task.class);
        when(taskMock.getTaskContext()).thenReturn(taskContext);
        when(taskMock.getGlobalContext()).thenReturn(JobContextTestUtils.createJobExecContext());
        return taskMock;
    }
}
