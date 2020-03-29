package es.jcyl.ita.frmdrd;
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

import org.apache.commons.jexl3.JexlBuilder;
import org.apache.commons.jexl3.JexlContext;
import org.apache.commons.jexl3.JexlEngine;
import org.apache.commons.jexl3.JxltEngine;
import org.apache.commons.jexl3.MapContext;
import org.apache.commons.jexl3.internal.Engine;
import org.apache.commons.jexl3.internal.TemplateEngine;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.RobolectricTestRunner;

import java.util.Date;

import es.jcyl.ita.crtrepo.Entity;
import es.jcyl.ita.frmdrd.el.JexlUtils;

/**
 * @author Gustavo Río (gustavo.rio@itacyl.es)
 */
@RunWith(RobolectricTestRunner.class)
public class TestJexlExpressions {
    protected static final JexlEngine jexl = new JexlBuilder().cache(256)
            .strict(true).silent(false).create();

    @Test
    public void testOnSelectContextProps() {
        // create database with random table with 1 entity
//        Context ctx = InstrumentationRegistry.getInstrumentation().getContext();

        Entity entity = new DummyEntity(null, null, null);
        String expected = "xvasdfasdfvv";
        entity.set("value1", expected);
        Date expectedDate = new Date();
        entity.set("value2", expectedDate);

        Object value = JexlUtils.eval(entity, "entity.value1");
        Assert.assertEquals(expected, value);
        value = JexlUtils.eval(entity, "entity.value2");
        Assert.assertEquals(expectedDate, value);


    }

    @Test
    public void testJexlTemplate() {
        String[] expressions = new String[]{"${value1}", "  ${value1}  ", "${value1.getDate()} + 34", "${value3} + 34",
                "${value2} + 'value3' + ${value1.toString().substring(0,2)} + 34"};

        JxltEngine engine = new TemplateEngine((Engine) jexl, true, 256, '$', '#');

        JexlContext context = new MapContext();
//		context.set("value1", new Date());
//		context.set("value2", 123);
//		context.set("value3", "test string");

        for (String expression : expressions) {
            System.out.println("---------------" + expression + "--------------------");
            JxltEngine.Expression e = engine.createExpression(expression);

            System.out.println(e.getVariables());
//			System.out.println(e.evaluate(context));

        }

    }

}
