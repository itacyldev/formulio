package es.jcyl.ita.formic.core.context;
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

import es.jcyl.ita.formic.core.context.impl.BasicContext;
import es.jcyl.ita.formic.core.context.impl.MapCompositeContext;
import es.jcyl.ita.formic.forms.config.ConfigConverters;
import es.jcyl.ita.formic.core.context.impl.UnPrefixedCompositeContext;
import es.jcyl.ita.formic.forms.el.JexlUtils;

/**
 * @author Gustavo Río (gustavo.rio@itacyl.es)
 * <p>
 * Test access to Entity and View through the FormContext.
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
        Assert.assertEquals(5, JexlUtils.eval((JexlContext)ctx, "${bc1.A + bc2.a}"));
        Assert.assertEquals(7, JexlUtils.eval((JexlContext)ctx, "${bc1.B + bc2.b}"));
        // test access to methods in context objects
        Assert.assertEquals("test", JexlUtils.eval((JexlContext)ctx, "${bc2.str.substring(0,4)}"));

    }
}
