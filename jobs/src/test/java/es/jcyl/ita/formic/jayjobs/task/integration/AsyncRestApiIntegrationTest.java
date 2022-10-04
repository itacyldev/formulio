package es.jcyl.ita.formic.jayjobs.task.integration;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import androidx.annotation.NonNull;

import com.android.volley.Header;
import com.android.volley.RequestQueue;
import com.android.volley.mock.VolleyMocks;
import com.android.volley.toolbox.HttpResponse;

import org.apache.commons.lang3.RandomStringUtils;
import org.junit.Assert;
import org.junit.Test;

import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import es.jcyl.ita.formic.core.context.CompositeContext;
import es.jcyl.ita.formic.core.context.Context;
import es.jcyl.ita.formic.core.context.impl.BasicContext;
import es.jcyl.ita.formic.core.jexl.JexlUtils;
import es.jcyl.ita.formic.jayjobs.jobs.JobFacade;
import es.jcyl.ita.formic.jayjobs.jobs.config.JobConfig;
import es.jcyl.ita.formic.jayjobs.jobs.config.JobConfigRepo;
import es.jcyl.ita.formic.jayjobs.jobs.models.JobExecutionMode;
import es.jcyl.ita.formic.jayjobs.task.exception.TaskException;
import es.jcyl.ita.formic.jayjobs.task.models.Task;
import es.jcyl.ita.formic.jayjobs.task.processor.httpreq.HttpRequestProcessor;
import es.jcyl.ita.formic.jayjobs.task.processor.httpreq.RQProvider;
import es.jcyl.ita.formic.jayjobs.utils.ContextTestUtils;
import es.jcyl.ita.formic.jayjobs.utils.JobContextTestUtils;
import util.Log;

/**
 * Instrumented test to check reader/writer job execution in device
 */
public class AsyncRestApiIntegrationTest {

    JobFacade facade = new JobFacade();

    /**
     * Runs test agains d
     *
     * @throws Exception
     */
    @Test
    public void testRunAsyncMock() throws Exception {
        // read job content
        CompositeContext context = JobContextTestUtils.createJobExecContext();
        JobConfigRepo repo = new JobConfigRepo();
        JobConfig jobConfig = repo.get(context, "async_rest_api");
        Log.setLevel(android.util.Log.DEBUG);

        List<HttpResponse> lst = new ArrayList<>();
        // First url invocation to init async job
        String expectedUrl = "http://jobstart.url.com";
        Header h = new Header("Location", expectedUrl);
        List<Header> headers = Collections.singletonList(h);
        lst.add(new HttpResponse(200, headers, "".getBytes(StandardCharsets.UTF_8)));

        // Second and third url invocation to check job state
        lst.add(new HttpResponse(200, new ArrayList<>(), "{ \"state\": \"EXECUTING\"}".getBytes(StandardCharsets.UTF_8)));
        String downloadURL = "http://download.url.com";
        String jobResponse = "{\n" +
                "   \"state\": \"FINISHED\",\n" +
                "   \"resources\": [\"" + downloadURL + "\"]\n" +
                "}";
        lst.add(new HttpResponse(200, new ArrayList<>(), jobResponse.getBytes(StandardCharsets.UTF_8)));

        // Fourth, download received resources
        String expectedFinalContent = RandomStringUtils.randomAlphabetic(20);
        lst.add(new HttpResponse(200, new ArrayList<>(), expectedFinalContent.getBytes(StandardCharsets.UTF_8)));

        RequestQueue queue = VolleyMocks.createMockRQ(lst);
        try {
            RQProvider.setRQ(queue);
            facade.executeJob(context, jobConfig, JobExecutionMode.FG);
        } finally {
            RQProvider.clearRQ();
        }
        // 2- Check the content of the final response (task t4) is in the context
        Assert.assertEquals(expectedFinalContent, context.get("t4.output"));
    }

    @Test
    public void testRunAsync() throws Exception {
        // read job content
        CompositeContext context = JobContextTestUtils.createJobExecContext();
        JobConfigRepo repo = new JobConfigRepo();
        JobConfig jobConfig = repo.get(context, "async_rest_api");

        RequestQueue queue = VolleyMocks.createMockRQRealNetwork();
        try {
            RQProvider.setRQ(queue);
            facade.executeJob(context, jobConfig, JobExecutionMode.FG);
        } finally {
            RQProvider.clearRQ();
        }
    }
    @Test
    public void testResponseToFile() throws TaskException, IOException, InterruptedException {
        Context taskContext = new BasicContext("t1");
        Task taskMock = createTaskMock(taskContext);

        RequestQueue queue = VolleyMocks.createMockRQRealNetwork();

        HttpRequestProcessor processor = new HttpRequestProcessor();
        processor.setMethod("post");
        processor.setUrl("https://serviciosprex.itacyl.es/uqserv/cnt/rest/jobs/?apikey=USER-RIOBRIGU-uqserv&jobType=dummyReportJob&execMode=FG");
        processor.setTask(taskMock);
        processor.setRequestQueue(queue);
        processor.process();
        // wait to finish
        Thread.sleep(3000);
        // check job state
        String location = JexlUtils.eval(taskContext, "${responseHeaders.Location}", String.class);
        location += "?apikey=USER-RIOBRIGU-uqserv";
        processor.setMethod("get");
        processor.setUrl(location);
        processor.setOutputFile(null);
        processor.process();

        String fileToDownload = JexlUtils.eval(taskContext, "${outputJson.resources[0]}", String.class);
        fileToDownload += "?apikey=USER-RIOBRIGU-uqserv";
        // download resources
        processor.setMethod("get");
        processor.setUrl(fileToDownload);
        processor.setOutputFile(null);
        processor.setOutputFileExtension(".pdf");
        processor.process();

        String outputFile = (String) taskContext.get("outputFile");
        File f = new File(outputFile);
        Assert.assertTrue(f.exists());

    }

    @NonNull
    private Task createTaskMock(Context taskContext) {
        // create task mock and set the task context
        Task taskMock = mock(Task.class);
        when(taskMock.getTaskContext()).thenReturn(taskContext);
        when(taskMock.getGlobalContext()).thenReturn(ContextTestUtils.createGlobalContext());
        return taskMock;
    }

}

