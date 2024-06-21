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

import com.fasterxml.jackson.databind.JsonNode;

import org.junit.Assert;
import org.junit.Test;

import java.util.Collections;
import java.util.List;

import es.jcyl.ita.formic.jayjobs.task.models.GroupTask;

/**
 * @autor Gustavo Río Briones (gustavo.rio@itacyl.es)
 */
public class TestJsonUtils {

    @Test
    public void testCopyFieldsFromNode() throws Exception {
        String expectedExpression = "MY_EXPRESSION";
        String jsonString = "{\n" +
                "\"exitLoopExpression\": \""+expectedExpression+"\" " +
                "}";
        JsonNode jsonNode = JsonUtils.mapper().readTree(jsonString);

        String fieldName = "exitLoopExpression";
        List<String> fields = Collections.singletonList(fieldName);
        GroupTask destObject = new GroupTask();
        JsonUtils.copyFieldsFromNode(fields, jsonNode, destObject);

        // check the value has been set
        Assert.assertEquals(expectedExpression, destObject.getExitLoopExpression());
    }

}
