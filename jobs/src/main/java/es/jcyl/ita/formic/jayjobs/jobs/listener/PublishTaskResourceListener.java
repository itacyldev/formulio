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
import es.jcyl.ita.formic.jayjobs.jobs.config.JobResourceFilter;
import es.jcyl.ita.formic.jayjobs.jobs.exec.JobExecRepo;
import es.jcyl.ita.formic.jayjobs.jobs.config.JobResource;
import es.jcyl.ita.formic.jayjobs.jobs.models.JobExecutionState;
import es.jcyl.ita.formic.jayjobs.task.models.Task;
import es.jcyl.ita.formic.jayjobs.task.utils.ContextAccessor;

/**
 * Task listener that uses a JobExecution repositorio to persist task execution
 *
 * @autor Gustavo Río Briones (gustavo.rio@itacyl.es)
 */
public class PublishTaskResourceListener implements JobExecListener {

    private final JobResourceFilter filter;
    private JobExecRepo repo;

    public PublishTaskResourceListener(JobExecRepo execRepo, JobResourceFilter filter) {
        this.repo = execRepo;
        this.filter = filter;
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
        // publish task outputFiles and add to the job execution published resources
        if (task.getTaskContext().containsKey("outputFile")) {
            String resourceId = String.format("%s.outputFile", task.getTaskContext().getPrefix());
            String path = task.getTaskContext().getString("outputFile");
            JobResource resource = new JobResource(jobExecId, resourceId, path);
            // check if the task result has to be published. By default all resources al published
            boolean publish = (this.filter == null || this.filter.accept(resourceId, path));
            if (publish) {
                repo.publishResource(jobExecId, resource);
            }
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
        repo.updateState(jobExecId, JobExecutionState.EXECUTING, "Job Started");
    }

    @Override
    public void onJobEnd(JobConfig job, long jobExecId, JobExecRepo jobExecRepo) {
        repo.updateState(jobExecId, JobExecutionState.FINISHED, "Job Finished");
    }

    @Override
    public void onJobError(JobConfig job, long jobExecId, JobExecRepo jobExecRepo) {
        repo.updateState(jobExecId, JobExecutionState.ERROR, "Job Error");
    }
}
