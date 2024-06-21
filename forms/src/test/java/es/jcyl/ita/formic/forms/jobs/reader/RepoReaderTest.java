package es.jcyl.ita.formic.forms.jobs.reader;/*
 * Copyright 2020 Gustavo Río (mungarro@itacyl.es), ITACyL (http://www.itacyl.es).
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

import org.apache.commons.lang3.RandomStringUtils;
import org.junit.Assert;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.RobolectricTestRunner;

import java.io.File;

import es.jcyl.ita.formic.core.context.CompositeContext;
import es.jcyl.ita.formic.core.context.Context;
import es.jcyl.ita.formic.core.context.impl.BasicContext;
import es.jcyl.ita.formic.core.context.impl.UnPrefixedCompositeContext;
import es.jcyl.ita.formic.forms.utils.RepositoryUtils;
import es.jcyl.ita.formic.jayjobs.task.exception.TaskException;
import es.jcyl.ita.formic.jayjobs.task.models.RecordPage;
import es.jcyl.ita.formic.jayjobs.task.models.Task;
import es.jcyl.ita.formic.repo.builders.DevDbBuilder;
import es.jcyl.ita.formic.repo.meta.EntityMeta;
import es.jcyl.ita.formic.repo.test.utils.TestUtils;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;


/**
 * @autor Rosa María Muñiz (mungarro@itacyl.es)
 */
@RunWith(RobolectricTestRunner.class)
public class RepoReaderTest {
    @Test
    @Ignore
    public void testRepo() throws TaskException {

        // create task mock and set global and task contexts
        Task taskMock = createTaskMock();

        RepositoryUtils.registerMock("contacts");

        RepoReader reader = new RepoReader();
        reader.setTask(taskMock);
        reader.setId("contacts");
        File dbFile = TestUtils.findFile("dbTest.sqlite");
       reader.setDbFile(dbFile.getPath());
        reader.setDbTable("contacts");

        reader.open();
        RecordPage page = reader.read();
        reader.close();

        Assert.assertNotNull(page);

    }

    private Task createTaskMock() {
        Context taskContext = new BasicContext("t1");
        Task taskMock = mock(Task.class);
        when(taskMock.getTaskContext()).thenReturn(taskContext);
        when(taskMock.getGlobalContext()).thenReturn(createJobExecContext());
        return taskMock;
    }

    private static String[] createPopulatedDatabase(int numRows) {
        File tempDirectory = TestUtils.createTempDirectory();
        String dbFileName = String.format("%s/%s.sqlite", tempDirectory.getAbsolutePath(),
                RandomStringUtils.randomAlphanumeric(10));
        String randomTableName = RandomStringUtils.randomAlphanumeric(10);
        return createPopulatedDatabase(dbFileName, randomTableName, numRows);
    }

    private static String[] createPopulatedDatabase(String dbFileName, String tableName, int numRows) {
        SQLiteDatabase sqLiteDatabase = SQLiteDatabase.openOrCreateDatabase(dbFileName, null);
        DevDbBuilder devDbBuilder = new DevDbBuilder();
        EntityMeta meta = DevDbBuilder.buildRandomMeta(tableName);
        devDbBuilder.withMeta(meta).withNumEntities(numRows).build(sqLiteDatabase);
        return new String[]{dbFileName, tableName};
    }



    private static CompositeContext createJobExecContext() {
        // find "jobs" folder in test resources
        File jobsFolder = TestUtils.findFile("jobs");
        return createJobExecContext(jobsFolder.getParent());
    }

    public static CompositeContext createJobExecContext(String projectBaseFolder) {
        CompositeContext ctx = new UnPrefixedCompositeContext();
        Context appContext = new BasicContext("app");

        // Create temporary folder under SO tmp
        File tmpDir = TestUtils.createTempDirectory();
        appContext.put("workingFolder", tmpDir.getAbsolutePath());
        ctx.addContext(appContext);

        // create job context and set the folder of job definition files
        Context projectCtx = new BasicContext("project");
        projectCtx.put("folder", projectBaseFolder);//resources folder
        ctx.addContext(projectCtx);
        return ctx;
    }
}

