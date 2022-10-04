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

import com.android.volley.RequestQueue;
import com.android.volley.mock.VolleyMocks;

import org.apache.commons.io.FilenameUtils;
import org.junit.Test;

import java.io.File;

import es.jcyl.ita.formic.core.context.CompositeContext;
import es.jcyl.ita.formic.core.context.impl.BasicContext;
import es.jcyl.ita.formic.jayjobs.jobs.config.JobConfig;
import es.jcyl.ita.formic.jayjobs.jobs.config.JobConfigRepo;
import es.jcyl.ita.formic.jayjobs.jobs.models.JobExecutionMode;
import es.jcyl.ita.formic.jayjobs.task.processor.httpreq.RQProvider;
import es.jcyl.ita.formic.jayjobs.task.utils.ContextAccessor;
import es.jcyl.ita.formic.jayjobs.utils.JobContextTestUtils;

/**
 * @autor Rosa María Muñiz (mungarro@itacyl.es)
 */
//@RunWith(RobolectricTestRunner.class)
public class JobActaTomaMuestrasTest {

    JobFacade facade = new JobFacade();

    @Test
    public void testUploadCsv() throws Exception {
        // read job content
        CompositeContext context = JobContextTestUtils.createJobExecContext();
        JobConfigRepo repo = new JobConfigRepo();

        String projectBaseFolder = ContextAccessor.projectFolder(context);
        File cabecera = new File(projectBaseFolder, String.format("%s.csv", "cabecera"));
        File muestras = new File(projectBaseFolder, String.format("%s.csv", "muestras"));
        File firmas = new File(projectBaseFolder, String.format("%s.csv", "firmas"));
        //File firmas = new File(projectBaseFolder, String.format("%s.csv", " params.put(\"muestras\", FilenameUtils.separatorsToUnix(muestras.getPath()));"));

        BasicContext params = new BasicContext("params");
        params.put("cabecera", FilenameUtils.separatorsToUnix(cabecera.getPath()));
        params.put("muestras", FilenameUtils.separatorsToUnix(muestras.getPath()));
        params.put("firmas", FilenameUtils.separatorsToUnix(firmas.getPath()));

        context.addContext(params);
        File dbFile = new File(projectBaseFolder, String.format("%s.sqlite", "calidad"));

        params.put("dbFile", dbFile.getName());
        JobConfig jobConfig = repo.get(context, "job_acta_upload_csv");

        RequestQueue queue = VolleyMocks.createMockRQRealNetwork();
        try {
            RQProvider.setRQ(queue);
            facade.executeJob(context, jobConfig, JobExecutionMode.FG);
        } finally {
            RQProvider.clearRQ();
        }
    }

}
