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

import java.util.HashMap;
import java.util.Map;

import es.jcyl.ita.formic.core.context.CompositeContext;
import es.jcyl.ita.formic.core.context.Context;
import es.jcyl.ita.formic.core.context.ContextAwareComponent;
import es.jcyl.ita.formic.jayjobs.jobs.config.JobConfig;
import es.jcyl.ita.formic.jayjobs.jobs.config.JobConfigException;
import es.jcyl.ita.formic.jayjobs.jobs.config.JobConfigRepo;
import es.jcyl.ita.formic.jayjobs.jobs.exception.JobException;
import es.jcyl.ita.formic.jayjobs.jobs.executor.JobExec;
import es.jcyl.ita.formic.jayjobs.jobs.executor.JobExecRepo;
import es.jcyl.ita.formic.jayjobs.jobs.executor.JobExecutor;
import es.jcyl.ita.formic.jayjobs.jobs.executor.MainThreadExecutor;
import es.jcyl.ita.formic.jayjobs.jobs.models.JobExecutionMode;

/**
 * Front class to execute jobs.
 *
 * @author Gustavo Río (gustavo.rio@itacyl.es)
 */
public class JobFacade implements ContextAwareComponent {

    private Context globalContext;
    private JobConfigRepo jobConfigRepo;
    private JobExecRepo jobExecRepo;

    private Map<JobExecutionMode, JobExecutor> executors = new HashMap<>();

    public JobFacade() {
        // init executors
        executors.put(JobExecutionMode.FG, new MainThreadExecutor());
    }

    public Long executeJob(CompositeContext ctx, String jobType) throws JobException {
        JobConfig job = jobConfigRepo.get(ctx, jobType);
        return doExecuteJob(ctx, jobType, job.getExecMode());
    }

    private Long doExecuteJob(CompositeContext ctx, String jobType, JobExecutionMode execMode) throws JobException {
        JobConfig job = jobConfigRepo.get(ctx, jobType);
        if (job == null) {
            Context prjCtx = ctx.getContext("project");
            String prjName = prjCtx.getString("name");
            throw new JobConfigException(String.format(
                    "The required job type [%s] doesn't exists, make sure the file [%s/jobs/%s.json] " +
                            "exists.",
                    prjName, jobType));
        }
        // checks needed contexts and permissions
        checkContexts(job, ctx);
        checkPermissions(job);

        JobExec jobExecutionInfo = jobExecRepo.registerExecInit(ctx, job, execMode);

        JobExecutor executor = getExecutor(job);
        executor.execute(ctx, job, jobExecutionInfo);

        return jobExecutionInfo.getId();
    }

    private JobExecutor getExecutor(JobConfig job) {
        if (job.getExecMode() == null) {
            // TODO: IF EXEC MODE IS NULL USE FG_ASYNC by default
        }
        JobExecutor jobExecutor = this.executors.get(job.getExecMode());
        if (jobExecutor == null) {
            throw new UnsupportedOperationException(String.format("%s is not implemented", job.getExecMode()));
        }
        return jobExecutor;
    }

    /**
     * Checks if the execution context for the job contains all the contexts needed by the job
     *
     * @param job
     * @param ctx
     */
    private void checkContexts(JobConfig job, CompositeContext ctx) {
    }


    /**
     * Checks if the user has given all the needed permissions to execute the job
     *
     * @param job
     */
    private void checkPermissions(JobConfig job) {
    }

    @Override
    public void setContext(Context ctx) {
        this.globalContext = ctx;
    }

    public JobConfigRepo getJobConfigRepo() {
        return jobConfigRepo;
    }

    public void setJobConfigRepo(JobConfigRepo jobConfigRepo) {
        this.jobConfigRepo = jobConfigRepo;
    }

    public JobExecRepo getJobExecRepo() {
        return jobExecRepo;
    }

    public void setJobExecRepo(JobExecRepo jobExecRepo) {
        this.jobExecRepo = jobExecRepo;
    }
}
