package es.jcyl.ita.formic.jayjobs.utils;
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

import org.apache.commons.lang3.StringUtils;

import java.util.Date;

import es.jcyl.ita.formic.core.context.CompositeContext;
import es.jcyl.ita.formic.core.context.Context;
import es.jcyl.ita.formic.jayjobs.jobs.config.JobConfig;
import es.jcyl.ita.formic.jayjobs.jobs.exec.JobExecStatus;
import es.jcyl.ita.formic.jayjobs.jobs.exec.JobExecInMemo;
import es.jcyl.ita.formic.jayjobs.jobs.exec.JobExecRepo;
import es.jcyl.ita.formic.repo.test.utils.RandomUtils;
import es.jcyl.ita.formic.repo.test.utils.TestUtils;

/**
 * Handy tools to create test data
 *
 * @author Gustavo Río (gustavo.rio@itacyl.es)
 */
public class DevJobsBuilder {


    public static JobConfig createDummyJobConfig() {
        JobConfig jobConfig = new JobConfig();
        jobConfig.setId(RandomUtils.randomString(10));
        return jobConfig;
    }

    public static class CreateDummyJobExec {
        public CompositeContext globalContext;
        public JobExecStatus execInfo;
        public JobExecRepo jobExecRepo;
        public JobConfig jobConfig;

        public CreateDummyJobExec() {
        }

        private String taskConfig;

        public CreateDummyJobExec withContext(CompositeContext context) {
            this.globalContext = context;
            return this;
        }

        public CreateDummyJobExec withTasks(String taskFile) {
            taskConfig = TestUtils.readAsString(taskFile);
            return this;
        }

        public CreateDummyJobExec addContext(Context context) {
            this.globalContext.addContext(context);
            return this;
        }

        public CreateDummyJobExec withJobConfig(JobConfig jobConfig) {
            this.jobConfig = jobConfig;
            return this;
        }

        public void build() {
            if (this.globalContext == null) {
                this.globalContext = JobContextTestUtils.createJobExecContext();
            }
            if (this.jobConfig == null) {
                this.jobConfig = createDummyJobConfig();
            }
            if (StringUtils.isNotBlank(this.taskConfig)) {
                this.jobConfig.setTaskConfig(this.taskConfig);
            }
            this.execInfo = new JobExecStatus();
            this.execInfo.setJobId(this.jobConfig.getId());
            this.execInfo.setExecInit(new Date());
            this.execInfo.setMode(jobConfig.getExecMode());

            this.jobExecRepo = new JobExecInMemo(this.globalContext);
        }
    }
}
