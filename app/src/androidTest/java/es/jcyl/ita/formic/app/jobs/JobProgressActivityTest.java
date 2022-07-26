package es.jcyl.ita.formic.app.jobs;

import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.matcher.ViewMatchers.hasDescendant;
import static androidx.test.espresso.matcher.ViewMatchers.withId;
import static androidx.test.espresso.matcher.ViewMatchers.withText;
import static androidx.test.platform.app.InstrumentationRegistry.getInstrumentation;

import android.content.Intent;
import android.content.res.Resources;
import android.os.Bundle;

import androidx.test.core.app.ActivityScenario;
import androidx.test.core.app.ApplicationProvider;
import androidx.test.espresso.ViewInteraction;
import androidx.test.ext.junit.rules.ActivityScenarioRule;
import androidx.test.ext.junit.runners.AndroidJUnit4;

import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.io.File;

import es.jcyl.ita.formic.R;
import es.jcyl.ita.formic.core.context.CompositeContext;
import es.jcyl.ita.formic.jayjobs.jobs.config.JobConfig;
import es.jcyl.ita.formic.jayjobs.jobs.config.JobConfigException;
import es.jcyl.ita.formic.jayjobs.jobs.config.JobConfigRepo;
import es.jcyl.ita.formic.jayjobs.jobs.exec.JobExecInMemo;

@RunWith(AndroidJUnit4.class)
public class JobProgressActivityTest {

    ActivityScenario scenario;
    JobConfig jobConfig;
    JobProgressListener progressListener;

    static Intent intent;

    static {
        intent = new Intent(ApplicationProvider.getApplicationContext(), JobProgressActivity.class);
        Bundle bundle = new Bundle();
        bundle.putLong("jobExecId", 1);
        intent.putExtras(bundle);
    }


    @Rule
    public ActivityScenarioRule rule = new ActivityScenarioRule<>(intent);

    @Before
    public void initJob() throws JobConfigException {
        Resources res = getInstrumentation().getTargetContext().getResources();

        File jobDir = JobContextTestUtils.createJobFolderInstrTest();
        CompositeContext ctx = JobContextTestUtils.createJobExecContext(jobDir.getParent());

        JobConfigRepo repo = new JobConfigRepo();
        JobExecInMemo jobExecRepo = JobExecInMemo.getInstance();
        jobExecRepo.setCtx(ctx);

        TestUtils.copyResourceToFolder(String.format("jobs/%s.json", "basic_job_repoTests"), jobDir);

        jobConfig = repo.get(ctx, "basic_job_repoTests");

        scenario = rule.getScenario();

        progressListener = new JobProgressListener(false);
        progressListener.onJobStart(jobConfig, 1, jobExecRepo);


        //scenario.moveToState(Lifecycle.State.STARTED);
    }

    @Test
    public void testJobMessages() throws Exception {
        Thread.sleep(500);

        ViewInteraction view = onView(withId(R.id.progressLayout));
        view.check(matches(hasDescendant(withText("Job basic_job_repoTests has started!"))));


        progressListener.onMessage(null, "message1");
        Thread.sleep(500);
        view.check(matches(hasDescendant(withText("message1"))));

        progressListener.onJobEnd(jobConfig, 1, JobExecInMemo.getInstance());
        Thread.sleep(500);
        view.check(matches(hasDescendant(withText("Job basic_job_repoTests has finished!"))));

    }


    @After
    public void tearDown() {
        if (scenario != null) {
            scenario.close();
        }
    }


}