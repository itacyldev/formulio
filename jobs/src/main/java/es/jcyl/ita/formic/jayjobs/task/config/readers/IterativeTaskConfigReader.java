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

import java.io.IOException;

import es.jcyl.ita.formic.core.context.Context;
import es.jcyl.ita.formic.jayjobs.task.config.TaskConfigException;
import es.jcyl.ita.formic.jayjobs.task.config.TaskConfigFactory;
import es.jcyl.ita.formic.jayjobs.task.models.IterativeTask;

/**
 * Reads an IterativeTask from a json node
 *
 * @author Gustavo Río (gustavo.rio@itacyl.es)
 */
public class IterativeTaskConfigReader extends AbstractConfigObjectReader
        implements TaskConfigReader<IterativeTask> {

    public IterativeTaskConfigReader(TaskConfigFactory factory) {
        super(factory);
    }

    public IterativeTask read(JsonNode json, Context context) throws TaskConfigException {
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

}
