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

import es.jcyl.ita.formic.jayjobs.jobs.config.JobConfig;
import es.jcyl.ita.formic.jayjobs.jobs.exception.JobException;
import es.jcyl.ita.formic.jayjobs.jobs.models.JobExecutionMode;
import es.jcyl.ita.formic.jayjobs.jobs.models.JobExecutionState;

/**
 * @author Gustavo Río (gustavo.rio@itacyl.es)
 */
public interface JobExecRepo {

    /**
     * Registers execution record for the passed job before it starts.
     *
     * @param job
     * @param execMode
     * @return
     */
    JobExecStatus registerExecInit(JobConfig job, JobExecutionMode execMode) throws JobException;

    void updateState(Long jobExecId, JobExecutionState state, String message) throws JobException;

    /**
     * Publishes a list of resources related to the referenced jobExecId.
     *
     * @param jobExecId
     * @param resources
     */
    void publishResources(Long jobExecId, List<JobResource> resources) throws JobException;

    /**
     * Adds a resources to the list of published resources of the job execution.
     *
     * @param jobExecId
     * @param resource
     */
    void publishResource(Long jobExecId, JobResource resource) throws JobException;

    List<JobResource> getResources(Long jobExecId) throws JobException;

    JobExecStatus getJobStatus(Long jobExecId);

    void updateJobStatus(JobExecStatus jobStatus);
}
