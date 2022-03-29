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

import es.jcyl.ita.formic.core.context.CompositeContext;
import es.jcyl.ita.formic.jayjobs.jobs.listener.JobExecListener;
import es.jcyl.ita.formic.jayjobs.jobs.config.JobConfig;

/**
 * @author Gustavo Río (gustavo.rio@itacyl.es)
 */
public abstract class AbstractJobRunner {

    JobExecListener listener;
    JobExecRepo jobExecRepo;

    public JobExecListener getListener() {
        return listener;
    }

    protected void notifyStart(JobConfig job, JobExecStatus jobExecInfo, JobExecRepo jobExecRepo) {
        if (this.listener != null) {
            this.listener.onJobStart(job, jobExecInfo, jobExecRepo);
        }
    }

    protected void notifyEnd(JobConfig job, JobExecStatus jobExecInfo, JobExecRepo jobExecRepo) {
        if (this.listener != null) {
            this.listener.onJobEnd(job, jobExecInfo, jobExecRepo);
        }
    }

    protected void notifyError(CompositeContext ctx, JobConfig job, JobExecStatus jobExecInfo, JobExecRepo jobExecRepo) {
        if (this.listener != null) {
            this.listener.onJobError(job, jobExecInfo, jobExecRepo);
        }
    }

    public void setListener(JobExecListener listener) {
        this.listener = listener;
    }
}
