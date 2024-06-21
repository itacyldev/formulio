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

import org.junit.Assert;
import org.junit.Test;
import org.mini2Dx.beanutils.MethodUtils;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import es.jcyl.ita.formic.core.context.CompositeContext;
import es.jcyl.ita.formic.jayjobs.jobs.config.JobConfig;
import es.jcyl.ita.formic.jayjobs.jobs.listener.JobExecListener;
import es.jcyl.ita.formic.jayjobs.task.models.Task;
import es.jcyl.ita.formic.sharedTest.utils.DevJobsBuilder;

public class ConcurrentJobRunnerTest {


    @Test
    public void testExecution() throws Exception {
        ExecutorService executorService = Executors.newSingleThreadExecutor();

        JobRunner runner = new ConcurrentJobRunner(executorService);
        runner.setListener(dummyListener);

        // create test objects for job execution
        DevJobsBuilder.CreateDummyJobExec builder = new DevJobsBuilder.CreateDummyJobExec();
        builder.withTasks("tasks/basic_tasks.json").build();

        runner.execute(builder.globalContext, builder.jobConfig, builder.execInfo.getId(), builder.jobExecRepo);

        // if the execution goes well, there must be task contexts inserted in the global contexts
        CompositeContext gCtx = builder.globalContext;

        // active wait until concurrent thread finishes
        boolean isFinished = false;
        while (!isFinished) {
            isFinished = (Boolean) MethodUtils.invokeMethod(dummyListener, "isFinished", null);
        }
        Assert.assertNotNull(gCtx.getContext("t1"));
        Assert.assertNotNull(gCtx.getContext("t2"));
    }
//
//    @Test
//    public void testListenerCalls() throws Exception {
//        MainThreadRunner executor = new MainThreadRunner();
//
//        // create test objects for job execution
//        DevJobsBuilder.CreateDummyJobExec builder = new DevJobsBuilder.CreateDummyJobExec();
//        builder.withTasks("tasks/basic_tasks.json").build();
//        JobExecListener listenerMock = mock(JobExecListener.class);
//        executor.setListener(listenerMock);
//        executor.execute(builder.globalContext, builder.jobConfig, builder.execInfo);
//
//        // check listener has been called
//        verify(listenerMock, times(1)).onJobEnd(any(), any());
//        verify(listenerMock, times(1)).onJobStart(any(), any());
//    }
//
//    @Test
//    public void testListenerCallOnError() throws Exception {
//        MainThreadRunner executor = new MainThreadRunner();
//
//        // create test objects for job execution
//        DevJobsBuilder.CreateDummyJobExec builder = new DevJobsBuilder.CreateDummyJobExec();
//        builder.withTasks("tasks/basic_tasks.json").build();
//        JobExecListener listenerMock = mock(JobExecListener.class);
//        executor.setListener(listenerMock);
//        builder.jobConfig.setTaskConfig("{ an erroneous Json to provoke error.....");
//        boolean hasFailed = false;
//        try {
//            executor.execute(builder.globalContext, builder.jobConfig, builder.execInfo);
//        } catch (Exception e) {
//            hasFailed = true;
//        }
//        Assert.assertTrue(hasFailed);
//        // check listener has been called
//        verify(listenerMock, times(1)).onJobError(any(), any());
//    }

    JobExecListener dummyListener = new JobExecListener() {
        boolean finished = false;

        @Override
        public void onJobStart(CompositeContext ctx, JobConfig job, long jobExecId, JobExecRepo jobExecRepo) {

        }

        @Override
        public void onJobEnd(JobConfig job, long jobExecId, JobExecRepo jobExecRepo) {
            finished = true;
        }

        @Override
        public void onJobError(JobConfig job, long jobExecId, JobExecRepo jobExecRepo) {
            finished = true;
        }


        @Override
        public void onTaskStart(Task task) {

        }

        @Override
        public void onTaskError(Task task, String message, Throwable t) {

        }

        @Override
        public void onTaskEnd(Task task) {

        }

        @Override
        public void onProgressUpdate(Task task, int total, float progress, String units) {

        }

        @Override
        public void onMessage(Task task, String message) {

        }

        public boolean isFinished() {
            return finished;
        }
    };
}
