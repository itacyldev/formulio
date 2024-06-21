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

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import java.util.Map;

import es.jcyl.ita.formic.jayjobs.jobs.models.JobExecutionMode;

/**
 * @author Gustavo Río (gustavo.rio@itacyl.es)
 */
@JsonIgnoreProperties({"tasks"})
public class JobConfig {
    private String id; // taken from the file name
    private String description;
    private String nextJob;
    private String configFile;
    // required contexts and Android permissions to execute the job
    private String[] requiredContexts;
    private String[] requiredPermissions;
    // selected execution mode for the job (BG/FG)
      private String executionMode;
    private Map<String, Object> globalParams;
    private boolean debug = false;
    // part of the job json config that defines the tasks
    @JsonIgnore
    private String taskConfig;
    @JsonIgnore
    private JobResourceFilter filter;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getNextJob() {
        return nextJob;
    }

    public void setNextJob(String nextJob) {
        this.nextJob = nextJob;
    }

    public String getConfigFile() {
        return this.configFile;
    }

    public void setConfigFile(String configFile) {
        this.configFile = configFile;
    }

    public String[] getRequiredContexts() {
        return requiredContexts;
    }

    public void setRequiredContexts(String[] requiredContexts) {
        this.requiredContexts = requiredContexts;
    }

    public String[] getRequiredPermissions() {
        return requiredPermissions;
    }

    public void setRequiredPermissions(String[] requiredPermissions) {
        this.requiredPermissions = requiredPermissions;
    }

    public String getExecutionMode() {
        return executionMode;
    }

    public void setExecutionMode(String executionMode) {
        this.executionMode = executionMode;
    }

    public JobExecutionMode getExecMode() {
        return getExecutionMode()!=null?Enum.valueOf(JobExecutionMode.class, getExecutionMode()):null;
    }

    public String getTaskConfig() {
        return taskConfig;
    }

    public void setTaskConfig(String taskConfig) {
        this.taskConfig = taskConfig;
    }

    public Map<String, Object> getGlobalParams() {
        return globalParams;
    }

    public void setGlobalParams(Map<String, Object> globalParams) {
        this.globalParams = globalParams;
    }

    public boolean isDebug() {
        return debug;
    }

    public void setDebug(boolean debug) {
        this.debug = debug;
    }

    public JobResourceFilter getFilter() {
        return filter;
    }

    public void setFilter(JobResourceFilter filter) {
        this.filter = filter;
    }
}
