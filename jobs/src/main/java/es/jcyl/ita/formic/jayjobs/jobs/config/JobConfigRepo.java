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

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;

import org.apache.commons.io.FileUtils;

import java.io.File;
import java.io.IOException;

import es.jcyl.ita.formic.core.context.CompositeContext;
import es.jcyl.ita.formic.jayjobs.task.utils.ContextAccessor;
import es.jcyl.ita.formic.jayjobs.utils.JsonUtils;

/**
 * Reads job configuration from the job definition json files.
 * Uses the context to locate the project job folder.
 *
 * @author Gustavo Río (gustavo.rio@itacyl.es)
 */
public class JobConfigRepo {

    public JobConfig get(CompositeContext ctx, String jobType) throws JobConfigException {
        File cfgFile = findJobConfigFile(ctx, jobType);

        // read json content
        String json;
        try {
            json = FileUtils.readFileToString(cfgFile, "UTF-8");
        } catch (IOException e) {
            throw new JobConfigException(String.format(
                    "An error occurred while reading the file [%s].", cfgFile.getAbsoluteFile()), e);
        }

        // load job configuration
        JobConfig jobConf;
        try {
            jobConf = parseJsonConfig(json);
        } catch (JsonProcessingException e) {
            throw new JobConfigException(String.format(
                    "An error occurred while parsing the json content of the file [%s].",
                    cfgFile.getAbsoluteFile()), e);
        }
        jobConf.setId(jobType);
        jobConf.setConfigFile(cfgFile.getAbsolutePath());
        // TODO: proper job/tasks config validation
        return jobConf;
    }

    /**
     * Reads job and tasks configuration from json.
     *
     * @param json
     * @return
     * @throws JsonProcessingException
     */
    public JobConfig parseJsonConfig(String json) throws JsonProcessingException,
            JobConfigException {
        // create job config object from json
        JobConfig jobConfig = JsonUtils.mapper().readValue(json, JobConfig.class);

        // read tasks definition and set it in the jobConfig as plain json
        String taskJson = getTasksDefinition(jobConfig, json);
        jobConfig.setTaskConfig(taskJson);

        return jobConfig;
    }

    private String getTasksDefinition(JobConfig jobConfig, String json) throws JobConfigException,
            JsonProcessingException {
        // El atributo "tasks" est� ignorado, hay que leerlo a mano
        JsonNode rootNode = JsonUtils.mapper().readTree(json);
        JsonNode tasksNode = rootNode.path("tasks");
        if (tasksNode.isNull() || tasksNode == null) {
            throw new JobConfigException("Couldn't find 'task' element in job configuration");
        } else {
            return tasksNode.toString();
        }
    }

    /**
     * Locates job configuration file in project jobs folders
     *
     * @param ctx
     * @param jobId
     * @return
     * @throws JobConfigException
     */
    private File findJobConfigFile(CompositeContext ctx, String jobId) throws JobConfigException {
        // locate file in project jobs folder
        String jobsFolder = ContextAccessor.jobsFolder(ctx);
        File cfgFile = new File(jobsFolder, jobId + ".json");
        if (!cfgFile.exists()) {
            throw new JobConfigException(String.format(
                    "The job file [%s.json] doesn't exists in folder [%s]. " +
                            "Make sure the file exists and upper and lower case letters in the " +
                            "file name match the job id: [%s].", jobId, jobsFolder, jobId));
        }
        return cfgFile;
    }
}
