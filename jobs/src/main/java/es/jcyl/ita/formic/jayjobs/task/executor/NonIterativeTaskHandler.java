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

import org.mini2Dx.collections.CollectionUtils;

import java.util.List;

import es.jcyl.ita.formic.core.context.CompositeContext;
import es.jcyl.ita.formic.jayjobs.task.exception.TaskException;
import es.jcyl.ita.formic.jayjobs.task.models.NonIterTask;
import es.jcyl.ita.formic.jayjobs.task.processor.NonIterProcessor;

/**
 * Class that handles the execution of Non iterative tasks.
 *
 * @author Gustavo Río (gustavo.rio@itacyl.es)
 */

public class NonIterativeTaskHandler implements TaskHandler<NonIterTask> {

    @Override
    public void handle(CompositeContext context, NonIterTask task)
            throws TaskException {
        try {
            List<NonIterProcessor> processors = task.getProcessors();
            if (CollectionUtils.isNotEmpty(processors)) {
                for (NonIterProcessor processor : processors) {
                    processor.setTask(task);
                    processor.process();
                }
            }
        } catch (Throwable e) {
            throw e;
        }
    }
}
