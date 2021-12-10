package es.jcyl.ita.formic.jayjobs.jobs.config;
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

import org.junit.Assert;
import org.junit.Test;

import es.jcyl.ita.formic.core.context.CompositeContext;
import es.jcyl.ita.formic.jayjobs.utils.JobContextTestUtils;

public class JobConfigRepoTest {

    @Test
    public void testFindJobFile() throws Exception {
        CompositeContext ctx = JobContextTestUtils.createJobExecContext();
        JobConfigRepo repo = new JobConfigRepo();

        JobConfig jobConfig = repo.get(ctx, "basic_job_repoTests.json");

        Assert.assertNotNull(jobConfig);
        Assert.assertNotNull(jobConfig.getExecMode());
        Assert.assertNotNull(jobConfig.getDescription());
        Assert.assertNotNull(jobConfig.getConfigFile());
        Assert.assertNotNull(jobConfig.getId());
        Assert.assertNotNull(jobConfig.getRequiredContexts());
    }
}
