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

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import es.jcyl.ita.formic.core.context.CompositeContext;
import es.jcyl.ita.formic.jayjobs.jobs.config.JobConfig;
import es.jcyl.ita.formic.jayjobs.jobs.exception.JobException;
import es.jcyl.ita.formic.jayjobs.task.executor.TaskExecutor;

/**
 * Basic implementation of job executor that uses main thread to run job tasks.
 *
 * @author Gustavo Río (gustavo.rio@itacyl.es)
 */
public class MainThreadRunner extends AbstractJobRunner implements JobRunner {
    private static final Logger LOGGER = LoggerFactory.getLogger(MainThreadRunner.class);

    private TaskExecutor taskExecutor = new TaskExecutor();

    @Override
    public void execute(CompositeContext ctx, JobConfig job, long jobExecId, JobExecRepo jobExecRepo) throws JobException {
        notifyStart(ctx,job, jobExecId, jobExecRepo);
        try {
            taskExecutor.setListener(listener);
            taskExecutor.execute(ctx, job.getTaskConfig());
        } catch (Throwable e) {
            notifyError(ctx, job, jobExecId, jobExecRepo);
            String msg = String.format(
                    "An error occurred during the execution id [%s] of the job [%s].",
                    jobExecId, job.getId());
            LOGGER.error(msg, e);
            throw new JobException(msg, e);
        }
        notifyEnd(job, jobExecId,jobExecRepo);
    }
}
