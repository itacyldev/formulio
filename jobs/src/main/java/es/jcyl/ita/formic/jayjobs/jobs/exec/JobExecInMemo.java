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

import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import es.jcyl.ita.formic.core.context.CompositeContext;
import es.jcyl.ita.formic.jayjobs.jobs.config.JobConfig;
import es.jcyl.ita.formic.jayjobs.jobs.exception.JobException;
import es.jcyl.ita.formic.jayjobs.jobs.models.JobExecutionMode;
import es.jcyl.ita.formic.jayjobs.jobs.models.JobExecutionState;

/**
 * In memory Job execution repository implementation.
 *
 * @autor Gustavo Río Briones (gustavo.rio@itacyl.es)
 */
public class JobExecInMemo extends AbstractJobExecRepo {

    private static JobExecInMemo _instance;

    private static final Map<Long, List<String>> resources = new HashMap<>();
    private static final Map<Long, JobExecStatus> executions = new HashMap<>();


    private CompositeContext ctx;


    private JobExecInMemo() {
    }

    public static JobExecInMemo getInstance() {
        if (_instance == null) {
            _instance = new JobExecInMemo();
        }
        return _instance;
    }

    /*public JobExecInMemo(CompositeContext ctx) {
        this.ctx = ctx;
    }*/

    @Override
    public JobExecStatus registerExecInit(JobConfig job, JobExecutionMode execMode)
            throws JobException {
        // create execution record using job config
        JobExecStatus execution = new JobExecStatus();
        execution.setJobId(job.getId());
        //execution.setUserId(ContextAccessor.userId(ctx));
        execution.setUserId("PRUEBA_JOB");
        execution.setMode(job.getExecMode());

        Long execId = Long.valueOf(executions.size() + 1);
        execution.setId(execId);
        execution.setState(JobExecutionState.INITIALIZED);
        execution.setMessage(String.format("Job %s has started!", job.getId()));
        execution.setLastTimeUpdated(new Date());
        executions.put(execId, execution);
        addMessage(execId, execution.getMessage());
        return execution;
    }

    @Override
    public void updateState(Long jobExecId, JobExecutionState state, String message) throws JobException {
        JobExecStatus jobExec = executions.get(jobExecId);
        if (jobExec == null) {
            throw new JobException("Job execution id not found: " + jobExecId);
        }
        jobExec.setState(state);
        jobExec.setMessage(message);
        addMessage(jobExecId, message);
        jobExec.setLastTimeUpdated(new Date());
    }

    @Override
    public void publishResources(Long jobExecId, List<JobResource> resources) throws JobException {
        JobExecStatus jobExec = executions.get(jobExecId);
        if (jobExec == null) {
            throw new JobException("Job execution id not found: " + jobExecId);
        }
        jobExec.setResources(resources);
    }

    @Override
    public void publishResource(Long jobExecId, JobResource resource) throws JobException {
        publishResources(jobExecId, Collections.singletonList(resource));
    }

    @Override
    public List<JobResource> getResources(Long jobExecId) {
        JobExecStatus jobExec = executions.get(jobExecId);
        return jobExec.getResources();
    }

    @Override
    public JobExecStatus getJobStatus(Long jobExecId) {
        JobExecStatus status = null;
        if (executions.containsKey(jobExecId)) {
            status = executions.get(jobExecId);
        } else {
            status = new JobExecStatus();
            status.setState(JobExecutionState.NOT_INITIALIZED);
        }
        return status;
    }

    @Override
    public void updateJobStatus(JobExecStatus jobStatus) {
        jobStatus.setLastTimeUpdated(new Date());
        executions.put(jobStatus.getId(), jobStatus);
        addMessage(jobStatus.getId(), jobStatus.getMessage());
    }

    public CompositeContext getCtx() {
        return ctx;
    }

    public void setCtx(CompositeContext ctx) {
        this.ctx = ctx;
    }

}
