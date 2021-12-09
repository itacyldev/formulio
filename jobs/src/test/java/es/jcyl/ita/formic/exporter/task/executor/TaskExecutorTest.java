package es.jcyl.ita.formic.exporter.task.executor;
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

import org.junit.Assert;
import org.junit.Test;

import java.util.ArrayList;
import java.util.List;

import es.jcyl.ita.formic.core.context.CompositeContext;
import es.jcyl.ita.formic.core.context.Context;
import es.jcyl.ita.formic.core.context.impl.UnPrefixedCompositeContext;
import es.jcyl.ita.formic.exporter.task.exception.TaskException;
import es.jcyl.ita.formic.exporter.task.models.IterativeTask;
import es.jcyl.ita.formic.exporter.task.models.NonIterTask;
import es.jcyl.ita.formic.exporter.task.models.Task;
import es.jcyl.ita.formic.exporter.task.reader.RandomDataReader;
import es.jcyl.ita.formic.repo.test.utils.TestUtils;

/**
 * @author Gustavo Río (gustavo.rio@itacyl.es)
 */
public class TaskExecutorTest {

    /**
     * Checks de execution of a very basic task list
     *
     * @throws TaskException
     */
    @Test
    public void testExecTasksFromJson() throws TaskException {
        String json = TestUtils.readAsString("tasks/basic_tasks.json");

        TaskExecutor executor = new TaskExecutor();
        CompositeContext gCtx = new UnPrefixedCompositeContext();
        executor.execute(gCtx, json);

        // check the content of the context
        Assert.assertEquals(3, gCtx.getContexts().size());
        // try to access the context by the task nam
        for (int i = 1; i <= 3; i++) {
            Context ctx = gCtx.getContext("t" + i);
            Assert.assertNotNull(ctx);
        }
    }

    /**
     * Uses an executor instance to execute some task instances
     *
     * @throws TaskException
     */
    @Test
    public void testExecNonIterTaskInstances() throws TaskException {
        int NUM_TASKS = 5;
        List<Task> taskInstances = createNonIterTasks(NUM_TASKS);

        TaskExecutor executor = new TaskExecutor();
        CompositeContext gCtx = new UnPrefixedCompositeContext();
        executor.execute(gCtx, taskInstances);

        // check the content of the context
        Assert.assertEquals(NUM_TASKS, gCtx.getContexts().size());
        // try to access the context by the task nam
        for (int i = 0; i < NUM_TASKS; i++) {
            Context ctx = gCtx.getContext("t" + i);
            Assert.assertNotNull(ctx);
        }
    }

    /**
     * Creates dummy iterative tasks to check the execution flow.
     *
     * @throws TaskException
     */
    @Test
    public void testExecIterTaskInstances() throws TaskException {
        int NUM_TASKS = 5;
        List<Task> taskInstances = createIterTasks(NUM_TASKS);

        TaskExecutor executor = new TaskExecutor();
        CompositeContext gCtx = new UnPrefixedCompositeContext();
        executor.execute(gCtx, taskInstances);

        // check the content of the context
        Assert.assertEquals(NUM_TASKS, gCtx.getContexts().size());
        // try to access the context by the task nam
        for (int i = 0; i < NUM_TASKS; i++) {
            Context ctx = gCtx.getContext("t" + i);
            Assert.assertNotNull(ctx);
        }
    }

    /**
     * Creates a group task and checks the iterations has been performed check the contex
     *
     * @throws TaskException
     */
    @Test
    public void testExecGroupTaskFromJson() throws TaskException {
        String json = TestUtils.readAsString("tasks/basic_group_task.json");
        int NUM_EXPECTED_ITERS = 5; // configured in json
        TaskExecutor executor = new TaskExecutor();
        CompositeContext gCtx = new UnPrefixedCompositeContext();
        executor.execute(gCtx, json);

        Assert.assertNotNull(gCtx);
        Assert.assertEquals(NUM_EXPECTED_ITERS, gCtx.get("r1.size"));
    }

    /**
     * Creates dummy tasks
     *
     * @param num
     * @return
     */
    private List<Task> createNonIterTasks(int num) {
        List<Task> taskList = new ArrayList<>();

        for (int i = 0; i < num; i++) {
            // dummy tasks without processor
            Task task = new NonIterTask();
            task.setName("t" + i);
            taskList.add(task);
        }
        return taskList;
    }

    private List<Task> createIterTasks(int num) {
        List<Task> taskList = new ArrayList<>();
        for (int i = 0; i < num; i++) {
            // dummy tasks without reader/processor/writer
            IterativeTask task = new IterativeTask();
            task.setReader(new RandomDataReader());
            task.setName("t" + i);
            taskList.add(task);
        }
        return taskList;
    }

}
