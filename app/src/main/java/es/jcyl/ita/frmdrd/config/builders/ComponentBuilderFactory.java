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

import es.jcyl.ita.frmdrd.config.ConfigConsole;
import es.jcyl.ita.frmdrd.config.ConfigurationException;

/**
 * @author Gustavo Río (gustavo.rio@itacyl.es)
 */
public class ComponentBuilderFactory {

    private XmlPullParser xpp;
    private final ConfigConsole console = new ConfigConsole();
    private static ComponentBuilderFactory _instance;
    private static final Map<String, Class<? extends ComponentBuilder>> _builders = new HashMap<>();

    public static ComponentBuilderFactory getInstance() {
        if (_instance == null) {
            _instance = new ComponentBuilderFactory();
        }
        return _instance;
    }

    private ComponentBuilderFactory() {
        // default registering
        registerBuilder("form", FormConfigBuilder.class);
        registerBuilder("list", GroupingBuilder.class);
        registerBuilder("edit", GroupingBuilder.class);
    }

    public void registerBuilder(String tagName, Class<? extends ComponentBuilder> builder) {
        _builders.put(tagName, builder);
    }

    public ComponentBuilder getBuilder(String tagName) {
        Class builderClass = _builders.get(tagName);
        if (builderClass == null) {
            return null;
        }
        ComponentBuilder builder = instantiate(builderClass, tagName);
        builder.setName(tagName);
        builder.setConsole(console);

        return builder;
    }

    private ComponentBuilder instantiate(Class clazz, String tagName) {
        // try with no parameter
        try {
            return (ComponentBuilder) clazz.getDeclaredConstructor().newInstance();
        } catch (Exception e) {
        }
        // try with one String parameter
        try {
            return (ComponentBuilder) clazz.getDeclaredConstructor(new Class[]{String.class}).newInstance(tagName);
        } catch (Exception e) {
            String msg = String.format("Class [%s] couldn't be instantiated, check it has a " +
                    "no-parameter constructor or a constructor with one string parameter " +
                    "(tagName).", clazz.getName());
            console.error(msg, e);
            throw new ConfigurationException(msg, e);
        }


    }

    public void setXmlParser(XmlPullParser xpp) {
        this.xpp = xpp;
    }
}
