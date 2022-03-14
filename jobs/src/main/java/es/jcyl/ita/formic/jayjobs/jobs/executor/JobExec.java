package es.jcyl.ita.formic.jayjobs.jobs.executor;
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

import java.util.Date;

import es.jcyl.ita.formic.jayjobs.jobs.models.JobExecutionMode;
import es.jcyl.ita.formic.jayjobs.jobs.models.JobExecutionState;

/**
 * Keeps information about a job execution
 *
 * @author Gustavo Río (gustavo.rio@itacyl.es)
 */
public class JobExec {
    private Long id;
    private String jobId;
    private String userId;
    private String userParam;
    /**
     * Job is initially registered for execution
     */
    private Date execInit;
    /**
     * Job starts its execution
     */
    private Date execTime;
    private Date execEnd;
    private JobExecutionState state;
    private JobExecutionMode mode;
    /**
     * Context used to launch the job execution serialized as json
     */
    private String context;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getJobId() {
        return jobId;
    }

    public void setJobId(String jobId) {
        this.jobId = jobId;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getUserParam() {
        return userParam;
    }

    public void setUserParam(String userParam) {
        this.userParam = userParam;
    }

    public Date getExecInit() {
        return execInit;
    }

    public void setExecInit(Date execInit) {
        this.execInit = execInit;
    }

    public Date getExecTime() {
        return execTime;
    }

    public void setExecTime(Date execTime) {
        this.execTime = execTime;
    }

    public Date getExecEnd() {
        return execEnd;
    }

    public void setExecEnd(Date execEnd) {
        this.execEnd = execEnd;
    }

    public JobExecutionState getState() {
        return state;
    }

    public void setState(JobExecutionState state) {
        this.state = state;
    }

    public JobExecutionMode getMode() {
        return mode;
    }

    public void setMode(JobExecutionMode mode) {
        this.mode = mode;
    }

    public String getContext() {
        return context;
    }

    public void setContext(String context) {
        this.context = context;
    }
}
