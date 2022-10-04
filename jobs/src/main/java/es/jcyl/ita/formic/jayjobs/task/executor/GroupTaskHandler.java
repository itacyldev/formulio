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

import es.jcyl.ita.formic.core.context.CompositeContext;
import es.jcyl.ita.formic.jayjobs.task.exception.TaskException;
import es.jcyl.ita.formic.jayjobs.task.iteration.TaskContextIterator;
import es.jcyl.ita.formic.jayjobs.task.models.GroupTask;
import util.Log;

/**
 * Class that handlers the execution of group tasks
 *
 * @author: gustavo.rio@itacyl.es
 */
public class GroupTaskHandler implements TaskHandler<GroupTask> {

    private TaskExecutor executor;

    public GroupTaskHandler(TaskExecutor executor) {
        this.executor = executor;
    }

    @Override
    public void handle(CompositeContext context, GroupTask task)
            throws TaskException {
        TaskContextIterator it = task.getLoopIterator();
        it.setGlobalContext(context);
        int counter = 0;
        while (it.hasNext()) {
            if (!it.evalEnterIterationExpr()) {
                // if entering condition is not met exit the loop
                break;
            }
            CompositeContext currentIterCtx = it.next();
            try {
                if (task.getTasks() != null) {
                    executor.execute(currentIterCtx, task.getTasks());
                } else {
                    executor.execute(currentIterCtx, task.getTaskConfig());
                }
            } catch (Exception e) {
                String errorMsg = "An error occurred during the groupTask execution in the interation: " + counter;
                if (task.getListener() != null) {
                    task.getListener().onTaskError(task, errorMsg, e);
                }
                Log.error(errorMsg);
                // check if we have to go on with the execution of other iterations
                if (task.isStopOnError()) {
                    throw e;
                }
            }
            if (it.evalExitIterationExpr()) {
                // if exit condition is met exit the loop
                break;
            }
            counter++;
        }
    }
}
