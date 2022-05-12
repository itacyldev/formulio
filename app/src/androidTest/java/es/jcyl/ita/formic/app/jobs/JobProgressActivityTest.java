package es.jcyl.ita.formic.app.jobs;

import android.content.Intent;
import android.content.res.Resources;
import android.os.Bundle;

import androidx.lifecycle.Lifecycle;
import androidx.test.core.app.ActivityScenario;
import androidx.test.core.app.ApplicationProvider;
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

import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.matcher.ViewMatchers.withId;
import static androidx.test.espresso.matcher.ViewMatchers.withText;
import static androidx.test.platform.app.InstrumentationRegistry.getInstrumentation;

@RunWith(AndroidJUnit4.class)
public class JobProgressActivityTest {

    ActivityScenario scenario;

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

        JobConfig jobConfig = repo.get(ctx, "basic_job_repoTests");

        progressListener = new JobProgressListener(false);
        progressListener.onJobStart(jobConfig, 1, jobExecRepo);

        scenario = rule.getScenario();
        scenario.moveToState(Lifecycle.State.STARTED);
    }

    @Test
    public void testStartJob() throws Exception {

        // TextView
        onView(withId(R.id.progress_textView)).check(matches(withText("Job has started!")));
        //textView.check(matches(withText("Job has started!")));

//        onView(withId(es.jcyl.ita.formic.forms.R.id.progress_textView))
//                .check(matches(hasDescendant(withText("Job has started!"))));

        //progressListener.onMessage(null, "message1");

        // check first element in view
        //onView(withId(es.jcyl.ita.formic.forms.R.id.progress_textView))
        //textView.check(matches(withText("message1")));

        //progressListener.onMessage(null, "message2");

        // check first element in view
        //onView(withId(es.jcyl.ita.formic.forms.R.id.progress_textView))
        //textView.check(matches(withText("message2")));
    }

    @After
    public void tearDown() {
        if (scenario != null) {
            scenario.close();
        }
    }

}
