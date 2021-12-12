package es.jcyl.ita.formic.jayjobs.task.executor;
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

import org.apache.commons.io.FileUtils;
import org.junit.Assert;
import org.junit.Test;

import java.io.File;
import java.io.IOException;

import es.jcyl.ita.formic.core.context.CompositeContext;
import es.jcyl.ita.formic.jayjobs.task.exception.TaskException;
import es.jcyl.ita.formic.jayjobs.utils.JobContextTestUtils;
import es.jcyl.ita.formic.repo.test.utils.TestUtils;

/**
 * Reference tests to test jobs.
 *
 * @author Gustavo Río (gustavo.rio@itacyl.es)
 */
public class TaskExecutorExampleTest {

    /**
     * Checks de execution of a very basic task list
     *
     * @throws TaskException
     */
    @Test
    public void testExecTasksFromJson() throws TaskException, IOException {
        String json = TestUtils.readAsString("tasks/integration_RandomReader_CSVWriter.json");

//        CompositeContext gCtx = new UnPrefixedCompositeContext();
        CompositeContext gCtx = JobContextTestUtils.createJobExecContext();
        TaskExecutor executor = new TaskExecutor();
        executor.execute(gCtx, json);


        // check the content of the context
        Assert.assertTrue(gCtx.containsKey("t1.outputFile"));
        // check file exists and is not empty
        String fileName = gCtx.getString("t1.outputFile");
        File f = new File(fileName);
        Assert.assertTrue(f.exists());
        String fileContent = FileUtils.readFileToString(f, "UTF-8");
        // it has at least one line
        Assert.assertTrue(fileContent.split("\\n").length>1);
    }

}
