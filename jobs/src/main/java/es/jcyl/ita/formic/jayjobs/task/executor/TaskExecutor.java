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

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import es.jcyl.ita.formic.core.context.CompositeContext;
import es.jcyl.ita.formic.core.context.impl.BasicContext;
import es.jcyl.ita.formic.jayjobs.jobs.listener.NopJobListener;
import es.jcyl.ita.formic.jayjobs.task.config.TaskConfigFactory;
import es.jcyl.ita.formic.jayjobs.task.config.TaskConfigIterator;
import es.jcyl.ita.formic.jayjobs.task.exception.StopTaskExecutionSignal;
import es.jcyl.ita.formic.jayjobs.task.exception.TaskException;
import es.jcyl.ita.formic.jayjobs.task.listener.TaskExecListener;
import es.jcyl.ita.formic.jayjobs.task.models.GroupTask;
import es.jcyl.ita.formic.jayjobs.task.models.IterativeTask;
import es.jcyl.ita.formic.jayjobs.task.models.NonIterTask;
import es.jcyl.ita.formic.jayjobs.task.models.Task;
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

/**
 * Class to execute a list of tasks.
 *
 * @author Gustavo Río (gustavo.rio@itacyl.es)
 */
public class TaskExecutor {
    private static final Logger LOGGER = LoggerFactory.getLogger(TaskExecutor.class);

    private Map<Class, TaskHandler> handlers = new HashMap<>();

    private TaskExecListener listener = new NopJobListener();

    public TaskExecutor() {
        // different handlers for different execution strategies
        handlers.put(IterativeTask.class, new IterativeTaskHandler());
        handlers.put(NonIterTask.class, new NonIterativeTaskHandler());
        handlers.put(GroupTask.class, new GroupTaskHandler(this));
    }

    /**
     * Executes the tasks defined in the json passed as parameter.
     *
     * @param context
     * @param jsonTaskConf
     * @throws TaskException
     */
    public void execute(CompositeContext context, String jsonTaskConf) throws TaskException {
        if (StringUtils.isEmpty(jsonTaskConf)) {
            throw new TaskException("The json task config is empty");
        }
        // para cada tarea se interpreta su configuraci�n con el texto actual
        Iterator<Task> taskIterator = TaskConfigFactory.getInstance().taskIterator(context, jsonTaskConf);
        doExecute(context, taskIterator);
    }

    public void execute(CompositeContext context, List<Task> tasks) throws TaskException {
        doExecute(context, tasks.iterator());
    }

    public void execute(CompositeContext context, Task task) throws TaskException {
        execute(context, Collections.singletonList(task));
    }

    /*****************************/
    /**** Internal implementation */
    /*****************************/

    private void doExecute(CompositeContext context, Iterator<Task> taskIterator)
            throws TaskException {
        boolean stopTasks = false;
        TaskHandler handler;

        while (taskIterator.hasNext()) {
            Task task = taskIterator.next();
            // set global and task contexts
            BasicContext tCtx = new BasicContext(task.getName());
            context.addContext(tCtx);
            task.setGlobalContext(context);
            task.setTaskContext(tCtx);
            // configure task listener
            task.setListener(listener);

            handler = getHandler(task);
            try {
                listener.onTaskStart(task);
                LOGGER.info(String.format(">>>> Starting task execution [%s]. >>>", task.getName()));

                handler.handle(context, task);

                LOGGER.info(String.format("<<<< Task [%s] successfully finished.<<<", task.getName()));
                listener.onTaskEnd(task);
            } catch (StopTaskExecutionSignal e) {
                stopTasks = true;
            } catch (TaskException e) {
                listener.onTaskError(task, "Error during task execution", e);
                if (task.isStopOnError()) {
                    String msg = String.format(
                            "An error occurred during execution of task [%s]. Job is stopped.",
                            task.getName());
                    LOGGER.error(msg, e);
                    throw new TaskException(msg, e);
                }
                LOGGER.error(String.format(
                        "<<<< An error occurred during execution of task [%s]. Job execution continues.<<<",
                        task.getName()), e);
            }
            if (stopTasks) {
                /* One of the tasks of the group has thrown a stopTaskSignal */
                break;
            }
        }
    }

    private TaskHandler getHandler(Task t) {
        return handlers.get(t.getClass());
    }

    public TaskExecListener getListener() {
        return listener;
    }

    public void setListener(TaskExecListener listener) {
        if(listener != null){
            this.listener = listener;
        }
    }
}
