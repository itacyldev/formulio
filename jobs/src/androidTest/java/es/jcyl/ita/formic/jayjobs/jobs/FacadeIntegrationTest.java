package es.jcyl.ita.formic.jayjobs.jobs;

import androidx.test.ext.junit.runners.AndroidJUnit4;

import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.io.File;

import es.jcyl.ita.formic.core.context.CompositeContext;
import es.jcyl.ita.formic.jayjobs.jobs.config.JobConfig;
import es.jcyl.ita.formic.jayjobs.jobs.config.JobConfigRepo;
import es.jcyl.ita.formic.jayjobs.jobs.exec.JobExecRepo;
import es.jcyl.ita.formic.jayjobs.jobs.models.JobExecutionMode;
import es.jcyl.ita.formic.jayjobs.utils.DevJobsBuilder;
import es.jcyl.ita.formic.jayjobs.utils.JobContextTestUtils;
import es.jcyl.ita.formic.repo.test.utils.TestUtils;

import static org.mockito.Mockito.*;

/**
 * Instrumented test to check facade execution on android device
 *
 * @see <a href="http://d.android.com/tools/testing">Testing documentation</a>
 */
@RunWith(AndroidJUnit4.class)
public class FacadeIntegrationTest {

    @Test
    public void testRunForeGroundJob() throws Exception {
        String jobId = "job_exporter";
        // Create project folder:
        File projectFolder = JobContextTestUtils.createProjectFolderInstrTest();

        // create support execution objects
        DevJobsBuilder.CreateDummyJobExec builder = new DevJobsBuilder.CreateDummyJobExec();
        builder.withContext(JobContextTestUtils.createJobExecContext(projectFolder.getAbsolutePath())).build();
        // copy job definition file to project/jobs folder
        TestUtils.copyResourceToFolder(String.format("jobs/%s.json",jobId), new File(projectFolder, "jobs"));

        // create facade and related repositories
        JobFacade facade = new JobFacade();
        JobConfigRepo repo = new JobConfigRepo();
        facade.setJobConfigRepo(repo);
        // mock execution repo calls to return the dummy JobExecInfo
        JobExecRepo execRepo = mock(JobExecRepo.class);
        when(execRepo.registerExecInit(
                any(JobConfig.class),
                any(JobExecutionMode.class))).thenReturn(builder.execInfo);
        facade.setJobExecRepo(execRepo);

        facade.executeJob(builder.globalContext, jobId, JobExecutionMode.FG);

        // check context has been update with the task context
        CompositeContext gCtx = builder.globalContext;
        Assert.assertNotNull(gCtx.getContext("t1"));
    }
}

