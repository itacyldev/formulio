package es.jcyl.ita.formic.jayjobs.task.processor;

import org.junit.Assert;
import org.junit.Test;

import java.util.Map;

import es.jcyl.ita.formic.core.context.Context;
import es.jcyl.ita.formic.core.context.impl.BasicContext;
import es.jcyl.ita.formic.jayjobs.task.exception.TaskException;
import es.jcyl.ita.formic.jayjobs.task.models.Task;
import es.jcyl.ita.formic.repo.test.utils.RandomUtils;

import static org.mockito.Mockito.*;

public class ContextPopulateProcessorTest {

    @Test
    public void testSetSimpleValue() throws TaskException {
        Context taskContext = new BasicContext("t1");
        // create task mock and set the task context
        Task taskMock = mock(Task.class);
        when(taskMock.getTaskContext()).thenReturn(taskContext);

        ContextPopulateProcessor processor = new ContextPopulateProcessor();
        processor.setName("myVariable");
        Long expectedValue = 123l;
        processor.setValue(expectedValue);
        processor.setTask(taskMock);

        processor.process();

        Assert.assertTrue(taskContext.containsKey("myVariable"));
        Assert.assertTrue(taskContext.get("myVariable").equals(expectedValue));
    }

    @Test
    public void testSetValueMap() throws TaskException {
        Context taskContext = new BasicContext("t1");
        // create task mock and set the task context
        Task taskMock = mock(Task.class);
        when(taskMock.getTaskContext()).thenReturn(taskContext);

        ContextPopulateProcessor processor = new ContextPopulateProcessor();
        // create random map and set to the populator
        Map valueMap = RandomUtils.randomMap(10, String.class, Long.class);
        processor.setValueMap(valueMap);
        processor.setTask(taskMock);

        processor.process();
        // check all keys and values in the map are set in the context
        valueMap.forEach((key, value) -> {
            Assert.assertTrue(taskContext.containsKey(key));
            Assert.assertTrue(taskContext.get(key).equals(value));
        });
    }

}
