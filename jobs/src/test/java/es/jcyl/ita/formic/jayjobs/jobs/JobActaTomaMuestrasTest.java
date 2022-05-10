package es.jcyl.ita.formic.jayjobs.jobs;/*
 * Copyright 2020 Rosa María Muñiz (mungarro@itacyl.es), ITACyL (http://www.itacyl.es).
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

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import org.apache.commons.io.FileUtils;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.RobolectricTestRunner;

import java.io.File;

import es.jcyl.ita.formic.core.context.CompositeContext;
import es.jcyl.ita.formic.core.context.impl.BasicContext;
import es.jcyl.ita.formic.jayjobs.jobs.config.JobConfig;
import es.jcyl.ita.formic.jayjobs.jobs.config.JobConfigRepo;
import es.jcyl.ita.formic.jayjobs.jobs.exec.JobExecRepo;
import es.jcyl.ita.formic.jayjobs.jobs.models.JobExecutionMode;
import es.jcyl.ita.formic.jayjobs.task.utils.ContextAccessor;
import es.jcyl.ita.formic.jayjobs.utils.DevJobsBuilder;

/**
 * @autor Rosa María Muñiz (mungarro@itacyl.es)
 */
@RunWith(RobolectricTestRunner.class)
public class JobActaTomaMuestrasTest {

    @Test
    public void testJobActaCabecera() throws Exception {
        // create support execution objects
        DevJobsBuilder.CreateDummyJobExec builder = new DevJobsBuilder.CreateDummyJobExec();
        builder.build();

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

        BasicContext params = new BasicContext("params");

        String projectBaseFolder = ContextAccessor.projectFolder(builder.globalContext);
        File dbFile = new File(projectBaseFolder, String.format("%s.sqlite", "tierravino"));

        params.put("dbFile", dbFile.getName());
        params.put("expediente_id", 1);
        builder.globalContext.addContext(params);

        facade.executeJob(builder.globalContext, "job_acta_cabecera", JobExecutionMode.FG);

        // check context has been update with the task context
        CompositeContext gCtx = builder.globalContext;
        Assert.assertNotNull(gCtx.getContext("t1"));
        String outputFile = gCtx.getString("t1.outputFile");
        Assert.assertNotNull(outputFile);
        File f = new File(outputFile);
        Assert.assertTrue(f.exists());
        // check the file contains expected number of lines
        String fileContent = FileUtils.readFileToString(f, "UTF-8");
        // it has at least one line
        Assert.assertTrue(fileContent.split("\\n").length>1);
    }

    @Test
    public void testJobActaMuestras() throws Exception {
        // create support execution objects
        DevJobsBuilder.CreateDummyJobExec builder = new DevJobsBuilder.CreateDummyJobExec();
        builder.build();

        // create facade and related repositories
        JobFacade facade = new JobFacade();
        JobConfigRepo repo = new JobConfigRepo();
        facade.setJobConfigRepo(repo);
        // mock execution repo calls to return the dummy JobExecInfo
        JobExecRepo execRepo = mock(JobExecRepo.class);
        when(execRepo.registerExecInit( any(JobConfig.class),
                any(JobExecutionMode.class))).thenReturn(builder.execInfo);
        facade.setJobExecRepo(execRepo);

        BasicContext params = new BasicContext("params");

        String projectBaseFolder = ContextAccessor.projectFolder(builder.globalContext);
        File dbFile = new File(projectBaseFolder, String.format("%s.sqlite", "tierravino"));

        params.put("dbFile", dbFile.getName());
        params.put("expediente_id", 1);
        builder.globalContext.addContext(params);

        facade.executeJob(builder.globalContext, "job_acta_muestras", JobExecutionMode.FG);

        // check context has been update with the task context
        CompositeContext gCtx = builder.globalContext;
        Assert.assertNotNull(gCtx.getContext("t1"));
        String outputFile = gCtx.getString("t1.outputFile");
        Assert.assertNotNull(outputFile);
        File f = new File(outputFile);
        Assert.assertTrue(f.exists());
        // check the file contains expected number of lines
        String fileContent = FileUtils.readFileToString(f, "UTF-8");
        // it has at least one line
        Assert.assertTrue(fileContent.split("\\n").length>1);

    }
}
