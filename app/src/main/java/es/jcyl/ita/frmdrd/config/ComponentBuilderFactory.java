package es.jcyl.ita.frmdrd.config;
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

import org.xmlpull.v1.XmlPullParser;

import java.util.HashMap;
import java.util.Map;

import es.jcyl.ita.frmdrd.config.builders.AbstractComponentBuilder;
import es.jcyl.ita.frmdrd.config.builders.DefaultComponentBuilder;
import es.jcyl.ita.frmdrd.config.builders.FormBuilder;
import es.jcyl.ita.frmdrd.config.builders.FormConfigBuilder;
import es.jcyl.ita.frmdrd.config.builders.FormEditControllerBuilder;
import es.jcyl.ita.frmdrd.config.builders.FormListControllerBuilder;
import es.jcyl.ita.frmdrd.config.resolvers.ComponentResolver;
import es.jcyl.ita.frmdrd.config.resolvers.RepositoryAttributeResolver;
import es.jcyl.ita.frmdrd.forms.FCAction;

import static es.jcyl.ita.frmdrd.config.DevConsole.error;

/**
 * @author Gustavo Río (gustavo.rio@itacyl.es)
 */
public class ComponentBuilderFactory {

    private XmlPullParser xpp;
    private static ComponentBuilderFactory _instance;
    private static final Map<String, ComponentBuilder> _builders = new HashMap<>();
    private ComponentResolver componentResolver;
    private RepositoryAttributeResolver repoAttResolver;

    public static ComponentBuilderFactory getInstance() {
        if (_instance == null) {
            _instance = new ComponentBuilderFactory();
        }
        return _instance;
    }

    private ComponentBuilderFactory() {
        // default registering
        registerBuilder("main", newBuilder(FormConfigBuilder.class, "main"));
        registerBuilder("list", newBuilder(FormListControllerBuilder.class, "list"));
        registerBuilder("edit", newBuilder(FormEditControllerBuilder.class, "edit"));
        registerBuilder("form", newBuilder(FormBuilder.class, "form"));

        ComponentBuilder defaultActionBuilder = newDefaultBuilder(FCAction.class, "action");
        // same component builder with different alias
        registerBuilder("nav", defaultActionBuilder);
        registerBuilder("new", defaultActionBuilder);
        registerBuilder("update", defaultActionBuilder);
        registerBuilder("delete", defaultActionBuilder);
        registerBuilder("cancel", defaultActionBuilder);

        repoAttResolver = new RepositoryAttributeResolver();
    }


    public void registerBuilder(String tagName, ComponentBuilder builder) {
        _builders.put(tagName, builder);
    }

    public <T> ComponentBuilder<T> getBuilder(String tagName, Class<T> type) {
        return (ComponentBuilder<T>) getBuilder(tagName);
    }

    public ComponentBuilder getBuilder(String tagName) {
        ComponentBuilder builder = _builders.get(tagName);
//        if (builder == null) {
//            // create default builder for this tag and register
//            registerBuilder(tagName, new ComponentBuilder() {
//
//            // use default builder
//            builderClass = DefaultComponentBuilder.class;
//        }
//        ComponentBuilder builder = newComponentBuilder(builderClass, tagName);
//        if (builder == null) {
//            throw new ConfigurationException("No builder found for tagName: " + tagName);
//        }

        return builder;
    }

    private ComponentBuilder newBuilder(Class clazz, String tagName) {
        ComponentBuilder builder = null;
        // try with no parameter
        try {
            builder = (ComponentBuilder) clazz.getDeclaredConstructor().newInstance();
        } catch (Exception e) {
        }
        // try with one String parameter
        if (builder == null) {
            try {
                builder = (ComponentBuilder) clazz.getDeclaredConstructor(new Class[]{String.class}).newInstance(tagName);
            } catch (Exception e) {
                String msg = String.format("Class [%s] couldn't be instantiated, check it has a " +
                        "no-parameter constructor or a constructor with one string parameter " +
                        "(tagName).", clazz.getName());
                throw new ConfigurationException(error(msg, e), e);
            }
        }
        ((AbstractComponentBuilder) builder).setFactory(this);
        return builder;
    }

    private ComponentBuilder newDefaultBuilder(Class elementType, String tag) {
        AbstractComponentBuilder builder = new DefaultComponentBuilder(tag, elementType);
        builder.setFactory(this);
        return builder;
    }


    public void setXmlParser(XmlPullParser xpp) {
        this.xpp = xpp;
    }

    /**
     * Externally created and initialized
     *
     * @param componentResolver
     */
    public void setComponentResolver(ComponentResolver componentResolver) {
        this.componentResolver = componentResolver;
    }


    public ComponentResolver getComponentResolver() {
        return componentResolver;
    }

    public RepositoryAttributeResolver getRepoAttResolver() {
        return repoAttResolver;
    }
}
