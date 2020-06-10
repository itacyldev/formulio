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

import es.jcyl.ita.frmdrd.config.AttributeResolver;
import es.jcyl.ita.frmdrd.config.ComponentBuilder;
import es.jcyl.ita.frmdrd.config.Config;
import es.jcyl.ita.frmdrd.config.ConfigurationException;
import es.jcyl.ita.frmdrd.config.elements.OptionsConfig;
import es.jcyl.ita.frmdrd.config.reader.ConfigReadingInfo;
import es.jcyl.ita.frmdrd.config.resolvers.AbstractAttributeResolver;
import es.jcyl.ita.frmdrd.config.resolvers.BindingExpressionAttResolver;
import es.jcyl.ita.frmdrd.config.resolvers.ComponentResolver;
import es.jcyl.ita.frmdrd.config.resolvers.RelativePathAttResolver;
import es.jcyl.ita.frmdrd.config.resolvers.RepositoryAttributeResolver;
import es.jcyl.ita.frmdrd.el.ValueExpressionFactory;
import es.jcyl.ita.frmdrd.forms.FCAction;
import es.jcyl.ita.frmdrd.project.handlers.RepositoryConfHandler;
import es.jcyl.ita.frmdrd.ui.components.column.UIColumn;
import es.jcyl.ita.frmdrd.ui.components.option.UIOption;
import es.jcyl.ita.frmdrd.ui.components.tab.UITab;

import static es.jcyl.ita.frmdrd.config.DevConsole.error;

/**
 * @author Gustavo Río (gustavo.rio@itacyl.es)
 */
public class ComponentBuilderFactory {

    private XmlPullParser xpp;
    private static ComponentBuilderFactory _instance;
    private static final Map<String, ComponentBuilder> _builders = new HashMap<>();
    private Map<String, AbstractAttributeResolver> _resolvers = new HashMap<>();

    private ConfigReadingInfo info;
    private ComponentResolver componentResolver;
    private ValueExpressionFactory expressionFactory = ValueExpressionFactory.getInstance();

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
        registerBuilder("form", newBuilder(UIFormBuilder.class, "form"));

        registerBuilder("repo", newBuilder(RepoConfigBuilder.class, "repo"));

        registerBuilder("datatable", newBuilder(UIDatatableBuilder.class, "datatable"));
        registerBuilder("column", newDefaultBuilder(UIColumn.class, "column"));


        ComponentBuilder defaultActionBuilder = newDefaultBuilder(FCAction.class, "action");
        // same component builder with different alias
        registerBuilder("nav", defaultActionBuilder);
        registerBuilder("add", defaultActionBuilder);
        registerBuilder("update", defaultActionBuilder);
        registerBuilder("delete", defaultActionBuilder);
        registerBuilder("save", defaultActionBuilder);
        registerBuilder("cancel", defaultActionBuilder);

        ComponentBuilder inputFieldBuilder = newBuilder(UIFieldBuilder.class, "input");
        registerBuilder("input", inputFieldBuilder);
        registerBuilder("text", inputFieldBuilder);
        registerBuilder("switcher", inputFieldBuilder);
        registerBuilder("date", inputFieldBuilder);

        registerBuilder("select", newBuilder(UISelectBuilder.class, "select"));
        registerBuilder("autocomplete", newBuilder(UIAutocompleteBuilder.class, "autocomplete"));

        registerBuilder("option", newDefaultBuilder(UIOption.class, "option"));
        registerBuilder("options", newDefaultBuilder(OptionsConfig.class, "options"));

        registerBuilder("tab", newBasicBuilder(UITab.class, "tab"));
        registerBuilder("tabitem", newBuilder(UITabItemBuilder.class, "tabitem"));

        registerBuilder("validator", newBuilder(ValidatorBuilder.class, "validator"));

        //registerBuilder("param", newBasicBuilder(ValidatorBuilder.class, "validator"));

        BindingExpressionAttResolver exprResolver = new BindingExpressionAttResolver();
        registerAttResolver("binding", exprResolver);
        registerAttResolver("repo", new RepositoryAttributeResolver());
        registerAttResolver("pathResolver", new RelativePathAttResolver());
    }


    public void registerBuilder(String tagName, ComponentBuilder builder) {
        _builders.put(tagName, builder);
    }

    public void registerAttResolver(String resolverId, AbstractAttributeResolver resolver) {
        resolver.setFactory(this);
        _resolvers.put(resolverId, resolver);

    }

    public <T> ComponentBuilder<T> getBuilder(String tagName, Class<T> type) {
        return (ComponentBuilder<T>) getBuilder(tagName);
    }

    public ComponentBuilder getBuilder(String tagName) {
        return _builders.get(tagName);
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

    private ComponentBuilder newBasicBuilder(Class elementType, String tag) {
        AbstractComponentBuilder builder = new BaseUIComponentBuilder(tag, elementType);
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


    public ValueExpressionFactory getExpressionFactory() {
        return expressionFactory;
    }

    public AttributeResolver getAttributeResolver(String resolver) {
        return this._resolvers.get(resolver);
    }

    public ConfigReadingInfo getInfo() {
        return info;
    }

    public void setInfo(ConfigReadingInfo info) {
        this.info = info;
    }

    public RepositoryConfHandler getRepoReader() {
        return Config.getInstance().getRepoConfigReader();
    }
}
