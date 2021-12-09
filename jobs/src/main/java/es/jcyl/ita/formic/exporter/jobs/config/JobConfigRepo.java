package es.jcyl.ita.formic.exporter.jobs.config;
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

/**
 * @author Gustavo Río (gustavo.rio@itacyl.es)
 */
public class JobConfigRepo {

    public JobConfig get(String jobType) throws JobConfigException {
        JobConfig jobConf = findJobConfig(jobType);

        if (jobConf == null) {
            throw new JobConfigException(String.format(
                    "The required job type [%s] doesn't exists, make sure the file [%s.json] exists in the /jobs folder of your project",
                    jobType));
        }
        // load job configuration
        loadJsonConfig(jobConf);
        // TODO: proper job config validation

        // load
        loadTasksFromJson(jobConf);
        return jobConf;
    }

    public void loadJsonConfig(JobConfig jobConfig) {
        // si en la configuraci�n json vienen configuradas las tareas se
        // utilizan �stas.
        if (StringUtils.isBlank(jobConfig.getConfig())) {
            throw new ConfigurationException(
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
            throw new ConfigurationException(String.format(
                    "Se produjo un error al intentar parsear la configuraci�n del job [%s].",
                    jobConfig.getId()), e);

        }
    }
}
