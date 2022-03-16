package es.jcyl.ita.formic.jayjobs.jobs;
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

import org.junit.Assert;
import org.junit.Test;

import es.jcyl.ita.formic.core.context.CompositeContext;
import es.jcyl.ita.formic.jayjobs.jobs.config.JobConfig;
import es.jcyl.ita.formic.jayjobs.jobs.config.JobConfigRepo;
import es.jcyl.ita.formic.jayjobs.jobs.exec.JobExecRepo;
import es.jcyl.ita.formic.jayjobs.jobs.models.JobExecutionMode;
import es.jcyl.ita.formic.jayjobs.utils.DevJobsBuilder;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

/**
 * @author Gustavo Río (gustavo.rio@itacyl.es)
 */
public class JobFacadeTest {

    @Test
    public void testRunJob() throws Exception {
        // create support execution objects
        DevJobsBuilder.CreateDummyJobExec builder = new DevJobsBuilder.CreateDummyJobExec();
        builder.build();

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

        facade.executeJob(builder.globalContext, "basic_job_repoTests");

        // check context has been update with the task context
        CompositeContext gCtx = builder.globalContext;
        Assert.assertNotNull(gCtx.getContext("t1"));
    }
}
