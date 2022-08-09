package es.jcyl.ita.formic.app.jobs;

/*
 * Copyright 2022 Javier Ramos (javier.ramos@itacyl.es), ITACyL (http://www.itacyl.es).
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

import static androidx.test.platform.app.InstrumentationRegistry.getInstrumentation;

import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.os.Bundle;

import androidx.test.core.app.ApplicationProvider;
import androidx.test.ext.junit.rules.ActivityScenarioRule;
import androidx.test.ext.junit.runners.AndroidJUnit4;

import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.io.File;

import es.jcyl.ita.formic.core.context.CompositeContext;
import es.jcyl.ita.formic.forms.App;
import es.jcyl.ita.formic.jayjobs.jobs.config.JobConfig;
import es.jcyl.ita.formic.jayjobs.jobs.config.JobConfigRepo;
import es.jcyl.ita.formic.jayjobs.jobs.exec.JobExecInMemo;
import es.jcyl.ita.formic.jayjobs.jobs.exec.JobExecRepo;

/**
 * @author Javier Ramos (javier.ramos@itacyl.es)
 */

@RunWith(AndroidJUnit4.class)
public class JobProgressListenerTest {

    static Intent intent;

    static {
        intent = new Intent(ApplicationProvider.getApplicationContext(), JobProgressActivity.class);
        Bundle bundle = new Bundle();
        bundle.putLong("jobExecId", 1);
        intent.putExtras(bundle);
    }

    @BeforeClass
    public static void init() {
        System.setProperty("formic.classLoading", "android");
        Context appContext = getInstrumentation().getTargetContext();
        String path = appContext.getCacheDir().getPath();
        System.setProperty("formic.classCache", path);


        App.init(appContext, "");

//        intent = new Intent(appContext, JobProgressActivity.class);
//        intent.putExtra("jobExecId", 1);
    }


    /**
     * Use {@link ActivityScenarioRule} to create and launch the activity under test, and close it
     * after test completes. This is a replacement for {@link androidx.test.rule.ActivityTestRule}.
     */
//    @Rule
//    public ActivityScenarioRule rule = new ActivityScenarioRule<>(intent);
    @Test
    public void testJobStart() throws Exception {
        Resources res = getInstrumentation().getTargetContext().getResources();
        File jobDir = JobContextTestUtils.createJobFolderInstrTest();
        CompositeContext ctx = JobContextTestUtils.createJobExecContext(jobDir.getParent());

        JobConfigRepo repo = new JobConfigRepo();
        JobExecInMemo jobExecRepo = JobExecInMemo.getInstance();
        jobExecRepo.setCtx(ctx);

        TestUtils.copyResourceToFolder(String.format("jobs/%s.json", "basic_job_repoTests"), jobDir);


        JobConfig jobConfig = repo.get(ctx, "basic_job_repoTests");


        JobProgressListener listener = new JobProgressListener();
        listener.onJobStart(ctx, jobConfig, 1, jobExecRepo);


    }

}
