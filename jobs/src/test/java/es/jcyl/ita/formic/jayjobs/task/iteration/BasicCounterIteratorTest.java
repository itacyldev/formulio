package es.jcyl.ita.formic.jayjobs.task.iteration;
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

import static org.junit.Assert.assertEquals;

import org.junit.Test;
import org.mini2Dx.beanutils.ConvertUtils;

import java.util.Arrays;

import es.jcyl.ita.formic.core.context.CompositeContext;
import es.jcyl.ita.formic.jayjobs.task.exception.TaskException;
import es.jcyl.ita.formic.jayjobs.task.executor.TaskExecutor;
import es.jcyl.ita.formic.jayjobs.task.models.GroupTask;
import es.jcyl.ita.formic.jayjobs.task.models.NonIterTask;
import es.jcyl.ita.formic.jayjobs.task.models.Task;
import es.jcyl.ita.formic.jayjobs.task.processor.AbstractProcessor;
import es.jcyl.ita.formic.jayjobs.task.processor.NonIterProcessor;
import es.jcyl.ita.formic.sharedTest.utils.TestContextUtils;

/**
 * @autor Gustavo Río Briones (gustavo.rio@itacyl.es)
 */
public class BasicCounterIteratorTest {

    @Test
    public void testLoopExpression() throws Exception {
        int NUM_ITERS = 5;
        CompositeContext context = TestContextUtils.createGlobalContext();

        // Prepare tasks
        NonIterTask task1 = new NonIterTask(null, "t1");
        task1.setProcessor(new MyTestProcessor());

        GroupTask groupTask = new GroupTask();
        groupTask.setName("gt1");
        groupTask.setTasks(Arrays.asList(new Task[]{task1}));
        groupTask.setIterSize(NUM_ITERS);
        groupTask.setExitLoopExpression("${myCounter > 2}");
        groupTask.setLoopIterator(new BasicCounterIterator(context, groupTask));

        TaskExecutor taskExecutor = new TaskExecutor();
        taskExecutor.execute(context, groupTask);

        // check the Group context is present and counter and idx has been properly updated
        assertEquals(3, context.get("gt1.myCounter")); // ${myCounter > 2}"=> 3
    }


    public class MyTestProcessor extends AbstractProcessor implements NonIterProcessor {
        /**
         * Test class that increments a variable
         *
         * @throws TaskException
         */
        @Override
        public void process() throws TaskException {
            Object counter = getGlobalContext().get("gt1.myCounter");
            int value = (counter == null) ? 0 : (int) ConvertUtils.convert(counter, Integer.class);
            value += 1;
            getGlobalContext().put("gt1.myCounter", value);
        }
    }
}
