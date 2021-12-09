package es.jcyl.ita.formic.jayjobs.jobs.config;
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

import org.apache.commons.lang3.StringUtils;

import es.jcyl.ita.formic.core.context.Context;
import es.jcyl.ita.formic.core.context.ContextAwareComponent;

/**
 * @author Gustavo Río (gustavo.rio@itacyl.es)
 */
public class JobConfigRepo implements ContextAwareComponent {

    private Context context;

    public JobConfig get(String jobType) throws JobConfigException {
        JobConfig jobConf = findJobConfig(jobType);
        if (jobConf == null) {
            return null;
        }
        // load job configuration
        loadJsonConfig(jobConf);
        // TODO: proper job config validation

        // load
//        loadTasksFromJson(jobConf);
        return jobConf;
    }

    public void loadJsonConfig(JobConfig jobConfig) {
        // si en la configuraci�n json vienen configuradas las tareas se
        // utilizan �stas.
        if (StringUtils.isBlank(jobConfig.getConfig())) {
            throw new JobConfigException(
                    String.format("La configuraci�n del job [%s] est� vac�a.",
                            jobConfig.getId()));
        }

        // leer los valores a configurar y copiarlos al objeto de conf.
        // principal obviando las propiedades que tenemos que mantener de la BD.
        JobConfig jsonConfig;
        try {
            jsonConfig = this.mapper.readValue(jobConfig.getConfig(),
                    JobConfig.class);
            BeanUtils.copyProperties(jsonConfig, jobConfig, "config", "id",
                    "name", "description", "app", "cronExpression",
                    "configFile", "lastExec", "lastSuccessExec", "nextExec",
                    "active", "execMode", "allowedExecMode");

            // si el la app es nula en el original la cogemos del json
            if (StringUtils.isBlank(jobConfig.getApp())) {
                jobConfig.setApp(jsonConfig.getApp());
            }
        } catch (Exception e) {
            throw new JobConfigException(String.format(
                    "Se produjo un error al intentar parsear la configuraci�n del job [%s].",
                    jobConfig.getId()), e);

        }
    }


    private JobConfig findJobConfig(String jobId) {
        JobConfig jobConf = null;
        try {
            jobConf = this.jobService.get(JobConfig.class, jobId);
        } catch (DAOException e) {
            throw new ConfigurationException(String.format(
                    "Se produjo un error al intentar la conf. del tipo de job [%s]",
                    jobId), e);
        }
        if (jobConf != null
                && StringUtils.isNotBlank(jobConf.getConfigFile())) {
            // buscar configuraci�n en fichero
            String config;
            try {
                config = this.fileConfigLocator
                        .getConfigFromFile(jobConf.getConfigFile());
                jobConf.setConfig(config);
            } catch (IOException e) {
                throw new ConfigurationException(String.format(
                        "No se ha podido consultar el fichero de configuraci�n [%s] para el tipo de job [%s]. "
                                + "Se est� buscando en la ruta recursos_app/configs de UQSERV: [%s].",
                        jobConf.getConfigFile(), jobId,
                        Paths.get(AppConfig.getDirRecursos(), "configs")
                                .toString()),
                        e);
            }
        }
        return jobConf;
    }

    @Override
    public void setContext(Context ctx) {
        this.context = ctx;
    }
}
