package es.jcyl.ita.formic.exporter.jobs;
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

import es.jcyl.ita.formic.core.context.Context;
import es.jcyl.ita.formic.exporter.jobs.exception.JobException;

/**
 * Front class to execute jobs.
 *
 * @author Gustavo Río (gustavo.rio@itacyl.es)
 */
public class JobFacade {


    public Long executeJob(Context ctx, String jobType) throws JobException {
        JobConfig job = jobFactory.getJob(jobType);
        return doExecuteJob(ctx, jobType, JobExecutionType.BY_REQUEST, job.getExecMode());
    }
    protected Long doExecuteJob(Context ctx, String jobType, JobExecutionType execType,
                                JobExecutionMode execMode) throws JobException {

        JobConfig job = jobFactory.getJob(jobType);
        if (job == null) {
            throw new IllegalArgumentException(
                    String.format("El tipo de job indicado no existe [%s].", jobType));
        }
        // completar contexto de ejecuci�n del job
        CompositeContext cmpCtx = completeContext(ctx, job);
        // registrar el incio del job en BD
        String userId = cmpCtx.getString("user.id");
        if (StringUtils.isBlank(userId)) {
            throw new JobException("El contexto de ejecuci�n pasado no contiene el identificador "
                    + "de usuario. Debes a�adir un contexto \"user\" que contenga "
                    + "la clave \"id\"");
        }
        JobExec jobExecutionInfo = registerExecution(job, execType, execMode, userId, cmpCtx);

        if (execMode == JobExecutionMode.FG) {
            jobExecutor.executeJob(cmpCtx, job, jobExecutionInfo);
            if (StringUtils.isNotBlank(job.getNextJob())) {
                executeNextJob(ctx, job);
            }
        } else { // BG
            // launch bg task to process job
            BackGroundJobExecutor bgExecutor = new BackGroundJobExecutor();
            Long taskId = bgExecutor.executeJob(cmpCtx, job, jobExecutionInfo);
            if (isRegisterEnabled()) {
                // Recargamos la informaci�n del job (para casos en los que el
                // proceso se ha ejecutado en local)
                jobExecutionInfo = registry.findById(jobExecutionInfo.getId());
                // Actualizar el registro de ejecuci�n para vincular el id de la
                // tarea a la ejecuci�n del job
                jobExecutionInfo.setTaskId(taskId);
                registry.updateJobExecution(jobExecutionInfo);
            }
        }
        return jobExecutionInfo.getId();
    }
}
