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

import es.jcyl.ita.formic.core.context.CompositeContext;
import es.jcyl.ita.formic.jayjobs.jobs.listener.JobExecListener;
import es.jcyl.ita.formic.jayjobs.utils.DevJobsBuilder;

import static org.mockito.Mockito.*;

public class MainThreadRunnerTest {

    @Test
    public void testExecution() throws Exception {
        MainThreadRunner executor = new MainThreadRunner();

        // create test objects for job execution
        DevJobsBuilder.CreateDummyJobExec builder = new DevJobsBuilder.CreateDummyJobExec();
        builder.withTasks("tasks/basic_tasks.json").build();

        executor.execute(builder.globalContext, builder.jobConfig, builder.execInfo);

        // if the execution goes well, there must be task contexts inserted in the global contexts
        CompositeContext gCtx = builder.globalContext;
        Assert.assertNotNull(gCtx.getContext("t1"));
        Assert.assertNotNull(gCtx.getContext("t2"));
    }

    @Test
    public void testListenerCalls() throws Exception {
        MainThreadRunner executor = new MainThreadRunner();

        // create test objects for job execution
        DevJobsBuilder.CreateDummyJobExec builder = new DevJobsBuilder.CreateDummyJobExec();
        builder.withTasks("tasks/basic_tasks.json").build();
        JobExecListener listenerMock = mock(JobExecListener.class);
        executor.setListener(listenerMock);
        executor.execute(builder.globalContext, builder.jobConfig, builder.execInfo);

        // check listener has been called
        verify(listenerMock, times(1)).onJobEnd(any(), any());
        verify(listenerMock, times(1)).onJobStart(any(), any());
    }

    @Test
    public void testListenerCallOnError() throws Exception {
        MainThreadRunner executor = new MainThreadRunner();

        // create test objects for job execution
        DevJobsBuilder.CreateDummyJobExec builder = new DevJobsBuilder.CreateDummyJobExec();
        builder.withTasks("tasks/basic_tasks.json").build();
        JobExecListener listenerMock = mock(JobExecListener.class);
        executor.setListener(listenerMock);
        builder.jobConfig.setTaskConfig("{ an erroneous Json to provoke error.....");
        boolean hasFailed = false;
        try {
            executor.execute(builder.globalContext, builder.jobConfig, builder.execInfo);
        } catch (Exception e) {
            hasFailed = true;
        }
        Assert.assertTrue(hasFailed);
        // check listener has been called
        verify(listenerMock, times(1)).onJobError(any(), any());
    }
}
