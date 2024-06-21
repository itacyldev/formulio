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

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.MissingNode;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import es.jcyl.ita.formic.core.context.Context;
import es.jcyl.ita.formic.jayjobs.task.config.TaskConfigException;
import es.jcyl.ita.formic.jayjobs.task.config.TaskConfigFactory;
import es.jcyl.ita.formic.jayjobs.task.models.IterativeTask;
import es.jcyl.ita.formic.jayjobs.task.processor.Processor;
import es.jcyl.ita.formic.jayjobs.task.reader.Reader;
import es.jcyl.ita.formic.jayjobs.task.writer.Writer;

/**
 * @author Gustavo Río (gustavo.rio@itacyl.es)
 */
public class AbstractConfigObjectReader {
    protected static final String ERROR_ON_JSON_READING = "An error occurred while trying to read JSON. %n";
    protected static final String ERROR_ON_JSON_PARSING = "Couldn't parse JSON configuration. %n";
    protected static final String ERROR_ON_CONFIG_BUILDING = "Couldn't create config object from JSON. %n";

    protected final TaskConfigFactory factory;
    protected static ObjectMapper mapper; // threadsafe

    public AbstractConfigObjectReader(TaskConfigFactory factory) {
        this.factory = factory;
        // configure common mapper
        mapper = new ObjectMapper();
        mapper.enable(JsonParser.Feature.ALLOW_COMMENTS);
        mapper.configure(DeserializationFeature.FAIL_ON_NULL_CREATOR_PROPERTIES, false);
    }

    protected void readStepItems(IterativeTask task, JsonNode root, Context context)
            throws IOException, TaskConfigException {
        JsonNode objectNode = root.path("reader");
        Object obj = readInnerObjectFromType(objectNode, context);
        task.setReader((Reader) obj);

        objectNode = root.path("writer");
        obj = readInnerObjectFromType(objectNode, context);
        task.setWriter((Writer) obj);

        // single processor
        objectNode = root.path("processor");
        obj = readInnerObjectFromType(objectNode, context);
        try {
            task.setProcessor((Processor) obj);
        } catch (ClassCastException e) {
            throw new TaskConfigException(
                    String.format("Invalid Processor implementation, [%s] cannot be used in " +
                                    "iterativeTask, choose a processor that implements Processor " +
                                    "interface. "
                            , objectNode.toString()));
        }

        // list of processors
        objectNode = root.path("processors");
        if (objectNode.isMissingNode()) {
            return;
        }
        if (!objectNode.isArray()) {
            throw new TaskConfigException(String.format(
                    "Hay un error en la configuraci�n de la tarea [%s]: el atributo 'processors' "
                            + "debe tener formato de lista con un objeto Processor por item.",
                    task.getName()));
        } else {
            List<Processor> processorList = new ArrayList<>();
            Iterator<JsonNode> nodeList = ((ArrayNode) objectNode).elements();
            while (nodeList.hasNext()) {
                JsonNode node = nodeList.next();
                obj = readInnerObjectFromType(node, context);
                processorList.add((Processor) obj);
            }
            task.setProcessors(processorList);
        }
    }

    protected Object readInnerObjectFromType(JsonNode objectNode, Context context)
            throws JsonProcessingException {
        if (objectNode.equals(MissingNode.getInstance())) {
            // no se ha definido el objeto en el JSON
            return null;
        }
        JsonNode typeNode = objectNode.get("type");
        if (typeNode == null) {
            throw new IllegalArgumentException(
                    "Invalid json config, attribute 'type' is missing in the task config element.");
        }
        String objectType = typeNode.asText();
//        Validator validator = objectValidators.get(objectType.toLowerCase());
//        if (validator != null) {
//            validateInnerObject(validator, objectNode, objectType);
//        }
        Class<?> clazz = getClassFromType(objectType);
        if (clazz == null) {
            throw new IllegalArgumentException(String.format(
                    "Invalid value for attribute 'type': [%s]. Valid values are: %s. " +
                            "Check de registry of TaskConfigFactory.", objectType,
                    factory.getRegistry().keySet()));
        }
        return mapper.treeToValue(objectNode, clazz);
    }


    protected Class<?> getClassFromType(String objectType) {
        if (!factory.getRegistry().containsKey(objectType.toUpperCase())) {
            return null;
        }
        return factory.getRegistry().get(objectType.toUpperCase());
    }

}
