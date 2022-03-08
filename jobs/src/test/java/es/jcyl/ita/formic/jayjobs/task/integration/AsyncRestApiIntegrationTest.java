package es.jcyl.ita.formic.jayjobs.task.integration;

import com.android.volley.RequestQueue;
import com.android.volley.mock.VolleyMocks;

import org.junit.Before;
import org.junit.Test;

import java.util.logging.Level;
import java.util.logging.Logger;

import es.jcyl.ita.formic.core.context.CompositeContext;
import es.jcyl.ita.formic.jayjobs.jobs.JobFacade;
import es.jcyl.ita.formic.jayjobs.jobs.config.JobConfig;
import es.jcyl.ita.formic.jayjobs.jobs.config.JobConfigRepo;
import es.jcyl.ita.formic.jayjobs.jobs.models.JobExecutionMode;
import es.jcyl.ita.formic.jayjobs.task.processor.httpreq.RQProvider;
import es.jcyl.ita.formic.jayjobs.utils.JobContextTestUtils;
import es.jcyl.ita.formic.repo.test.utils.AssertUtils;
import es.jcyl.ita.formic.repo.test.utils.TestUtils;

/**
 * Instrumented test to check reader/writer job execution in device
 */
public class AsyncRestApiIntegrationTest {


    JobFacade facade = new JobFacade();
    @Test
    public void test() throws Exception {

        // read job content
        CompositeContext context = JobContextTestUtils.createJobExecContext();
        JobConfigRepo repo = new JobConfigRepo();
        JobConfig jobConfig = repo.get(context, "async_rest_api");

        RequestQueue queue = VolleyMocks.createMockRQRealNetwork();
        RQProvider.setRQ(queue);

        facade.executeJob(context, jobConfig, JobExecutionMode.FG);


    }
}

