package es.jcyl.ita.formic.jayjobs.task.models;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

import java.util.Arrays;

import es.jcyl.ita.formic.core.context.CompositeContext;
import es.jcyl.ita.formic.core.context.Context;
import es.jcyl.ita.formic.jayjobs.task.executor.TaskExecutor;
import es.jcyl.ita.formic.jayjobs.task.iteration.BasicCounterIterator;
import es.jcyl.ita.formic.jayjobs.utils.ContextTestUtils;

/**
 * @author Gustavo Río (gustavo.rio@itacyl.es)
 */

public class GroupTaskTest {

    @Test
    public void testBasicGroup() throws Exception {
        int NUM_ITERS = 5;
        CompositeContext context = ContextTestUtils.createGlobalContext();

        // Prepare tasks
        NonIterTask task1 = new NonIterTask(null, "t1");
        task1.setProcessor(new DummyProcessor());
        NonIterTask task2 = new NonIterTask(null, "t2");
        task2.setProcessor(new DummyProcessor());

        GroupTask groupTask = new GroupTask();
        groupTask.setName("gt1");
        groupTask.setTasks(Arrays.asList(new Task[]{task1, task2}));
        groupTask.setIterSize(NUM_ITERS);
        groupTask.setLoopIterator(new BasicCounterIterator(context, groupTask));

        TaskExecutor taskExecutor = new TaskExecutor();
        taskExecutor.execute(context, groupTask);

        // check the group counter has reached the expected value
        Context gtContext = context.getContext(groupTask.getName());
        int value = (Integer) gtContext.get("idx");
        assertEquals(NUM_ITERS, value + 1);
    }

}
