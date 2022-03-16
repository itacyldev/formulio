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
import es.jcyl.ita.formic.jayjobs.jobs.exception.JobException;
import es.jcyl.ita.formic.jayjobs.jobs.models.JobExecutionMode;
import es.jcyl.ita.formic.jayjobs.jobs.models.JobExecutionState;
import es.jcyl.ita.formic.jayjobs.task.utils.ContextAccessor;

/**
 * @autor Gustavo Río Briones (gustavo.rio@itacyl.es)
 */
public class JobExecDBRepo implements JobExecRepo {
    @Override
    public JobExec registerExecInit(CompositeContext ctx, JobConfig job, JobExecutionMode execMode) {
        // create execution record using job config
        JobExec execution = new JobExec();
        execution.setJobId(job.getId());
        execution.setUserId(ContextAccessor.userId(ctx));
        execution.setMode(job.getExecMode());

        //TODO: insert into database
        // OJOOOOOO: hay que fijar en el execInfo el id de ejecución antes de devolver la instancia
        Long execId = 1l;
        execution.setId(execId);

        return execution;
    }

    @Override
    public void updateState(CompositeContext ctx, Long jobExecId, JobExecutionState state) throws JobException {

    }

    @Override
    public void publishResources(CompositeContext ctx, Long jobExecId, List<String> resources) throws JobException {

    }

    @Override
    public void publishResource(CompositeContext ctx, Long jobExecId, String resource) throws JobException {

    }

    @Override
    public List<String> getResources(CompositeContext ctx, Long jobExecId) throws JobException {
        return null;
    }
}
