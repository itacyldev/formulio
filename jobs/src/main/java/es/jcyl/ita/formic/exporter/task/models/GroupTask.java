package es.jcyl.ita.formic.exporter.task.models;
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

import java.util.List;

import es.jcyl.ita.formic.exporter.task.iteration.TaskContextIterator;

public class GroupTask extends AbstractTask {

    private int iterSize;
    private int pageSize;
    private int parallelize = 1;
    private String iterQuerySQL;

    @JsonIgnore
    private String taskConfig;
    @JsonIgnore
    private List<Task> tasks;
    private TaskContextIterator loopIterator;

    public List<Task> getTasks() {
        return tasks;
    }

    public void setTasks(List<Task> tasks) {
        this.tasks = tasks;
    }

    public String getIterQuerySQL() {
        return iterQuerySQL;
    }

    public void setIterQuerySQL(String iterQuerySQL) {
        this.iterQuerySQL = iterQuerySQL;
    }

    public int getIterSize() {
        return iterSize;
    }

    public void setIterSize(int iterSize) {
        this.iterSize = iterSize;
    }

    public String getTaskConfig() {
        return taskConfig;
    }

    public void setTaskConfig(String taskConfig) {
        this.taskConfig = taskConfig;
    }

    public int getPageSize() {
        return pageSize;
    }

    public void setPageSize(int pageSize) {
        this.pageSize = pageSize;
    }

    public TaskContextIterator getLoopIterator() {
        return loopIterator;
    }

    public void setLoopIterator(TaskContextIterator loopIterator) {
        this.loopIterator = loopIterator;
    }
}
