package es.jcyl.ita.formic.exporter.task.config.readers;
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

import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.JsonNode;

import org.apache.commons.lang3.StringUtils;

import java.io.IOException;

import es.jcyl.ita.formic.core.context.Context;
import es.jcyl.ita.formic.exporter.task.config.TaskConfigException;
import es.jcyl.ita.formic.exporter.task.config.TaskConfigFactory;
import es.jcyl.ita.formic.exporter.task.iteration.BasicCounterIterator;
import es.jcyl.ita.formic.exporter.task.iteration.TaskContextIterator;
import es.jcyl.ita.formic.exporter.task.models.GroupTask;

/**
 * Reads an NonIterTask from a json node
 *
 * @author Gustavo Río (gustavo.rio@itacyl.es)
 */
public class GroupTaskConfigReader extends AbstractConfigObjectReader
        implements TaskConfigReader<GroupTask> {

    public GroupTaskConfigReader(TaskConfigFactory factory) {
        super(factory);
    }

    public GroupTask read(JsonNode json, Context context) throws TaskConfigException {
        GroupTask task;
        try {
            // read general parameters
            task = mapper.treeToValue(json, GroupTask.class);
            if (StringUtils.isBlank(task.getName())) {
                task.setName("tGroup-" + Math.random() * 1000000);
            }
            // find the task element that defines the nested tasks
            JsonNode tasksNode = json.get("tasks");
            if (tasksNode == null) {
                throw new IllegalArgumentException(
                        "The group element (iterator) must have the property 'tasks'");
            }
            task.setTaskConfig(tasksNode.toString());
            // get the iterator for the task loop
            TaskContextIterator it = getIteratorForTask(json);
            it.setTask(task);
            task.setLoopIterator(it);

            return task;
        } catch (JsonParseException e) {
            throw new TaskConfigException(ERROR_ON_JSON_PARSING + json, e);
        } catch (JsonMappingException e) {
            throw new TaskConfigException(
                    ERROR_ON_CONFIG_BUILDING + json, e);
        } catch (IOException e) {
            throw new TaskConfigException(
                    ERROR_ON_JSON_READING + json, e);
        }
    }

    public TaskContextIterator getIteratorForTask(JsonNode jsonNode) {
        if (jsonNode.has("iterSize")) {
            // fixed iterator
            return new BasicCounterIterator();
        } else {
//            return new SQLQueryIterator();
            throw new UnsupportedOperationException("Not implemented yet!");
        }
    }
}
