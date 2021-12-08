package es.jcyl.ita.formic.exporter.task.models;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import es.jcyl.ita.formic.core.context.CompositeContext;
import es.jcyl.ita.formic.core.context.Context;
import es.jcyl.ita.formic.exporter.task.Task;

@JsonIgnoreProperties({"processor"})
public class AbstractTask implements Task {

    protected Long id;
    protected String name;
    protected String description;

    @JsonIgnore
    private Context taskContext;
    @JsonIgnore
    private CompositeContext globalContext;

    public Context getTaskContext() {
        return taskContext;
    }

    public void setTaskContext(Context taskContext) {
        this.taskContext = taskContext;
    }

    public CompositeContext getGlobalContext() {
        return globalContext;
    }

    public void setGlobalContext(CompositeContext globalContext) {
        this.globalContext = globalContext;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

}
