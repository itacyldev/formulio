package es.jcyl.ita.formic.jayjobs.jobs;

import androidx.test.ext.junit.runners.AndroidJUnit4;

import org.apache.commons.io.FileUtils;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.io.File;

import es.jcyl.ita.formic.core.context.CompositeContext;
import es.jcyl.ita.formic.core.context.impl.BasicContext;
import es.jcyl.ita.formic.jayjobs.jobs.config.JobConfig;
import es.jcyl.ita.formic.jayjobs.jobs.config.JobConfigRepo;
import es.jcyl.ita.formic.jayjobs.jobs.executor.JobExecRepo;
import es.jcyl.ita.formic.jayjobs.jobs.models.JobExecutionMode;
import es.jcyl.ita.formic.jayjobs.utils.DbTestUtils;
import es.jcyl.ita.formic.jayjobs.utils.DevJobsBuilder;
import es.jcyl.ita.formic.jayjobs.utils.JobContextTestUtils;
import es.jcyl.ita.formic.repo.test.utils.TestUtils;

import static org.mockito.Mockito.*;

/**
 * Instrumented test to check reader/writer job execution in device
 */
@RunWith(AndroidJUnit4.class)
public class ReaderWriterIntegrationTest {

    @Test
    public void testSQLReaderCSVWriter() throws Exception {
        String jobId = "sqlReader_csvWriter";
        // Create project folder:
        File projectFolder = JobContextTestUtils.createProjectFolderInstrTest();

        // create support execution objects
        DevJobsBuilder.CreateDummyJobExec builder = new DevJobsBuilder.CreateDummyJobExec();
        builder.withContext(JobContextTestUtils.createJobExecContext(projectFolder.getAbsolutePath())).build();
        // copy job definition file to project/jobs folder
        TestUtils.copyResourceToFolder(String.format("jobs/%s.json", jobId), new File(projectFolder, "jobs"));

        // create facade and related repositories
        JobFacade facade = new JobFacade();
        JobConfigRepo repo = new JobConfigRepo();
        facade.setJobConfigRepo(repo);
        // mock execution repo calls to return the dummy JobExecInfo
        JobExecRepo execRepo = mock(JobExecRepo.class);
        when(execRepo.registerExecInit(any(CompositeContext.class),
                any(JobConfig.class),
                any(JobExecutionMode.class))).thenReturn(builder.execInfo);
        facade.setJobExecRepo(execRepo);

        // Create test database
        int EXPECTED_NUMRESULTS = 200;
        String[] dbInfo = DbTestUtils.createPopulatedDatabase(EXPECTED_NUMRESULTS);
        String dbFile = dbInfo[0];
        String tableName = dbInfo[1];

        // add params to replace variable values in job json file
        BasicContext params = new BasicContext("params");
        params.put("tableName", tableName);
        params.put("dbFile", dbFile);
        builder.globalContext.addContext(params);

        facade.executeJob(builder.globalContext, jobId, JobExecutionMode.FG);

        // check context has been update with the task context
        CompositeContext gCtx = builder.globalContext;
        Assert.assertNotNull(gCtx.getContext("t1"));
        String outputFile = gCtx.getString("t1.outputFile");
        Assert.assertNotNull(outputFile);
        File f = new File(outputFile);
        Assert.assertTrue(f.exists());
        // check the file contains expected number of lines
        String fileContent = FileUtils.readFileToString(f, "UTF-8");
        Assert.assertEquals(EXPECTED_NUMRESULTS, fileContent.split("\\n").length - 1);// -1 -->header
    }
}

