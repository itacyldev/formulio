package es.jcyl.ita.formic.jayjobs.task.processor.httpreq;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import com.android.volley.RequestQueue;
import com.android.volley.mock.VolleyMocks;

import org.apache.commons.io.FileUtils;
import org.junit.Assert;
import org.junit.Test;

import java.io.File;
import java.io.IOException;

import es.jcyl.ita.formic.core.context.Context;
import es.jcyl.ita.formic.core.context.impl.BasicContext;
import es.jcyl.ita.formic.jayjobs.task.exception.TaskException;
import es.jcyl.ita.formic.jayjobs.task.models.Task;
import es.jcyl.ita.formic.jayjobs.utils.ContextTestUtils;

public class HttpRequestProcessorTest {

    @Test
    public void testSimpleRequest() throws TaskException, IOException {
        Context taskContext = new BasicContext("t1");
        // create task mock and set the task context
        Task taskMock = mock(Task.class);
        when(taskMock.getTaskContext()).thenReturn(taskContext);
        when(taskMock.getGlobalContext()).thenReturn(ContextTestUtils.createGlobalContext());

        RequestQueue queue = VolleyMocks.createMockRequestQueue();

        HttpRequestProcessor processor = new HttpRequestProcessor();
        processor.setTask(taskMock);
        processor.setRequestQueue(queue);
        String url = "https://my-json-server.typicode.com/typicode/demo/db";
        processor.setUrl(url);
        processor.process();

        // check the file has been created and is not empty
        String outputFile = processor.getOutputFile();
        File f = new File(outputFile);
        Assert.assertTrue(f.exists());
        Assert.assertTrue(FileUtils.readFileToByteArray(f).length > 0);
    }

}
