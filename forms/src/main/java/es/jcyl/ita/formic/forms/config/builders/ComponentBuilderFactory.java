package es.jcyl.ita.formic.forms.config.builders;
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

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import es.jcyl.ita.formic.forms.components.buttonbar.UIButtonBar;
import es.jcyl.ita.formic.forms.components.datalist.UIDatalistItem;
import es.jcyl.ita.formic.forms.components.option.UIOption;
import es.jcyl.ita.formic.forms.components.placeholders.UIDivisor;
import es.jcyl.ita.formic.forms.components.placeholders.UIParagraph;
import es.jcyl.ita.formic.forms.components.select.UISelect;
import es.jcyl.ita.formic.forms.components.tab.UITab;
import es.jcyl.ita.formic.forms.components.table.UITable;
import es.jcyl.ita.formic.forms.config.AttributeResolver;
import es.jcyl.ita.formic.forms.config.Config;
import es.jcyl.ita.formic.forms.config.ConfigurationException;
import es.jcyl.ita.formic.forms.config.builders.context.ContextBuilder;
import es.jcyl.ita.formic.forms.config.builders.controllers.FormConfigBuilder;
import es.jcyl.ita.formic.forms.config.builders.controllers.FormEditControllerBuilder;
import es.jcyl.ita.formic.forms.config.builders.controllers.FormListControllerBuilder;
import es.jcyl.ita.formic.forms.config.builders.controllers.UIActionBuilder;
import es.jcyl.ita.formic.forms.config.builders.controllers.UIViewBuilder;
import es.jcyl.ita.formic.forms.config.builders.repo.EntityMappingBuilder;
import es.jcyl.ita.formic.forms.config.builders.repo.FileRepoConfigBuilder;
import es.jcyl.ita.formic.forms.config.builders.repo.MemoRepoConfigBuilder;
import es.jcyl.ita.formic.forms.config.builders.repo.RepoConfigBuilder;
import es.jcyl.ita.formic.forms.config.builders.repo.RepoFilterBuilder;
import es.jcyl.ita.formic.forms.config.builders.repo.RepoMetaConfigBuilder;
import es.jcyl.ita.formic.forms.config.builders.scripts.ScriptSourceBuilder;
import es.jcyl.ita.formic.forms.config.builders.ui.BaseUIComponentBuilder;
import es.jcyl.ita.formic.forms.config.builders.ui.UIAutocompleteBuilder;
import es.jcyl.ita.formic.forms.config.builders.ui.UIButtonBuilder;
import es.jcyl.ita.formic.forms.config.builders.ui.UICardBuilder;
import es.jcyl.ita.formic.forms.config.builders.ui.UIColumnBuilder;
import es.jcyl.ita.formic.forms.config.builders.ui.UIDatalistBuilder;
import es.jcyl.ita.formic.forms.config.builders.ui.UIDatatableBuilder;
import es.jcyl.ita.formic.forms.config.builders.ui.UIFieldBuilder;
import es.jcyl.ita.formic.forms.config.builders.ui.UIFormBuilder;
import es.jcyl.ita.formic.forms.config.builders.ui.UIImageBuilder;
import es.jcyl.ita.formic.forms.config.builders.ui.UILinkBuilder;
import es.jcyl.ita.formic.forms.config.builders.ui.UIMultiOptionBuilder;
import es.jcyl.ita.formic.forms.config.builders.ui.UIRowBuilder;
import es.jcyl.ita.formic.forms.config.builders.ui.UITabItemBuilder;
import es.jcyl.ita.formic.forms.config.builders.ui.ValidatorBuilder;
import es.jcyl.ita.formic.forms.config.elements.OptionsConfig;
import es.jcyl.ita.formic.forms.config.elements.PropertyConfig;
import es.jcyl.ita.formic.forms.config.reader.ConfigReadingInfo;
import es.jcyl.ita.formic.forms.config.reader.ReadingProcessListener;
import es.jcyl.ita.formic.forms.config.resolvers.AbstractAttributeResolver;
import es.jcyl.ita.formic.forms.config.resolvers.ActionAttributeResolver;
import es.jcyl.ita.formic.forms.config.resolvers.BindingExpressionAttResolver;
import es.jcyl.ita.formic.forms.config.resolvers.ColorAttributeResolver;
import es.jcyl.ita.formic.forms.config.resolvers.ComponentResolver;
import es.jcyl.ita.formic.forms.config.resolvers.RelativePathAttResolver;
import es.jcyl.ita.formic.forms.config.resolvers.RepositoryAttributeResolver;
import es.jcyl.ita.formic.forms.config.resolvers.ValidatorAttResolver;
import es.jcyl.ita.formic.forms.el.ValueExpressionFactory;
import es.jcyl.ita.formic.forms.project.handlers.RepoConfigHandler;
import es.jcyl.ita.formic.forms.scripts.ScriptEngine;
import es.jcyl.ita.formic.repo.RepositoryFactory;
import es.jcyl.ita.formic.repo.source.EntitySourceFactory;

import static es.jcyl.ita.formic.forms.config.DevConsole.error;

/**
 * Maps each xml tag with the builder responsible for the component creation.
 *
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
    private RepositoryFactory repoFactory = RepositoryFactory.getInstance();
    private EntitySourceFactory sourceFactory = EntitySourceFactory.getInstance();
    private ScriptEngine scriptEngine = ScriptEngine.getInstance();

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
        registerBuilder("view", newBuilder(UIViewBuilder.class, "view"));

        registerBuilder("repo", newBuilder(RepoConfigBuilder.class, "repo"));
        registerBuilder("filerepo", newBuilder(FileRepoConfigBuilder.class, "fileRepo"));
        registerBuilder("memorepo", newBuilder(MemoRepoConfigBuilder.class, "memoRepo"));

        registerBuilder("repofilter", newBuilder(RepoFilterBuilder.class, "repofilter"));
        registerBuilder("meta", newBuilder(RepoMetaConfigBuilder.class, "meta"));
        registerBuilder("property", newDefaultBuilder(PropertyConfig.class, "property"));
        registerBuilder("mapping", newBuilder(EntityMappingBuilder.class, "mapping"));

        registerBuilder("datatable", newBuilder(UIDatatableBuilder.class, "datatable"));
        registerBuilder("column", newBuilder(UIColumnBuilder.class, "column"));

        registerBuilder("datalist", newBuilder(UIDatalistBuilder.class, "datalist"));
        registerBuilder("datalistitem", newBasicBuilder(UIDatalistItem.class, "datalistitem"));

        registerBuilder("card", newBuilder(UICardBuilder.class, "card"));
        registerBuilder("p", newDefaultBuilder(UIParagraph.class, "p"));
        registerBuilder("divisor", newDefaultBuilder(UIDivisor.class, "divisor"));

        registerBuilder("link", newBuilder(UILinkBuilder.class, "link"));
        registerBuilder("button", newBuilder(UIButtonBuilder.class, "button"));
        registerBuilder("buttonbar", newDefaultGroupBuilder(UIButtonBar.class, "buttonbar"));

        ComponentBuilder actionBuilder = newBuilder(UIActionBuilder.class, "action");
        // same component builder with different aliases
        registerBuilder("action", actionBuilder);
        registerBuilder("nav", actionBuilder);
        registerBuilder("add", actionBuilder);
        registerBuilder("create", actionBuilder);
        registerBuilder("update", actionBuilder);
        registerBuilder("delete", actionBuilder);
        registerBuilder("save", actionBuilder);
        registerBuilder("cancel", actionBuilder);

        ComponentBuilder inputFieldBuilder = newBuilder(UIFieldBuilder.class, "input");
        registerBuilder("input", inputFieldBuilder);
        registerBuilder("text", inputFieldBuilder);
        registerBuilder("textarea", newBuilder(UIFieldBuilder.class, "textarea"));
        registerBuilder("switcher", inputFieldBuilder);
        registerBuilder("date", inputFieldBuilder);
        registerBuilder("datetime", inputFieldBuilder);
        registerBuilder("image", newBuilder(UIImageBuilder.class, "image"));

        registerBuilder("select", newBuilder(UIMultiOptionBuilder.class, "select", UISelect.class));
        registerBuilder("radio", newBuilder(UIMultiOptionBuilder.class, "radio", es.jcyl.ita.formic.forms.components.radio.UIRadio.class));
        registerBuilder("autocomplete", newBuilder(UIAutocompleteBuilder.class, "autocomplete"));

        registerBuilder("option", newDefaultBuilder(UIOption.class, "option"));
        registerBuilder("options", newDefaultBuilder(OptionsConfig.class, "options"));

        registerBuilder("tab", newBasicBuilder(UITab.class, "tab"));
        registerBuilder("tabitem", newBuilder(UITabItemBuilder.class, "tabitem"));

        registerBuilder("table", newBasicBuilder(UITable.class, "table"));
        registerBuilder("row", newBuilder(UIRowBuilder.class, "row"));

        registerBuilder("validator", newBuilder(ValidatorBuilder.class, "validator"));
        registerBuilder("context", new ContextBuilder());

        registerBuilder("script", newBuilder(ScriptSourceBuilder.class, "script"));

        BindingExpressionAttResolver exprResolver = new BindingExpressionAttResolver();
        registerAttResolver("binding", exprResolver);
        registerAttResolver("repo", new RepositoryAttributeResolver());
        registerAttResolver("pathResolver", new RelativePathAttResolver());
        registerAttResolver("validator", new ValidatorAttResolver());
        registerAttResolver("color", new ColorAttributeResolver());
        ActionAttributeResolver attResolver = new ActionAttributeResolver();
        registerAttResolver("action", attResolver);
        registerAttResolver("binding-route", attResolver);
    }

    /**
     * Returns the builders and resolvers that need to be notify during the XML reading process.
     *
     * @return
     */
    public List<ReadingProcessListener> getListeners() {
        List<ReadingProcessListener> listeners = new ArrayList<>();
        // get unique instances
        Set<ComponentBuilder> registeredBuilders = new HashSet<>();
        for (ComponentBuilder builder : _builders.values()) {
            if (builder instanceof ReadingProcessListener) {
                // do not duplicated instance registering
                if (!registeredBuilders.contains(builder)) {
                    listeners.add((ReadingProcessListener) builder);
                    registeredBuilders.add(builder);
                }
            }
        }
        Set<AttributeResolver> registeredResolvers = new HashSet<>();
        for (AttributeResolver resolver : _resolvers.values()) {
            if (resolver instanceof ReadingProcessListener) {
                // do not duplicated instance registering
                if (!registeredResolvers.contains(resolver)) {
                    listeners.add((ReadingProcessListener) resolver);
                    registeredResolvers.add(resolver);
                }
            }
        }
        return listeners;
    }


    public void registerBuilder(String tagName, ComponentBuilder builder) {
        _builders.put(tagName.toLowerCase(), builder);
    }

    public void registerAttResolver(String resolverId, AbstractAttributeResolver resolver) {
        resolver.setFactory(this);
        _resolvers.put(resolverId, resolver);

    }

    public <T> ComponentBuilder<T> getBuilder(String tagName, Class<T> type) {
        return (ComponentBuilder<T>) getBuilder(tagName);
    }

    public ComponentBuilder getBuilder(String tagName) {
        return _builders.get(tagName.toLowerCase());
    }

    private ComponentBuilder newBuilder(Class clazz, String tagName) {
        return newBuilder(clazz, tagName, null);
    }

    private ComponentBuilder newBuilder(Class clazz, String tagName, Class subClazz) {
        ComponentBuilder builder = null;
        // try with no parameter
        try {
            builder = (ComponentBuilder) clazz.getDeclaredConstructor().newInstance();
        } catch (Exception e) {
            // keep on trying
        }
        // try with one String parameter
        if (builder == null) {
            try {
                if (subClazz == null) {
                    builder = (ComponentBuilder) clazz.getDeclaredConstructor(new Class[]{String.class}).newInstance(tagName);
                } else {
                    builder = (ComponentBuilder) clazz.getDeclaredConstructor(new Class[]{String.class, Class.class}).newInstance(tagName, subClazz);
                }
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

    private ComponentBuilder newDefaultGroupBuilder(Class elementType, String tag) {
        AbstractComponentBuilder builder = new DefaultGroupComponentBuilder(tag, elementType);
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

    public RepoConfigHandler getRepoReader() {
        return Config.getInstance().getRepoConfigReader();
    }

    public RepositoryFactory getRepoFactory() {
        return repoFactory;
    }

    public EntitySourceFactory getSourceFactory() {
        return sourceFactory;
    }

    public ScriptEngine getScriptEngine() {
        return scriptEngine;
    }
}
