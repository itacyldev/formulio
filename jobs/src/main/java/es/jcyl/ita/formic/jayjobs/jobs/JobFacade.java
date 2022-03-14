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
import java.util.List;
import java.util.Map;

import es.jcyl.ita.formic.core.context.CompositeContext;
import es.jcyl.ita.formic.core.context.Context;
import es.jcyl.ita.formic.core.context.impl.BasicContext;
import es.jcyl.ita.formic.jayjobs.jobs.config.JobConfig;
import es.jcyl.ita.formic.jayjobs.jobs.config.JobConfigException;
import es.jcyl.ita.formic.jayjobs.jobs.config.JobConfigRepo;
import es.jcyl.ita.formic.jayjobs.jobs.exception.JobException;
import es.jcyl.ita.formic.jayjobs.jobs.executor.JobExec;
import es.jcyl.ita.formic.jayjobs.jobs.executor.JobExecInMemo;
import es.jcyl.ita.formic.jayjobs.jobs.executor.JobExecRepo;
import es.jcyl.ita.formic.jayjobs.jobs.executor.JobRunner;
import es.jcyl.ita.formic.jayjobs.jobs.executor.MainThreadRunner;
import es.jcyl.ita.formic.jayjobs.jobs.models.JobExecutionMode;
import es.jcyl.ita.formic.jayjobs.task.listener.NopTaskListener;
import es.jcyl.ita.formic.jayjobs.task.listener.TaskExecListener;
import es.jcyl.ita.formic.jayjobs.task.utils.ContextAccessor;

/**
 * Front class to execute jobs.
 *
 * @author Gustavo Río (gustavo.rio@itacyl.es)
 */
public class JobFacade {

    private JobConfigRepo jobConfigRepo;
    private JobExecRepo jobExecRepo = new JobExecInMemo();
    private TaskExecListener listener = new NopTaskListener();

    // static resources
    private static String cacheFolder;
    private Map<JobExecutionMode, JobRunner> runners = new HashMap<>();

    public JobFacade() {
        // init executors
        runners.put(JobExecutionMode.FG, new MainThreadRunner());
        jobConfigRepo = new JobConfigRepo();
    }

    public JobConfig getJobConfig(CompositeContext ctx, String jobType) throws JobConfigException {
        return this.jobConfigRepo.get(ctx, jobType);
    }

    public Long executeJob(CompositeContext ctx, String jobType) throws JobException {
        JobConfig job = jobConfigRepo.get(ctx, jobType);
        return doExecuteJob(ctx, job, job.getExecMode());
    }

    public Long executeJob(CompositeContext ctx, String jobType, JobExecutionMode execMode) throws JobException {
        JobConfig job = jobConfigRepo.get(ctx, jobType);
        return doExecuteJob(ctx, job, execMode);
    }

    public Long executeJob(CompositeContext ctx, JobConfig job, JobExecutionMode execMode) throws JobException {
        return doExecuteJob(ctx, job, execMode);
    }

    private Long doExecuteJob(CompositeContext ctx, JobConfig job, JobExecutionMode execMode) throws JobException {
        if (job == null) {
            Context prjCtx = ctx.getContext("project");
            String prjName = prjCtx.getString("name");
            throw new JobConfigException(String.format(
                    "The required job type [%s] doesn't exists, make sure the file [%s/jobs/%s.json] " +
                            "exists.",
                    prjName, job.getId()));
        }
        // check execution permissions
//        checkPermissions(job); // TODO: --> Esto lo debería hacer el cliente porque requerirá acceso al Contexto Android

        JobExec jobExecutionInfo = jobExecRepo.registerExecInit(ctx, job, execMode);
        // checks needed contexts
        checkContexts(ctx, job, jobExecutionInfo);

        JobRunner executor = getJobRunner(job);
        executor.execute(ctx, job, jobExecutionInfo);

        return jobExecutionInfo.getId();
    }

    private JobRunner getJobRunner(JobConfig job) {
        if (job.getExecMode() == null) {
            // TODO: IF EXEC MODE IS NULL USE FG_ASYNC by default
        }
        JobRunner jobRunner = this.runners.get(job.getExecMode());
        if (jobRunner == null) {
            throw new UnsupportedOperationException(String.format("%s is not implemented", job.getExecMode()));
        }
        jobRunner.setListener(listener);
        return jobRunner;
    }

    /**
     * Checks if the execution context for the job contains all the contexts needed by the job
     *
     * @param ctx
     * @param jobConfig
     * @param jobExecutionInfo
     */
    private void checkContexts(CompositeContext ctx, JobConfig jobConfig, JobExec jobExecutionInfo) {
        // add global params context
        Map<String, Object> globalParams = jobConfig.getGlobalParams();
        BasicContext bc = new BasicContext("gparams");
        if (globalParams != null) {
            bc.putAll(globalParams);
        }
        ctx.addContext(bc);
        // Add job context if it doesn't exists and register job execution id
        if (!ctx.hasContext("job")) {
            ctx.addContext(new BasicContext("job"));
        }
        ContextAccessor.setJobExecId(ctx, jobExecutionInfo.getId());
    }

    /**
     * Checks if the user has given all the needed permissions to execute the job
     *
     * @param job
     */
    private void checkPermissions(JobConfig job) {
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

    public static String getCacheFolder() {
        return cacheFolder;
    }

    public static void setCacheFolder(String cacheFolder) {
        JobFacade.cacheFolder = cacheFolder;
    }

    public List<String> getResources(CompositeContext ctx, Long jobExecId) throws JobException{
        return jobExecRepo.getResources(ctx, jobExecId);
    }

    public void setListener(TaskExecListener listener) {
        if (listener != null) {
            this.listener = listener;
        }
    }
}
