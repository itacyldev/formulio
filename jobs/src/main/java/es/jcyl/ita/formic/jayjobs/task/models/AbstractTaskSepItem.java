package es.jcyl.ita.formic.jayjobs.task.models;
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

import es.jcyl.ita.formic.core.context.CompositeContext;
import es.jcyl.ita.formic.core.context.Context;
import es.jcyl.ita.formic.jayjobs.task.listener.TaskExecListener;

/**
 * @author Gustavo Río (gustavo.rio@itacyl.es)
 */

public abstract class AbstractTaskSepItem implements TaskSepItem {

    private String type;
    private String className;

    @JsonIgnore
    protected Task task;

    public Task getTask() {
        return task;
    }

    @Override
    public void setTask(Task task) {
        this.task = task;
    }

    protected Context getTaskContext() {
        return getTask().getTaskContext();
    }

    protected CompositeContext getGlobalContext() {
        return (getTask() == null) ? null : getTask().getGlobalContext();
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getClassName() {
        return className;
    }

    public void setClassName(String className) {
        this.className = className;
    }

    public void notifyInfo(String message) {
        TaskExecListener listener = getTask().getListener();
        listener.onMessage(this.getTask(), message);
    }

    public void notifyError(String message, Throwable e) {
        TaskExecListener listener = getTask().getListener();
        listener.onTaskError(this.getTask(), message, e);
    }

    public void notifyProgress(int total, float progress, String units) {
        TaskExecListener listener = getTask().getListener();
        listener.onProgressUpdate(getTask(), total, progress, units);
    }
}
