package es.jcyl.ita.formic.forms.context;
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

import org.apache.commons.jexl3.JexlContext;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;

import es.jcyl.ita.formic.core.context.CompositeContext;
import es.jcyl.ita.formic.core.context.Context;
import es.jcyl.ita.formic.core.context.impl.BasicContext;
import es.jcyl.ita.formic.core.context.impl.UnPrefixedCompositeContext;
import es.jcyl.ita.formic.forms.config.ConfigConverters;
import es.jcyl.ita.formic.forms.el.JexlFormUtils;
import es.jcyl.ita.formic.forms.utils.ContextTestUtils;
import es.jcyl.ita.formic.forms.view.render.renderer.WidgetContext;

/**
 * Test access to Entity and View through the FormContext.
 * <p>
 * @author Gustavo Río (gustavo.rio@itacyl.es)
 */

public class UnPrefixedCompositeContextTest {

    @BeforeClass
    public static void setUp() {
        ConfigConverters confConverter = new ConfigConverters();
        confConverter.init();
    }

    @Test
    public void testAccessWithPrefixedKeys() {
        CompositeContext ctx = new UnPrefixedCompositeContext();
        Context bc1 = new BasicContext("bc1");
        ctx.addContext(bc1);
        Context bc2 = new BasicContext("bc2");
        ctx.addContext(bc2);
        bc1.put("A", 1);
        bc1.put("B", 2);
        bc2.put("a", 4);
        bc2.put("b", 5);

        // try access with prefix
        Assert.assertEquals(1, ctx.get("bc1.A"));
        Assert.assertEquals(2, ctx.get("bc1.B"));
        Assert.assertEquals(4, ctx.get("bc2.a"));
        Assert.assertEquals(5, ctx.get("bc2.b"));
    }

    /**
     * Combine global contexto with other context to create and effective execution context
     */
    @Test
    public void testCombineGlobalContext() {
        CompositeContext globalContext = ContextTestUtils.createGlobalContext();
        Context device = new BasicContext("device");
        device.put("camera", false);
        globalContext.addContext(device);

        BasicContext params = new BasicContext("params");
        params.put("A", 1);
        params.put("B", 2);

        // combine both contexts using unprefixed context
        UnPrefixedCompositeContext ctx = new UnPrefixedCompositeContext();
        ctx.addContext(params);
        ctx.addContext(globalContext);

        Assert.assertEquals(1, ctx.get("params.A"));
        Assert.assertEquals(2, ctx.get("params.B"));
        Object o = ctx.get("device.camera");
        Assert.assertEquals(false, o);
    }

    @Test
    public void testCombineWidgetContext() {
        WidgetContext ctx = ContextTestUtils.createWidgetContext();

        Context bc1 = new BasicContext("bc1");
        ctx.addContext(bc1);
        Context bc2 = new BasicContext("bc2");
        ctx.addContext(bc2);
        bc1.put("A", 1);
        bc1.put("B", 2);
        bc2.put("a", 4);
        bc2.put("b", 5);

        Context device = new BasicContext("device");
        device.put("camera", false);
        ctx.addContext(device);

        // try access with prefix
        Assert.assertEquals(1, ctx.get("bc1.A"));
        Assert.assertEquals(2, ctx.get("bc1.B"));
        Assert.assertEquals(4, ctx.get("bc2.a"));
        Assert.assertEquals(5, ctx.get("bc2.b"));
        Object o = ctx.get("device.camera");
        Assert.assertEquals(false, o);
    }

    @Test
    public void testJEXLExpression() {
        UnPrefixedCompositeContext ctx = new UnPrefixedCompositeContext();
        Context bc1 = new BasicContext("bc1");
        ctx.addContext(bc1);
        Context bc2 = new BasicContext("bc2");
        ctx.addContext(bc2);
        bc1.put("A", 1);
        bc1.put("B", 2);
        bc2.put("a", 4);
        bc2.put("b", 5);
        bc2.put("str", "testText");

        // try access with prefix
        Assert.assertEquals(5, JexlFormUtils.eval((JexlContext) ctx, "${bc1.A + bc2.a}"));
        Assert.assertEquals(7, JexlFormUtils.eval((JexlContext) ctx, "${bc1.B + bc2.b}"));
        // test access to methods in context objects
        Assert.assertEquals("test", JexlFormUtils.eval((JexlContext) ctx, "${bc2.str.substring(0,4)}"));

    }
}
