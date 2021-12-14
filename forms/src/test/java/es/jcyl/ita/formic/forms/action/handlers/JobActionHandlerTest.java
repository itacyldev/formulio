package es.jcyl.ita.formic.forms.action.handlers;
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

import android.content.Context;

import androidx.test.platform.app.InstrumentationRegistry;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.robolectric.RobolectricTestRunner;

import java.util.HashMap;
import java.util.Map;

import es.jcyl.ita.formic.core.context.CompositeContext;
import es.jcyl.ita.formic.forms.MainController;
import es.jcyl.ita.formic.forms.actions.ActionContext;
import es.jcyl.ita.formic.forms.actions.ActionType;
import es.jcyl.ita.formic.forms.actions.JobActionHandler;
import es.jcyl.ita.formic.forms.actions.UserAction;
import es.jcyl.ita.formic.forms.config.Config;
import es.jcyl.ita.formic.forms.utils.MockingUtils;
import es.jcyl.ita.formic.jayjobs.jobs.JobFacade;

import static org.mockito.Mockito.*;

/**
 * @author Gustavo Río (gustavo.rio@itacyl.es)
 */
@RunWith(RobolectricTestRunner.class)
public class JobActionHandlerTest {
    static Context ctx;

    @Before
    public void setUp() {
        if (ctx != null) {
            ctx = InstrumentationRegistry.getInstrumentation().getContext();
        }
        Config.init(ctx, "");
    }

    @Test
    public void testExecuteJob() throws Exception {
        // mock main controller and prepare action controller
        MainController mc = MockingUtils.mockMainController(ctx);

        // prepare user Action
        UserAction userAction = new UserAction(ActionType.JOB);
        Map<String, Object> params = new HashMap<>();
        params.put("param1", "value1");
        params.put("param2", "value2");
        String JOB_ID = "myMockedJob";
        params.put("jobId", JOB_ID);
        userAction.setParams(params);

        // mock facade
        JobFacade facadeMock = mock(JobFacade.class);
        Config.getInstance().setJobFacade(facadeMock);

        // act - execute action
        JobActionHandler handler = new JobActionHandler(mc, mc.getRouter());
        handler.handle(new ActionContext(mc.getViewController(), ctx), userAction);

        // check the parameters passed to the facade
        ArgumentCaptor<CompositeContext> argument = ArgumentCaptor.forClass(CompositeContext.class);
        verify(facadeMock).executeJob(argument.capture(), eq(JOB_ID));

        CompositeContext execContext = argument.getValue();
        Assert.assertTrue(execContext.containsKey("params.param1"));
        Assert.assertEquals("value1", execContext.get("params.param1"));
        Assert.assertTrue(execContext.containsKey("params.param2"));
        Assert.assertEquals("value2", execContext.get("params.param2"));
    }

}
