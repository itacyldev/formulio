package es.jcyl.ita.frmdrd.el;
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
import org.junit.BeforeClass;
import org.junit.Test;
import org.mini2Dx.beanutils.ConvertUtils;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import es.jcyl.ita.crtrepo.Entity;
import es.jcyl.ita.crtrepo.context.impl.BasicContext;
import es.jcyl.ita.crtrepo.meta.EntityMeta;
import es.jcyl.ita.crtrepo.meta.PropertyType;
import es.jcyl.ita.crtrepo.test.utils.AssertUtils;
import es.jcyl.ita.crtrepo.test.utils.RandomUtils;
import es.jcyl.ita.crtrepo.types.ByteArray;
import es.jcyl.ita.frmdrd.DummyEntity;
import es.jcyl.ita.frmdrd.configuration.ConfigConverters;

/**
 * @author Gustavo Río (gustavo.rio@itacyl.es)
 */

public class TestJexlExpressions {
    ValueExpressionFactory factory = ValueExpressionFactory.getInstance();

    protected static final JexlEngine jexl = new JexlBuilder().cache(256)
            .strict(true).silent(false).create();

    @BeforeClass
    public static void setUp() {
        ConfigConverters confConverter = new ConfigConverters();
        confConverter.init();
    }


    @Test
    public void testOnSelectContextProps() {
        // create database with random table with 1 entity
//        Context ctx = InstrumentationRegistry.getInstrumentation().getContext();

        Entity entity = new DummyEntity(null, new EntityMeta("xxx", new PropertyType[]{}, null), null);
        String expected = "xvasdfasdfvv";
        entity.set("value1", expected);
        Date expectedDate = new Date();
        entity.set("value2", expectedDate);

        Object value = JexlUtils.eval(entity, "${entity.value1}");
        Assert.assertEquals(expected, value);
        value = JexlUtils.eval(entity, "${entity.value2}");
        Assert.assertEquals(expectedDate, value);


    }

    @Test
    public void testJexlTemplate() {
        String[] expressions = new String[]{"${form1.view.field}", "${form1.view.field.substring(0)}", "${value1}", "  ${value1}  ", "${value1.value2.method()}",
                "${value1.getDate()} + 34", "${value3} + 34", "${value3[0].value5.method()} + 34",
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
            System.out.println(e.getClass());
//			System.out.println(e.evaluate(context));

        }
    }

    @Test
    public void testLiteralExpressions() {
        Class[] clazzez = new Class[]{Double.class, Date.class, ByteArray.class, Boolean.class,
                String.class, Float.class, Integer.class, Long.class};

        for (Class c : clazzez) {
            Object value = RandomUtils.randomObject(c);
            String strValue = (String) ConvertUtils.convert(value, String.class);
            ValueBindingExpression ve = factory.create(strValue, c);
            Assert.assertNotNull(ve);
            Assert.assertEquals(ve.getClass(), LiteralBindingExpression.class);
            AssertUtils.assertEquals(value, JexlUtils.eval(new BasicContext("t"), ve));
        }
    }

    /**
     * Checks the expression is correctly interpreted as readOnly (no two-way binding uicomponent-entityProperty
     * is possible
     */
    @Test
    public void testReadOnlyExpressions() {

        String[] expressions = new String[]{
                "${entity.property}",
                "   ${entity.property   }   ",
                "   ${  entity.property   }   ",
                "${entity.property.substring(0,2)}",
                "${entity.property1 + entity.property2}",
                "${entity.property1} + ${entity.property2}"

        };

        boolean[] expected = new boolean[]{
                false, false, false, true, true, true
        };
        ValueExpressionFactory expressionFactory = ValueExpressionFactory.getInstance();
        int i = 0;
        for (String expr : expressions) {
            System.out.println("Tested expression: " + expr);
            ValueBindingExpression vbExpression = expressionFactory.create(expr);
            Assert.assertEquals(expr, expected[i], vbExpression.isReadOnly());
            i++;
        }
    }

    @Test
    public void testFunctions() {

        String[] funcExpression = new String[]{
                "var t = 20; var s = function(x, y) {x + y + t}; t = 54; s(#{form1.view.%s}, 7) "
        };
        JxltEngine engine = new TemplateEngine((Engine) jexl, true, 256, '$', '#');

        JexlContext context = new MapContext();
        Map<String, Object> form = new HashMap<>();
        context.set("form1", form);
        Map<String, Object> view = new HashMap<>();
        form.put("view", view);
        view.put("string", RandomUtils.randomString(4));
        view.put("date", RandomUtils.randomDate());
        view.put("long", RandomUtils.randomLong(0, 10000));
        String[] properties = new String[]{"string", "date", "long"};


        for (String expr : funcExpression) {
            for (String property : properties) {
                String effectiveExpression = String.format(expr, property);
                JxltEngine.Expression e = engine.createExpression(effectiveExpression);
                Object value = e.evaluate(context);
                System.out.println(">>>> using: " + property);
                System.out.println(value);
            }
        }
    }

}
