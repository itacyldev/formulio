package es.jcyl.ita.formic.jayjobs.jobs;
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

import es.jcyl.ita.formic.core.context.CompositeContext;
import es.jcyl.ita.formic.core.context.Context;
import es.jcyl.ita.formic.core.context.ContextAwareComponent;
import es.jcyl.ita.formic.jayjobs.jobs.config.JobConfig;
import es.jcyl.ita.formic.jayjobs.jobs.config.JobConfigException;
import es.jcyl.ita.formic.jayjobs.jobs.config.JobConfigRepo;
import es.jcyl.ita.formic.jayjobs.jobs.exception.JobException;

/**
 * Front class to execute jobs.
 *
 * @author Gustavo Río (gustavo.rio@itacyl.es)
 */
public class JobFacade implements ContextAwareComponent {

    private Context globalContext;
    private JobConfigRepo repo;


    public void executeJob(Context ctx, String jobType) throws JobException {
        JobConfig job = jobFactory.getJob(jobType);
        return doExecuteJob(ctx, jobType, JobExecutionType.BY_REQUEST, job.getExecMode());
    }

    protected void doExecuteJob(CompositeContext ctx, String jobType, JobExecutionMode execMode) throws JobConfigException {

        JobConfig job = repo.get(jobType);
        if (job == null) {
            Context prjCtx = ctx.getContext("project");
            String prjName = prjCtx.getString("name");
            throw new JobConfigException(String.format(
                    "The required job type [%s] doesn't exists, make sure the file [%s/jobs/%s.json] " +
                            "exists.",
                    prjName, jobType));
        }
//        // completar contexto de ejecuci�n del job
//        CompositeContext cmpCtx = completeContext(ctx, job);
//        // registrar el incio del job en BD
//        String userId = cmpCtx.getString("user.id");
//        if (StringUtils.isBlank(userId)) {
//            throw new JobException("El contexto de ejecuci�n pasado no contiene el identificador "
//                    + "de usuario. Debes a�adir un contexto \"user\" que contenga "
//                    + "la clave \"id\"");
//        }
//        JobExec jobExecutionInfo = registerExecution(job, execType, execMode, userId, cmpCtx);
//
//        if (execMode == JobExecutionMode.FG) {
//            jobExecutor.executeJob(cmpCtx, job, jobExecutionInfo);
//            if (StringUtils.isNotBlank(job.getNextJob())) {
//                executeNextJob(ctx, job);
//            }
//        } else { // BG
//            // launch bg task to process job
//            BackGroundJobExecutor bgExecutor = new BackGroundJobExecutor();
//            Long taskId = bgExecutor.executeJob(cmpCtx, job, jobExecutionInfo);
//            if (isRegisterEnabled()) {
//                // Recargamos la informaci�n del job (para casos en los que el
//                // proceso se ha ejecutado en local)
//                jobExecutionInfo = registry.findById(jobExecutionInfo.getId());
//                // Actualizar el registro de ejecuci�n para vincular el id de la
//                // tarea a la ejecuci�n del job
//                jobExecutionInfo.setTaskId(taskId);
//                registry.updateJobExecution(jobExecutionInfo);
//            }
//        }
        return jobExecutionInfo.getId();
    }

    @Override
    public void setContext(Context ctx) {
        this.globalContext = ctx;
    }

    public JobConfigRepo getRepo() {
        return repo;
    }

    public void setRepo(JobConfigRepo repo) {
        this.repo = repo;
    }
}
