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
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import java.util.List;

import es.jcyl.ita.formic.core.context.CompositeContext;
import es.jcyl.ita.formic.core.context.Context;
import es.jcyl.ita.formic.jayjobs.task.processor.Processor;

/**
 * Abstract class for Task interface implementations.
 *
 * @author Gustavo Río (gustavo.rio@itacyl.es)
 */

@JsonIgnoreProperties({"processor"})
public class AbstractTask implements Task {

    protected Long id;
    protected String name;
    protected String description;
    @JsonIgnore
    private Context taskContext;
    @JsonIgnore
    private CompositeContext globalContext;
    @JsonIgnore
    private TaskListener listener;
    private boolean stopOnError = true;

    @JsonIgnore
    private List<Processor> processors;

    @Override
    public Context getTaskContext() {
        return taskContext;
    }

    @Override
    public void setTaskContext(Context taskContext) {
        this.taskContext = taskContext;
    }

    @Override
    public CompositeContext getGlobalContext() {
        return globalContext;
    }

    @Override
    public void setGlobalContext(CompositeContext globalContext) {
        this.globalContext = globalContext;
    }

    @Override
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    @Override
    public TaskListener getListener() {
        return listener;
    }

    @Override
    public void setListener(TaskListener listener) {
        this.listener = listener;
    }

    @Override
    public boolean isStopOnError() {
        return stopOnError;
    }

    public void setStopOnError(boolean stopOnError) {
        this.stopOnError = stopOnError;
    }
}
