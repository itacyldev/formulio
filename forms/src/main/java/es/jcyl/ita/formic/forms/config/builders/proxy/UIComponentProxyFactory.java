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
import es.jcyl.ita.formic.forms.config.ConfigurationException;
import es.jcyl.ita.formic.forms.config.meta.Attribute;
import es.jcyl.ita.formic.forms.el.ValueBindingExpression;
import es.jcyl.ita.formic.forms.view.render.renderer.RenderingEnv;

/**
 * @author Gustavo Río (gustavo.rio@itacyl.es)
 */
public class UIComponentProxyFactory {

    public static final String PROP_CLASS_LOADING_STRATEGY = "formic.classLoading";
    public static final String PROP_CLASS_CACHE = "formic.classCache";
    private static UIComponentProxyFactory _instance;
    private static Field handlerField;
    private ClassLoadingStrategy strategy;
    private static RenderingEnv env;
    private ByteBuddy byteBuddy;

    public static UIComponentProxyFactory getInstance() {
        if (_instance == null) {
            _instance = new UIComponentProxyFactory();
        }
        return _instance;
    }

    private UIComponentProxyFactory() {
        byteBuddy = new ByteBuddy();
        initClassLoadingStrategy();
    }

    private void initClassLoadingStrategy() {
        String property = System.getProperty(PROP_CLASS_LOADING_STRATEGY);
        if (property != null && property.contains("android")) {
            String path = System.getProperty(PROP_CLASS_CACHE);
            this.strategy = new AndroidClassLoadingStrategy.Wrapping(new File(path));
        }
    }

    public UIComponent create(Object delegate, String[] methodNames, Attribute[] attributes, ValueBindingExpression[] expressions) {
        try {

            Object instance = instantiateProxy(delegate.getClass());

            UIComponentInvocationHandler handler = new UIComponentInvocationHandler(delegate, methodNames, attributes, expressions);
            handler.setFactory(this);
//            this.handlerField = instance.getClass()
//                    .getDeclaredField("handler");
//            handlerField.setAccessible(true);
//            handlerField.set(instance, handler);

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

    private Object instantiateProxy(Class clazz) throws InstantiationException, IllegalAccessException {
        DynamicType.Unloaded unloaded = new ByteBuddy()
                .subclass(clazz)
                .defineField("handler", InvocationHandler.class, Visibility.PUBLIC)
                .method(ElementMatchers.any())
                .intercept(InvocationHandlerAdapter.toField("handler"))
                .make();
        Class dynamicType;
        if (strategy != null) {
            dynamicType = unloaded.load(getClass().getClassLoader(), strategy).getLoaded();
        } else {
            dynamicType = unloaded.load(getClass().getClassLoader()).getLoaded();
        }
        Object instance = dynamicType.newInstance();
        return instance;
    }

    public RenderingEnv getEnv() {
        return env;
    }

    public void setEnv(RenderingEnv env) {
        UIComponentProxyFactory.env = env;
    }
}
