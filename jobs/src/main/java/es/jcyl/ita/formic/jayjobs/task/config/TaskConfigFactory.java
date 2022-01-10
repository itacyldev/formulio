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

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import es.jcyl.ita.formic.core.context.Context;
import es.jcyl.ita.formic.jayjobs.task.config.readers.GroupTaskConfigReader;
import es.jcyl.ita.formic.jayjobs.task.config.readers.IterativeTaskConfigReader;
import es.jcyl.ita.formic.jayjobs.task.config.readers.NonIterTaskConfigReader;
import es.jcyl.ita.formic.jayjobs.task.config.readers.TaskConfigReader;
import es.jcyl.ita.formic.jayjobs.task.listener.LogFileTaskListener;
import es.jcyl.ita.formic.jayjobs.task.models.GroupTask;
import es.jcyl.ita.formic.jayjobs.task.models.IterativeTask;
import es.jcyl.ita.formic.jayjobs.task.models.NonIterTask;
import es.jcyl.ita.formic.jayjobs.task.models.Task;
import es.jcyl.ita.formic.jayjobs.task.models.TaskListener;
import es.jcyl.ita.formic.jayjobs.task.processor.CartodruidSyncProcessor;
import es.jcyl.ita.formic.jayjobs.task.processor.ContextPopulateProcessor;
import es.jcyl.ita.formic.jayjobs.task.reader.RandomDataReader;
import es.jcyl.ita.formic.jayjobs.task.reader.SQLReader;
import es.jcyl.ita.formic.jayjobs.task.writer.CSVWriter;

/**
 * @author Gustavo Río (gustavo.rio@itacyl.es)
 */

public class TaskConfigFactory {
    protected static final String ERROR_ON_JSON_PARSING = "Couldn't parse JSON configuration. %n";

    private static TaskConfigFactory _instance;

    protected static final Map<String, Class<?>> registry = new HashMap<>();
    private Map<Class, TaskConfigReader> readers = new HashMap<>();
    private static ObjectMapper mapper;

    static {
        // readers
        registry.put("RANDOMDATAREADER", RandomDataReader.class);
        registry.put("SQLREADER", SQLReader.class);
        // writers
        registry.put("CSVWRITER", CSVWriter.class);
        // processors
        registry.put("CONTEXTPOPULATOR", ContextPopulateProcessor.class);
        registry.put("CARTODRUIDSYNC", CartodruidSyncProcessor.class);
    }


    private boolean validateConfig = true;

    public static TaskConfigFactory getInstance() {
        if (_instance == null) {
            _instance = new TaskConfigFactory();
        }
        return _instance;
    }

    private TaskConfigFactory() {
        // readers to parse task instances from json nodes
        readers.put(IterativeTask.class, new IterativeTaskConfigReader(this));
        readers.put(NonIterTask.class, new NonIterTaskConfigReader(this));
        readers.put(GroupTask.class, new GroupTaskConfigReader(this));

        mapper = new ObjectMapper();
        mapper.enable(JsonParser.Feature.ALLOW_COMMENTS);
        mapper.configure(DeserializationFeature.FAIL_ON_NULL_CREATOR_PROPERTIES, false);
//        initValidators();
    }

    /**
     * Creates task instances based on the json string configuration passed as parameter.
     *
     * @param json
     * @param context
     * @return
     * @throws TaskConfigException
     */
    public List<Task> getTaskList(String json, Context context) throws TaskConfigException {
        JsonNode arrNode;
        List<Task> lst = new ArrayList<>();
        try {
            arrNode = mapper.readTree(json);
            if (!arrNode.isArray()) {
                throw new TaskConfigException(
                        "Invalid json, the parser expects a JsonArray for the task list " +
                                "but found this: " + arrNode.asText());
            } else {
                for (final JsonNode objNode : arrNode) {
                    Task t = getTask(objNode, context);
                    lst.add(t);
                }
            }
        } catch (IOException e) {
            throw new TaskConfigException(
                    "An error occurred while trying to parse the task list json.", e);
        }
        return lst;
    }

    public List<Task> getTaskList(JsonNode arrNode, Context context) throws TaskConfigException {
        List<Task> lst = new ArrayList<>();
        if (!arrNode.isArray()) {
            throw new TaskConfigException(
                    "The jsonNode passed is not an Array instance.");
        } else {
            for (final JsonNode objNode : arrNode) {
                Task t = getTask(objNode, context);
                lst.add(t);
            }
        }
        return lst;
    }

    /**
     * Creates a task instance from the json node using the context information
     *
     * @param json: json string with the task definition
     * @return
     */
    public Task getTask(String json, Context context) throws TaskConfigException {
        JsonNode jsonNode;
        try {
            jsonNode = mapper.readTree(json);
        } catch (IOException e) {
            throw new TaskConfigException(ERROR_ON_JSON_PARSING + json, e);
        }
        return getTask(jsonNode, context);
    }

    /**
     * Creates a task instance from the json node using the context information
     *
     * @param jsonNode
     * @return
     */
    public Task getTask(JsonNode jsonNode, Context context) throws TaskConfigException {
        Class taskClzz = getTaskClass(jsonNode);
        TaskConfigReader taskReader = readers.get(taskClzz);
        Task t = taskReader.read(jsonNode, context);

        // configure task listener
        TaskListener tlistener = new LogFileTaskListener();
        if (tlistener != null) {
            t.setListener(tlistener);
            tlistener.setTask(t);
        }
        return t;
    }

    /**
     * Decides the Task type defined in the JsonNode
     *
     * @param jsonNode
     * @return
     */
    private Class getTaskClass(JsonNode jsonNode) {
        if (jsonNode.has("reader")) { // reader/processor/writer task
            return IterativeTask.class;
        } else if (jsonNode.has("tasks")) {
            // has inner tasks
            return GroupTask.class;
        } else {
            return NonIterTask.class;
        }
    }

    /**
     * Creates an iterator that dynamically evaluates the task config in each step. The task config
     * is lazily read evaluating the json to replace the ${} variables in the task config before
     * each step is executed.
     *
     * @param context
     * @param json
     * @return
     */
    public Iterator<Task> taskIterator(Context context, String json) {
        return new TaskConfigIterator(this, context, json);
    }

    public static Map<String, Class<?>> getRegistry() {
        return registry;
    }
}
