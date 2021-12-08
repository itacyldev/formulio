package es.jcyl.ita.formic.exporter.task.config;
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
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.MissingNode;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import es.jcyl.ita.formic.core.context.Context;
import es.jcyl.ita.formic.exporter.task.listener.LogFileTaskListener;
import es.jcyl.ita.formic.exporter.task.models.IterativeTask;
import es.jcyl.ita.formic.exporter.task.models.NonIterTask;
import es.jcyl.ita.formic.exporter.task.models.Task;
import es.jcyl.ita.formic.exporter.task.models.TaskListener;
import es.jcyl.ita.formic.exporter.task.processor.ContextPopulateProcessor;
import es.jcyl.ita.formic.exporter.task.processor.NonIterProcessor;
import es.jcyl.ita.formic.exporter.task.processor.Processor;
import es.jcyl.ita.formic.exporter.task.reader.Reader;
import es.jcyl.ita.formic.exporter.task.writer.Writer;

/**
 * @author Gustavo Río (gustavo.rio@itacyl.es)
 */

public class TaskConfigFactory {
    private static final String ERROR_ON_JSON_READING = "An error occurred while trying to read JSON. %n";
    private static final String ERROR_ON_JSON_PARSING = "Couldn't parse JSON configuration. %n";
    private static final String ERROR_ON_CONFIG_BUILDING = "Couldn't create config object from JSON. %n";

    private static TaskConfigFactory _instance;

    private static final Map<String, Class<?>> registry = new HashMap<>();

    static {
//        // readers
//        registry.put("SQLREADER", SQLReader.class);
//
//        // writers
//        registry.put("CONTEXTWRITER", ContextWriter.class);
//
//        // processors
        registry.put("CONTEXTPOPULATOR", ContextPopulateProcessor.class);

    }

    private final ObjectMapper mapper; // threadsafe

    private boolean validateConfig = true;

    public static TaskConfigFactory getInstance() {
        if (_instance == null) {
            _instance = new TaskConfigFactory();
        }
        return _instance;
    }

    private TaskConfigFactory() {
        mapper = new ObjectMapper();
        mapper.enable(JsonParser.Feature.ALLOW_COMMENTS);
        mapper.configure(DeserializationFeature.FAIL_ON_NULL_CREATOR_PROPERTIES, false);
//        initValidators();
    }

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
//
//    public List<Task> getTaskList(JsonNode arrNode, Context context) {
//        List<Task> lst = new ArrayList<>();
//        if (!arrNode.isArray()) {
//            throw new TaskConfigException(
//                    "El json pasado para parsear la lista de tareas no es un array.");
//        } else {
//            for (final JsonNode objNode : arrNode) {
//                Task t = getTask(objNode, context);
//                lst.add(t);
//            }
//        }
//        return lst;
//    }

    /**
     * M�todo principal que obtiene un objeto Task a partir de un nodo json
     *
     * @param jsonNode
     * @return
     */
    public Task getTask(JsonNode jsonNode, Context context) throws TaskConfigException {
        Task t;
        // TODO: validate task data
        // TODO: mover esto a una implementaci�n de estrategia, cada tipo de
        // clase con un parseador en una clase diferente con interfaz com�n y
        // paquete "confir.parsers"
        if (isIterativeTask(jsonNode)) {
            t = readIterativeTask(jsonNode, context);
        } else {
            t = readNonIterativeTask(jsonNode, context);
        }
        // configure task listener
        TaskListener tlistener = new LogFileTaskListener();
        if (tlistener != null) {
            t.setListener(tlistener);
            tlistener.setTask(t);
        }
        return t;
    }


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

    private Class<?> getTaskClass(JsonNode jsonNode) {
        if (isIterativeTask(jsonNode)) {
            return IterativeTask.class;
        } else {
            return NonIterTask.class;
        }
    }


    /**
     * Comprueba si la tarea que se est� creando es iterativa (tiene un reader y hay
     * que procesar registros por separado) o es de un solo paso
     *
     * @param jsonNode
     * @return
     */
    private boolean isIterativeTask(JsonNode jsonNode) {
        return jsonNode.has("reader");
    }

    /**************
     * NON ITERATIVE TASK
     *
     * @param context
     *******************/

    private Task readNonIterativeTask(JsonNode json, Context context) throws TaskConfigException {
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

    /**************
     * ITERATIVE TASK
     *
     * @param context
     *******************/

    private Task readIterativeTask(JsonNode json, Context context) throws TaskConfigException {
        // leer bloques de readers/writers/processors
        try {
            // lectura de par�metros generales
            IterativeTask task = mapper.treeToValue(json, IterativeTask.class);

            // obtenemos manualmente los objetos reader/writer/processor
            readStepItems(task, json, context);
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

    private void readStepItems(IterativeTask task, JsonNode root, Context context)
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

    private Object readInnerObjectFromType(JsonNode objectNode, Context context)
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
                    "Invalid value for attribute 'type': [%s]. Valid values are: [%s]. ",
                    objectType, registry.keySet()));
        }
        return mapper.treeToValue(objectNode, clazz);
    }


    private Class<?> getClassFromType(String objectType) {
        if (!registry.containsKey(objectType.toUpperCase())) {
            return null;
        }
        return registry.get(objectType.toUpperCase());
    }

    /*******************************/
    /*** CONFIG VALIDATION ***/
    /*******************************/
//
//    private Validator getValidator(Class<?> c) {
//        return validators.get(c);
//    }
//
//    public boolean isValidateConfig() {
//        return validateConfig;
//    }
//
//    public void setValidateConfig(boolean validateConfig) {
//        this.validateConfig = validateConfig;
//    }

    /**
     * Valida la estructura json de la tarea antes de realizar la instanciaci�n del
     * correspondiente objeto.
     *
     * @param jsonNode
     * @param tClazz
     */
//    private void validateJSONConfig(JsonNode jsonNode, Class<?> tClazz) {
//        Validator validator = getValidator(tClazz);
//        if (validator != null) {
//            BasicErrorReport err = new BasicErrorReport(tClazz.getName());
//            validator.validate(jsonNode, err);
//            if (err.hasErrors()) {
//                String msg = String.format(
//                        "Se produjeron errores durante la validaci�n de la configuraci�n de la tarea %n[%s]%n[%s]",
//                        jsonNode, err);
//                LOGGER.error(msg);
//                LOGGER.error(err.getReport());
//                throw new ConfigurationException(msg);
//            }
//        }
//    }

//    /**
//     * Valida la estructura json de jsonNode (puede ser un writer, reader o
//     * processor)
//     *
//     * @param validator clase con la que se va a validar
//     * @param jsonNode  nodo JSON a validar
//     * @param className typo que se va a validar
//     */
//    private void validateInnerObject(Validator validator, JsonNode jsonNode, String className) {
//        BasicErrorReport err = new BasicErrorReport(className);
//        validator.validate(jsonNode, err);
//        if (err.hasErrors()) {
//            String msg = String.format(
//                    "Se produjeron errores durante la validaci�n de la configuraci�n de la tarea %n[%s]%n[%s]",
//                    jsonNode, err);
//            LOGGER.error(msg);
//            LOGGER.error(err.getReport());
//            throw new TaskConfigException(msg);
//        }
//    }
}
