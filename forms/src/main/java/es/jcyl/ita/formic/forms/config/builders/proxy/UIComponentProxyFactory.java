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

import net.bytebuddy.ByteBuddy;
import net.bytebuddy.android.AndroidClassLoadingStrategy;
import net.bytebuddy.description.modifier.Visibility;
import net.bytebuddy.dynamic.DynamicType;
import net.bytebuddy.dynamic.loading.ClassLoadingStrategy;
import net.bytebuddy.implementation.InvocationHandlerAdapter;
import net.bytebuddy.matcher.ElementMatchers;

import java.io.File;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationHandler;

import es.jcyl.ita.formic.forms.components.UIComponent;
import es.jcyl.ita.formic.forms.config.Config;
import es.jcyl.ita.formic.forms.config.ConfigurationException;
import es.jcyl.ita.formic.forms.config.meta.Attribute;
import es.jcyl.ita.formic.forms.el.ValueBindingExpression;
import es.jcyl.ita.formic.forms.view.render.renderer.RenderingEnv;

/**
 * @author Gustavo Río (gustavo.rio@itacyl.es)
 */
public class UIComponentProxyFactory {

    private static UIComponentProxyFactory _instance;
    private static Field handlerField;
    private ClassLoadingStrategy strategy;
    private static RenderingEnv env;

    public static UIComponentProxyFactory getInstance() {
        if (_instance == null) {
            _instance = new UIComponentProxyFactory();
        }
        return _instance;
    }

    private UIComponentProxyFactory() {
        initClassLoadingStrategy();
    }

    private void initClassLoadingStrategy() {
        if (System.getProperty("java.vendor").toLowerCase().contains("android") ||
                System.getProperty("formic.classLoading").contains("android")) {
            String path = System.getProperty("formic.classCache");
            this.strategy = new AndroidClassLoadingStrategy.Wrapping(new File(path));
        }
    }

    public UIComponent create(Object delegate, String[] methodNames, Attribute[] attributes, ValueBindingExpression[] expressions) {
        Class clazz = delegate.getClass();
        try {
            DynamicType.Unloaded builder = new ByteBuddy()
                    .subclass(clazz)
                    .defineField("handler", InvocationHandler.class, Visibility.PUBLIC)
                    //                .implement(HandlerSetter.class)
                    .method(ElementMatchers.any())
                    .intercept(InvocationHandlerAdapter.toField("handler"))
                    .make();
            if (strategy != null) {
                builder.load(getClass().getClassLoader(), strategy);
            } else {
                builder.load(getClass().getClassLoader());
            }
            Object instance = builder.load(clazz.getClassLoader()).getLoaded().newInstance();
            UIComponentInvocationHandler handler = new UIComponentInvocationHandler(delegate, methodNames, attributes, expressions);
            handler.setFactory(this);

            if (handlerField == null) {
                this.handlerField = instance.getClass()
                        .getDeclaredField("handler");
            }
            handlerField.setAccessible(true);
            handlerField.set(instance, handler);

            return (UIComponent) instance;
        } catch (Exception e) {
            throw new ConfigurationException("Cannot create dynamic proxy for delegate: " + delegate, e);
        }
    }

    public RenderingEnv getEnv() {
        return env;
    }

    public void setEnv(RenderingEnv env) {
        UIComponentProxyFactory.env = env;
    }
}
