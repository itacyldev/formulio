package es.jcyl.ita.formic.jayjobs.task.processor.httpreq;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import androidx.annotation.NonNull;

import com.android.volley.Network;
import com.android.volley.NetworkResponse;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.mock.VolleyMocks;

import org.apache.commons.io.FileUtils;
import org.junit.Assert;
import org.junit.Test;
import org.mini2Dx.collections.CollectionUtils;
import org.mockito.ArgumentCaptor;

import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import es.jcyl.ita.formic.core.context.Context;
import es.jcyl.ita.formic.core.context.impl.BasicContext;
import es.jcyl.ita.formic.jayjobs.task.exception.TaskException;
import es.jcyl.ita.formic.jayjobs.task.models.Task;
import es.jcyl.ita.formic.jayjobs.utils.TestContextUtils;
import es.jcyl.ita.formic.jayjobs.utils.VolleyUtils;

public class HttpRequestProcessorTest {

    @Test
    public void testResponseToFile() throws TaskException, IOException {
        Context taskContext = new BasicContext("t1");
        Task taskMock = createTaskMock(taskContext);

        String expectedString = "expected response content";
        RequestQueue queue = VolleyMocks.createMockRQ(expectedString);

        HttpRequestProcessor processor = new HttpRequestProcessor();
        processor.setTask(taskMock);
        processor.setRequestQueue(queue);

        processor.process();

        // check the file has been created and is not empty
        String outputFile = processor.getOutputFile();
        File f = new File(outputFile);
        Assert.assertTrue(f.exists());
        Assert.assertTrue(FileUtils.readFileToByteArray(f).length > 0);
        // check file has been published
        Assert.assertEquals(outputFile, (String) taskContext.get("outputFile"));
    }

    @Test
    public void testTextResponseToContext() throws TaskException, IOException {
        Context taskContext = new BasicContext("t1");
        Task taskMock = createTaskMock(taskContext);

        String expectedString = "Example response";
        RequestQueue queue = VolleyMocks.createMockRQ(expectedString);

        HttpRequestProcessor processor = new HttpRequestProcessor();
        processor.setTask(taskMock);
        processor.setRequestQueue(queue);
        processor.setContentType("text/plain");

        processor.process();

        // inspect task context
        Object output = taskContext.get("output");
        Assert.assertNotNull(output);
        Assert.assertEquals(expectedString, (String) output);
    }

    @Test
    public void testJsonResponseToContext() throws TaskException, IOException {
        Context taskContext = new BasicContext("t1");
        Task taskMock = createTaskMock(taskContext);

        String response = "{ \"variable\": 123}";
        RequestQueue queue = VolleyMocks.createMockRQ(response, "application/json");

        HttpRequestProcessor processor = new HttpRequestProcessor();
        processor.setTask(taskMock);
        processor.setRequestQueue(queue);
        processor.setContentType("application/json");

        processor.process();

        // inspect task context
        Map output = (Map) taskContext.get("outputJson");
        Assert.assertNotNull(output);
        Assert.assertTrue(output.containsKey("variable"));
        Assert.assertEquals(123, (int) output.get("variable"));
    }

    @Test
    public void testHttpMethods() throws Exception {
        Context taskContext = new BasicContext("t1");
        Task taskMock = createTaskMock(taskContext);

        Network network = mock(Network.class);
        when(network.performRequest(any(Request.class))).thenReturn(new NetworkResponse("test response".getBytes()));
        RequestQueue queue = VolleyMocks.createMockRQ(network);

        HttpRequestProcessor processor = new HttpRequestProcessor();
        processor.setTask(taskMock);
        processor.setRequestQueue(queue);

        String methods[] = {"get", "post", "PUT", "DELETE"};

        for (String method : methods) {
            processor.setMethod(method);
            processor.process();
        }
        ArgumentCaptor<Request> argumentCaptor = ArgumentCaptor.forClass(Request.class);
        verify(network, times(4)).performRequest(argumentCaptor.capture());
        List<Request> values = argumentCaptor.getAllValues();

        // compare values with expectations
        List<Integer> expected = Stream.of(methods).map(v -> VolleyUtils.getMethodFromString(v)).collect(Collectors.toList());
        List<Integer> used = values.stream().map(v -> v.getMethod()).collect(Collectors.toList());

        Assert.assertTrue(CollectionUtils.isEqualCollection(expected, used));
    }

    @NonNull
    private Task createTaskMock(Context taskContext) {
        // create task mock and set the task context
        Task taskMock = mock(Task.class);
        when(taskMock.getTaskContext()).thenReturn(taskContext);
        when(taskMock.getGlobalContext()).thenReturn(TestContextUtils.createGlobalContext());
        return taskMock;
    }
}
