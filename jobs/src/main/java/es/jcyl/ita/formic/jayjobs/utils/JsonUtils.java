package es.jcyl.ita.formic.jayjobs.utils;
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

import org.mini2Dx.beanutils.PropertyUtils;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.List;

import es.jcyl.ita.formic.jayjobs.task.models.Task;

/**
 * @author Gustavo Río (gustavo.rio@itacyl.es)
 */
public class JsonUtils {
    // shared mapper instance (thread safe)
    private static ObjectMapper mapper;

    static {
        mapper = new ObjectMapper();
        mapper.enable(JsonParser.Feature.ALLOW_COMMENTS);
        mapper.configure(DeserializationFeature.FAIL_ON_NULL_CREATOR_PROPERTIES, false);
    }

    public static ObjectMapper mapper() {
        return mapper;
    }

    public static String pretty(String json) {
        try {
            return mapper.readTree(json).toPrettyString();
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * Copies the selected fields from the origin node and sets them into the passed object.
     * The field must exist in the node, and there must be a viable setter in the destination object Class
     *
     * @param fields
     * @param origNode
     * @param object
     */
    public static void copyFieldsFromNode(List<String> fields, JsonNode origNode, Object object) {
        try {
            for (String fieldName : fields) {
                // get the value from the original json node
                JsonNode jsonNode = origNode.get(fieldName);
                if (jsonNode != null && !jsonNode.isNull()) {
                    // TODO: generalize this to all data types, write now we just need strings
                    String value = jsonNode.textValue();
                    Method method = PropertyUtils.getPropertyDescriptor(object, fieldName).getWriteMethod();
                    method.invoke(object, value);
                }
            }
        } catch (Exception e) {
            throw new RuntimeException("An error occurred while trying to set the node value to the object: " + object.toString(), e);
        }
    }
}
