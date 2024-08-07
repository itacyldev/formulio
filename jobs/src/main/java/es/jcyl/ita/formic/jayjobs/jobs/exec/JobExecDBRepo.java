package es.jcyl.ita.formic.jayjobs.jobs.exec;
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

import java.util.List;

import es.jcyl.ita.formic.core.context.CompositeContext;
import es.jcyl.ita.formic.jayjobs.jobs.config.JobConfig;
import es.jcyl.ita.formic.jayjobs.jobs.config.JobResource;
import es.jcyl.ita.formic.jayjobs.jobs.models.JobExecutionMode;
import es.jcyl.ita.formic.jayjobs.jobs.models.JobExecutionState;
import es.jcyl.ita.formic.jayjobs.task.utils.ContextAccessor;

/**
 * @autor Gustavo Río Briones (gustavo.rio@itacyl.es)
 */
public class JobExecDBRepo extends AbstractJobExecRepo {

    private final CompositeContext ctx;

    public JobExecDBRepo(CompositeContext ctx) {
        this.ctx = ctx;
        initDB();
    }

    private void initDB() {


    }

    @Override
    public JobExecStatus registerExecInit(JobConfig job, JobExecutionMode execMode) {
        // create execution record using job config
        JobExecStatus execution = new JobExecStatus();
        execution.setJobId(job.getId());
        execution.setUserId(ContextAccessor.userId(ctx));
        execution.setMode(job.getExecMode());

        //TODO: insert into database
        // OJOOOOOO: hay que fijar en el execInfo el id de ejecución antes de devolver la instancia
        Long execId = 1l;
        execution.setId(execId);
        execution.setState(JobExecutionState.INITIALIZED);
        return execution;
    }

    @Override
    public void publishResources(Long jobExecId, List<JobResource> resources) {

    }

    @Override
    public void publishResource(Long jobExecId, JobResource resource) {

    }

    @Override
    public List<JobResource> getResources(Long jobExecId) {
        return null;
    }

    @Override
    public JobExecStatus getJobStatus(Long jobExecId) {
        return null;
    }

    @Override
    public void updateState(Long jobExecId, JobExecutionState state) {

    }

    @Override
    public void updateState(Long jobExecId, JobExecutionState state, String message) {

    }


}
