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

import org.junit.Test;
import org.mini2Dx.beanutils.PropertyUtils;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;

import es.jcyl.ita.formic.jayjobs.task.models.GroupTask;

/**
 * @autor Gustavo Río Briones (gustavo.rio@itacyl.es)
 */
public class DynamicExpressionTest {
    @Test
    public void test() throws InvocationTargetException, IllegalAccessException, NoSuchMethodException {
        GroupTask gt = new GroupTask();

        Field[] declaredFields = GroupTask.class.getDeclaredFields();
        for (Field f : GroupTask.class.getDeclaredFields()) {
            if (f.isAnnotationPresent(DynamicExpression.class)) {
                System.out.println(f.getName());
            }
        }
        PropertyUtils.getPropertyDescriptor(gt, "getEnterLoopExpression");


    }

}
