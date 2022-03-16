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

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import es.jcyl.ita.formic.core.context.CompositeContext;
import es.jcyl.ita.formic.jayjobs.jobs.config.JobConfig;
import es.jcyl.ita.formic.jayjobs.jobs.exception.JobException;
import es.jcyl.ita.formic.jayjobs.jobs.models.JobExecutionMode;
import es.jcyl.ita.formic.jayjobs.jobs.models.JobExecutionState;
import es.jcyl.ita.formic.jayjobs.task.utils.ContextAccessor;

/**
 * In memory Job execution repository implementation.
 *
 * @autor Gustavo Río Briones (gustavo.rio@itacyl.es)
 */
public class JobExecInMemo implements JobExecRepo {

    private static final Map<Long, List<String>> resources = new HashMap<>();
    private static final Map<Long, JobExec> executions = new HashMap<>();

    @Override
    public JobExec registerExecInit(CompositeContext ctx, JobConfig job, JobExecutionMode execMode)
            throws JobException {
        // create execution record using job config
        JobExec execution = new JobExec();
        execution.setJobId(job.getId());
        execution.setUserId(ContextAccessor.userId(ctx));
        execution.setMode(job.getExecMode());

        Long execId = Long.valueOf(executions.size() + 1);
        execution.setId(execId);
        executions.put(execId, execution);
        return execution;
    }

    @Override
    public void updateState(CompositeContext ctx, Long jobExecId, JobExecutionState state) throws JobException {
        JobExec jobExec = executions.get(jobExecId);
        if (jobExec == null) {
            throw new JobException("Job execution id not found: " + jobExecId);
        }
        jobExec.setState(state);
    }

    @Override
    public void publishResources(CompositeContext ctx, Long jobExecId, List<String> resources) {
        List<String> current = this.resources.get(jobExecId);
        if (current == null) {
            this.resources.put(jobExecId, new ArrayList(resources));
        }
        this.resources.get(jobExecId).addAll(resources);
    }

    @Override
    public void publishResource(CompositeContext ctx, Long jobExecId, String resource) {
        publishResources(ctx, jobExecId, Collections.singletonList(resource));
    }

    @Override
    public List<String> getResources(CompositeContext ctx, Long jobExecId) {
        return this.resources.get(jobExecId);
    }

}
