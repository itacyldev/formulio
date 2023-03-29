package es.jcyl.ita.formic.jayjobs.jobs.listener;
/*
 *
 *  * Copyright 2020 Javier Ramos (javier.ramos@itacyl.es), ITACyL (http://www.itacyl.es).
 *  *
 *  * Licensed under the Apache License, Version 2.0 (the "License");
 *  * you may not use this file except in compliance with the License.
 *  * You may obtain a copy of the License at
 *  *
 *  *      https://www.apache.org/licenses/LICENSE-2.0
 *  *
 *  * Unless required by applicable law or agreed to in writing, software
 *  * distributed under the License is distributed on an "AS IS" BASIS,
 *  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  * See the License for the specific language governing permissions and
 *  * limitations under the License.
 *
 */

import es.jcyl.ita.formic.core.context.CompositeContext;
import es.jcyl.ita.formic.jayjobs.jobs.config.JobConfig;
import es.jcyl.ita.formic.jayjobs.jobs.exception.JobException;
import es.jcyl.ita.formic.jayjobs.jobs.exception.JobRuntimeException;
import es.jcyl.ita.formic.jayjobs.jobs.exec.JobExecRepo;
import es.jcyl.ita.formic.jayjobs.jobs.exec.JobResource;
import es.jcyl.ita.formic.jayjobs.jobs.models.JobExecutionState;
import es.jcyl.ita.formic.jayjobs.task.models.Task;
import es.jcyl.ita.formic.jayjobs.task.utils.ContextAccessor;
import util.Log;

/**
 * Task listener that uses a JobExecution repositorio to persist task execution
 *
 * @autor Gustavo Río Briones (gustavo.rio@itacyl.es)
 */
public class PublishTaskResourceListener implements JobExecListener {

    private JobExecRepo repo;

    public PublishTaskResourceListener(JobExecRepo execRepo) {
        this.repo = execRepo;
    }

    @Override
    public void onTaskStart(Task task) {
    }

    @Override
    public void onTaskError(Task task, String message, Throwable t) {
    }

    @Override
    public void onTaskEnd(Task task) {
        Long jobExecId = ContextAccessor.jobExecId(task.getGlobalContext());
        try {
            // publish task outputFiles and add to the job execution published resources
            if (task.getTaskContext().containsKey("outputFile")) {
                JobResource resource = new JobResource();
                resource.setResourcePath(task.getTaskContext().getString("outputFile"));
                repo.publishResource(jobExecId, resource);
            }
        } catch (JobException e) {
            throw new JobRuntimeException("There was an error while trying to publish the resources for " +
                    "job execution " + jobExecId);
        }
    }

    @Override
    public void onMessage(Task task, String message) {
    }

    @Override
    public void onProgressUpdate(Task task, int total, float progress, String units) {
    }

    @Override
    public void onJobStart(CompositeContext ctx, JobConfig job, long jobExecId, JobExecRepo jobExecRepo) {
        try {
            repo.updateState(jobExecId, JobExecutionState.EXECUTING, "Job Started");
        } catch (JobException e) {
            Log.error(String.format("Error while trying to update job state: [%s] to [%s]" + job.getId(),
                    JobExecutionState.EXECUTING), e);
        }
    }

    @Override
    public void onJobEnd(JobConfig job, long jobExecId, JobExecRepo jobExecRepo) {
        try {
            repo.updateState(jobExecId, JobExecutionState.FINISHED, "Job Finished");
        } catch (JobException e) {
            Log.error(String.format("Error while trying to update job state: [%s] to [%s]" + job.getId(),
                    JobExecutionState.FINISHED), e);
        }
    }

    @Override
    public void onJobError(JobConfig job, long jobExecId, JobExecRepo jobExecRepo) {
        try {
            repo.updateState(jobExecId, JobExecutionState.ERROR, "Job Error");
        } catch (JobException e) {
            Log.error(String.format("Error while trying to update job state: [%s] to [%s]" + job.getId(),
                    JobExecutionState.ERROR), e);
        }
    }

}
