package es.jcyl.ita.formic.jayjobs.jobs.config;

import static org.junit.Assert.*;

import org.junit.Test;

import es.jcyl.ita.formic.core.context.CompositeContext;
import es.jcyl.ita.formic.jayjobs.utils.TestContextUtils;

/*
 *
 *  * Copyright 2020 Javier Ramos (javier.ramos@itacyl.es), ITACyL (http://www.itacyl.es).
 *  *
 *  * Licensed under the Apache License, Version 2.0 (the "License");
 *  * you may not use this file except in compliance with the License.
 *  * You may obtain a copy of the License at
 *  *
 *  *      https://www.apache.org/licenses/LICENSE-2.0
 *  *
 *  * Unless required by applicable law or agreed to in writing, software
 *  * distributed under the License is distributed on an "AS IS" BASIS,
 *  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  * See the License for the specific language governing permissions and
 *  * limitations under the License.
 *
 */

/**
 * @autor Gustavo Río Briones (gustavo.rio@itacyl.es)
 */
public class JexlExpressionResourceFilterTest {

    @Test
    public void testJexlExpression() {
        CompositeContext ctx = TestContextUtils.createGlobalContext();

        JexlExpressionResourceFilter filter =
                new JexlExpressionResourceFilter(ctx, "${resourceId == 't1.outputFile' or path.endsWith('.json')}");
        assertTrue(filter.accept("t1.outputFile", "mypath.csv"));
        assertTrue(filter.accept("t2.outputFile", "mypath.json"));
        assertFalse(filter.accept("t3.outputFile", "mypath.xlsx"));
    }

    @Test
    public void testPathToVariable() {
        CompositeContext ctx = TestContextUtils.createGlobalContext();
        ctx.put("t1.outputFile", "myPathToFile");

        JexlExpressionResourceFilter filter =
                new JexlExpressionResourceFilter(ctx, "${resourceId == 't1.outputFile'}");
        assertTrue(filter.accept("t1.outputFile", "mypath.csv"));
        assertFalse(filter.accept("t2.outputFile", "mypath.csv"));

    }

}