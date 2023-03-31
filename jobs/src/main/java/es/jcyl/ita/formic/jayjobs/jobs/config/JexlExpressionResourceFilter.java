package es.jcyl.ita.formic.jayjobs.jobs.config;
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

import org.mini2Dx.beanutils.ConvertUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import es.jcyl.ita.formic.core.context.CompositeContext;
import es.jcyl.ita.formic.core.jexl.JexlContextWrapper;
import es.jcyl.ita.formic.core.jexl.JexlUtils;
import es.jcyl.ita.formic.jayjobs.jobs.exception.JobRuntimeException;

/**
 * Filtering object that evaluates a jexl expression to decide if the job resource is presented to the user.
 *
 * @autor Gustavo Río Briones (gustavo.rio@itacyl.es)
 */
public class JexlExpressionResourceFilter implements JobResourceFilter {
    private static final Logger LOGGER = LoggerFactory.getLogger(JexlExpressionResourceFilter.class);

    private final String expression;
    private final CompositeContext ctx;

    public JexlExpressionResourceFilter(CompositeContext ctx, String expression) {
        this.ctx = ctx;
        this.expression = expression;
    }

    public boolean accept(String resourceId, String path) {
        JexlContextWrapper jxCtx = new JexlContextWrapper(ctx);
        jxCtx.set("resourceId", resourceId);
        jxCtx.set("path", path);
        Object value = JexlUtils.eval(jxCtx, expression);
        try {
            return (value == null) ? false : (boolean) ConvertUtils.convert(JexlUtils.eval(jxCtx, expression), Boolean.class);
        } catch (Exception e) {
            String msg = String.format("An error occurred while trying to evaluate this expression as boolean: [%s]", expression);
            LOGGER.error(msg, e);
            throw new JobRuntimeException(msg, e);
        }
    }
}
