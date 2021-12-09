package es.jcyl.ita.formic.jayjobs.task.config;
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

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;

import java.io.IOException;
import java.util.Iterator;

import es.jcyl.ita.formic.core.context.Context;
import es.jcyl.ita.formic.core.context.VarSubstitutor;
import util.Log;
import es.jcyl.ita.formic.jayjobs.jobs.config.ProcessConfigException;
import es.jcyl.ita.formic.jayjobs.task.models.Task;
import es.jcyl.ita.formic.jayjobs.task.exception.TaskException;

/**
 * @author Gustavo Río (gustavo.rio@itacyl.es)
 */

public class TaskConfigIterator implements Iterator<Task> {

    private int taskNum = 0;
    private ObjectMapper mapper; // threadsafe

    TaskConfigFactory taskFactory;
    String jsonConfig;
    Context context;
    private Iterator<JsonNode> iterator;

    public TaskConfigIterator(TaskConfigFactory taskJSONFactory, Context context,
                              String tasksConfig) {
        this.taskFactory = taskJSONFactory;
        jsonConfig = tasksConfig;
        this.context = context;
        JsonNode tasksNode;

        mapper = new ObjectMapper();
        mapper.enable(JsonParser.Feature.ALLOW_COMMENTS);
        mapper.enable(SerializationFeature.INDENT_OUTPUT);
        mapper.configure(DeserializationFeature.FAIL_ON_NULL_CREATOR_PROPERTIES, false);

        try {
            tasksNode = mapper.readTree(tasksConfig);
            if (!tasksNode.isNull()) {
                iterator = tasksNode.iterator();
            }
        } catch (IOException e) {
            throw new ProcessConfigException(
                    "An error occurred while trying to parse the json task configuration.", e);
        }
    }

    public boolean hasNext() {
        return iterator.hasNext();
    }

    public Task next() {
        Task task;
        JsonNode taskNode = iterator.next();

        String jsonStr = taskNode.toString();
        String replaced;

        replaced = VarSubstitutor.replace(jsonStr, context);
        if (Log.isDebugEnabled()) {
            Log.debug("Effective task JSON:\n" + replaced);
        }

        JsonNode jsonNode = null;
        try {
            jsonNode = readJsonTree(replaced);
            task = taskFactory.getTask(jsonNode, context);
        } catch (TaskException e) {
            throw new RuntimeException(e);
        }
        taskNum++;
        return task;
    }

    private JsonNode readJsonTree(String json) throws TaskException {
        try {
            JsonNode newTree = mapper.readTree(json);
            return newTree;
        } catch (Exception e) {
            throw new TaskException(String.format(
                    "Se ha producido un error al intentar" + " parsear el json de la tarea: \n%s",
                    json), e);
        }
    }
}
