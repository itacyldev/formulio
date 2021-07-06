package es.jcyl.ita.formic.forms.config.builders.proxy;
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

import org.jetbrains.annotations.NotNull;
import org.mini2Dx.beanutils.ConvertUtils;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import es.jcyl.ita.formic.forms.components.UIComponent;
import es.jcyl.ita.formic.forms.config.ConfigurationException;
import es.jcyl.ita.formic.forms.el.JexlFormUtils;
import es.jcyl.ita.formic.forms.el.ValueBindingExpression;
import es.jcyl.ita.formic.forms.view.render.renderer.WidgetContext;

/**
 * Handler that receives UIComponent method invocations to dynamically evaluate expressions
 * against the context to allow define a JEXL expression in any UIComponent attribute.
 *
 * @author Gustavo Río (gustavo.rio@itacyl.es)
 */
public class UIComponentInvocationHandler implements InvocationHandler {

    private static final String getValueBindingExpressions_METHOD = "getvaluebindingexpressions";
    private UIComponentProxyFactory factory;

    private final Object delegate;
    private Method staticMethod = null;
    private String[] methodNames;
    //    private Attribute[] attributes;
    private ValueBindingExpression[] expressions;

    public UIComponentInvocationHandler(Object delegate, List<ExpressionMethodRef> expressionMethods) {
        this.delegate = delegate;

        try {
            staticMethod = UIComponent.class.getMethod("getValueExpression");
        } catch (NoSuchMethodException e) {
            throw new ConfigurationException("Cannot find method getValueExpression in UIComponent", e);
        }
    }

    public UIComponentInvocationHandler(Object delegate, String[] methodNames, ValueBindingExpression[] expressions) {
        this.delegate = delegate;
        this.methodNames = methodNames;
        this.expressions = expressions;
        try {
            staticMethod = UIComponent.class.getMethod("getValueExpression");
        } catch (NoSuchMethodException e) {
            throw new ConfigurationException("Cannot find method getValueExpression in UIComponent", e);
        }
    }

    @Override
    public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
        String methodName = method.getName().toLowerCase();

        if (methodName.equals(getValueBindingExpressions_METHOD)) {
            // gather component expressions
            return calculateBindingExpressions(proxy);
        } else {
            int arraysPos = findMethodByName(methodName);
            if (arraysPos == -1 || isIgnoredMethod(methodName)) {
                // call delegate
                return method.invoke(delegate, args);
            } else {
                ValueBindingExpression expression = expressions[arraysPos];
                WidgetContext widgetContext = factory.getWidgetContext();

                Object attValue = (widgetContext == null) ? expression :
                        JexlFormUtils.eval(widgetContext, expression);
                // convert object value to the method return type
                return ConvertUtils.convert(attValue, method.getReturnType());
            }
        }
    }

    @NotNull
    private Set<ValueBindingExpression> calculateBindingExpressions(Object proxy) throws IllegalAccessException, InvocationTargetException {
        Set<ValueBindingExpression> expressions = new HashSet<>();
        ValueBindingExpression valueBindingExpression = (ValueBindingExpression) staticMethod.invoke(delegate, new Object[]{});
        if (valueBindingExpression != null) {
            expressions.add(valueBindingExpression);
        }
        for (ValueBindingExpression expr : this.expressions) {
            expressions.add(expr);
        }
        return expressions;
    }

    private boolean isIgnoredMethod(String methodName) {
        return methodName.startsWith("set") ||
                methodName.equals("getvalue")
                || methodName.equals("getid");
    }


    private int findMethodByName(String methodName) {
        for (int i = 0; i < methodNames.length; i++) {
            if (methodName.equals(methodNames[i])) {
                return i;
            }
        }
        return -1;
    }

    public UIComponentProxyFactory getFactory() {
        return factory;
    }

    public void setFactory(UIComponentProxyFactory factory) {
        this.factory = factory;
    }
}
