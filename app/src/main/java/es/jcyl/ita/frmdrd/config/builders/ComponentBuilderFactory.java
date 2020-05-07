package es.jcyl.ita.frmdrd.config.builders;
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

import es.jcyl.ita.frmdrd.config.ConfigurationException;
import es.jcyl.ita.frmdrd.config.resolvers.ComponentResolver;
import es.jcyl.ita.frmdrd.forms.FormListController;

import static es.jcyl.ita.frmdrd.config.DevConsole.error;

/**
 * @author Gustavo Río (gustavo.rio@itacyl.es)
 */
public class ComponentBuilderFactory {

    private XmlPullParser xpp;
    private static ComponentBuilderFactory _instance;
    private static final Map<String, Class<? extends ComponentBuilder>> _builders = new HashMap<>();
    private ComponentResolver componentResolver;

    public static ComponentBuilderFactory getInstance() {
        if (_instance == null) {
            _instance = new ComponentBuilderFactory();
        }
        return _instance;
    }

    private ComponentBuilderFactory() {
        // default registering
        registerBuilder("main", FormConfigBuilder.class);
        registerBuilder("list", FormListControllerBuilder.class);
        registerBuilder("form", FormBuilder.class);
    }

    public void registerBuilder(String tagName, Class<? extends ComponentBuilder> builder) {
        _builders.put(tagName, builder);
    }

    public ComponentBuilder getBuilder(String tagName) {
        Class builderClass = _builders.get(tagName);
        if (builderClass == null) {
            // use default builder
            builderClass = DefaultComponentBuilder.class;
        }
        ComponentBuilder builder = newComponentBuilder(builderClass, tagName);
        builder.setName(tagName);

        return builder;
    }

    private ComponentBuilder newComponentBuilder(Class clazz, String tagName) {
        ComponentBuilder builder = null;
        // try with no parameter
        try {
            builder = (ComponentBuilder) clazz.getDeclaredConstructor().newInstance();
        } catch (Exception e) {
        }
        // try with one String parameter
        try {
            builder = (ComponentBuilder) clazz.getDeclaredConstructor(new Class[]{String.class}).newInstance(tagName);
        } catch (Exception e) {
            String msg = String.format("Class [%s] couldn't be instantiated, check it has a " +
                    "no-parameter constructor or a constructor with one string parameter " +
                    "(tagName).", clazz.getName());
            throw new ConfigurationException(error(msg, e), e);
        }
        if (builder instanceof AbstractComponentBuilder) {
            // common builder configuration
            ((AbstractComponentBuilder) builder).setResolver(this.componentResolver);
        }
        return builder;
    }

    public void setXmlParser(XmlPullParser xpp) {
        this.xpp = xpp;
    }

    public void setComponentResolver(ComponentResolver componentResolver) {
        this.componentResolver = componentResolver;
    }
}
