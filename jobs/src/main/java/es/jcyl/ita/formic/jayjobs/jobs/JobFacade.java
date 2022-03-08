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
import es.jcyl.ita.formic.core.context.impl.BasicContext;
import es.jcyl.ita.formic.jayjobs.jobs.config.JobConfig;
import es.jcyl.ita.formic.jayjobs.jobs.config.JobConfigException;
import es.jcyl.ita.formic.jayjobs.jobs.config.JobConfigRepo;
import es.jcyl.ita.formic.jayjobs.jobs.exception.JobException;
import es.jcyl.ita.formic.jayjobs.jobs.executor.JobExec;
import es.jcyl.ita.formic.jayjobs.jobs.executor.JobExecNopRepo;
import es.jcyl.ita.formic.jayjobs.jobs.executor.JobExecRepo;
import es.jcyl.ita.formic.jayjobs.jobs.executor.JobExecutor;
import es.jcyl.ita.formic.jayjobs.jobs.executor.MainThreadExecutor;
import es.jcyl.ita.formic.jayjobs.jobs.models.JobExecutionMode;

/**
 * Front class to execute jobs.
 *
 * @author Gustavo Río (gustavo.rio@itacyl.es)
 */
public class JobFacade {

    private JobConfigRepo jobConfigRepo;
    private JobExecRepo jobExecRepo = new JobExecNopRepo();

    // static resources
    private static String cacheFolder;

    private Map<JobExecutionMode, JobExecutor> executors = new HashMap<>();

    public JobFacade() {
        // init executors
        executors.put(JobExecutionMode.FG, new MainThreadExecutor());
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
        // checks needed contexts and permissions
        checkContexts(job, ctx);
//        checkPermissions(job); // TODO: --> Esto lo debería hacer el cliente porque requerirá acceso al Contexto Android

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
    private void checkContexts(JobConfig jobConfig, CompositeContext ctx) {
        // add global params context
        Map<String, Object> globalParams = jobConfig.getGlobalParams();
        BasicContext bc = new BasicContext("gparams");
        bc.putAll(globalParams);
        ctx.addContext(bc);
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
}
