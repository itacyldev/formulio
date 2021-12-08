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
import es.jcyl.ita.formic.exporter.task.models.NonIterTask;
import es.jcyl.ita.formic.exporter.task.models.Task;
import es.jcyl.ita.formic.exporter.task.models.TaskListener;
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
    public void testExecTaskInstances() throws TaskException {

        int NUM_TASKS = 5;
        List<Task> taskInstances = createTasks(NUM_TASKS);

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
     * Creates dummy tasks
     * @param num
     * @return
     */
    private List<Task> createTasks(int num) {
        List<Task> taskList = new ArrayList<>();

        for (int i = 0; i < num; i++) {
            int finalI = i;
            Task task = new NonIterTask() {

                @Override
                public Long getId() {
                    return Long.valueOf(finalI);
                }

                @Override
                public String getName() {
                    return "t" + finalI;
                }

                @Override
                public void setName(String name) {

                }

                @Override
                public void setGlobalContext(CompositeContext ctx) {

                }

                @Override
                public void setTaskContext(Context ctx) {

                }

                @Override
                public Context getTaskContext() {
                    return null;
                }

                @Override
                public CompositeContext getGlobalContext() {
                    return null;
                }

                @Override
                public void setListener(TaskListener listener) {

                }

                @Override
                public TaskListener getListener() {
                    return null;
                }

                @Override
                public boolean isStopOnError() {
                    return false;
                }
            };
            taskList.add(task);
        }
        return taskList;
    }
}
