package es.jcyl.ita.formic.jayjobs.task.iteration;
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

import org.apache.commons.lang3.StringUtils;
import org.mini2Dx.beanutils.ConvertUtils;

import es.jcyl.ita.formic.core.context.CompositeContext;
import es.jcyl.ita.formic.core.context.Context;
import es.jcyl.ita.formic.core.context.impl.BasicContext;
import es.jcyl.ita.formic.jayjobs.task.exception.TaskException;
import es.jcyl.ita.formic.jayjobs.task.models.GroupTask;
import es.jcyl.ita.formic.core.jexl.JexlUtils;

/**
 * @author Gustavo Río (gustavo.rio@itacyl.es)
 */

public class BasicCounterIterator extends AbstractTaskContextIterator {

    public BasicCounterIterator() {
    }

    public BasicCounterIterator(CompositeContext globalContext, GroupTask task) {
        setTask(task);
        setGlobalContext(globalContext);
    }

    @Override
    void doInit() {
    }

    @Override
    protected boolean doHasNext() {
        long delay = task.getIterDelay();
        if (delay != -1) {
            // active wait between iterations
            try {
                Thread.sleep(delay);
            } catch (InterruptedException e) {
                new TaskException(e);
            }
        }
        // evaluate iter counter expression
        return idx < task.getIterSize();
    }

    @Override
    protected Context createTaskContext() {
        Context ctx = new BasicContext(task.getName());
        ctx.put("idx", idx);
        return ctx;
    }

    @Override
    public boolean evalEnterIterationExpr() {
        if (StringUtils.isBlank(task.getEnterLoopExpression())) {
            return true;
        }
        return evaluateExpression(task.getEnterLoopExpression());
    }

    @Override
    public boolean evalExitIterationExpr() {
        if (StringUtils.isBlank(task.getExitLoopExpression())) {
            return false;
        }
        return evaluateExpression(task.getExitLoopExpression());
    }

    /**
     * Evaluates expression to obtain a boolean value
     *
     * @param expression
     * @return
     */
    private boolean evaluateExpression(String expression) {
        boolean evaluation;
        Object eval = JexlUtils.eval(getCurrentIterCtx(), expression);
        if (eval == null) {
            evaluation = false;
        } else {
            Boolean boolValue;
            try {
                boolValue = (Boolean) ConvertUtils.convert(eval, Boolean.class);
            } catch (Exception e) {
                throw new IllegalArgumentException(String.format("Wrong expression in task [%s] attribute is wrong, " +
                                "the expression must return a boolean, but the evaluated value is [%s]. Expression = [%s]",
                        task.getName(), eval, expression));
            }
            evaluation = boolValue.booleanValue();
        }
        if (LOGGER.isInfoEnabled()) {
            LOGGER.info(String.format("Evaluated expression : [%s] = [%s]", expression, evaluation));
        }
        return evaluation;
    }
}
