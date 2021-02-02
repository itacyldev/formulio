package es.jcyl.ita.formic.forms.config.builders.ui;
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

import org.apache.commons.lang3.ArrayUtils;
import org.mini2Dx.collections.CollectionUtils;

import es.jcyl.ita.formic.forms.components.UIGroupComponent;
import es.jcyl.ita.formic.forms.components.UIInputComponent;
import es.jcyl.ita.formic.forms.components.form.UIForm;
import es.jcyl.ita.formic.forms.components.inputfield.UIField;
import es.jcyl.ita.formic.forms.config.ConfigNodeHelper;
import es.jcyl.ita.formic.forms.config.builders.BuilderHelper;
import es.jcyl.ita.formic.forms.config.builders.ComponentBuilder;
import es.jcyl.ita.formic.forms.config.builders.ComponentBuilderFactory;
import es.jcyl.ita.formic.forms.config.reader.ConfigNode;
import es.jcyl.ita.formic.repo.Repository;
import es.jcyl.ita.formic.repo.meta.PropertyType;
import es.jcyl.ita.formic.repo.util.TypeUtils;

/**
 * @author Javier Ramos (javier.ramos@itacyl.es)
 */
public class UIGroupComponentBuilder<E extends UIGroupComponent> extends BaseUIComponentBuilder<E> {

    public UIGroupComponentBuilder(String tagName, Class<E> clazz) {
        super(tagName, clazz);
    }


    @Override
    protected void setupOnSubtreeStarts(ConfigNode<E> node) {
        Repository repo = null;
        if (node.getElement() instanceof UIForm) {
            BuilderHelper.setUpRepo(node, true);
            // Add a child node for all the properties defined in the properties attribute
            repo = ((UIForm) node.getElement()).getRepo();
        }

        if (repo == null) {
            ConfigNode ascendant = ConfigNodeHelper.findAscendantWithAttribute(node, "repo");
            repo = (Repository) BuilderHelper.getElementValue(ascendant.getElement(), "repo");
        }

        addNodesFromPropertiesAtt(node, repo);
    }

    /**
     * @param root
     * @param repo
     */
    protected void addNodesFromPropertiesAtt(ConfigNode<E> root, Repository repo) {
        // get the existing properties in the repo
        String[] propertyNames = BuilderHelper.getEffectiveAttributeProperties(repo, root);

        PropertyType[] properties;

        if (CollectionUtils.isEmpty(root.getChildren()) && ArrayUtils.isEmpty(propertyNames) || !ArrayUtils.isEmpty(propertyNames)) {
            properties = BuilderHelper.getPropertiesFromRepo(repo, propertyNames);
            for (PropertyType property : properties) {
                ConfigNode<UIInputComponent> node = createNode(property);
                root.addChild(node);
            }
        }
    }

    /**
     * Creates a ConfigNode for the property
     *
     * @param property
     * @return
     */
    private ConfigNode createNode(PropertyType property) {
        ConfigNode node = new ConfigNode("input");
        node.setId(property.name);
        node.setAttribute("type", UIFieldBuilderHelper.getType(property).toString());
        node.setAttribute("label", property.name);

        ComponentBuilder<UIField> builder = ComponentBuilderFactory.getInstance().getBuilder("input", UIField.class);

        UIField field = builder.build(node);
        node.setElement(field);
        node.setAttribute("value", "${entity." + property.name + "}", TypeUtils.getType(property.getType()));

        if (property.isPrimaryKey()) {
            // if the property is pk, do not show if the value is empty
            node.setAttribute("render", "${not empty(entity." + property.name + ")}");
            node.setAttribute("readOnly", "true");
        }

        UIFieldBuilderHelper.addValidators(node, property);

        return node;
    }
}
