package es.jcyl.ita.formic.forms.actions.handlers;
/*
 *
 *  * Copyright 2020 Javier Ramos (javier.ramos@itacyl.es), ITACyL (http://www.itacyl.es).
 *  *
 *  * Licensed under the Apache License, Version 2.0 (the "License");
 *  * you may not use this file except in compliance with the License.
 *  * You may obtain a copy of the License at
 *  *
 *  *      https://www.apache.org/licenses/LICENSE-2.0
 *  *
 *  * Unless required by applicable law or agreed to in writing, software
 *  * distributed under the License is distributed on an "AS IS" BASIS,
 *  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  * See the License for the specific language governing permissions and
 *  * limitations under the License.
 *
 */

import static es.jcyl.ita.formic.forms.config.DevConsole.error;

import android.os.Handler;
import android.os.Looper;

import java.util.Map;

import es.jcyl.ita.formic.core.context.CompositeContext;
import es.jcyl.ita.formic.core.context.impl.BasicContext;
import es.jcyl.ita.formic.core.context.impl.UnPrefixedCompositeContext;
import es.jcyl.ita.formic.forms.App;
import es.jcyl.ita.formic.forms.MainController;
import es.jcyl.ita.formic.forms.actions.ActionContext;
import es.jcyl.ita.formic.forms.actions.ActionController;
import es.jcyl.ita.formic.forms.actions.UserAction;
import es.jcyl.ita.formic.forms.actions.UserActionException;
import es.jcyl.ita.formic.forms.router.Router;
import es.jcyl.ita.formic.forms.view.UserMessagesHelper;
import es.jcyl.ita.formic.jayjobs.jobs.JobFacade;
import es.jcyl.ita.formic.jayjobs.jobs.config.JobConfig;
import es.jcyl.ita.formic.jayjobs.jobs.exception.JobException;
import es.jcyl.ita.formic.jayjobs.jobs.exec.JobExecRepo;
import es.jcyl.ita.formic.jayjobs.jobs.listener.JobExecListener;
import es.jcyl.ita.formic.jayjobs.task.models.Task;

/**
 * Action handler to execute jobs.
 *
 * @author Gustavo Río (gustavo.rio@itacyl.es)
 */
public class JobActionHandler extends AbstractActionHandler {

    public JobActionHandler(MainController mc, Router router) {
        super(mc, router);
    }

    @Override
    public void handle(ActionContext actionContext, UserAction action) {
        if (action.getParams() == null || !action.getParams().containsKey("jobId")) {
            throw new UserActionException(error("No jobId parameter found, to call job " +
                    "from a component, set a parameter <param name='jobId' " +
                    "value='yourJobId'/> and make sure the file " +
                    "'yourProject/jobs/yourJobId.json' exists"));
        }
        String jobId = (String) action.getParams().get("jobId");

        // clone context and add parameter context
        CompositeContext ctx = prepareContext(action.getParams());
        JobFacade jobFacade = App.getInstance().getJobFacade();
        JobActionResumeListener actionListener = new JobActionResumeListener(mc.getActionController(),
                actionContext, action);
        try {
            action.setStopFlowControl(true);
            jobFacade.addListener(actionListener);
            jobFacade.executeJob(ctx, jobId);
        } catch (JobException e) {
            throw new UserActionException(error(String.format("An error occurred while executing " +
                    "the job [%s].", jobId), e));
        } finally {
            jobFacade.removeListener(actionListener);
        }
    }

    private CompositeContext prepareContext(Map<String, Object> params) {
        BasicContext paramContext = new BasicContext("params");
        for (Map.Entry<String, Object> entry : params.entrySet()) {
            if (!entry.getKey().equalsIgnoreCase("jobId")) {
                paramContext.put(entry.getKey(), entry.getValue());
            }
        }
        // create execution context linking params and globalContext
        CompositeContext execContext = new UnPrefixedCompositeContext();
        CompositeContext globalCtx = App.getInstance().getGlobalContext();
        if (globalCtx.hasContext("params")) {
            globalCtx.getContext("params").putAll(paramContext);
        } else {
            execContext.addContext(paramContext);
        }
        execContext.addContext(App.getInstance().getGlobalContext());
        return execContext;
    }

    public class JobActionResumeListener implements JobExecListener {
        private final ActionController actionController;
        private final UserAction action;
        private final Handler handler;
        private final ActionContext actionContext;

        public JobActionResumeListener(ActionController actionController, ActionContext actionContext, UserAction action) {
            this.actionController = actionController;
            this.actionContext = actionContext;
            this.action = action;
            this.handler = new Handler(Looper.getMainLooper());
        }

        @Override
        public void onJobStart(CompositeContext ctx, JobConfig job, long jobId, JobExecRepo jobExecRepo) {
        }

        @Override
        public void onJobEnd(JobConfig job, long jobId, JobExecRepo jobExecRepo) {
            handler.post(() -> {
                actionController.resumeAction();
            });
        }

        @Override
        public void onJobError(JobConfig job, long jobId, JobExecRepo jobExecRepo) {
            handler.post(() -> {
                UserMessagesHelper.toast(actionContext.getViewContext(), "An error occurred during job execution");
            });
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
    }
}
