package es.jcyl.ita.formic.forms.repo.el;
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

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.greaterThan;
import static org.hamcrest.Matchers.notNullValue;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

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

import es.jcyl.ita.formic.core.context.CompositeContext;
import es.jcyl.ita.formic.core.context.impl.BasicContext;
import es.jcyl.ita.formic.core.context.impl.UnPrefixedCompositeContext;
import es.jcyl.ita.formic.forms.config.ConfigConverters;
import es.jcyl.ita.formic.core.context.impl.DateTimeContext;
import es.jcyl.ita.formic.forms.el.JexlFormUtils;
import es.jcyl.ita.formic.forms.el.LiteralBindingExpression;
import es.jcyl.ita.formic.forms.el.ValueBindingExpression;
import es.jcyl.ita.formic.forms.el.ValueExpressionFactory;
import es.jcyl.ita.formic.repo.Entity;
import es.jcyl.ita.formic.repo.memo.MemoEntity;
import es.jcyl.ita.formic.repo.meta.EntityMeta;
import es.jcyl.ita.formic.repo.meta.PropertyType;
import es.jcyl.ita.formic.repo.meta.types.ByteArray;
import es.jcyl.ita.formic.repo.test.utils.AssertUtils;
import es.jcyl.ita.formic.repo.test.utils.RandomUtils;

/**
 * @author Gustavo Río (gustavo.rio@itacyl.es)
 */

public class JexlExpressionsTest {
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

        Entity entity = new MemoEntity(null, new EntityMeta("xxx", new PropertyType[]{}, null), null);
        String expected = "xvasdfasdfvv";
        entity.set("value1", expected);
        Date expectedDate = new Date();
        entity.set("value2", expectedDate);

        Object value = JexlFormUtils.eval(entity, "${entity.value1}");
        assertEquals(expected, value);
        value = JexlFormUtils.eval(entity, "${entity.value2}");
        assertEquals(expectedDate, value);
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
            assertEquals(ve.getClass(), LiteralBindingExpression.class);
            AssertUtils.assertEquals(value, JexlFormUtils.eval(new BasicContext("t"), ve));
        }
    }

    /**
     * Checks the expression is correctly interpreted as readonly (no two-way binding uicomponent-entityProperty
     * is possible
     */
    @Test
    public void testReadonlyExpressions() {

        String[] expressions = new String[]{
                "${entity.property}",
                "   ${entity.property   }   ",
                "   ${  entity.property   }   ",
                "${entity.property.substring(0,2)}",
                "${entity.property1 + entity.property2}",
                "${entity.property1} + ${entity.property2}",
                "superheroes/${entity.property1.toLowerCase().replace('_','')+'.jpg'}"
        };

        boolean[] expected = new boolean[]{
                false, false, false, true, true, true, true
        };
        ValueExpressionFactory expressionFactory = ValueExpressionFactory.getInstance();
        int i = 0;
        for (String expr : expressions) {
            System.out.println("Tested expression: " + expr);
            ValueBindingExpression vbExpression = expressionFactory.create(expr);
            assertEquals(expr, expected[i], vbExpression.isReadonly());
            i++;
        }
    }

    /**
     * Test method and getter calls with JExl
     */
    @Test
    public void methodCalls() {
        JexlContext context = new MapContext();
        context.set("myObject", new MyTestClass());
        Object o = JexlFormUtils.eval(context, "${myObject.getMethod()}");
        assertThat((Integer) o, greaterThan(1));
        o = JexlFormUtils.eval(context, "${myObject.method}");
        assertThat((Integer) o, greaterThan(1));
    }


    @Test
    public void testFunctions() {

        String[] funcExpression = new String[]{
                "var t = 20; var s = function(x, y) {x + y + t}; t = 54; s(#{form1.view.%s}, 7) "
        };
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
                JxltEngine.Expression e = JexlFormUtils.createExpression(effectiveExpression);
                Object value = e.evaluate(context);
                System.out.println(">>>> using: " + property);
                System.out.println(value);
            }
        }
    }

    @Test
    public void testAccessHelperFunctions() {
        JexlContext context = new MapContext();

        JxltEngine.Expression e = JexlFormUtils.createExpression("${math:cos(23.0)}");
        assertEquals(Math.cos(23), e.evaluate(context));

        String str = (String) JexlFormUtils.createExpression("${random:string(30)}").evaluate(context);
        assertEquals(30, str.length());

        int value = (int) JexlFormUtils.createExpression("${random:integer(5,15)}").evaluate(context);
        assertTrue(value >=5 && value <=15);

        float floatValue = (float) JexlFormUtils.createExpression("${random:decimal()}").evaluate(context);
        assertTrue(floatValue >=0 && floatValue <=1);
    }

    @Test
    public void testBasicScripting1() {
        JexlContext context = new MapContext();
        Map<String, Object> params = new HashMap<>();
        params.put("entityId", 123);
        context.set("params", params);

        Map<String, Object> entity = new HashMap<>();
        entity.put("string", RandomUtils.randomString(4));
        entity.put("date", RandomUtils.randomDate());
        entity.put("long", RandomUtils.randomLong(0, 10000));

        Fixture[] fxts = new Fixture[]{
                new Fixture("${params.entityId}", 123),
                new Fixture("${empty params.entityId}", false),
                new Fixture("${empty (params.entityId)}", false),
                new Fixture("${not empty (params.entityId)? params.entityId : 44}", 123),
                new Fixture("${empty (params.MissingParam)? 456 : 'defaultValue'}", 456),
                new Fixture("${if(empty(params.MissingParam)) {3} else {4}}", 3),
                new Fixture("${if(empty(params.MissingParam)) {params.MissingParam=99}; params.MissingParam}", 99),
        };

        for (Fixture fixture : fxts) {
            JxltEngine.Expression e = JexlFormUtils.createExpression(fixture.expression);
            Object value = e.evaluate(context);
            assertEquals("Error evaluating expression: " + fixture.expression, fixture.expected, value);
        }
    }


    class Fixture {
        public Fixture(String expression, Object expected) {
            this.expression = expression;
            this.expected = expected;
        }

        String expression;
        Object expected;
    }


    @Test
    public void testAccessCompositeContext() {
        CompositeContext context = createContext();
        Object o = JexlFormUtils.eval(context, "${date.now}");
        assertThat(o, notNullValue());
        o = JexlFormUtils.eval(context, "${location.method}");
        assertThat(o, notNullValue());
        assertThat((Integer) o, greaterThan(1));

    }

    private CompositeContext createContext() {
        CompositeContext globalContext = new UnPrefixedCompositeContext();
        globalContext.addContext(new DateTimeContext("date"));
        globalContext.put("location", new MyTestClass());

        return globalContext;
    }


    public class MyTestClass {

        public Integer getMethod() {
            return RandomUtils.randomInt(5, 100);
        }
    }
}
