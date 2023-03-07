package es.jcyl.ita.formic.jayjobs.task.processor;
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

/**
 * @autor Gustavo Río Briones (gustavo.rio@itacyl.es)
 */

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import java.util.ArrayList;
import java.util.List;

import es.jcyl.ita.formic.jayjobs.jexl.EvalExpression;
import es.jcyl.ita.formic.jayjobs.jexl.ExpressionEvaluator;
import es.jcyl.ita.formic.jayjobs.task.exception.StopTaskExecutionSignal;
import es.jcyl.ita.formic.jayjobs.task.exception.TaskException;
import es.jcyl.ita.formic.jayjobs.task.models.RecordPage;

public class ConditionalStopProcessor extends AbstractProcessor
        implements Processor, NonIterProcessor {
    protected static final Log LOGGER = LogFactory.getLog(ConditionalStopProcessor.class);

    /**
     * Expresión JEXLs to evaluate against the context
     */
    private String expression;
    /**
     * if true, the task will stop when an error is found
     */
    private boolean forceError = false;
    /**
     * Mensaje a utilizar en la excepción
     */
    private String message;
    private List<EvalExpression> expressionList = new ArrayList<>();

    @Override
    public RecordPage process(RecordPage page) {
        initExpression();
        if (evaluate()) {
            throw new StopTaskExecutionSignal(
                    String.format("Ejecución detenida desde tarea: [%s], expression: [%s].",
                            getTask().getId(), expression));
        }
        return page;
    }

    @Override
    public void process() throws TaskException {
        initExpression();
        if (evaluate()) {
            String msg = (StringUtils.isBlank(message) ? message
                    : String.format(
                    "Se ha cumplido la condición [%s] de la tarea [%s] se detiene el proceso.",
                    expression, task.getName()));
            if (forceError) {
                throw new TaskException(msg);
            } else {
                throw new StopTaskExecutionSignal(msg);
            }
        }
    }

    private void initExpression() {
        if (StringUtils.isNotBlank(expression)) {
            EvalExpression expr = new EvalExpression(expression, (forceError) ? "ERROR" : "INFO",
                    message);
            expressionList.add(expr);
        }
    }

    private boolean evaluate() {
        boolean eval = false;
        for (EvalExpression evalExpression : expressionList) {
            try {
                eval = ExpressionEvaluator.evalAsBool(getGlobalContext(), evalExpression.getExpression()
                );
            } catch (Exception e) {
                LOGGER.error("Error al evaluar la expresión " + evalExpression.getExpression(), e);
                eval = true;
            }
            if (eval) {
                String msgType = ("INFO".equalsIgnoreCase(evalExpression.getMessageType())) ? "INFO"
                        : "ERROR";
                String msg = evalExpression.getMessage();
//                getListener().msg(msgType, 0, msg, evalExpression.getExpression());
                LOGGER.info(msg);
                eval = true;
            }
        }
        return eval;
    }

    public String getExpression() {
        return expression;
    }

    public void setExpression(String expression) {
        this.expression = expression;
    }

    public boolean isForceError() {
        return forceError;
    }

    public void setForceError(boolean forceError) {
        this.forceError = forceError;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public List<EvalExpression> getExpressions() {
        return expressionList;
    }

    public void setExpressions(List<EvalExpression> expressionList) {
        this.expressionList = expressionList;
    }

}
