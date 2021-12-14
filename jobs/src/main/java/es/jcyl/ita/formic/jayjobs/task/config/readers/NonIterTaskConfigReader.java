package es.jcyl.ita.formic.jayjobs.task.config.readers;
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
import com.fasterxml.jackson.databind.node.ArrayNode;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import es.jcyl.ita.formic.core.context.Context;
import es.jcyl.ita.formic.jayjobs.task.config.TaskConfigException;
import es.jcyl.ita.formic.jayjobs.task.config.TaskConfigFactory;
import es.jcyl.ita.formic.jayjobs.task.models.NonIterTask;
import es.jcyl.ita.formic.jayjobs.task.processor.NonIterProcessor;

/**
 * Reads an NonIterTask from a json node
 *
 * @author Gustavo Río (gustavo.rio@itacyl.es)
 */
public class NonIterTaskConfigReader extends AbstractConfigObjectReader
        implements TaskConfigReader<NonIterTask> {

    public NonIterTaskConfigReader(TaskConfigFactory factory) {
        super(factory);
    }

    public NonIterTask read(JsonNode json, Context context) throws TaskConfigException {
        NonIterTask task;
        try {
            task = mapper.treeToValue(json, NonIterTask.class);
            JsonNode processorNode = json.get("processor");
            NonIterProcessor processor = (NonIterProcessor) readInnerObjectFromType(processorNode,
                    context);
            task.setProcessor(processor);

            // list of processors
            JsonNode objectNode = json.path("processors");
            if (objectNode.isMissingNode()) {
                return task;
            }
            if (!objectNode.isArray()) {
                throw new TaskConfigException(String.format(
                        "An error found in task [%s], attribute 'processor' must be a json list " +
                                "with processor configs as items.",
                        task.getName()));
            } else {
                List<NonIterProcessor> processorList = new ArrayList<>();
                Iterator<JsonNode> nodeList = ((ArrayNode) objectNode).elements();
                while (nodeList.hasNext()) {
                    JsonNode node = nodeList.next();
                    Object obj = readInnerObjectFromType(node, context);
                    processorList.add((NonIterProcessor) obj);
                }
                task.setProcessors(processorList);
            }
            return task;
        } catch (JsonParseException e) {
            throw new TaskConfigException(
                    ERROR_ON_JSON_PARSING + json, e);
        } catch (JsonMappingException e) {
            throw new TaskConfigException(
                    ERROR_ON_CONFIG_BUILDING + json, e);
        } catch (IOException e) {
            throw new TaskConfigException(
                    ERROR_ON_JSON_READING + json, e);
        }
    }
}
