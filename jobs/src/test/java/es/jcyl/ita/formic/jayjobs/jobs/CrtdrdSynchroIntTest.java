package es.jcyl.ita.formic.jayjobs.jobs;/*
 * Copyright 2020 Rosa María Muñiz (mungarro@itacyl.es), ITACyL (http://www.itacyl.es).
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

import androidx.annotation.NonNull;

import com.android.volley.RequestQueue;
import com.android.volley.mock.VolleyMocks;

import org.apache.commons.io.FilenameUtils;
import org.codehaus.plexus.util.StringUtils;
import org.junit.Test;

import java.io.File;
import java.util.HashMap;
import java.util.Map;

import es.jcyl.ita.formic.core.context.CompositeContext;
import es.jcyl.ita.formic.core.context.impl.BasicContext;
import es.jcyl.ita.formic.jayjobs.jobs.config.JobConfig;
import es.jcyl.ita.formic.jayjobs.jobs.config.JobConfigRepo;
import es.jcyl.ita.formic.jayjobs.jobs.models.JobExecutionMode;
import es.jcyl.ita.formic.jayjobs.task.processor.httpreq.RQProvider;
import es.jcyl.ita.formic.jayjobs.task.utils.ContextAccessor;
import es.jcyl.ita.formic.jayjobs.utils.JobContextTestUtils;
import es.jcyl.ita.formic.repo.test.utils.TestUtils;

/**
 * Testing jobs in a more complex scenario, craeting a synchronization client to send a file to a REST endpoint and
 * downloading results from job execution.
 *
 * @author Gustavo Río (gustavo.rio@itacyl.es)
 */
public class CrtdrdSynchroIntTest {

    JobFacade facade = new JobFacade();

    @Test
    public void testRunSynchro() throws Exception {
        // read job content
        CompositeContext context = JobContextTestUtils.createJobExecContext();
        JobConfigRepo repo = new JobConfigRepo();

        Map<String, Object> gParams = getCredentials();

        String fileName = TestUtils.findFileName("jobs/testsyn.sqlite");
        gParams.put("dbFile", fileName);

        JobConfig jobConfig = repo.get(context, "cartodruid_sync_job");
        jobConfig.setGlobalParams(gParams);

        RequestQueue queue = VolleyMocks.createMockRQRealNetwork();
        try {
            RQProvider.setRQ(queue);
            facade.executeJob(context, jobConfig, JobExecutionMode.FG);
        } finally {
            RQProvider.clearRQ();
        }
    }

    private Map<String, Object> getCredentials() {
        // set variable in .profile
        String wksPwd = System.getenv("CRTSYN_CRED_TESTSYN");
        if (StringUtils.isBlank(wksPwd)) {
            throw new IllegalStateException("CRTSYN_CRED_TESTSYN must be defined with user/pass value for testing wks. Check .profile.");
        }
        String[] credentials = wksPwd.split("#");
        if (credentials.length != 4) {
            throw new IllegalStateException("Invalid value for env variable CRTSYN_CRED_TESTSYN format: endpoint#wksId#user#pwd.");
        }
        Map<String, Object> gParams = new HashMap<>();
        gParams.put("endpoint", "https://serviciosprex.itacyl.es/crtchk10/cnt/rest/syncro/");//credentials[0]
        gParams.put("wksId", credentials[1]);
        gParams.put("wksUser", credentials[2]);
        gParams.put("wksPwd", credentials[3]);
        return gParams;
    }

}
